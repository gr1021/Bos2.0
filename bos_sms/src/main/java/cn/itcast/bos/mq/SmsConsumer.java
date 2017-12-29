package cn.itcast.bos.mq;


import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.stereotype.Service;

@Service("smsConsumer")
public class SmsConsumer implements MessageListener {

	@Override
	public void onMessage(Message message) {
		MapMessage mapMessage = (MapMessage) message;
		// 调用sms发送短信：
		try {
			// String result =
			// SmsUtils.sendSmsByHTTP(mapMessage.getString("telephone"),
			// mapMessage.getString("msg"));
			String result = "000/xxxx";
			if (result.startsWith("000")) {
				// 验证码正确：
				System.out.println("短信发送成功，手机号："
						+ mapMessage.getString("telephone") + "验证码："
						+ mapMessage.getString("msg"));
			} else {
				// 验证码错误：
				throw new RuntimeException("短信发送失败，信息码：" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
