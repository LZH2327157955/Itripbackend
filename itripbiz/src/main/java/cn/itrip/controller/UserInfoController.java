package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import cn.itrip.beans.vo.userinfo.ItripModifyUserLinkUserVO;
import cn.itrip.beans.vo.userinfo.ItripSearchUserLinkUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.orderlinkuser.ItripOrderLinkUserService;
import cn.itrip.service.userlinkuser.ItripUserLinkUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@Api(value = "API", basePath = "/http://api.itrap.com/api")
@RequestMapping(value="/api/userinfo")
public class UserInfoController {
    private Logger logger = Logger.getLogger(UserInfoController.class);
    @Resource
    private ItripUserLinkUserService itripUserLinkUserService;
    @Resource
    private ItripOrderLinkUserService itripOrderLinkUserService;
    @Resource
    private ValidationToken validationToken;


    @ApiOperation(value = "修改常用联系人接口", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "修改常用联系人信息"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100421 : 修改常用联系人失败 </p>"+
            "<p>100422 : 不能提交空，请填写常用联系人信息</p>"+
            "<p>100000 : token失效，请重登录 </p>")
    @RequestMapping(value="/modifyuserlinkuser",method= RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Dto<Object> updateUserLinkUser(@RequestBody ItripModifyUserLinkUserVO itripModifyUserLinkUserVO, HttpServletRequest request){
        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if(null != currentUser && null != itripModifyUserLinkUserVO){
            ItripUserLinkUser itripUserLinkUser = new ItripUserLinkUser();
            itripUserLinkUser.setId(itripModifyUserLinkUserVO.getId());
            itripUserLinkUser.setLinkUserName(itripModifyUserLinkUserVO.getLinkUserName());
            itripUserLinkUser.setLinkIdCardType(itripModifyUserLinkUserVO.getLinkIdCardType());
            itripUserLinkUser.setLinkIdCard(itripModifyUserLinkUserVO.getLinkIdCard());
            itripUserLinkUser.setUserId(currentUser.getId());
            itripUserLinkUser.setLinkPhone(itripModifyUserLinkUserVO.getLinkPhone());
            itripUserLinkUser.setModifiedBy(currentUser.getId());
            itripUserLinkUser.setModifyDate(new Date(System.currentTimeMillis()));
            try {
                itripUserLinkUserService.modifyItripUserLinkUser(itripUserLinkUser);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("修改常用联系人失败", "100421");
            }
            return DtoUtil.returnSuccess("修改常用联系人成功");
        }else if(null != currentUser && null == itripModifyUserLinkUserVO){
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息","100422");
        }else{
            return DtoUtil.returnFail("token失效，请重新登录", "100000");
        }
    }




    @ApiOperation(value = "删除常用联系人接口", httpMethod = "GET",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "删除常用联系人信息"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100431 : 所选的常用联系人中有与某条待支付的订单关联的项，无法删除 </p>"+
            "<p>100432 : 删除常用联系人失败 </p>"+
            "<p>100433 : 请选择要删除的常用联系人</p>"+
            "<p>100000 : token失效，请重登录 </p>")
    @RequestMapping(value="/deluserlinkuser",method=RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto<Object> delUserLinkUser(Long[] ids, HttpServletRequest request) {
        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        List<Long> idsList = new ArrayList<Long>();
        if(null != currentUser && EmptyUtils.isNotEmpty(ids)){
            try {
                List<Long> linkUserIds = itripOrderLinkUserService.getItripOrderLinkUserIdsByOrder();
                Collections.addAll(idsList, ids);
                idsList.retainAll(linkUserIds);
                if(idsList.size() > 0)
                {
                    return DtoUtil.returnFail("所选的常用联系人中有与某条待支付的订单关联的项，无法删除","100431");
                }else{
                    itripUserLinkUserService.deleteItripUserLinkUserByIds(ids);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("删除常用联系人失败","100432");
            }
            return DtoUtil.returnSuccess("删除常用联系人成功");
        }else if(null != currentUser && EmptyUtils.isEmpty(ids)){
            return DtoUtil.returnFail("请选择要删除的常用联系人","100433");
        }else{
            return DtoUtil.returnFail("token失效，请重新登录","100000");
        }
    }

    @ApiOperation(value = "新增常用联系人接口", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "新增常用联系人信息"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100411 : 新增常用联系人失败 </p>"+
            "<p>100412 : 不能提交空，请填写常用联系人信息</p>"+
            "<p>100000 : token失效，请重登录 </p>")
    @RequestMapping(value="/adduserlinkuser",method=RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Dto<Object> addUserLinkUser(@RequestBody ItripAddUserLinkUserVO itripAddUserLinkUserVO, HttpServletRequest request){
        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if(null != currentUser && null != itripAddUserLinkUserVO){
            ItripUserLinkUser itripUserLinkUser = new ItripUserLinkUser();
            itripUserLinkUser.setLinkUserName(itripAddUserLinkUserVO.getLinkUserName());
            itripUserLinkUser.setLinkIdCardType(itripAddUserLinkUserVO.getLinkIdCardType());
            itripUserLinkUser.setLinkIdCard(itripAddUserLinkUserVO.getLinkIdCard());
            itripUserLinkUser.setUserId(currentUser.getId());
            itripUserLinkUser.setLinkPhone(itripAddUserLinkUserVO.getLinkPhone());
            itripUserLinkUser.setCreatedBy(currentUser.getId());
            itripUserLinkUser.setCreationDate(new Date(System.currentTimeMillis()));
            try {
                itripUserLinkUserService.addItripUserLinkUser(itripUserLinkUser);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("新增常用联系人失败", "100411");
            }
            return DtoUtil.returnSuccess("新增常用联系人成功");
        }else if(null != currentUser && null == itripAddUserLinkUserVO){
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息","100412");
        }else{
            return DtoUtil.returnFail("token失效，请重新登录", "100000");
        }
    }

    /**
     * 根据UserId,联系人姓名查询常用联系人-add by donghai
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "查询常用联系人接口", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "查询常用联系人信息(可根据联系人姓名进行模糊查询)"+
            "<p>若不根据联系人姓名进行查询，不输入参数即可 | 若根据联系人姓名进行查询，须进行相应的入参，比如：{\"linkUserName\":\"张三\"}</p>" +
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100401 : 获取常用联系人信息失败 </p>"+
            "<p>100000 : token失效，请重登录</p>")
    @RequestMapping(value = "/queryuserlinkuser",method= RequestMethod.POST)
    @ResponseBody
    public Dto<ItripUserLinkUser> queryUserLinkUser(@RequestBody ItripSearchUserLinkUserVO itripSearchUserLinkUserVO, HttpServletRequest request){
        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        List<ItripUserLinkUser> userLinkUserList = new ArrayList<ItripUserLinkUser>();
        String linkUserName = (null == itripSearchUserLinkUserVO)?null:itripSearchUserLinkUserVO.getLinkUserName();
        Dto dto = null;
        if(null != currentUser){
            Map param = new HashMap();
            param.put("userId", currentUser.getId());
            param.put("linkUserName", linkUserName);
            try {
                userLinkUserList = itripUserLinkUserService.getItripUserLinkUserListByMap(param);
                logger.debug("userLinkUserList size " + userLinkUserList.size());
                return DtoUtil.returnSuccess("获取常用联系人信息成功",userLinkUserList);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("获取常用联系人信息失败","100401");
            }
        }else{
            return DtoUtil.returnFail("token失效，请重新登录","100000");
        }
    }

}
