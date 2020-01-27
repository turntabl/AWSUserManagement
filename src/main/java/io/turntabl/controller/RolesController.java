package io.turntabl.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.models.*;
import io.turntabl.services.EMail;
import io.turntabl.services.GSuite;
import io.turntabl.services.PermissionStorage;
import io.turntabl.services.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api
@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {
    @Autowired
    private PermissionStorage permissionStorage;


    @ApiOperation("user submits list of aws arns to be given access to..")
    @PostMapping(value = "/v1/api/aws-mgnt/send", consumes = "application/json", produces = "application/json")
    public PermissionStatus sendPermission(@RequestBody RolesRequest rolesRequest){
        try {
            long insertId = permissionStorage.insert(rolesRequest.getEmail(), rolesRequest.getAwsArns());
            EMail.requestMessage(rolesRequest.getEmail(), rolesRequest.getAwsArns(), String.valueOf(insertId));
            return new PermissionStatus(true);
        }catch (Exception e){
            e.printStackTrace();
            return new PermissionStatus(false);
        }
    }

    @ApiOperation("approve a request to gain permission to a set of services")
    @GetMapping(value = "/v1/api/aws-mgnt/approve/{requestId}", produces = "application/json")
    public PermissionStatus approve(  @PathVariable("requestId") long requestId ){
        try {
            Request requestDetails = permissionStorage.getRequestDetails(requestId);
            permissionStorage.approvedRequest( requestId );

            String userEmail = requestDetails.getUserEmail();
            List<String> strings = Arrays.asList(requestDetails.getARN().split(" -,,- "));
            Set<String> awsArns = new HashSet<>(strings);
           GSuite.grantMultipleAWSARN(userEmail, awsArns);

           EMail.feedbackMessage(userEmail, true);

            return new PermissionStatus(true);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new PermissionStatus(false);
        }
    }

    @ApiOperation("decline a request to gain permission to a set of services")
    @GetMapping(value = "/v1/api/aws-mgnt/decline/{requestId}", produces = "application/json")
    public PermissionStatus decline(  @PathVariable("requestId") long requestId ){
        try {
            String email = permissionStorage.removeRequest(requestId);
            EMail.feedbackMessage(email, false);
            return new PermissionStatus(true);
        } catch (MessagingException e) {
            e.printStackTrace();
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

   /* @ApiOperation("grant a user permission to use a service on aws using the aws role arn")
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
*/

}
