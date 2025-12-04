// This class represents the response the backend sends back to the frontend
// after checking a user's answer to a challenge question.
//
// It bundles together everything the frontend needs to display:
// - whether the user was correct
// - a message ("Correct!" or "Try again")
// - the explanation for the answer
// - the questionId (so the frontend knows which question was graded)
// - the categoryId (helps with tracking progress)
//
// This gets turned into JSON and sent to the browser after every question.

package com.gradgoals;

public class ChallengeResponse {

    // true = user got the answer correct; false = incorrect
    private boolean correct;

    // short message shown on the frontend
    private String message;

    // explanation to teach the user why the answer is what it is
    private String explanation;

    // which question this response belongs to
    private int questionId;

    // category the question belongs to (budgeting, saving, etc.)
    private String categoryId;

    // empty constructor — required for Spring Boot to build this object from JSON if needed
    public ChallengeResponse() {
    }

    // main constructor — used by the controller when sending a response back to the frontend
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

    // getters — these allow the frontend to read the values from the response
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

    // ---------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------
    // These are used when Spring maps JSON → Java object.
    // Setters always return void because their only job is to
    // update internal values — they don't need to return anything.
    // ("void" = the method performs an action but sends back nothing.)
    // ---------------------------------------------------------

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
