package com.example.testcore.data;

import com.example.testcore.models.Standard;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Standard> standardArrayList);
}
