package com.example.testcore.data;

import com.example.testcore.models.Standard;

import java.util.ArrayList;
import java.util.HashMap;

interface QuestionByStandardAsyncResponse {
    void processFinished(HashMap<String, String> questionByStandardHash);

}
