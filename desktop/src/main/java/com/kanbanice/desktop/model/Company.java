package com.kanbanice.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
    private Long id;
    private String name;
    private List<UserProfile> users;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<UserProfile> getUsers() { return users; }
    public void setUsers(List<UserProfile> users) { this.users = users; }
}
