package com.cine.demo.email;

import com.cine.demo.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void emailService_isProperlyWiredInContext() {
        assertThatCode(() -> emailService.sendEmail(
                "test@test.com",
                "Integration test subject",
                "Integration test body"
        )).doesNotThrowAnyException();

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
