package io.turntabl.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.models.BasicRole;
import io.turntabl.models.PermissionStatus;
import io.turntabl.models.RolesRequest;
import io.turntabl.models.UserProfileLight;
import io.turntabl.services.EMail;
import io.turntabl.services.PermissionStorage;
import io.turntabl.services.GSuite;
import io.turntabl.services.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Api
@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {
    @Autowired
    private PermissionStorage permissionStorage;

    @Autowired
    public EMail eMail;

    @ApiOperation("user submits list of aws arns to be given access to..")
    @PostMapping(value = "/v1/api/aws-mgnt/send", consumes = "application/json", produces = "application/json")
    public PermissionStatus sendPermission(@RequestBody RolesRequest rolesRequest){
        try {
            permissionStorage.insert("requests", rolesRequest.getEmail(), rolesRequest.getAwsArns());
            eMail.send(rolesRequest.getEmail(), rolesRequest.getAwsArns());
            return new PermissionStatus(true);
        }catch (Exception e){
            return new PermissionStatus(false);
        }
    }

    @ApiOperation("get all roles on the aws account")
    @GetMapping(value = "/v1/api/aws-mgnt/roles", produces = "application/json")
    public List<BasicRole> allRoles(){
        return Roles.getAllAvailableRoles();
    }

    @ApiOperation("get all users who are engineer on the gsuite account")
    @GetMapping(value = "/v1/api/aws-mgnt/users", produces = "application/json")
    public List<UserProfileLight> getAllUsers(){
        return  GSuite.fetchAllUsers();
    }

    @ApiOperation("grant a user permission to use a service on aws using the aws role arn")
    @PostMapping(value = "/v1/api/aws-mgnt/grant", produces = "application/json")
    public PermissionStatus grantMultiplePermission(
            @RequestBody RolesRequest rolesRequest
    ){
        try {
            String userId = GSuite.fetchEmailToIds().getOrDefault(rolesRequest.getEmail(), "");
            if ( userId.isEmpty()){
                return new PermissionStatus(false);
            }
            GSuite.grantMultipleAWSARN(userId, rolesRequest.getAwsArns());
            return new PermissionStatus(true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new PermissionStatus(false);
        }
    }

    @ApiOperation("revoke a user permission to use a service on aws using the aws role arn")
    @PostMapping(value = "/v1/api/aws-mgnt/revoke", produces = "application/json")
    public PermissionStatus revokeMultiplePermission(
            @RequestBody RolesRequest rolesRequest
    ){
        try {
            String userId = GSuite.fetchEmailToIds().getOrDefault(rolesRequest.getEmail(), "");
            if ( userId.isEmpty()){
                return new PermissionStatus(false);
            }
            GSuite.revokeMultipleAWSARN(userId, rolesRequest.getAwsArns());
            return new PermissionStatus(true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new PermissionStatus(false);
        }
    }


}
