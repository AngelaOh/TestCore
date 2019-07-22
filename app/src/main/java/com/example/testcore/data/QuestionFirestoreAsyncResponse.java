package com.example.testcore.data;

import com.example.testcore.models.Question;
import com.example.testcore.models.Test;

import java.util.ArrayList;

public interface QuestionFirestoreAsyncResponse {
    void processFinished(ArrayList<Question> firestoreArrayList);
}
