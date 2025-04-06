package com.example;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.URISyntaxException;

@DisplayName("ApiTest")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ApiTest {

    @BeforeAll
    public static void Setup() throws URISyntaxException {

    }

    @Test
    @Tag("Test1")
    public void Test1(){
        Assertions.assertEquals("11051", "Проверка esflErrorCode:");
    }

    @Test
    @Tag("Test2")
    public void Test2(){
        Assertions.assertEquals("11051", "11051");
    }
}
