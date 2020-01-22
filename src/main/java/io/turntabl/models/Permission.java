package io.turntabl.models;

import java.util.HashMap;
import java.util.Map;

public class Permission {
    private String value;

    public Permission(String value) {
        this.value = value;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        String type = "work";
        map.put("type", type);
        map.put("value", value + ", arn:aws:iam::926377470665:saml-provider/gsuite-idp");
        return map;
    }
}
