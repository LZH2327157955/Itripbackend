package cn.itrip.auth.controller;

import cn.itrip.common.MD5;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import springfox.documentation.annotations.ApiIgnore;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;

/**
 * 用户管理控制器
 * @author hduser
 *
 */
@Controller
@RequestMapping(value = "/api")
public class UserController {
	@Resource
	private UserService userService;
	
	@ApiIgnore
	@RequestMapping("/register")
	public String showRegisterForm() {
		return "register";
	}
	
	/**
	 * 使用邮箱注册 
	 * @param userVO
	 * @return
	 */
	@ApiOperation(value="使用邮箱注册",httpMethod = "POST",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="使用邮箱注册 ")	
	@RequestMapping(value="/doregister",method=RequestMethod.POST,produces = "application/json")
	public @ResponseBody
	Dto doRegister(
			@ApiParam(name="userVO",value="用户实体",required=true)
			@RequestBody ItripUserVO userVO) {		
		if(!validEmail(userVO.getUserCode()))
			return  DtoUtil.returnFail("请使用正确的邮箱地址注册",ErrorCode.AUTH_ILLEGAL_USERCODE);
		
		try {
			ItripUser user=new ItripUser();
			user.setUserCode(userVO.getUserCode());
			user.setUserPassword(userVO.getUserPassword());
			user.setUserType(0);
			user.setUserName(userVO.getUserName());
//			user.setFlatID(userVO.getFlatID());
//			user.setWeChat(userVO.getWeChat());
//			user.setQQ(userVO.getQQ());
//			user.setWeibo(userVO.getWeibo());
//			user.setBaidu(userVO.getBaidu());
			if (null == userService.findByUsername(user.getUserCode())) {
				user.setUserPassword(MD5.getMd5(user.getUserPassword(), 32));
				userService.itriptxCreateUser(user);				
				return DtoUtil.returnSuccess();							
			}else
			{
				return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);				
			}
		} catch (Exception e) {
			e.printStackTrace();		
			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
		}
		
	}
	
	/**
	 * 使用手机注册
	 * @param userVO
	 * @return
	 */
	@ApiOperation(value="使用手机注册",httpMethod = "POST",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="使用手机注册 ")	
	@RequestMapping(value="/registerbyphone",method=RequestMethod.POST,produces = "application/json")
	public @ResponseBody Dto registerByPhone(
			@ApiParam(name="userVO",value="用户实体",required=true)
			@RequestBody ItripUserVO userVO){
		try {	
			if(!validPhone(userVO.getUserCode()))
				return  DtoUtil.returnFail("请使用正确的手机号注册",ErrorCode.AUTH_ILLEGAL_USERCODE);
			
			ItripUser user=new ItripUser();
			user.setUserCode(userVO.getUserCode());
			user.setUserPassword(userVO.getUserPassword());
			user.setUserType(0);
			user.setUserName(userVO.getUserName());
			if (null == userService.findByUsername(user.getUserCode())) {
				user.setUserPassword(MD5.getMd5(user.getUserPassword(), 32));
				userService.itriptxCreateUserByPhone(user);				
				return DtoUtil.returnSuccess();							
			}else
			{
				return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();			
			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
		}			
	}
	

	/**
	 * 检查用户是否已注册
	 * @param name
	 * @return
	 */
	@ApiOperation(value="用户名验证",httpMethod = "GET",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="验证是否已存在该用户名")	
	@RequestMapping(value="/ckusr",method=RequestMethod.GET,produces= "application/json")
	public @ResponseBody
	Dto checkUser(
			@ApiParam(name="name",value="被检查的用户名",defaultValue="test@bdqn.cn")
			@RequestParam String name) {		
		try {	
		/*	if(!validEmail(name))
				return  DtoUtil.returnFail("请使用正确的邮箱地址注册",ErrorCode.AUTH_ILLEGAL_USERCODE);*/
			if (null == userService.findByUsername(name))
			{			
				return DtoUtil.returnSuccess("用户名可用");
			}
			else
			{
				return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
			}
		} catch (Exception e) {
			e.printStackTrace();			
			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
		}		
	}
	
	@ApiOperation(value="邮箱注册用户激活",httpMethod = "PUT",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="邮箱激活")	
	@RequestMapping(value="/activate",method=RequestMethod.PUT,produces= "application/json")
	public @ResponseBody Dto activate(
			@ApiParam(name="user",value="注册邮箱地址",defaultValue="test@bdqn.cn")
			@RequestParam String user,
			@ApiParam(name="code",value="激活码",defaultValue="018f9a8b2381839ee6f40ab2207c0cfe")
			@RequestParam String code){			
		try {
			if(userService.activate(user, code))
			{	
				return DtoUtil.returnSuccess("激活成功");
			}else{
				return DtoUtil.returnSuccess("激活失败");	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			return DtoUtil.returnFail("激活失败", ErrorCode.AUTH_ACTIVATE_FAILED);
		}		
	} 
	
	@ApiOperation(value="手机注册用户短信验证",httpMethod = "PUT",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="手机注册短信验证")	
	@RequestMapping(value="/validatephone",method=RequestMethod.PUT,produces= "application/json")
	public @ResponseBody Dto validatePhone(
			@ApiParam(name="user",value="手机号码",defaultValue="13811565189")
			@RequestParam String user,
			@ApiParam(name="code",value="验证码",defaultValue="8888")
			@RequestParam String code){			
		try {
			if(userService.validatePhone(user, code))
			{	
				return DtoUtil.returnSuccess("验证成功");
			}else{
				return DtoUtil.returnSuccess("验证失败");	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			return DtoUtil.returnFail("验证失败", ErrorCode.AUTH_ACTIVATE_FAILED);
		}		
	} 
	
	
	/**			 *
	 * 合法E-mail地址：     
	 * 1. 必须包含一个并且只有一个符号“@”    
	 * 2. 第一个字符不得是“@”或者“.”
	 * 3. 不允许出现“@.”或者.@   
	 * 4. 结尾不得是字符“@”或者“.”    
	 * 5. 允许“@”前的字符中出现“＋” 
	 * 6. 不允许“＋”在最前面，或者“＋@” 
	 */
	private boolean validEmail(String email){
		
		String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  ;			
		return Pattern.compile(regex).matcher(email).find();			
	}
	/**
	 * 验证是否合法的手机号
	 * @param phone
	 * @return
	 */
	private boolean validPhone(String phone) {
		String regex="^1[3578]{1}\\d{9}$";
		return Pattern.compile(regex).matcher(phone).find();
	}
}
