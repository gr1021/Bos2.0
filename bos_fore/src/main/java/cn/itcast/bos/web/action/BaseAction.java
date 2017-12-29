package cn.itcast.bos.web.action;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * 
 * @author Administrator
 *抽取Action的公共代码：简化开发
 */
public class BaseAction<T> extends ActionSupport implements ModelDriven<T>{

	protected T model;
	@Override
	public T getModel() {
		return model;
	}
	
	//构造器：
	public BaseAction() {
		//构造子类对象，获取继承父类的泛型
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		//获取类型第一个泛型参数
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Class<T> modelClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		try {
			model = modelClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("模型构造失败");
		} 
	}

	//接收分页参数
	protected int page;
	protected int rows;
	public void setPage(int page) {
		this.page = page;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	//将数据压入值栈：
	protected void  pushPageDataToValueStack(Page<T> pageData){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pageData.getNumberOfElements());
		map.put("rows",pageData.getContent());
		
		ActionContext.getContext().getValueStack().push(map);
	}
	
	
	
}
