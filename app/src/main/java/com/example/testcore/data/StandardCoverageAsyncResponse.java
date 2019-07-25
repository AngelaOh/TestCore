package com.example.testcore.data;

import com.example.testcore.models.Standard;

import java.util.ArrayList;
import java.util.HashMap;

public interface StandardCoverageAsyncResponse {

    void processFinished(HashMap<String, String> allStandardsHashMap);
}
