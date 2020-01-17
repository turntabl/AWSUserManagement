package io.turntabl.models;

public class BasicRole {
    private String roleName;
    private String roleARN;

    public BasicRole() { }

    public BasicRole(String roleName, String roleARM) {
        this.roleName = roleName;
        this.roleARN = roleARM;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleARN() {
        return roleARN;
    }

    public void setRoleARN(String roleARN) {
        this.roleARN = roleARN;
    }
}
