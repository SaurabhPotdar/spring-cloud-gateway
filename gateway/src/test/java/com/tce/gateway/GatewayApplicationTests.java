package com.tce.gateway;

import com.amazonaws.auth.BasicAWSCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewayApplicationTests {


	@Autowired
	BasicAWSCredentials basicAWSCredentials;

	@Test
	void contextLoads() {
		System.out.println(basicAWSCredentials.getAWSAccessKeyId());
	}

}
