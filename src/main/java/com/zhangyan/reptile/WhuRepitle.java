package com.zhangyan.reptile;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class WhuRepitle implements PageProcessor{
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

	@Override
	public void process(Page page) {
		 page.putField("信息", page.getHtml().regex("<tr>.*</tr>").all());
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
    	Spider test = Spider.create(new  WhuRepitle());
        test.addUrl("http://www.xsjy.whu.edu.cn/type_zplist/00001012309.html");
        test.addPipeline(new ConsolePipeline ());
        test.thread(3);
        test.run();
    }

}
