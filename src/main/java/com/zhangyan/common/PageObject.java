package com.zhangyan.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageObject {
	private List rslist;
	private int totalRecords;
	private int pageSize = 10;
	private int pageNo = 1;
	private Map<String,Object> params = new HashMap<String,Object>();
	private List schoolList;
	
	
	
	
	public List getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(List schoolList) {
		this.schoolList = schoolList;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public int getTotalPages(){
		return (totalRecords+pageSize-1)/pageSize;
	}
	public int getTopPageNo(){
		return 1;
	}
	public int getPreviousPageNo(){
		if(pageNo<=1){
			return 1;
		}
		return pageNo-1;
	}
	public int getNextPageNo(){
		if(pageNo>=getBottomPageNo()){
			return getBottomPageNo();
		}
		return pageNo+1;
	}
	public int getBottomPageNo(){
		return getTotalPages();
	}
	public List getRslist() {
		return rslist;
	}
	public void setRslist(List rslist) {
		this.rslist = rslist;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public List getPageList(){
		int n = getTotalPages();
		List  pageList = new ArrayList();
		for(int i =1;i<=n;i++){
			pageList.add(i);	
		}
		return pageList;
	}
	@Override
	public String toString() {
		return "PageObject [rslist=" + rslist + ", totalRecords="
				+ totalRecords + ", pageSize=" + pageSize + ", pageNo="
				+ pageNo + ", params=" + params + "]";
	}
	
	
	
	

}
