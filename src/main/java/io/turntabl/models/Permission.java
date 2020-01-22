package io.turntabl.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Permission {
    private String value;

    public Permission(String value) {
        this.value = value.trim();
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        String type = "work";
        map.put("type", type);
        map.put("value", value + ", arn:aws:iam::926377470665:saml-provider/gsuite-idp");
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
