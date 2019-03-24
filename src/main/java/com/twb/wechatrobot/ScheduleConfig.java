package com.twb.wechatrobot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {

	 @Bean
	    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
	        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
	        scheduler.setPoolSize(5);
	        scheduler.setThreadNamePrefix("scheduler-");
	        return scheduler;
	    }
}
