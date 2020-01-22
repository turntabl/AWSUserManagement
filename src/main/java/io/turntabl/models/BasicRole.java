package io.turntabl.models;

public class BasicRole {
    private String roleName;
    private String roleARN;
    private String description;

    public BasicRole() { }

    public BasicRole(String roleName, String roleARN, String description) {
        this.roleName = roleName;
        this.roleARN = roleARN;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
