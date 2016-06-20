package com.zhangyan.reptile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhangyan.entiy.WorkInfo;


import com.zhangyan.service.JobService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;



@Component
public class StuPinel implements Pipeline {
	
	private WorkInfo workInfo;
	@Autowired
	private JobService jobService;
	
	
	public void process(ResultItems resultItems, Task task) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List rs = (List) resultItems.getAll().get("信息");
		List rs1 = (List) resultItems.getAll().get("日期");
		List rs2 = (List) resultItems.getAll().get("url");
		for(int i=0;i<rs.size();i++){
			workInfo = new WorkInfo();
			workInfo.setName((String)rs.get(i));
			workInfo.setUrl("http://stu.hnust.edu.cn/jy/jiuyeIndex.do?method=showZphInfo2&id="+rs2.get(i));
			Date date;
			try {
				date = sdf.parse((String) rs1.get(i));
				workInfo.setRealDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			workInfo.setCreateTime(new Date());
	    	workInfo.setUpdateTime(new Date());
	    	workInfo.setSchool("湖南科技大学");
	    	System.out.println(workInfo);
	    	jobService.save(workInfo);
		}
	}

}
