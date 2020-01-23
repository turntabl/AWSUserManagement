package io.turntabl;

import io.turntabl.services.EMail;
import io.turntabl.services.PermissionStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;

@SpringBootApplication
@EnableSwagger2
public class AwsAccountManagementApplication {

	public static void main(String[] args) {
        SpringApplication.run(AwsAccountManagementApplication.class, args);
	}

	@Bean
	public PermissionStorage permissionStorage(){
		return new PermissionStorage();
	}

	@Bean
	public EMail eMail (){
		return new EMail();
	}

}

