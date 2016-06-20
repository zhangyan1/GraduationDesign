package com.zhangyan.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhangyan.common.PageObject;
import com.zhangyan.service.JobService;



@Controller
public class PageFindAction {
	
	
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
	
	@RequestMapping("/find")
	public String find(Model model,String pageNo,String date,String school,String name){
		log.info(pageNo);
		if(pageNo!=null){
			int n = Integer.parseInt(pageNo);
			pageObject.setPageNo(n);
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("date", date);
		params.put("school", school);
		params.put("name", name);
		pageObject.setParams(params);
		jobService.findAll(pageObject);
		model.addAttribute("pageObject", pageObject); 
		return "index";
	}
	
	public String findBy(){
		return null;
	}

}
