package cn.itrip.auth.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import cn.itrip.common.MD5;
import cn.itrip.common.UserAgentUtil;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.itrip.auth.exception.TokenValidationFailedException;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.RedisAPI;

import com.alibaba.fastjson.JSON;

import cz.mallat.uasparser.UserAgentInfo;

/**
 * Token管理接口相关业务服务实现
 * 
 * @author hduser
 *
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

	private Logger logger = Logger.getLogger(TokenServiceImpl.class);

	/**
	 * 调用RedisAPI
	 */
	@Resource
	private RedisAPI redisAPI;
	private int expire = SESSION_TIMEOUT;// 2h
	private String tokenPrefix = "token:";//统一加入 token前缀标识

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	/***
	 * @param agent Http头中的user-agent信息
	 * @param user 用户信息
	 * @return Token格式<br/>
	 * 	PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]” 
	 *  <br/>
	 *  Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
	 */
	@Override
	public String generateToken(String agent, ItripUser user) {
		// TODO Auto-generated method stub
		try {
			UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(
					agent);
			StringBuilder sb = new StringBuilder();
			sb.append(tokenPrefix);//统一前缀
			if (userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)) {
				if (UserAgentUtil.CheckAgent(agent)) {
					sb.append("MOBILE-");
				} else {
					sb.append("PC-");
				}
			} else if (userAgentInfo.getDeviceType()
					.equals("Personal computer")) {
				sb.append("PC-");
			} else
				sb.append("MOBILE-");
//			sb.append(user.getUserCode() + "-");
			sb.append(MD5.getMd5(user.getUserCode(),32) + "-");//加密用户名称
			sb.append(user.getId() + "-");
			sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
					+ "-");
			sb.append(MD5.getMd5(agent, 6));// 识别客户端的简化实现——6位MD5码
			
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void save(String token, ItripUser user) {
		if (token.startsWith(tokenPrefix+"PC-"))
			redisAPI.set(token, expire, JSON.toJSONString(user));
		else
			redisAPI.set(token, JSON.toJSONString(user));// 手机认证信息永不失效
	}

	@Override
	public ItripUser load(String token) {		
		return JSON.parseObject(redisAPI.get(token), ItripUser.class);
	}

	@Override
	public void delete(String token) {
		if (redisAPI.exist(token))
			redisAPI.delete(token);
	}

	private boolean exists(String token) {
		return redisAPI.exist(token);
	}

	@Override
	public String replaceToken(String agent, String token)
			throws TokenValidationFailedException {

		// 验证旧token是否有效
		if (!exists(token)) {// token不存在
			throw new TokenValidationFailedException("未知的token或 token已过期");// 终止置换
		}
		Date TokenGenTime;// token生成时间
		try {
			String[] tokenDetails = token.split("-");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			TokenGenTime = formatter.parse(tokenDetails[3]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new TokenValidationFailedException("token格式错误:" + token);
		}

		long passed = Calendar.getInstance().getTimeInMillis()
				- TokenGenTime.getTime();// token已产生时间
		if (passed < REPLACEMENT_PROTECTION_TIMEOUT * 1000) {// 置换保护期内
			throw new TokenValidationFailedException("token处于置换保护期内，剩余"
					+ (REPLACEMENT_PROTECTION_TIMEOUT * 1000 - passed) / 1000
					+ "(s),禁止置换");
		}
		// 置换token
		String newToken = "";
		ItripUser user = this.load(token);
		long ttl = redisAPI.ttl(token);// token有效期（剩余秒数 ）
		if (ttl > 0 || ttl == -1) {// 兼容手机与PC端的token在有效期
			newToken = this.generateToken(agent, user);
			this.save(newToken, user);// 缓存新token
			redisAPI.set(token, this.REPLACEMENT_DELAY,
					JSON.toJSONString(user));// 2分钟后旧token过期，注意手机端由永久有效变为2分钟（REPLACEMENT_DELAY默认值）后失效
		} else {// 其它未考虑情况，不予置换
			throw new TokenValidationFailedException("当前token的过期时间异常,禁止置换");
		}
		return newToken;
	}

	@Override
	public boolean validate(String agent, String token) {
		if (!exists(token)) {// token不存在
			return false;
		}
		try {
			Date TokenGenTime;// token生成时间
			String agentMD5;
			String[] tokenDetails = token.split("-");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			TokenGenTime = formatter.parse(tokenDetails[3]);			
			long passed = Calendar.getInstance().getTimeInMillis()
					- TokenGenTime.getTime();
			if(passed>this.SESSION_TIMEOUT*1000)
					return false;
			agentMD5 = tokenDetails[4];
			if(MD5.getMd5(agent, 6).equals(agentMD5))
				return true;
		} catch (ParseException e) {
			return false;
		}
		return false;
	}

}
