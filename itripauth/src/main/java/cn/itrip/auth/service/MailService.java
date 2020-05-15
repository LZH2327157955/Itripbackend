package cn.itrip.auth.service;

/**
 * 邮件发送接口
 * @author hduser
 *
 */
public interface MailService {
	public void sendActivationMail(String mailTo, String activationCode);
}
