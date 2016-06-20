package com.zhangyan.entiy;

import java.util.Date;

public class WorkInfo {
	
	private int id;
	private String name;
	private String url;
	private String school;
	private Date realDate;
	private Date createTime;
	private Date updateTime;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	
	public Date getRealDate() {
		return realDate;
	}
	public void setRealDate(Date realDate) {
		this.realDate = realDate;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Override
	public String toString() {
		return "WorkInfo [id=" + id + ", name=" + name + ", url=" + url
				+ ", school=" + school + ", realDate=" + realDate
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
	
	public WorkInfo(int id, String name, String url, String school,
			Date realDate, Date createTime, Date updateTime) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.school = school;
		this.realDate = realDate;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	public WorkInfo(){
		
	}
	
	
	

}
