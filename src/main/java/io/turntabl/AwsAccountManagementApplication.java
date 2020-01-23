package io.turntabl;

import io.turntabl.service.PermissionStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AwsAccountManagementApplication {

	public static void main(String[] args) {
        SpringApplication.run(AwsAccountManagementApplication.class, args);
	}
}

