package com.zhangyan.reptile;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;


@Component
public class StuRepitle  implements PageProcessor{
	
	@Autowired
	private StuPinel stuPinel;
	
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    
    static Logger log = Logger.getLogger( Repitle.class);

    public void process(Page page) {
//       page.putField("信息", page.getHtml().xpath("//td[@class='zlan2']/allText()").all());
//       List<String> url = page.getHtml().css("li a").regex("href=\"\" (.*)\\',").all();
    	 page.putField("信息", page.getHtml().css("div.right-1").css("li a","text").all());
    	 page.putField("url", page.getHtml().css("li a").regex("href=\"\" (.*)\\',").all());
    	 page.putField("日期", page.getHtml().regex("\\d{4}-\\d{2}-\\d{1,2}").all());
//       for(String s:url){
//    	   System.out.println(s);
//    	   page.addTargetRequest("http://stu.hnust.edu.cn/jy/jiuyeIndex.do?method=showZphInfo2&id="+s);
//       }
    }

    public Site getSite() {
        return site;
    }
    
    public void start(){
    	Spider test = Spider.create(new  StuRepitle());
    	Spider.create(new  StuRepitle());
        test.addUrl("http://stu.hnust.edu.cn/jy/jiuyeIndex.do?method=toCategoryZPH&byzd=TYPE16");
        test.addPipeline(stuPinel);
        test.thread(3);
        test.run();
    }
    public static void main(String args[]){
    	Spider test = Spider.create(new  StuRepitle());
        test.addUrl("http://stu.hnust.edu.cn/jy/jiuyeIndex.do?method=toCategoryZPH&byzd=TYPE16");
        test.addPipeline(new ConsolePipeline ());
        test.thread(3);
        test.run();
    }
    
}
