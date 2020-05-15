package cn.itrip.auth.service;

import javax.annotation.Resource;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import cn.itrip.common.RedisAPI;
/**
 * 邮件发送接口的实现
 * @author hduser
 *
 */
@Service("mailService")
public class MailServiceImpl implements MailService {

	@Resource
	private MailSender mailSender;
	@Resource
	private RedisAPI redisAPI;
	@Resource
	private SimpleMailMessage activationMailMessage;
	/**
	 * 发送注册激活邮件
	 */
	public void sendActivationMail(String mailTo, String activationCode) {
		activationMailMessage.setTo(mailTo);		
		activationMailMessage.setText("注册邮箱："+mailTo +"  激活码："+activationCode);
		mailSender.send(activationMailMessage);
		this.saveActivationInfo("activation:"+mailTo, activationCode);
	}

	/**
	 * 保存激活信息
	 * 
	 * @param key
	 * @param value
	 */
	private void saveActivationInfo(String key, String value) {
		redisAPI.set(key, 30*60, value);
	}
}
