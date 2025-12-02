package com.gradgoals;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeBackendTests {

    // 1) Category stores values
    @Test
    void categoryStoresFields() {
        // Arrange
        ChallengeCategory cat = new ChallengeCategory("budgeting", "Budgeting & Cash Flow", "blurb");

        // Act
        String id = cat.getId();
        String name = cat.getName();
        String blurb = cat.getBlurb();

        // Assert
        assertEquals("budgeting", id);
        assertEquals("Budgeting & Cash Flow", name);
        assertEquals("blurb", blurb);
    }

    // 2) Question stores fields
    @Test
    void questionStoresFields() {
        // Arrange
        ChallengeQuestion q = new ChallengeQuestion(1, "budgeting", "prompt", "1200", "explanation");

        // Act
        String correct = q.getCorrectAnswer();

        // Assert
        assertEquals("1200", correct);
    }

    // 3) AnswerRequest constructor stores values
    @Test
    void answerRequestConstructorStoresValues() {
        // Arrange
        ChallengeAnswerRequest req = new ChallengeAnswerRequest(10, "500");

        // Act
        int id = req.getQuestionId();
        String answer = req.getAnswer();

        // Assert
        assertEquals(10, id);
        assertEquals("500", answer);
    }

    // 4) AnswerRequest setters work
    @Test
    void answerRequestSettersWork() {
        // Arrange
        ChallengeAnswerRequest req = new ChallengeAnswerRequest();

        // Act
        req.setQuestionId(20);
        req.setAnswer("900");

        // Assert
        assertEquals(20, req.getQuestionId());
        assertEquals("900", req.getAnswer());
    }

    // 5) Response constructor stores values
    @Test
    void responseConstructorStoresValues() {
        // Arrange
        ChallengeResponse resp = new ChallengeResponse(true, "Correct!", "Math", 1, "budgeting");

        // Act
        String message = resp.getMessage();

        // Assert
        assertEquals("Correct!", message);
    }

    // 6) getCategories returns budgeting
    @Test
    void getCategoriesReturnsBudgeting() {
        // Arrange
        ChallengeController controller = new ChallengeController();

        // Act
        List<Map<String, Object>> categories = controller.getCategories();

        // Assert
        assertFalse(categories.isEmpty());
        assertTrue(categories.stream().anyMatch(c -> "budgeting".equals(c.get("id"))));
    }

    // 7) getRandomQuestion returns correct category
    @Test
    void getRandomQuestionReturnsCorrectCategory() {
        // Arrange
        ChallengeController controller = new ChallengeController();

        // Act
        ChallengeQuestion q = controller.getRandomQuestion("budgeting");

        // Assert
        assertEquals("budgeting", q.getCategoryId());
    }

    // 8) correct answer returns "Correct"
    @Test
    void correctAnswerReturnsCorrectMessage() {
        // Arrange
        ChallengeController controller = new ChallengeController();
        ChallengeAnswerRequest req = new ChallengeAnswerRequest(1, "1200");

        // Act
        ChallengeResponse resp = controller.checkAnswer(req);

        // Assert
        assertTrue(resp.getMessage().startsWith("Correct"));
    }

    // 9) wrong answer returns "Not quite"
    @Test
    void wrongAnswerReturnsNotQuiteMessage() {
        // Arrange
        ChallengeController controller = new ChallengeController();
        ChallengeAnswerRequest req = new ChallengeAnswerRequest(1, "999");

        // Act
        ChallengeResponse resp = controller.checkAnswer(req);

        // Assert
        assertTrue(resp.getMessage().startsWith("Not"));
    }

    // 10) formatted numeric answer counts as correct
    @Test
    void formattedNumberStillCountsAsCorrect() {
        // Arrange
        ChallengeController controller = new ChallengeController();
        ChallengeAnswerRequest req = new ChallengeAnswerRequest(1, "$1,200 ");

        // Act
        ChallengeResponse resp = controller.checkAnswer(req);

        // Assert
        assertTrue(resp.getMessage().startsWith("Correct"));
    }
}

