package com.gradgoals;

public class ChallengeQuestion {
    private final int id;            // unique question id
    private final String categoryId; // e.g. "renting"
    private final String prompt;     // question text
    private final String correctAnswer;
    private final String explanation;

    public ChallengeQuestion(int id,
                             String categoryId,
                             String prompt,
                             String correctAnswer,
                             String explanation) {
        this.id = id;
        this.categoryId = categoryId;
        this.prompt = prompt;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }
}
