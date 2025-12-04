package com.gradgoals;

// This class represents the data sent from the frontend when a user tries to log in.
// Spring automatically fills this object with the JSON fields "username" and "password".
public class LoginRequest {

    // Fields that store the login information from the client
    private String username;
    private String password;

    // Constructor used when creating a LoginRequest manually
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Empty constructor needed by Spring so it can build this object from JSON
    public LoginRequest() {
    }

    // Getter: lets other parts of the program read the username
    public String getUsername() {
        return username;
    }

    // Setter: allows updating the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter: lets other parts read the password
    public String getPassword() {
        return password;
    }

    // Setter: allows updating the password
    public void setPassword(String password) {
        this.password = password;
    }
}
