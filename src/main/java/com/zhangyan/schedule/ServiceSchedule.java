package com.zhangyan.schedule;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhangyan.reptile.CsustRepitle;
import com.zhangyan.service.JobService;







@Component
@Configurable
@EnableScheduling
public class ServiceSchedule {
	
	@Autowired
	private JobService jobService;
	@Autowired
	private CsustRepitle csuRepitle;
	
	private Logger log = Logger.getLogger(ServiceSchedule.class);
	
	
	@Scheduled(fixedRateString = "${rejectUser.time}")
	public void Start() {
		log.info("Schedule start" + new Date());
//		csuRepitle.start();
	}
	
	@Scheduled(fixedRateString = "${rejectUser.time}")
	public void delete(){
//		jobService.delete();	
	}

}
