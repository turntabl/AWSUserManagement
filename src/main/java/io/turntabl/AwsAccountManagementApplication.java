package io.turntabl;

import io.turntabl.services.EMail;
import io.turntabl.services.GSuite;
import io.turntabl.services.PermissionStorage;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

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

	@Scheduled(fixedDelay = 120000, initialDelay = 600000)
	public void autoRevokePermission(){
		for(Document perm : permissionStorage().approvedPermissions("requests")){
			String time = perm.getString("timestamp");
			LocalDateTime dateTime = LocalDateTime.parse(time);
			LocalDateTime now = LocalDateTime.now();

			long until = dateTime.until(now, ChronoUnit.MINUTES);
			if ( until >= 10 ){
				String userEmail = perm.getString("userEmail");
				Set<String> awsArns = (Set<String>) perm.get("awsArns");
				GSuite.revokeMultipleAWSARN(userEmail, awsArns);
				permissionStorage().removeRequest("requests", perm.getString("_id"));
			}
		}
	}

}

