package cn.itcast.bos.web.action;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.utils.MailUtils;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Controller
@Namespace("/")
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {

	// 注入jmsTemplate:
	@Autowired
	private JmsTemplate jmsTemplate;

	// 发送短信验证码：
	@Action(value = "customer_sendSms")
	public String sendSms() throws Exception {
		// 生成短信验证码：手机号保存在customer对象中
		String randomNumeric = RandomStringUtils.randomNumeric(4);
		// 将短信验证码保存到session：
		ServletActionContext.getRequest().getSession()
				.setAttribute(model.getTelephone(), randomNumeric);
		System.out.println("生成手机验证码为：" + randomNumeric);
		// 编辑短信内容：
		final String msg = "尊敬的用户您好，本次获取的验证码为：" + randomNumeric
				+ ",服务电话：4006184000";
		// 调用MSM服务发送短信：
		// String result = SmsUtils.sendSmsByHTTP(model.getTelephone(), msg);
		// 调用mq服务发送一条信息：
		jmsTemplate.send("bos_sms", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", model.getTelephone());
				mapMessage.setString("msg", msg);
				return mapMessage;
			}
		});
		String result = "000/xxxx";
		if (result.startsWith("000")) {
			// 发送成功：
			return NONE;
		} else {
			// 发送失败：
			throw new RuntimeException("短信发送失败，信息码：" + result);
		}
	}

	// 属性驱动：验证码
	private String checkCode;

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	// 属性驱动：
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 用户注册的方法:
	@Action(value = "customer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String regist() {
		// 先验证手机验证码是否存在或者正确：
		// 获取验证码：从session中获取：
		String checkcodeSession = (String) ServletActionContext.getRequest()
				.getSession().getAttribute(model.getTelephone());
		// 判断：
		if (checkcodeSession == null || !checkcodeSession.equals(checkCode)) {
			// 验证码不正确：
			System.out.println("短信验证码错误.....");
			return INPUT;
		} else {
			// 调用webService 连接crm 完成数据的保存：
			WebClient
					.create("http://localhost:9002/crm_management/services/customerService/customer")
					.accept(MediaType.APPLICATION_JSON).post(model);
			System.out.println("客户注册成功。。。。。");
			// 发送一份激活邮件：
			// 生成激活码：
			String activeCode = RandomStringUtils.randomNumeric(32);
			// 将激活码保存到Redis中：
			redisTemplate.opsForValue().set(model.getTelephone(), activeCode,
					24, TimeUnit.HOURS);
			// 调用mailUtils发送激活邮件：
			String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='"
					+ MailUtils.activeUrl
					+ "?telephone="
					+ model.getTelephone()
					+ "&activeCode="
					+ activeCode
					+ "'>速运快递邮箱绑定地址</a>";
			MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());

			return SUCCESS;
		}
	}

	// 属性驱动注入：
	private String activeCode;

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	// 激活邮件操作：接收客户手机号码 和 激活码
	@Action(value = "customer_activeMail")
	public String activeMail() throws Exception {
		System.out.println("进入邮箱绑定程序");
		// 设置中文乱码问题：
		ServletActionContext.getResponse().setContentType(
				"text/html;charset=UTF-8");
		// 判断激活码是否有效，如果激活码无效，提示用户
		// 获取激活码：
		String activeCodeRedis = redisTemplate.opsForValue().get(
				model.getTelephone());
		if (activeCodeRedis == null || !activeCodeRedis.equals(activeCode)) {
			// 激活码无效，提示用户
			ServletActionContext.getResponse().getWriter()
					.println("激活码无效，请重新登录，到邮箱激活！");
		} else {
			// 从数据库中查询customer是否注册过：
			Customer customer = WebClient
					.create("http://localhost:9002/crm_management/services/customerService/customer/telephone/"
							+ model.getTelephone())
					.accept(MediaType.APPLICATION_JSON).get(Customer.class);
			// 如果激活码有效 ，判断是否在重复绑定，T_CUSTOMER 表 type 字段 为 1，绑定
			if (customer.getType() == null || customer.getType() != 1) {
				System.out.println("没有绑定");
				// 没有绑定：如果用户没有绑定过邮箱，完成绑定
				WebClient
						.create("http://localhost:9002/crm_management/services/customerService/customer/updateType/"
								+ model.getTelephone())
						.accept(MediaType.APPLICATION_JSON).get();
				ServletActionContext.getResponse().getWriter()
						.println("邮箱绑定成功！");
			} else {
				// 已经绑定过：
				ServletActionContext.getResponse().getWriter()
						.println("该手机号码已经绑定过邮箱！");
			}
			// 删除redis中的激活码：
			redisTemplate.delete(model.getTelephone());
		}
		return NONE;
	}

	// 用户登录的方法：
	@Action(value = "customer_login", results = {
			@Result(name = "login", type = "redirect", location = "login.html"),
			@Result(name = "success", type = "redirect", location = "index.html#/myhome") })
	public String login() {
		System.out.println(model.getTelephone());
		System.out.println(model.getPassword());
		Customer customer = WebClient
				.create(Constants.CRM_MANAGEMENT_URL
						+ "/services/customerService/customer/login?telephone="
						+ model.getTelephone() + "&password="
						+ model.getPassword())
				.accept(MediaType.APPLICATION_JSON).get(Customer.class);
		//判断
		if (customer==null) {
			//登录失败
			return LOGIN;
		}else{
			//登录成功：
			ServletActionContext.getRequest().getSession().setAttribute("customer", customer);
			return SUCCESS;
		}
	}
}
