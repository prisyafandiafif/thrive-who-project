package com.alias.test;

/**
 * Created by aliakbars on 2/26/14.
 */
public class UserAnswer {

    private int questionId;
    private int answer;
    private int score;
    private double time;

    public UserAnswer(int questionId, int answer, int score, double time) {
        this.questionId = questionId;
        this.answer = answer;
        this.score = score;
        this.time = time;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public int getAnswer() {
        return this.answer;
    }

    public int getScore() {
        return this.score;
    }

    public double getTime() {
        return this.time;
    }
}
