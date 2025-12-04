// This class represents a single challenge submission from the user.
// Basically, when someone answers a budgeting question on the site,
// their answer gets stored in this object so the backend can check it
// and send back whether they got it right. Super simple data holder.
package com.gradgoals;

public class Challenge {
    private String answer;

    // empty constructor — needed so Spring can create a Challenge object automatically
    public Challenge() {}

    // constructor I can use when I want to manually create a Challenge with an answer pre-set
    public Challenge(String answer) {
        this.answer = answer;
    }

    // getter — lets the backend pull the user’s submitted answer
    public String getAnswer() {
        return answer;
    }

    // setter — updates the answer value (mainly used when Spring maps JSON → Java object)
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
