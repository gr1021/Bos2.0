package cn.itcast.bos.domain.constant;

/**
 * 常量工具类
 * 
 * @author itcast
 *
 */
public class Constants {

	public static final String BOS_MANAGEMENT_HOST = "http://localhost:9001";
	public static final String CRM_MANAGEMENT_HOST = "http://localhost:9002";
	public static final String BOS_FORE_HOST = "http://localhost:9003";
	public static final String BOS_SMS_HOST = "http://localhost:9004";
	public static final String BOS_MAIL_HOST = "http://localhost:9005";

	private static final String BOS_MANAGEMENT_CONTEXT = "/bos_management";
	private static final String CRM_MANAGEMENT_CONTEXT = "/crm_management";
	private static final String BOS_FORE_CONTEXT = "/bos_fore";
	private static final String BOS_SMS_CONTEXT = "/bos_sms";
	private static final String BOS_MAIL_CONTEXT = "/bos_mail";

	public static final String BOS_MANAGEMENT_URL = BOS_MANAGEMENT_HOST + BOS_MANAGEMENT_CONTEXT;
	public static final String CRM_MANAGEMENT_URL = CRM_MANAGEMENT_HOST + CRM_MANAGEMENT_CONTEXT;
	public static final String BOS_FORE_URL = BOS_FORE_HOST + BOS_FORE_CONTEXT;
	public static final String BOS_SMS_URL = BOS_SMS_HOST + BOS_SMS_CONTEXT;
	public static final String BOS_MAIL_URL = BOS_MAIL_HOST + BOS_MAIL_CONTEXT;
}
