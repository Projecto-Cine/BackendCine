package com.cine.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_startsApplicationWithoutException() {
		assertThatCode(() -> DemoApplication.main(new String[]{})).doesNotThrowAnyException();
	}
}
