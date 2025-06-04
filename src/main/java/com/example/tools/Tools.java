package com.example.tools;

import com.fasterxml.jackson.databind.JsonNode;

public class Tools {
    public void sayHello(JsonNode input) {
        String name = input.get("name").asText();
        System.out.println("Hello, " + name);
    }

    public void sayGoodbye(JsonNode input) {
        String name = input.get("name").asText();
        System.out.println("Goodbye, " + name);
    }
}
