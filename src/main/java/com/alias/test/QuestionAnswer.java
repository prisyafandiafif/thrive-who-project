package com.alias.test;

import java.util.List;

/**
 * Created by aliakbars on 9/30/13.
 */
public class QuestionAnswer {

    private int questionId;
    private String question;
    private List<String> answers;
    private int correctAnswer;

    public QuestionAnswer(int questionId, String question, List<String> answers, int correctAnswer) {
        this.questionId = questionId;
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
