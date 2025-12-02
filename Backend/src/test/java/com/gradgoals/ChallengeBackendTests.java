package com.gradgoals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeBackendTests {
    @Test
    void basicSanityTest() {
        // Arrange
        int a = 2;
        int b = 3;

        // Act
        int result = a + b;

        // Assert
        assertEquals(5, result);
    }
}
