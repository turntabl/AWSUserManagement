package io.turntabl;

import io.turntabl.services.GSuite;
import io.turntabl.utilities.CommonMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class AwsAccountManagementApplication {

	public static void main(String[] args) {
        // SpringApplication.run(AwsAccountManagementApplication.class, args);
		Map<String, String> allUsers = GSuite.fetchAllUsers();
		CommonMethods.MapToUserObject(allUsers);
	}
}

