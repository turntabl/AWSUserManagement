package io.turntabl.models;

public class Request {
    private long id;
    private String userEmail;
    private String status;
    private String approvedTime;
    private String ARN;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(String approvedTime) {
        this.approvedTime = approvedTime;
    }

    public String getARN() {
        return ARN;
    }

    public void setARN(String ARN) {
        this.ARN = ARN;
    }
}
