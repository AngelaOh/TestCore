package util;

import android.app.Application;

public class StandardApi extends Application {
    private String username;
    private String userId;
    private String jurisdictionId;
    private String userGrade;
    private String userContent;
    private String standardSetId;

    private static StandardApi instance;
    public StandardApi() {}



    public String getStandardSetId() {
        return standardSetId;
    }

    public void setStandardSetId(String standardSetId) {
        this.standardSetId = standardSetId;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserContent() {
        return userContent;
    }

    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    public static StandardApi getInstance() {
        if (instance == null )
            instance = new StandardApi();

        return instance;
    };

    public String getJurisdictionId() {
        return jurisdictionId;
    }

    public void setJurisdictionId(String jurisdictionId) {
        this.jurisdictionId = jurisdictionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
