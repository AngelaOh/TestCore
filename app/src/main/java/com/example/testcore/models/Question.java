package com.example.testcore.models;
// lombok library
// option look up later

public class Question {
    private String questionText;
    private String answerChoiceA, answerChoiceB, answerChoiceC, answerChoiceD;
    private String standardLabel;

    public Question() {}

    public Question(String questionText, String answerChoiceA, String answerChoiceB, String answerChoiceC, String answerChoiceD, String standardLabel) {
        this.questionText = questionText;
        this.answerChoiceA = answerChoiceA;
        this.answerChoiceB = answerChoiceB;
        this.answerChoiceC = answerChoiceC;
        this.answerChoiceD = answerChoiceD;
        this.standardLabel = standardLabel;
    }

    public String getAnswerChoiceA() {
        return answerChoiceA;
    }

    public void setAnswerChoiceA(String answerChoiceA) {
        this.answerChoiceA = answerChoiceA;
    }

    public String getAnswerChoiceB() {
        return answerChoiceB;
    }

    public void setAnswerChoiceB(String answerChoiceB) {
        this.answerChoiceB = answerChoiceB;
    }

    public String getAnswerChoiceC() {
        return answerChoiceC;
    }

    public void setAnswerChoiceC(String answerChoiceC) {
        this.answerChoiceC = answerChoiceC;
    }

    public String getAnswerChoiceD() {
        return answerChoiceD;
    }

    public void setAnswerChoiceD(String answerChoiceD) {
        this.answerChoiceD = answerChoiceD;
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

}
