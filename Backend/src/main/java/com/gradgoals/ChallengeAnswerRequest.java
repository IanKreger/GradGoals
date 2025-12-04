// This class represents the request the frontend sends when a user answers
// a challenge question. It holds both the questionId and the user's answer,
// so the backend knows *which* question they’re answering and *what* they typed.
// Basically the little packet of info that moves between the JS and the controller.

package com.gradgoals;

public class ChallengeAnswerRequest {

    // which question the user is answering (we track this so progress works)
    private int questionId;

    // the actual answer the user typed into the challenge
    private String answer;

    // empty constructor — needed for Spring to auto-create this from JSON
    public ChallengeAnswerRequest() {}

    // constructor I can use manually if I want to create a request in code
    public ChallengeAnswerRequest(int questionId, String answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    // getters — allow the backend to read the fields
    public int getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }

    // setters — used when Spring maps JSON → Java object
    //public → Spring Boot needs to call this method to fill in the data from JSON. If it isn’t public, Spring can’t set the field
    //void → A setter just updates the value inside the object and doesn’t return anything
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
