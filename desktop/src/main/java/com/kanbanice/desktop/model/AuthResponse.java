package com.kanbanice.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    @JsonProperty("accessToken") 
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
