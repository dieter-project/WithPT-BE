package com.sideproject.withpt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.config.location="
	+ "classpath:/application.yml,"
	+ "classpath:/application-oauth.yml,"
	+ "classpath:/application-swagger.yml")

class WithPtApplicationTests {

	@Test
	void contextLoads() {
	}

}
