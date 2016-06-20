package com.zhangyan.reptile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;






import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhangyan.entiy.WorkInfo;
import com.zhangyan.service.JobService;
import com.zhangyan.util.HttpClientUtil;


@Component
public class CsuRepitle  {
	
	private WorkInfo workInfo;
	
	@Autowired
	private JobService jobService;
	
	
	
	static Logger log = Logger.getLogger( CsuRepitle.class);	
	public void start() {
		  	String url = "http://jobsky.csu.edu.cn/Home/PartialArticleList";
		  	String baseUrl = "http://jobsky.csu.edu.cn";
		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  	List dateList = new ArrayList();
		  	Map<String,String>info  = new HashMap<String,String>();
			info.put("pageindex", "1");
			info.put("pagesize", "15");
			info.put("typeid", "1");
			info.put("followingdates", "-1");
			String rs = HttpClientUtil.post(url, info);
			Document doc = Jsoup.parse(rs);
			Elements content = doc.getElementsByTag("a");
			String  date = doc.text();
			Pattern p=Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{1,2}");
			Matcher m = p.matcher(date);
			while(m.find()){
				Date date1;
				String a = m.group().replaceAll("\\.", "-");
				try {
					date1 = sdf.parse(a);
					dateList.add(date1);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			for(int i=0;i<content.size();i++){
				workInfo = new WorkInfo();
				Element link = content.get(i);
				String rsUrl = link.attr("href");
				String text = link.text();
				workInfo.setName(text);
		    	workInfo.setUrl(baseUrl+rsUrl);
		    	workInfo.setCreateTime(new Date());
		    	workInfo.setUpdateTime(new Date());
		    	workInfo.setSchool("中南大学");
		    	workInfo.setRealDate((Date)dateList.get(i));
				System.out.println(workInfo);
				jobService.save(workInfo);
			}
	}
	
	
	public static void  main(String args[]){
		CsuRepitle aa = new CsuRepitle();
		aa.start();
	}
}


