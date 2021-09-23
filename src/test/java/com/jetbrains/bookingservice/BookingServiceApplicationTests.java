package com.jetbrains.bookingservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MySQLContainerExtension.class)
class BookingServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
