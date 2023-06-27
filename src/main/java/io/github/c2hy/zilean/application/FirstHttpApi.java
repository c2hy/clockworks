package io.github.c2hy.zilean.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstHttpApi {
    @GetMapping("hello-text")
    public TextJson respondHelloText() {
        var textJson = new TextJson();
        textJson.setText("Hello World");
        return textJson;
    }
}
