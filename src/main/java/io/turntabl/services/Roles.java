package io.turntabl.services;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import io.turntabl.models.BasicRole;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Roles {
    private static final ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile(new File("credentials/aws"));
    private static final ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider( profilesConfigFile, "idawud");

    public static List<BasicRole> getAllAvailableRoles() {
        List<BasicRole> roles = new ArrayList<>();

        AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_2)
                .withCredentials(credentialsProvider)
                .build();
        iam.listRoles().getRoles().forEach(role -> {
            if ( role.getAssumeRolePolicyDocument().contains("Federated")){
                BasicRole basicRole = new BasicRole(role.getRoleName(), role.getArn(), role.getDescription());
                roles.add(basicRole);
            }
        });
       return roles;
    }

}
