package com.gradgoals.challenge;

public class ChallengeAnswerRequest {
    private int questionId;
    private String answer;

    public ChallengeAnswerRequest() {
    }

    public ChallengeAnswerRequest(int questionId, String answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
