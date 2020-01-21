package io.turntabl;

import io.turntabl.services.GSuite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@SpringBootApplication
public class AwsAccountManagementApplication {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		// boolean b = GSuite.removeAWSARN("102924368074115126817", "arn:aws:iam::926377470665:role/gsuite-elasticbeanstalk-fullaccess-role");

		GSuite.fetchAllUserInfo().forEach( (s, user) -> {
			if (user.getName().getFullName().contains("Dawud") || user.getName().getFullName().contains("Osei") || user.getName().getFullName().contains("John")){
				System.out.println(user);
			}
		});

		// allUsers.forEach((x,y)-> System.out.println(x + " -> " + y));
        // SpringApplication.run(AwsAccountManagementApplication.class, args);



	}
}

