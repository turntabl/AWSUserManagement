package io.turntabl.controller;

import io.turntabl.models.BasicRole;
import io.turntabl.services.Roles;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = {"GET"} )
public class RolesController {

    @GetMapping("/v1/api/aws/roles")
    public List<BasicRole> allRoles(){
        return Roles.getAllAvailableRoles();
    }
}
