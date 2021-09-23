package com.jetbrains.bookingservice;

import com.jetbrains.test.MySQLContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MySQLContainerExtension.class)
class BookingServiceIntegrationTest {
    //TODO: Work in progress
}
