package com.example.testcore.models;
// lombok library
// option look up later

public class Question {
    private String questionText;
    private String answerText;
    private String standardLabel;

    public Question(String questionText, String answerText, String standardLabel) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.standardLabel = standardLabel;
    }

    public String getStandardLabel() {
        return standardLabel;
    }

    public void setStandardLabel(String standardLabel) {
        this.standardLabel = standardLabel;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
