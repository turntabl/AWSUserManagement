package io.turntabl.models;

public class PermissionStatus {
    private String status;

    public PermissionStatus() { }

    public PermissionStatus(boolean state) {
        if (state){
            this.status = "Successful";
        }else {
            this.status = "Not Successful";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
