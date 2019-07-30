package com.example.testcore.models;

import java.util.ArrayList;

public class Test {
    private String title;
    private ArrayList<Question> questions;
    private String testId;
    private String timestamp;

    public Test() { }

    public Test(String title, ArrayList<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
