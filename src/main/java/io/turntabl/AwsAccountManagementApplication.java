package io.turntabl;

import io.turntabl.models.Request;
import io.turntabl.services.EMail;
import io.turntabl.services.GSuite;
import io.turntabl.services.PermissionStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
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



	@Scheduled(fixedDelay = 90000, initialDelay = 300000)
	public void autoRevokePermission(){
		long duration = Long.parseLong(System.getenv("PERMISSION_DURATION_IN_MINUTES"));
		for(Request request : permissionStorage().approvedPermissions()){
			String raw = request.getApprovedTime();
			String time = raw.substring(0, raw.lastIndexOf("."));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
			LocalDateTime now = LocalDateTime.now();

			long until = dateTime.until(now, ChronoUnit.MINUTES);
			if ( until >= duration ){
				String userEmail = request.getUserEmail();
				List<String> strings = Arrays.asList(request.getARN().split(" -,,- "));
				Set<String> awsArns = new HashSet<>(strings);
				GSuite.revokeMultipleAWSARN(userEmail, awsArns);
				permissionStorage().removeRequest(request.getId());
			}
		}
	}

}

