package com.gradgoals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    //Tests constructors

    @Test
    void testParameterizedConstructor() {
        //Creates object using the (username, password) constructor
        LoginRequest request = new LoginRequest("testUser", "testPass");

        assertEquals("testUser", request.getUsername());
        assertEquals("testPass", request.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        //Ensures the empty constructor creates a non-null object
        LoginRequest request = new LoginRequest();

        assertNotNull(request);
        assertNull(request.getUsername()); // Should be null initially
    }

    //Tests setters and getters

    @Test
    void testSetAndGetUsername() {
        //Sets username manually and retrieves it
        LoginRequest request = new LoginRequest();
        request.setUsername("customUser");

        assertEquals("customUser", request.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        //Sets password manually and retrieves it
        LoginRequest request = new LoginRequest();
        request.setPassword("securePassword123");

        assertEquals("securePassword123", request.getPassword());
    }
}