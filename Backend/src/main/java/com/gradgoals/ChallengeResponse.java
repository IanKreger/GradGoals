package com.gradgoals.challenge;

public class ChallengeResponse {
    private boolean correct;
    private String message;
    private String explanation;
    private int questionId;
    private String categoryId;

    public ChallengeResponse() {
    }

    public ChallengeResponse(boolean correct,
                             String message,
                             String explanation,
                             int questionId,
                             String categoryId) {
        this.correct = correct;
        this.message = message;
        this.explanation = explanation;
        this.questionId = questionId;
        this.categoryId = categoryId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getMessage() {
        return message;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
