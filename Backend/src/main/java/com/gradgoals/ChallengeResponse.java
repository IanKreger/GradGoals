package com.gradgoals;

public class ChallengeResponse {
    private boolean correct;
    private String message;

    public ChallengeResponse(boolean correct, String message) {
        this.correct = correct;
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getMessage() {
        return message;
    }
}
