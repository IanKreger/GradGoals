package com.gradgoals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupabaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SupabaseClient supabaseClient;

    @BeforeEach
    void setUp() {
        //Manually set variables because they are private using @Value
        ReflectionTestUtils.setField(supabaseClient, "supabaseUrl", "https://fake-url.com");
        ReflectionTestUtils.setField(supabaseClient, "supabaseKey", "fake-key");
        
       
        ReflectionTestUtils.setField(supabaseClient, "restTemplate", restTemplate);
    }

    //Tests getTestData()

    @Test
    void testGetTestData_Success() {
        // Arrange: Pretend the API returns "Success"
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(mockResponse);

        String result = supabaseClient.getTestData();


        assertEquals("Success", result);
    }

    @Test
    void testGetTestData_ReturnsNullBody() {
        //API returns OK but body is null
        ResponseEntity<String> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(mockResponse);


        String result = supabaseClient.getTestData();

        assertNull(result);
    }

    @Test
    void testGetTestData_Exception() {
        //Simulates the network crashing
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Network Error"));

       
        assertThrows(RuntimeException.class, () -> {
            supabaseClient.getTestData();
        });
    }

    //Tests getPasswordForUser()
    
    @Test
    void testGetPasswordForUser_UserFound() {
        //Creates a mock list containing one user
        List<Map<String, String>> mockList = new ArrayList<>();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("password", "secret123");
        mockList.add(userMap);

        ResponseEntity<List<Map<String, String>>> mockResponse = new ResponseEntity<>(mockList, HttpStatus.OK);
        
        when(restTemplate.exchange(
                anyString(), 
                eq(HttpMethod.GET), 
                any(), 
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        String password = supabaseClient.getPasswordForUser("testUser");

        assertEquals("secret123", password);
    }

    @Test
    void testGetPasswordForUser_UserNotFound_EmptyList() {
        //Returns an empty list (user doesn't exist)
        List<Map<String, String>> mockList = new ArrayList<>();
        ResponseEntity<List<Map<String, String>>> mockResponse = new ResponseEntity<>(mockList, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        String password = supabaseClient.getPasswordForUser("ghostUser");
   
        assertNull(password);
    }

    @Test
    void testGetPasswordForUser_UserNotFound_NullBody() {
        //The response body itself is null
        ResponseEntity<List<Map<String, String>>> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        String password = supabaseClient.getPasswordForUser("ghostUser");

        assertNull(password);
    }

    @Test
    void testGetPasswordForUser_ListNotNullButEmptyMap() {
        // Edge Case: The list has an item, but the item is missing the "password" key
        List<Map<String, String>> mockList = new ArrayList<>();
        Map<String, String> userMap = new HashMap<>(); // Empty map
        mockList.add(userMap);

        ResponseEntity<List<Map<String, String>>> mockResponse = new ResponseEntity<>(mockList, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        
        String password = supabaseClient.getPasswordForUser("weirdUser");

        
        assertNull(password); // Should return null because map.get("password") is null
    }


    //Tests createUser()
    
    @Test
    void testCreateUser_Success() {
        //API returns 201 CREATED
        ResponseEntity<String> mockResponse = new ResponseEntity<>("User Created", HttpStatus.CREATED);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(mockResponse);

        boolean result = supabaseClient.createUser("newuser", "pass");

 
        assertTrue(result);
    }

    @Test
    void testCreateUser_Failure_BadRequest() {
        //API returns 400 BAD REQUEST (username taken, etc.)
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(mockResponse);

      
        boolean result = supabaseClient.createUser("baduser", "pass");


        assertFalse(result);
    }

    @Test
    void testCreateUser_ExceptionThrown() {
        //Simulates an exception (e.g., Supabase is down)
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class