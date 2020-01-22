package io.turntabl.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.common.collect.ImmutableList;
import io.turntabl.models.Permission;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class GSuite {
    /**
     * Static instances used in multiple ways
     */
    private static final String APPLICATION_NAME = "Turntabl G suite - AWS Role Management";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = ImmutableList.of( DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY, DirectoryScopes.ADMIN_DIRECTORY_USER);
    private static final String CREDENTIALS_FILE_PATH = "aws-account-management-265416-84d47f654b81.json";



    /**
     * Fetch all name and user ids Gsuite Users who are Engineers
     * @return ap key -> a users name -> user id
     */
    public static Map<String, String> fetchAllUsers(){
        Map<String, String> users = new HashMap<>();
        try {
            fetchAllUserNameAndIds(users);
            return users;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return users;
        }
    }


    /***
     * fetch all engineers on Gsuite account
     * @param users
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static void fetchAllUserNameAndIds(Map<String, String> users) throws IOException, GeneralSecurityException {
        Directory service = getDirectory();
        Directory.Users.List usersInDomain = service.users().list().setDomain("turntabl.io").setProjection("full");
        List<User> userList = usersInDomain.execute().getUsers();

        userList.forEach(user -> {
            if ( user.getOrgUnitPath().equals("/GH Tech")) {
                users.put(user.getName().getFullName(), user.getId());
            }
        });
    }


    /**
     * Fetch all Gsuite Users who are Engineers with all their details
     * @return Map key -> a users Id Value -> user profile
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static Map<String, User> fetchAllUserInfo() throws IOException, GeneralSecurityException {
        Map<String, User> users = new HashMap<>();
        Directory service = getDirectory();
        Directory.Users.List usersInDomain = service.users().list().setDomain("turntabl.io").setProjection("full");
        List<User> userList = usersInDomain.execute().getUsers();

        userList.forEach(user -> {
            if ( user.getOrgUnitPath().equals("/GH Tech")) {
                users.put(user.getId(), user);
            }
        });
        return users;
    }


    /**
     * Grant aws role or policy permission of a user
     * @param userId id of Gsuite User
     * @param awsArn aws Resource Name to be granted permission to
     * @return boolean true for permission granted successful && false if the permission is already available or something went wrong
     */
    public static boolean addAWSARN(String userId, String awsArn){
        try {
            Map<String, User> allUserInfo = fetchAllUserInfo();
            User user = allUserInfo.get(userId);

            Map<String, Map<String, Object>> customSchemas = user.getCustomSchemas();
            if ( customSchemas != null){
                Map<String, Object> o = customSchemas.getOrDefault("AWS_SAML", null);
                if ( o != null) {
                    List<Map<String, Object>> iam_role = (List<Map<String, Object>>) o.get("IAM_Role");

                    if (iam_role != null) {
                        long count = iam_role.stream().map(role -> (String) role.get("value")).filter(val -> val.trim().startsWith(awsArn.trim())).count();
                        if (count > 0) { return false; }

                        Permission permission = new Permission(awsArn);
                        iam_role.add(permission.toMap());

                        Directory service = getDirectory();
                        service.users().update(userId, user).execute();
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Revoke aws role or policy permission of a user
     * @param userId id of Gsuite User
     * @param awsArn aws Resource Name to be revoked
     * @return boolean true for permission revoke successful && false if the permission to revoke isn't available or something went wrong
     */
    public static boolean removeAWSARN(String userId, String awsArn){
        try {
            Map<String, User> allUserInfo = fetchAllUserInfo();
            User user = allUserInfo.get(userId);

            Map<String, Map<String, Object>> customSchemas = user.getCustomSchemas();
            if ( customSchemas != null){
                Map<String, Object> o = customSchemas.getOrDefault("AWS_SAML", null);
                if ( o != null) {
                    List<Map<String, Object>> iam_role = (List<Map<String, Object>>) o.get("IAM_Role");

                    if (iam_role != null) {
                        Optional<Map<String, Object>> permision = iam_role.stream().filter(role -> {
                            String value = (String) role.get("value");
                            return value.trim().startsWith(awsArn.trim());
                        }).findFirst();

                        if ( permision.isPresent()){
                            iam_role.remove(permision.get());
                            Directory service = getDirectory();
                            service.users().update(userId, user).execute();
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }

            return false;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Returns a directory instance that is used to get acccess too the resources on the Gsuite account, such as user, grops etc
     * @return Directory
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static Directory getDirectory() throws IOException, GeneralSecurityException {
        GoogleCredential gcFromJson = GoogleCredential
                                            .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                                            .createScoped(SCOPES);

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(gcFromJson.getTransport())
                .setJsonFactory(gcFromJson.getJsonFactory())
                .setServiceAccountId(gcFromJson.getServiceAccountId())
                .setServiceAccountUser("sam@turntabl.io")
                .setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
                .setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
                .setTokenServerEncodedUrl(gcFromJson.getTokenServerEncodedUrl())
                .build();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();
    }

}