package com.tigerit.soa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
//public class ActionTrackerApplication  extends SpringBootServletInitializer {
public class ActionTrackerApplication  {

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
//		return builder.sources(ActionTrackerApplication.class);
//	}

	public static void main(String[] args) {
		SpringApplication.run(ActionTrackerApplication.class, args);
	}


}
