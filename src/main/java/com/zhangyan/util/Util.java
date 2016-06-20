package com.zhangyan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhangyan.entiy.WorkInfo;

public class Util {
	private static WorkInfo workInfo;
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static WorkInfo Init(String name,String url,String time,String schoolName){
		workInfo = new WorkInfo();
		workInfo.setName(name);
    	workInfo.setUrl(url);
    	workInfo.setCreateTime(new Date());
    	workInfo.setUpdateTime(new Date());
    	workInfo.setSchool(schoolName);
    	try {
			Date date = sdf.parse(time.toString());
			workInfo.setRealDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return workInfo;
	}

}
