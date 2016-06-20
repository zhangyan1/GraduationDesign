package com.zhangyan.reptile;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhangyan.entiy.WorkInfo;
import com.zhangyan.service.JobService;
import com.zhangyan.util.HttpClientUtil;
import com.zhangyan.util.Util;

@Component
public class CsustRepitle {
	
	

	@Autowired
	private JobService jobService;
	
	
	
	static Logger log = Logger.getLogger( HnuRepitle.class);	
	private WorkInfo workInfo;
	private String baseUrl = "http://www.cslgzj.com/detail/career?id=";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public void start(){
		String url = "http://www.cslgzj.com/module/getcareers?start=0&count=11&keyword=&type=inner&day=";
		String rs = HttpClientUtil.get(url);
		System.out.println(rs);
		JSONObject json = JSONObject.parseObject(rs);
		JSONArray list = (JSONArray) json.get("data");
		for(int i=0;i<list.size();i++){
			JSONObject rsJson = (JSONObject) list.get(i);
			System.out.println(i+"|"+rsJson.get("company_name")+"|"+ rsJson.get("career_talk_id"));
			String name = rsJson.get("company_name").toString();
			String xUrl = rsJson.get("career_talk_id").toString();
			String time = rsJson.get("meet_day").toString();
			workInfo = Util.Init(name,baseUrl+xUrl,time, "长沙理工大学");
			jobService.save(workInfo);
		}
	}
	public static void main(String args[]){
		HnuRepitle a = new  HnuRepitle();
		a.start();
	}


}
