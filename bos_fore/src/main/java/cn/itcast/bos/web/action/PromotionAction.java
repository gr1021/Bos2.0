package cn.itcast.bos.web.action;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;

import com.opensymphony.xwork2.ActionContext;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
@SuppressWarnings("all")
public class PromotionAction extends BaseAction<Promotion> {

	@Action(value = "promotion_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 基于webService获取bos_management系统中的活动列表数据
		PageBean<Promotion> pageBean = WebClient
				.create("http://localhost:9001/bos_management/services/promotionService/pageQuery?page="
						+ page + "&rows=" + rows)
				.accept(MediaType.APPLICATION_JSON).get(PageBean.class);
		//压入值栈：
		ActionContext.getContext().getValueStack().push(pageBean);
		
		return SUCCESS;
	}

}
