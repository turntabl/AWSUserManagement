package io.turntabl;

import io.turntabl.services.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class AwsAccountManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsAccountManagementApplication.class, args);
	}

}
