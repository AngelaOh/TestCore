package com.example.testcore.data;

import com.example.testcore.models.Standard;
import com.example.testcore.models.Test;

import java.util.ArrayList;

public interface TestFirestoreAsyncResponse {
    void processFinished(ArrayList<Test> firestoreArrayList);

}
