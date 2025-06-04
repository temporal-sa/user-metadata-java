package com.example.activities;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.lang.reflect.InvocationTargetException;

@ActivityInterface
public interface Tools {

    @ActivityMethod
    void run(JsonNode input) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

}
