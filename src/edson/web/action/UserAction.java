package edson.web.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import edson.web.domain.User;
import edson.web.exceptions.UserException;
import edson.web.service.BusinessService;
import edson.web.utils.FileUtil;

public class UserAction extends ActionSupport implements ModelDriven<User>{
	
	private User user=new User();//模型驱动获取jsp页面传过来的值
	
	public User getModel(){
		return user;
	}
	
	List<User> users;//存放找到的User，在list.jsp中遍历
	private File upload;//获取上传的文件
	private String uploadFileName;//上传的文件名
	private String isUpload;//获取查询条件中“是否上传简历”条件的值
	private InputStream inputStream;//下载文件的输入流
	
	
	
	
	//登录
	public String login() throws UserException{
		
		BusinessService service=new BusinessService();
		try {
			user = service.findUser(user.getLogonName(),user.getLogonPwd());
			if(user!=null){
				ActionContext.getContext().getSession().put("user", user);
				return "login_success";
			}else{
				this.addActionError("用户不存在！");
				return "input";
			}
		} catch (Exception e) {
			//抓住异常并抛出自己产生的异常
			e.printStackTrace();
			throw new UserException("服务器内部问题，请稍后尝试！");
		}
	}
	
	//列出所有用户
	public String list() throws UserException{
		
		BusinessService service=new BusinessService();
		try {
			users = service.findUsers();
			if(users!=null){
				ActionContext.getContext().put("users", users);
			}
			return "list_success";
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
	}
	
	//添加用户
	public String add() throws UserException, IOException{
		BusinessService service=new BusinessService();
		
		//登录名和密码不能为空，且登录名为3-12位，手动进行校验
		if(user.getLogonName()==null || !user.getLogonName().matches("[0-9a-zA-Z]{3,12}")|| user.getLogonPwd()==null ){
			this.addActionMessage("添加失败！登录名和密码不能为空，且登录名为3-12位！");
			return "add_success";
		}
		//校验通过再将数据插入数据库
		
//		上传文件保存：
//		1.产生多级目录，避免文件夹臃肿
//		2.给文件名加上随即名避免重复
		
		fileUpload();
		
		try {
			service.add(user);
			this.addActionMessage("添加成功！");
			return "add_success";
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
	}
	
	public void fileUpload() throws IOException{
		if(upload!=null){
			String dirs=ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload")+FileUtil.mdMoreDirs(uploadFileName);
			File file=new File(dirs);
			if(!file.exists()){
				file.mkdirs();
			}
			
			File destFile=new File(dirs+"/"+FileUtil.getUUID()+"_"+uploadFileName);
			FileUtils.copyFile(upload,destFile );
			
			user.setPath(destFile.getAbsolutePath());
			user.setFilename(uploadFileName);
		
		}
	}

	//查询用户
	public String search() throws UserException{
		BusinessService service=new BusinessService();
		try {
			
			
			users = service.findUser(user.getUserName(),user.getEducation(),user.getSex(),isUpload);
			if(users.size()>0){
				//将找到的用户存到request域中并转发给list.jsp页面
				ActionContext.getContext().put("users", users);
				return "search_success";
			}
			//没有找到，存入一条消息：系统内未有该用户
			this.addActionMessage("系统内未有该用户!");
			return "search_success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UserException(e);
		}
	}

	
	//删除指定用户
	public String delete() throws UserException{
		BusinessService service=new BusinessService();
		try {
			service.delete(user.getUserID());
			//删除用户成功后，跳转到list.jsp，展示出所有用户
			users=service.findUsers();
			ActionContext.getContext().put("users", users);
			return "delete_success";
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
	}
	
	//查看指定用户的详细信息
	public String view() throws UserException{
		BusinessService service=new BusinessService();
		try {
			user=service.findUser(user.getUserID());
			return "view_success";
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw new UserException(e);
		}
		
		
	}
	
	//用户下载文件
	public String downloadFile() throws UserException{
		
		//查询需要被下载文件的用户
		BusinessService service=new BusinessService();
		try {
			user=service.findUser(user.getUserID());
			String path=user.getPath();
			File file=FileUtils.getFile(path);
			inputStream=FileUtils.openInputStream(file);
			uploadFileName=user.getFilename();
			
			return "downloadFile_success";
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
		
	}
	
	
	
	//编辑用户时，用于回显用户的数据
	public String editBack() throws UserException{
		BusinessService service=new BusinessService();
		try {
			user=service.findUser(user.getUserID());
			return "editBack_success";
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
		
	}
	
	//编辑用户后，将修改后的用户信息插入到数据库中（先将已有用户删除再插入修改后的用户数据信息）
	//修改数据中有新文件，应将原来的文件删除，若原来没有文件则文件上传
	public String editData() throws UserException{
		
		BusinessService service=new BusinessService();
		try {
			
			if(upload!=null){//新上传了文件
				User oldUser=service.findUser(user.getUserID());
				if(oldUser.getPath()!=null){//原来用户有文件
					//删除原来用户的文件
					new File(oldUser.getPath()).delete();
				}
				///原来用户没有文件
				service.delete(user.getUserID());
				//上传新文件
				fileUpload();
				service.add(user);
			}else{
				User oldUser=service.findUser(user.getUserID());
				if(oldUser.getPath()!=null){
					user.setPath(oldUser.getPath());//修改没有上传文件，保留原来的文件
					user.setFilename(oldUser.getFilename());
				}
				service.delete(user.getUserID());
				service.add(user);
			}
			this.addActionMessage("修改成功！");
			return "editData_success";
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(e);
		}
		
		
	}
	
	
	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(String isUpload) {
		this.isUpload = isUpload;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	

	
	
	

}
