package com.zhangyan.reptile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhangyan.entiy.WorkInfo;
import com.zhangyan.service.JobService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;


@Component("testPinel")
public class TestPinel implements Pipeline {
	
	private WorkInfo workInfo;
	@Autowired
	private JobService jobService;
	

	public void process(ResultItems resultItems, Task task) {
		System.out.println("get page: " + resultItems.getRequest().getUrl());
		List rs = (List) resultItems.getAll().get("信息");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<rs.size();i++){
			workInfo = new WorkInfo();
			String str = (String) rs.get(i);
			str = str.replaceAll("\\s", "");
			String []test =str.split("<");
	    	String b = test[1];
	    	String c = test[0].substring(16);
	    	Pattern p=Pattern.compile("\\d{4}/\\d{2}/\\d{1,2}");
	    	Matcher m=p.matcher(test[0]);
	    	while(m.find())
	        { 
	    		String a = m.group().replaceAll("/", "-");
	    		Date date;
				try {
					date = sdf.parse(a);
					workInfo.setRealDate(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
	        }
	    	workInfo.setName(test[0].substring(11));
	    	workInfo.setUrl(b.replace(">",""));
	    	workInfo.setCreateTime(new Date());
	    	workInfo.setUpdateTime(new Date());
	    	workInfo.setSchool("湖南大学");		
	    	System.out.println(workInfo);
	    	jobService.save(workInfo);
	    	
		}
	}

}
