package com.tsm.shop;

import com.squareup.square.SquareClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class SiteApplicationTests {

    @MockBean
    private SquareClient squareClient;

    @Test
    void contextLoads() {
    }
}