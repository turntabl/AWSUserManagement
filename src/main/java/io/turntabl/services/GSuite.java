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
import io.turntabl.models.UserProfileLight;

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
    private static final String CREDENTIALS_FILE_PATH = "credentials/google.json";



    /**
     * Fetch all name and user ids, Gsuite Users Engineers
     * @return ap key -> a users id -> user username
     */
    public static List<UserProfileLight> fetchAllUsers(){
        List<UserProfileLight> userProfileLights = new ArrayList<>();
        try {
            Directory service = getDirectory();
            Directory.Users.List usersInDomain = service.users().list().setDomain(System.getenv("GSUITE_DOMAIN")).setProjection("full");
            List<User> userList = usersInDomain.execute().getUsers();

            userList.forEach(user -> {
                if ( user.getOrgUnitPath().equals(System.getenv("GSUITE_OrgUnitPath"))) {
                    UserProfileLight profileLight = new UserProfileLight( user.getId(), user.getName().getFullName());
                    userProfileLights.add(profileLight);
                }
            });
            return userProfileLights;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return userProfileLights;
        }
    }


    /**
     * Fetch all Gsuite Users who are Engineers with all their details
     * @return Map key -> a users Id Value -> user profile
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static Map<String, String> fetchEmailToIds() throws IOException, GeneralSecurityException {
        Map<String, String> users = new HashMap<>();
        Directory service = getDirectory();
        Directory.Users.List usersInDomain = service.users().list().setDomain( System.getenv("GSUITE_DOMAIN")).setProjection("full");
        List<User> userList = usersInDomain.execute().getUsers();

        userList.forEach(user -> {
            if ( user.getOrgUnitPath().equals(System.getenv("GSUITE_OrgUnitPath"))) {
                users.put(user.getPrimaryEmail(), user.getId());
            }
        });
        return users;
    }

    public static Map<String, String> fetchEmailToUserName() throws IOException, GeneralSecurityException {
        Map<String, String> users = new HashMap<>();
        Directory service = getDirectory();
        Directory.Users.List usersInDomain = service.users().list().setDomain(System.getenv("GSUITE_DOMAIN")).setProjection("full");
        List<User> userList = usersInDomain.execute().getUsers();

        userList.forEach(user -> {
            if ( user.getOrgUnitPath().equals(System.getenv("GSUITE_OrgUnitPath"))) {
                users.put(user.getPrimaryEmail(), user.getName().getFullName());
            }
        });
        return users;
    }


    /**
     * Returns a directory instance that is used to get access too the resources on the Gsuite account, such as user, grops etc
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
                .setServiceAccountUser(System.getenv("GSUITE_ADMIN_EMAIL"))
                .setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
                .setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
                .setTokenServerEncodedUrl(gcFromJson.getTokenServerEncodedUrl())
                .build();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();
    }



    /**
     * Add multiple roles to a single user
     * @param userEmail -> the user gsuite id
     * @param awsArns -> list of arns to be added
     */
    public static void grantMultipleAWSARN(String userEmail, Set<String> awsArns) {
        try {
            Directory service = getDirectory();
            String userId = fetchEmailToIds().getOrDefault(userEmail, "");
            if (userId.isEmpty()) { return;}
            User user = service.users().get(userId).setProjection("full").execute();

            Map<String, Map<String, Object>> customSchemas = user.getCustomSchemas();
            if (customSchemas != null) {
                Map<String, Object> o = customSchemas.getOrDefault("AWS_SAML", null);
                if (o != null) {
                    List<Map<String, Object>> iam_role = (List<Map<String, Object>>) o.get("IAM_Role");

                    if (iam_role != null) {

                        awsArns.forEach( arn -> {
                            long count = iam_role.stream().filter(role -> {
                                String value = (String) role.get("value");
                                return value.trim().startsWith(arn.trim());
                            }).count();

                            if ( count == 0) {
                                Permission permission = new Permission(arn);
                                iam_role.add(permission.toMap());
                            }
                        });

                        // update
                        service.users().update(userId, user).execute();
                    }
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    /**
     * revoke multiple roles to a single user
     * @param userEmail -> the user gsuite id
     * @param awsArns -> list of arns to be added
     */
    public static void revokeMultipleAWSARN(String userEmail, Set<String> awsArns) {
        try {
            Directory service = getDirectory();
            String userId = fetchEmailToIds().getOrDefault(userEmail, "");
            if (userId.isEmpty()) { return;}
            User user = service.users().get(userId).setProjection("full").execute();

            Map<String, Map<String, Object>> customSchemas = user.getCustomSchemas();
            if (customSchemas != null) {
                Map<String, Object> o = customSchemas.getOrDefault("AWS_SAML", null);
                if (o != null) {
                    List<Map<String, Object>> iam_role = (List<Map<String, Object>>) o.get("IAM_Role");

                    if (iam_role != null) {

                         iam_role.stream().filter(role -> {
                            String value = (String) role.get("value");
                            return awsArnsContains(awsArns, value);
                        }).collect(Collectors.toList()).forEach(iam_role::remove);

                         // update
                        service.users().update(userId, user).execute();
                    }
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static boolean awsArnsContains(Set<String> awsArns, String value) {
        long count = awsArns.stream().filter(arn -> value.trim().startsWith(arn.trim())).count();
        return count > 0;
    }

}