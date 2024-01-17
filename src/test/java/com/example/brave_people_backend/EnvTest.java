package com.example.brave_people_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnvTest {

    @Value("${spring.datasource.password}")
    private String dbpw;

    @Test
    public void envTest() {
        System.out.println("dbpw = " + dbpw);
    }
}
