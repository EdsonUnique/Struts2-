package edson.web.utils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

import edson.web.domain.User;
/**
 * 拦截器：登录拦截
 * @author Acer
 *
 */
public class MyInterceptor extends MethodFilterInterceptor {

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		
		User user=(User) ActionContext.getContext().getSession().get("user");
		
		if(user!=null){
			return invocation.invoke();
		}
		
		//未登录则跳转到登陆页面
		ActionSupport spt=(ActionSupport) invocation.getAction();//所有的action都继承了actionsupport
		spt.addActionError("请先登录！");
		
		return "input";
		
	}

	

}
