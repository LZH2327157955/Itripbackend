package cn.itrip.auth.service;


import cn.itrip.common.MD5;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.itrip.auth.exception.UserLoginFailedException;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.RedisAPI;
import cn.itrip.dao.user.ItripUserMapper;

import java.util.*;

import javax.annotation.Resource;

/**
 * 用户管理接口的实现
 * @author hduser
 *
 */
@Service("useService")
public class UserServiceImpl implements UserService {

	private Logger logger=Logger.getLogger(UserServiceImpl.class);
    @Resource
    private ItripUserMapper itripUserMapper;
    @Resource
    private RedisAPI redisAPI;
    @Resource
	private MailService mailService;
    @Resource
    private SmsService smsService;
	private int expire=1;//过期时间（分钟）
    /**
     * 创建用户
     * @param user
     * @throws Exception 
     */
    public void itriptxCreateUser(ItripUser user) throws Exception { 
    	//发送激活邮件
		String activationCode = MD5.getMd5(new Date().toLocaleString(), 32);
		mailService.sendActivationMail(user.getUserCode(), activationCode);
		//保存用户信息
		itripUserMapper.insertItripUser(user);
    }
    /**
     * 创建手机账号
     */
    @Override
	public void itriptxCreateUserByPhone(ItripUser user) throws Exception {		
		//发送短信验证码
		String code=String.valueOf(MD5.getRandomCode());		
		smsService.send(user.getUserCode(), "1", new String[]{code,String.valueOf(expire)});
		//缓存验证码
		String key="activation:"+user.getUserCode();
		redisAPI.set(key, expire*60, code);	
		//保存用户信息
		itripUserMapper.insertItripUser(user);
	}
    public void updateUser(ItripUser user) throws Exception {
        itripUserMapper.updateItripUser(user);
    }

    public void deleteUser(Long userId) throws Exception {
        itripUserMapper.deleteItripUserById(userId);
    }

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     * @throws Exception 
     */
    public void changePassword(Long userId, String newPassword) throws Exception {
        ItripUser user =itripUserMapper.getItripUserById(userId);
        user.setUserPassword(newPassword);        
        itripUserMapper.updateItripUser(user);
    }

    public ItripUser findOne(Long userId) throws Exception {
        return itripUserMapper.getItripUserById(userId);
    }

    public List<ItripUser> findAll() throws Exception {
        return itripUserMapper.getItripUserListByMap(null);
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     * @throws Exception 
     */
    public ItripUser findByUsername(String username) throws Exception {
        Map<String, Object> param=new HashMap();
        param.put("userCode", username);
		List<ItripUser> list= itripUserMapper.getItripUserListByMap(param);
		if(list.size()>0)
			return list.get(0);
		else
			return null;
    }

	public Set<String> findRoles(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> findPermissions(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItripUser login(String name, String password) throws Exception  {
		// TODO Auto-generated method stub
		ItripUser user=this.findByUsername(name);
		if(null!=user&&user.getUserPassword().equals(password))
		{
			if(user.getActivated()!=1){
				throw new UserLoginFailedException("用户未激活");
			}
			return user;
		}
		else
			return null;
	}

	/**
	 * 激活邮箱用户
	 */
	@Override
	public boolean activate(String email, String code) throws Exception {
		String key="activation:"+email;		
		if(redisAPI.exist(key))
			if(redisAPI.get(key).equals(code)){
				ItripUser user=this.findByUsername(email);
				if(EmptyUtils.isNotEmpty(user))
				{
					logger.debug("激活用户"+email);
					user.setActivated(1);//激活用户
					user.setUserType(0);//自注册用户
					user.setFlatID(user.getId());
					itripUserMapper.updateItripUser(user);
					return true;
				}
			}
				
		return false;
	}
	/**
	 * 短信验证手机号
	 * @throws Exception 
	 */
	@Override
	public boolean validatePhone(String phoneNumber, String code) throws Exception {
		String key="activation:"+phoneNumber;
		if(redisAPI.exist(key))
			if(redisAPI.get(key).equals(code)){
				ItripUser user=this.findByUsername(phoneNumber);
				if(EmptyUtils.isNotEmpty(user))
				{
					logger.debug("用户手机验证已通过："+phoneNumber);
					user.setActivated(1);//激活用户
					user.setUserType(0);//自注册用户
					user.setFlatID(user.getId());
					itripUserMapper.updateItripUser(user);
					return true;
				}
			}
		return false;
	}

	

	
	
   
}
