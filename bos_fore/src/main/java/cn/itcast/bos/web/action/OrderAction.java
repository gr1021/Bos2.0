package cn.itcast.bos.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.service.base.OrderService;
import cn.itcast.crm.domain.Customer;
//前端 系统订单数据处理
@ParentPackage("json-default")
@Controller
@Namespace("/")
@Scope("prototype")
public class OrderAction extends BaseAction<Order> {
	
	private String sendAreaInfo;//发件人信息
	private String recAreaInfo;//收件人信息
	
	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}
	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}
	@Action(value="order_add",results={@Result(name="success",type="redirect",location="index.html")})
	public String add(){
		//手动封装area 发件
		Area sendArea = new Area();
		String[] sendAreaData = sendAreaInfo.split("/");
		sendArea.setProvince(sendAreaData[0]);
		sendArea.setCity(sendAreaData[1]);
		sendArea.setDistrict(sendAreaData[2]);
		
		//手动封装area 收件:
		Area recArea = new Area();
		String[] recAreaData = recAreaInfo.split("/");
		recArea.setProvince(recAreaData[0]);
		recArea.setCity(recAreaData[1]);
		recArea.setDistrict(recAreaData[2]);
		
		model.setSendArea(sendArea);
		model.setRecArea(recArea);
		
		//关联客户：
		Customer customer = (Customer) ServletActionContext.getRequest().getSession().getAttribute("customer");
		model.setCustomer_id(customer.getId());
		
		//调用webService
		WebClient.create(Constants.BOS_MANAGEMENT_URL+"/services/orderService/order").type(MediaType.APPLICATION_JSON).post(model);
		return SUCCESS;
	}
	
	
}
