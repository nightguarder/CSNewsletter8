package com.cyrils.csnewsletter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestCamundaClientConfig.class)
class CsNewsletterApplicationTests {

	@Test
	void contextLoads() {
	}

}
