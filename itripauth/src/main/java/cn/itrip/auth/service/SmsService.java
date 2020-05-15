package cn.itrip.auth.service;

/**
 * 短信发送接口 
 * @author hduser
 *
 */
public interface SmsService {

	/**
	 * 发送短信
	 * @param to 是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号
	 * @param templateId 是模板ID，在【云通讯】平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1
	 * @param datas 替换模板"您的验证码是{1}，请于{2}分钟内正确输入"中的内容
	 * @throws Exception 
	 */
	public void send(String to, String templateId, String[] datas) throws Exception;
}
