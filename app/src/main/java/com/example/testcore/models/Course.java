package com.example.testcore.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Course {
    private String courseName;
    private ArrayList<Test> tests;
    private ArrayList<Standard> standards;

    public Course(String courseName, ArrayList<Standard> standards, ArrayList<Test> tests) {
        this.courseName = courseName;
        this.standards = standards;
        this.tests = tests;
    }

    public ArrayList<Test> getTests() {
        return tests;
    }

    public void setTests(ArrayList<Test> tests) {
        this.tests = tests;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<Standard> getStandards() {
        return standards;
    }

    public void setStandards(ArrayList<Standard> standards) {
        this.standards = standards;
    }
}
