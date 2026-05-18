package com.cine.demo.email;

import com.cine.demo.service.EmailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires live SMTP credentials — run manually")
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendEmail_shouldDeliverSuccessfully() {
        emailService.sendEmail(
                "ana.agrodorolo@gmail.com",
                "Test - Spring Mail works",
                "If you're reading this, Spring Mail is configured correctly."
        );
    }
}
