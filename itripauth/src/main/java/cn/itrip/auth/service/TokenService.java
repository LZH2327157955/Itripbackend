package cn.itrip.auth.service;

import cn.itrip.auth.exception.TokenValidationFailedException;
import cn.itrip.beans.pojo.ItripUser;
/**
 * Token管理接口
 * @author hduser
 *
 */
public interface TokenService {

	/**
	 * 会话超时时间
	 */
	public final static int SESSION_TIMEOUT=2*60*60;//默认2h
	/**
	 * 置换保护时间
	 */
	public final static int REPLACEMENT_PROTECTION_TIMEOUT=60*60;//默认1h
	/**
	 * 旧token延迟过期时间 
	 */
	public final static int REPLACEMENT_DELAY=2*60;//默认2min
	
	/**
	 * 生成token
	 * @param agent Http头中的user-agent信息
	 * @param user 用户信息
	 * @return Token格式<br/>
	 * 		PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]” 
	 *  	<BR/>
	 *  	Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
	 */
	public String generateToken(String agent, ItripUser user);
	/**
	 * 保存用户信息至redis
	 * @param tokenID
	 * @param user
	 */
	public void save(String token, ItripUser user);
	/**
	 * 从redis获取用户信息
	 * @param tokenID
	 * @return
	 */
	public ItripUser load(String token);
	/**
	 * 删除token
	 * @param tokenID
	 */
	public void delete(String token);
	
	/**
	 * 置换Token <BR/>
	 *  1、首先要判断token是否有效 	<BR/>
	 *  2、生成token后的1个小时内不允许置换 	<BR/>
	 *  3、置换token时，需要生成新token，并且旧token不能立即失效，应设置为置换后的时间延长2分钟 <BR/>
	 *  4、兼容手机端和PC端 <BR/>
	 * @param agent	User-Agent
	 * @param token	旧的token
	 * @return 新的token
	 * @throws TokenValidationFailedException 
	 */	
	public String replaceToken(String agent, String token) throws TokenValidationFailedException;
	
	/**
	 * 验证token是否有效
	 * @param agent
	 * @param token
	 * @return
	 */
	public boolean validate(String agent, String token);
}
