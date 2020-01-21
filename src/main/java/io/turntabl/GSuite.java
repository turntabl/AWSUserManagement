package io.turntabl;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserPhone;
import com.google.common.collect.ImmutableList;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GSuite {
    private static final String APPLICATION_NAME = "Turntabl G suite - AWS Role Management";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = ImmutableList.of( DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY, DirectoryScopes.ADMIN_DIRECTORY_USER);
    private static final String CREDENTIALS_FILE_PATH = "aws-account-management-265416-84d47f654b81.json";


    public static void main(String... args) throws IOException, GeneralSecurityException {
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
        Directory service = new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();

        Directory.Users  usr = service.users();
        User dawud = usr.get("100262028487240433703").execute();
        dawud.getNonEditableAliases().forEach(System.out::println);
        Object phones = dawud.getPhones();
        if( phones != null){
                List<ArrayMap<String,Object>> objects = (List<ArrayMap<String,Object>>)phones;
                for (ArrayMap<String,Object> object: objects){
                    for (int i = 0; i < object.size(); i++){
                        System.out.println(object.getKey(i) + " -> " + object.getValue(i));
                    }
                       // id.put(object.getKey(i), object.getValue(i));
                }
        }

        System.out.println("***********************************************\n" );
        System.out.println(dawud.getAliases() != null);


        // dawud.getIms()
        /*execute.getUsers().forEach(usr -> System.out.println(usr.getName().getFullName()));*/
    }

}