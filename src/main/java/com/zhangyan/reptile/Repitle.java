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
public class Repitle implements PageProcessor{
	@Autowired
	private TestPinel testPinel;
	
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    
    static Logger log = Logger.getLogger( Repitle.class);

    public void process(Page page) {
      page.putField("信息", page.getHtml().xpath("//div[@class='r_list1']/ul/li/tidyText()").all());
      List<String> url = page.getHtml().css("div.page").links().regex(".*p.currentPage=[2-5].*").all();
//      page.addTargetRequests(url);
    }

    public Site getSite() {
        return site;
    }
    public void start(){
    	Spider test = Spider.create(new  Repitle());
    	Spider.create(new  Repitle());
        test.addUrl("http://scc.hnu.edu.cn/newsjob!getMore.action?Lb=1&p.currentPage=1");
        test.addPipeline(testPinel);
        test.thread(3);
        test.run();
    }

    public static void main(String[] args) {
    	Spider test = Spider.create(new  Repitle());
        test.addUrl("http://scc.hnu.edu.cn/newsjob!getMore.action?Lb=1&p.currentPage=1");
        test.addPipeline(new TestPinel ());
        test.thread(3);
        test.run();
    }

}
