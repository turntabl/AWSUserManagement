package io.turntabl.controller;

import io.turntabl.models.BasicRole;
import io.turntabl.models.UserProfileLight;
import io.turntabl.services.GSuite;
import io.turntabl.services.Roles;
import io.turntabl.utilities.CommonMethods;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {

    @GetMapping("/v1/api/aws-mgnt/roles")
    public List<BasicRole> allRoles(){
        return Roles.getAllAvailableRoles();
    }

    @GetMapping("/v1/api/aws-mgnt/users")
    public List<UserProfileLight> getAllUsers(){
        return  GSuite.fetchAllUsers();
    }
}
