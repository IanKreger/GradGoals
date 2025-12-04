// This class represents ONE challenge question in the GradGoals app.
// Each question has:
    // - an id (so the backend knows which question it is)
    // - a category (budgeting, saving, travel, etc.)
    // - the prompt (the actual question text shown on the site)
    // - the correct answer (for grading)
    // - an explanation (shown after the user submits)
//
// Basically, this is the "data model" for a question. The controller
// creates a bunch of these and the frontend displays them one at a time.

package com.gradgoals;

public class ChallengeQuestion {

    // unique ID number for the question
    private int id;

    // which category this question belongs to (ex: "budgeting", "renting")
    private String categoryId;

    // the actual question text that will show up on the site
    private String prompt;

    // the correct answer the backend will check against
    private String correctAnswer;

    // short explanation shown after the user submits their answer
    private String explanation;

    // constructor — used when we create each question in the controller
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

    // getter — lets the backend/frontend read the question ID
    public int getId() {
        return id;
    }

    // getter — tells which category the question is in
    public String getCategoryId() {
        return categoryId;
    }

    // getter — returns the question text for the frontend to display
    public String getPrompt() {
        return prompt;
    }

    // getter — the correct answer we compare to the user's answer
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // getter — explanation shown after grading the question
    public String getExplanation() {
        return explanation;
    }
}
