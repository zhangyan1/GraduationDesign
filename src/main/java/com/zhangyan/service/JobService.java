package com.zhangyan.service;

import org.apache.commons.logging.Log;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhangyan.common.PageObject;
import com.zhangyan.dao.JobDao;
import com.zhangyan.entiy.WorkInfo;


@Service
public class JobService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private JobDao jobDao;
	
	public void save(WorkInfo workInfo){
		List rs = jobDao.findByUrl(workInfo.getUrl());
		if(rs.size()>0){
			log.info("已经存在");
			return;
		}
		jobDao.add(workInfo);
	}
	public void findAll(PageObject pageObject){
		jobDao.findAll(pageObject);
	}
	
//	public void delete(){
//		jobDao.delete();
//	}

}
