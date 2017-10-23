package esdon.web.dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import edson.web.domain.User;
import edson.web.utils.DatabaseUtil;

public class User_dao {

	public User findUser(String logonName, String logonPwd) throws SQLException {
		
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		String sql="select * from s_user where logonName=? and logonPwd=?";
		Object []params=new Object[]{logonName,logonPwd};
		return (User) qr.query(sql,params, new BeanHandler(User.class));
		
	}

	public List<User> findUsers() throws SQLException {
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		String sql="select * from s_user";
		return (List<User>) qr.query(sql, new BeanListHandler(User.class));
	}

	public void add(User user) throws SQLException {
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		String sql="insert into s_user values(?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[]params=new Object[]{user.getUserID(),user.getUserName(),user.getLogonName(),user.getLogonPwd(),
									user.getSex(),user.getBirthday(),user.getEducation(),user.getTelephone(),
									user.getInterest(),user.getPath(),user.getFilename(),user.getRemark()};
		qr.update(sql, params);
	}

	public List<User> findUser(String userName, String education, String sex,
			String isUpload) throws SQLException {
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		List<Object>params=new ArrayList<Object>();
//		多个判断条件使用字符串拼接方式构成sql语句
		String sql="select * from s_user where 1=1";
		
		if(userName!=null && !userName.trim().equals("")){
			sql+=" and userName like ?";
			params.add("%"+userName+"%");
		}
		
		if(education!=null && !education.trim().equals("")){
			sql+=" and education=?";
			params.add(education);
		}
		
		if(sex!=null && !sex.trim().equals("")){
			sql+=" and sex=?";
			params.add(sex);
		}
		
		if(isUpload!=null && isUpload.trim().equals("")){
			
			if("1".equals(isUpload)){
				sql+=" and path is not null";
			}else{
				sql+=" and path is null";
			}
		}
		
		return  (List<User>) qr.query(sql, params.toArray(), new BeanListHandler(User.class));
		
	}

	public void delete(String userID) throws SQLException {
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		String sql="delete from s_user where userID=?";
		//有上传文件的把文件也同时删除
		User user=findUser(userID);
		if(user.getPath()!=null){
			new File(user.getPath()).delete();
		}
		
		qr.update(sql, userID);
		
		
	}

	public User findUser(String userID) throws SQLException {
		QueryRunner qr=new QueryRunner(DatabaseUtil.getDataSource());
		String sql="select * from s_user where userID=?";
		
		return (User) qr.query(sql, userID, new BeanHandler(User.class));
	}
	
	

}
