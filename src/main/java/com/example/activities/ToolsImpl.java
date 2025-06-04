package com.example.activities;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToolsImpl implements Tools {
    com.example.tools.Tools tools = new com.example.tools.Tools();
    public ToolsImpl() {

    }
    @Override
    public void run(JsonNode input) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String toolName = input.get("toolName").asText();
        Method method = com.example.tools.Tools.class.getMethod(toolName, JsonNode.class);
        method.invoke(tools, input);
    }
}
