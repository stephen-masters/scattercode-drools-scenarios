package uk.co.scattercode.drools.util;

import org.drools.builder.ResourceType;

/**
 * A simple representation of a Drools resource.
 * 
 * @author Stephen Masters
 */
public class DroolsResource {

    private String path;
    private ResourcePathType pathType;
    private ResourceType type;
    private String username = null;
    private String password = null;

    public DroolsResource(String path, ResourcePathType pathType, ResourceType type) {
        this.path = path;
        this.pathType = pathType;
        this.type = type;
    }

    public DroolsResource(String path, ResourcePathType pathType, ResourceType type, String username, String password) {
        this.path = path;
        this.pathType = pathType;
        this.type = type;
        this.username = username;
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ResourcePathType getPathType() {
        return pathType;
    }

    public void setPathType(ResourcePathType pathType) {
        this.pathType = pathType;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
