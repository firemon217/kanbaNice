package com.kanbanice.desktop.state;

import com.kanbanice.desktop.model.*;
import java.util.List;

public class AppState {

    private static AppState instance;

    private String token;
    private UserProfile user;
    private Company company;
    private List<Project> projects;
    private Project currentProject;

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) instance = new AppState();
        return instance;
    }

    public void clear() {
        token = null;
        user = null;
        company = null;
        projects = null;
        currentProject = null;
    }

    public String getToken() { return token; }
    public void setToken(String t) { this.token = t; }

    public UserProfile getUser() { return user; }
    public void setUser(UserProfile u) { this.user = u; }

    public Company getCompany() { return company; }
    public void setCompany(Company c) { this.company = c; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> p) { this.projects = p; }

    public Project getCurrentProject() { return currentProject; }
    public void setCurrentProject(Project p) { this.currentProject = p; }
}
