package io.turntabl.models;

import java.util.Set;

public class RolesRequest {
    private String email;
    private Set<String> awsArns;

    public RolesRequest() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getAwsArns() {
        return awsArns;
    }

    public void setAwsArns(Set<String> awsArns) {
        this.awsArns = awsArns;
    }
}
