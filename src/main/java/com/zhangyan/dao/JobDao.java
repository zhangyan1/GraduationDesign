package com.zhangyan.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.zhangyan.common.PageObject;
import com.zhangyan.entiy.WorkInfo;



@Component("jobDao")
public class JobDao {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected JdbcTemplate jobJdbc;
	
	private static final RowMapper<WorkInfo> workInfoMapper = new RowMapper<WorkInfo>() {
	    public WorkInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
	      if (rs == null) {
	        return null;
	      }
	      WorkInfo workInfo = new WorkInfo(rs.getInt("id"),rs.getString("name"),rs.getString("url"),
	    		  rs.getString("school"),rs.getDate("real_date"),rs.getDate("create_time"),rs.getDate("update_time"));
	      return workInfo;
	    }
};
	
	public void find(WorkInfo workInfo){
		System.out.println("111");
		List<WorkInfo> rs =  jobJdbc.query("select * from job_info", workInfoMapper);
		System.out.println(rs);
	}
	public void add(WorkInfo workInfo) {
	    String sql = "insert into job_info(name,url,school,real_date,create_time,update_time) values (?,?,?,?,?,?)";
	    jobJdbc.update(sql, new PreparedStatementSetter() {
	      public void setValues(PreparedStatement ps) throws SQLException {
	        ps.setString(1, workInfo.getName());
	        ps.setString(2, workInfo.getUrl());
	        ps.setString(3, workInfo.getSchool());
	        ps.setTimestamp(4, new Timestamp(workInfo.getRealDate().getTime()));
	        ps.setTimestamp(5, new Timestamp(workInfo.getCreateTime().getTime()));
	        ps.setTimestamp(6, new Timestamp(workInfo.getUpdateTime().getTime()));
	      }
	    });
	}
	
	
	public void findAll(PageObject pageObject){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from job_info a");
		sb.append(" where 1=1 ");
		if(StringUtils.isNotBlank((String)pageObject.getParams().get("date"))){
			sb.append("and TIMESTAMPDIFF(Day,a.real_date,"+"'"+pageObject.getParams().get("date")+"'"+") = 0");
		}
		if(StringUtils.isNotBlank((String) pageObject.getParams().get("school"))){
			sb.append(" and a.school='"+pageObject.getParams().get("school")+"'");
		}
		if(StringUtils.isNotBlank((String)pageObject.getParams().get("name"))){
			sb.append(" and a.name like '%"+pageObject.getParams().get("name") +"%'");
		}
		sb.append(" order by a.id desc");
		sb.append(" limit "+(pageObject.getPageNo()-1)*10+","+pageObject.getPageSize());
		System.out.println(sb.toString());
		List<WorkInfo> rs =  jobJdbc.query(sb.toString(),workInfoMapper);
		pageObject.setTotalRecords(findAll());
		pageObject.setRslist(rs);
		pageObject.setSchoolList(findSchool());
	}
	//这里要改要带参数
	public int findAll(){
		int number = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) from job_info");
		number = jobJdbc.queryForObject(sb.toString(),int.class);
		return number;
		
	}
	public List findByUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from job_info a");
		sb.append(" where 1=1 and a.url=?");
		Object[] args = new Object[]{url};
		List<WorkInfo> rs =  jobJdbc.query(sb.toString(),args,workInfoMapper);
		return rs;
	}
	public List findSchool(){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.name from job_school a");
		sb.append(" where 1=1");
		List rs =  jobJdbc.queryForList(sb.toString(),String.class);
		return rs;
	}
	
	public void delete(){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from job_info ");//这里改为delete
		sb.append("where 1=1 and TIMESTAMPDIFF(Day,real_date,Now())>'5'");
		List rs = jobJdbc.queryForList(sb.toString());
		System.out.println(rs);
	}
}
