package com.leknarm.boilerplate;

import com.leknarm.boilerplate.controller.HelloController;
import com.leknarm.boilerplate.model.response.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HelloControllerTests {

    @Spy
    public HelloController helloController;

    @Test
    public void testHelloShouldResponseWithHello() {
        ResponseEntity<BaseResponse<String>> response = helloController.hello();
        assertEquals("hello", Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    public void testHelloWithNameShouldResponseWithHelloWithName() {
        String name = "boilerplate";
        ResponseEntity<BaseResponse<String>> response = helloController.helloWithName(Map.of("name", name));
        assertEquals(String.format("hello: %s", name), Objects.requireNonNull(response.getBody()).getData());
    }

}
