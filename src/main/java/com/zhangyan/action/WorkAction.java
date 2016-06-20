package com.zhangyan.action;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.zhangyan.common.PageObject;
import com.zhangyan.service.JobService;


@Controller
//@RestController
public class WorkAction {
	@Autowired
	private JobService jobService;
	


	protected final Log log = LogFactory.getLog(getClass());
	
	private PageObject pageObject = new PageObject();;
	
	public PageObject getPageObject() {
		return pageObject;
	}
	public void setPageObject(PageObject pageObject) {
		this.pageObject = pageObject;
	}
	@RequestMapping("/login")
	public String index(){
		return null;
	}
	@RequestMapping("/index")
	public String index(Model model,String test){
		log.info("welcome to the webApp");
		log.info(test);
		jobService.findAll(pageObject);
		log.info(pageObject.getSchoolList());
		model.addAttribute("pageObject", pageObject);
		return "index";
	}

}
