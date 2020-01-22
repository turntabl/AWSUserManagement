package io.turntabl.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.models.BasicRole;
import io.turntabl.models.PermissionStatus;
import io.turntabl.models.UserProfileLight;
import io.turntabl.services.GSuite;
import io.turntabl.services.Roles;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {

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
    @GetMapping(value = "/v1/api/aws-mgnt/grant", produces = "application/json")
    public PermissionStatus grantPermission(
            @RequestParam("userId") String userId,
            @RequestParam("awsARN") String arn
    ){
        boolean b = GSuite.addAWSARN(userId, arn);
        return new PermissionStatus(b);
    }

    @ApiOperation("revoke a user's permission to use a service on aws using the aws role arn")
    @GetMapping(value = "/v1/api/aws-mgnt/revoke", produces = "application/json")
    public PermissionStatus revokePermission(
            @RequestParam("userId") String userId,
            @RequestParam("awsARN") String arn
    ){
        boolean b = GSuite.removeAWSARN(userId, arn);
        return new PermissionStatus(b);
    }
}
