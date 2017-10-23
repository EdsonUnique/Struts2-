package edson.web.service;

import java.sql.SQLException;
import java.util.List;

import edson.web.domain.User;
import esdon.web.dao.User_dao;

public class BusinessService {

	User_dao dao=new User_dao();
	
	public User findUser(String logonName, String logonPwd) throws SQLException {
		
		return dao.findUser(logonName,logonPwd);
	}

	public List<User> findUsers() throws SQLException {
		
		return dao.findUsers();
	}

	public void add(User user) throws SQLException {
		dao.add(user);
		
	}

	public List<User> findUser(String userName, String education, String sex,
			String isUpload) throws SQLException {
		return	dao.findUser(userName,education,sex,isUpload);
		
	}

	public void delete(String userID) throws SQLException {
		dao.delete(userID);
		
	}

	public User findUser(String userID) throws SQLException {
		return dao.findUser(userID);
	}

}
