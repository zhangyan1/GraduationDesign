package com.zhangyan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhangyan.reptile.CsuRepitle;
import com.zhangyan.reptile.CsustRepitle;
import com.zhangyan.reptile.HnuRepitle;
import com.zhangyan.reptile.Repitle;
import com.zhangyan.reptile.StuRepitle;
import com.zhangyan.service.JobService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = GraduationDesignApplication.class)
@WebAppConfiguration
public class GraduationDesignApplicationTests {
	
	
	

//	@Autowired
//	private JobService jobService;
	@Autowired
	private CsustRepitle csuRepitle;
	@Autowired
	private StuRepitle stuRepitle;
	@Autowired
	private Repitle repitle;
//	@Autowired
//	private StuRepitle stuRepitle;
//	@Autowired
//	private HnuRepitle hnuRepitle;
	
	@Test
	public void contextLoads() {
//		csuRepitle.start();
//		repitle.start();
//		hnuRepitle.start();
//		jobService.delete();
		stuRepitle.start();
//		repitle.start();
	}

}
