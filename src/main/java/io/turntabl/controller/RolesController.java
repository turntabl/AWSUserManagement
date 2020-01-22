package io.turntabl.controller;

import io.turntabl.models.BasicRole;
import io.turntabl.models.PermissionStatus;
import io.turntabl.models.UserProfileLight;
import io.turntabl.services.GSuite;
import io.turntabl.services.Roles;
import io.turntabl.utilities.CommonMethods;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {

    @GetMapping(value = "/v1/api/aws-mgnt/roles", produces = "application/json")
    public List<BasicRole> allRoles(){
        return Roles.getAllAvailableRoles();
    }

    @GetMapping(value = "/v1/api/aws-mgnt/users", produces = "application/json")
    public List<UserProfileLight> getAllUsers(){
        return  GSuite.fetchAllUsers();
    }

    @GetMapping(value = "/v1/api/aws-mgnt/grant", produces = "application/json")
    public PermissionStatus grantPermission(
            @RequestParam("userId") String userId,
            @RequestParam("awsARN") String arn
    ){
        boolean b = GSuite.addAWSARN(userId, arn);
        return new PermissionStatus(b);
    }

    @GetMapping(value = "/v1/api/aws-mgnt/revoke", produces = "application/json")
    public PermissionStatus revokePermission(
            @RequestParam("userId") String userId,
            @RequestParam("awsARN") String arn
    ){
        boolean b = GSuite.removeAWSARN(userId, arn);
        return new PermissionStatus(b);
    }
}
