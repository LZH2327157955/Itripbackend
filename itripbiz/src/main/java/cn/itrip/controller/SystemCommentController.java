package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.comment.ItripAddCommentVO;
import cn.itrip.beans.vo.comment.ItripHotelDescVO;
import cn.itrip.beans.vo.comment.ItripScoreCommentVO;
import cn.itrip.beans.vo.comment.ItripSearchCommentVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.Page;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.comment.ItripCommentService;
import cn.itrip.service.hotel.ItripHotelService;
import cn.itrip.service.image.ItripImageService;
import cn.itrip.service.labeldic.ItripLabelDicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import cn.itrip.common.SystemConfig;

@Controller
@Api(value = "API", basePath = "/http://api.itrap.com/api")
@RequestMapping(value="/api/comment")
public class SystemCommentController {
    private Logger logger = Logger.getLogger(SystemCommentController.class);

    @Resource
    private ItripCommentService itripCommentService;
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ValidationToken validationToken;
    @Resource
    private SystemConfig systemConfig;
    @Resource
    private ItripLabelDicService itripLabelDicService;
    @Resource
    private ItripHotelService itripHotelService;



    @ApiOperation(value = "据酒店id查询酒店平均分", httpMethod = "GET",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class, notes = "总体评分、位置评分、设施评分、服务评分、卫生评分" +
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>"+"<p>错误码：</p>"+
            "<p>100001 : 获取评分失败 </p>"+
            "<p>100002 : hotelId不能为空</p>")
    @RequestMapping(value = "/gethotelscore/{hotelId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<Object> getHotelScore(@ApiParam(required = true, name = "hotelId", value = "酒店ID")
                                     @PathVariable String hotelId) {
        Dto<Object> dto = new Dto<Object>();
        logger.debug("getHotelScore hotelId : " + hotelId);
        if (null != hotelId && !"".equals(hotelId)) {
            ItripScoreCommentVO itripScoreCommentVO = new ItripScoreCommentVO();
            try {
                itripScoreCommentVO = itripCommentService.getAvgAndTotalScore(Long.valueOf(hotelId));
                dto = DtoUtil.returnSuccess("获取评分成功", itripScoreCommentVO);
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("获取评分失败", "100001");
            }
        } else {
            dto = DtoUtil.returnFail("hotelId不能为空", "100002");
        }
        return dto;
    }

    @ApiOperation(value = "根据酒店id查询各类评论数量", httpMethod = "GET",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "根据酒店id查询评论数量（全部评论、值得推荐、有待改善、有图片）"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100014 : 获取酒店总评论数失败 </p>"+
            "<p>100015 : 获取酒店有图片评论数失败</p>"+
            "<p>100016 : 获取酒店有待改善评论数失败</p>"+
            "<p>100017 : 获取酒店值得推荐评论数失败</p>"+
            "<p>100018 : 参数hotelId为空</p>")
    @RequestMapping(value = "/getcount/{hotelId}",method=RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto<Object> getCommentCountByType(@ApiParam(required = true, name = "hotelId", value = "酒店ID")
                                             @PathVariable String hotelId){
        Dto<Object> dto = new Dto<Object>();
        logger.debug("hotelId ================= " + hotelId);
        Integer count = 0;
        Map<String,Integer> countMap = new HashMap<String,Integer>();
        Map<String,Object> param = new HashMap<String,Object>();
        if(null != hotelId && !"".equals(hotelId)){
            param.put("hotelId",hotelId);
            count = getItripCommentCountByMap(param);
            if(count != -1){
                countMap.put("allcomment",count);
            }else{
                return DtoUtil.returnFail("获取酒店总评论数失败","100014");
            }
            param.put("isOk",1);//0：有待改善 1：值得推荐
            count = getItripCommentCountByMap(param);
            if(count != -1){
                countMap.put("isok",count);
            }else{
                return DtoUtil.returnFail("获取酒店值得推荐评论数失败","100017");
            }
            param.put("isOk",0);//0：有待改善 1：值得推荐
            count = getItripCommentCountByMap(param);
            if(count != -1){
                countMap.put("improve",count);
            }else{
                return DtoUtil.returnFail("获取酒店有待改善评论数失败","100016");
            }
            param.put("isHavingImg",1);//0:无图片 1:有图片
            param.put("isOk",null);
            count = getItripCommentCountByMap(param);
            if(count != -1){
                countMap.put("havingimg",count);
            }else{
                return DtoUtil.returnFail("获取酒店有图片评论数失败","100015");
            }

        }else{
            return DtoUtil.returnFail("参数hotelId为空","100018");
        }
        dto = DtoUtil.returnSuccess("获取酒店各类评论数成功",countMap);
        return dto;
    }

    public Integer getItripCommentCountByMap(Map<String,Object> param){
        Integer count  = -1;
        try {
            count =  itripCommentService.getItripCommentCountByMap(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @ApiOperation(value = "根据评论类型查询评论列表，并分页显示", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "根据评论类型查询评论列表，并分页显示"+
            "<p>参数数据e.g：</p>" +
            "<p>全部评论：{\"hotelId\":10,\"isHavingImg\":-1,\"isOk\":-1,\"pageSize\":5,\"pageNo\":1}</p>" +
            "<p>有图片：{\"hotelId\":10,\"isHavingImg\":1,\"isOk\":-1,\"pageSize\":5,\"pageNo\":1}</p>" +
            "<p>值得推荐：{\"hotelId\":10,\"isHavingImg\":-1,\"isOk\":1,\"pageSize\":5,\"pageNo\":1}</p>" +
            "<p>有待改善：{\"hotelId\":10,\"isHavingImg\":-1,\"isOk\":0,\"pageSize\":5,\"pageNo\":1}</p>" +
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100020 : 获取评论列表错误 </p>")
    @RequestMapping(value = "/getcommentlist",method=RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Dto<Object> getCommentList(@RequestBody ItripSearchCommentVO itripSearchCommentVO){
        Dto<Object> dto = new Dto<Object>();
        Map<String,Object> param=new HashMap<>();
        logger.debug("hotelId : " + itripSearchCommentVO.getHotelId());
        logger.debug("isHavingImg : " + itripSearchCommentVO.getIsHavingImg());
        logger.debug("isOk : " + itripSearchCommentVO.getIsOk());
        if(itripSearchCommentVO.getIsOk() == -1){
            itripSearchCommentVO.setIsOk(null);
        }
        if(itripSearchCommentVO.getIsHavingImg() == -1){
            itripSearchCommentVO.setIsHavingImg(null);
        }
        param.put("hotelId",itripSearchCommentVO.getHotelId());
        param.put("isHavingImg",itripSearchCommentVO.getIsHavingImg());
        param.put("isOk",itripSearchCommentVO.getIsOk());
        try{
            Page page = itripCommentService.queryItripCommentPageByMap(param,
                    itripSearchCommentVO.getPageNo(),
                    itripSearchCommentVO.getPageSize());
            dto = DtoUtil.returnDataSuccess(page);
        }catch (Exception e){
            e.printStackTrace();
            dto = DtoUtil.returnFail("获取评论列表错误","100020");
        }

        return dto;
    }

    @ApiOperation(value = "根据targetId查询评论照片(type=2)", httpMethod = "GET",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "总体评分、位置评分、设施评分、服务评分、卫生评分"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100012 : 获取评论图片失败 </p>"+
            "<p>100013 : 评论id不能为空</p>")
    @RequestMapping(value = "/getimg/{targetId}",method=RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto<Object> getImgByTargetId(@ApiParam(required = true, name = "targetId", value = "评论ID")
                                        @PathVariable String targetId){
        Dto<Object> dto = new Dto<Object>();
        logger.debug("getImgBytargetId targetId : " + targetId);
        if(null != targetId && !"".equals(targetId)){
            List<ItripImageVO> itripImageVOList = null;
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("type","2");
            param.put("targetId",targetId);
            try {
                itripImageVOList =  itripImageService.getItripImageListByMap(param);
                dto = DtoUtil.returnSuccess("获取评论图片成功",itripImageVOList);
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("获取评论图片失败","100012");
            }

        }else{
            dto = DtoUtil.returnFail("评论id不能为空","100013");
        }
        return dto;
    }

    @ApiOperation(value = "新增评论接口", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "新增评论信息"+
            "<p style=‘color:red’>注意：若有评论图片，需要传图片路径</p>"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100003 : 新增评论失败 </p>"+
            "<p>100004 : 不能提交空，请填写评论信息</p>"+
            "<p>100005 : 新增评论，订单ID不能为空</p>"+
            "<p>100000 : token失效，请重登录 </p>")
    @RequestMapping(value = "/add",method=RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Dto<Object> addComment(@RequestBody ItripAddCommentVO itripAddCommentVO, HttpServletRequest request){
        //ItripComment
        Dto<Object> dto = new Dto<Object>();
        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if(null != currentUser && null != itripAddCommentVO){
            //新增评论，订单id不能为空
            if(itripAddCommentVO.getOrderId() == null
                    || itripAddCommentVO.getOrderId() == 0 ){
                return DtoUtil.returnFail("新增评论，订单ID不能为空","100005");

            }
            List<ItripImage> itripImages = null;
            ItripComment itripComment = new ItripComment();
            itripComment.setContent(itripAddCommentVO.getContent());
            itripComment.setHotelId(itripAddCommentVO.getHotelId());
            itripComment.setIsHavingImg(itripAddCommentVO.getIsHavingImg());
            itripComment.setPositionScore(itripAddCommentVO.getPositionScore());
            itripComment.setFacilitiesScore(itripAddCommentVO.getFacilitiesScore());
            itripComment.setHygieneScore(itripAddCommentVO.getHygieneScore());
            itripComment.setOrderId(itripAddCommentVO.getOrderId());
            itripComment.setServiceScore(itripAddCommentVO.getServiceScore());
            itripComment.setProductId(itripAddCommentVO.getProductId());
            itripComment.setProductType(itripAddCommentVO.getProductType());
            itripComment.setIsOk(itripAddCommentVO.getIsOk());
            itripComment.setTripMode(itripAddCommentVO.getTripMode());
            itripComment.setCreatedBy(currentUser.getId());
            itripComment.setCreationDate(new Date(System.currentTimeMillis()));
            itripComment.setUserId(currentUser.getId());
            try {
                if(itripAddCommentVO.getIsHavingImg() == 1 ){
                    itripImages = new ArrayList<ItripImage>();
                    //loop input imgs array
                    int i = 1;
                    for (ItripImage itripImage : itripAddCommentVO.getItripImages()) {
                        itripImage.setPosition(i);
                        itripImage.setCreatedBy(currentUser.getId());
                        itripImage.setCreationDate(itripComment.getCreationDate());
                        itripImage.setType("2");
                        itripImages.add(itripImage);
                        i++;
                    }
                }

                itripCommentService.itriptxAddItripComment(itripComment,(null == itripImages?new ArrayList<ItripImage>():itripImages));

                dto = DtoUtil.returnSuccess("新增评论成功");
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("新增评论失败","100003");
            }
        }else if(null != currentUser && null == itripAddCommentVO){
            dto = DtoUtil.returnFail("不能提交空，请填写评论信息","100004");
        }else{
            dto = DtoUtil.returnFail("token失效，请重登录","100000");
        }
        return dto;
    }

    @RequestMapping("/upload")
    @ResponseBody
    @ApiOperation(value = "图片上传接口", httpMethod = "POST",
            protocols = "HTTP",produces = "multipart/form-data",
            response = Dto.class, notes = "上传评论图片，最多支持4张图片同时上传，格式为：jpg、jpeg、png，大小不超过5M"+
            "<p style=‘color:red’>注意：input file 中的name不可重复 e:g : file1 、 file2 、 fileN</p>"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100006 : 文件上传失败 </p>"+
            "<p>100007 : 上传的文件数不正确，必须是大于1小于等于4 </p>" +
            "<p>100008 : 请求的内容不是上传文件的类型 </p>" +
            "<p>100009 : 文件大小超限 </p>"
    )
    public Dto<Object> uploadPic(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
        Dto<Object> dto = new Dto<Object>();
        List<String> dataList = new ArrayList<String>();
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传,即多部分请求
        if(multipartResolver.isMultipart(request)){
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            int fileCount = 0;
            try{
                fileCount = multiRequest.getFileMap().size();
            }catch (Exception e) {
                // TODO: handle exception
                fileCount = 0;
                return DtoUtil.returnFail("文件大小超限","100009");
            }
            logger.debug("user upload files count: " + fileCount);

            if(fileCount > 0 && fileCount <= 4 ){
                String tokenString  = multiRequest.getHeader("token");
                logger.debug("token name is from header : " + tokenString);
                ItripUser itripUser = validationToken.getCurrentUser(tokenString);
                if(null != itripUser){
                    logger.debug("user not null and id is : " + itripUser.getId());
                    //取得request中的所有文件名
                    Iterator<String> iter = multiRequest.getFileNames();
                    while(iter.hasNext()){
                        try{
                            //取得上传文件
                            MultipartFile file = multiRequest.getFile(iter.next());
                            if(file != null){
                                //取得当前上传文件的文件名称
                                String myFileName = file.getOriginalFilename();
                                //如果名称不为“”,说明该文件存在，否则说明该文件不存在
                                if(myFileName.trim() !=""
                                        &&
                                        (
                                                myFileName.toLowerCase().contains(".jpg")
                                                        || myFileName.toLowerCase().contains(".jpeg")
                                                        || myFileName.toLowerCase().contains(".png")
                                        ) ){
                                    //重命名上传后的文件名
                                    //命名规则：用户id+当前时间+随机数
                                    String suffixString = myFileName.substring(file.getOriginalFilename().indexOf("."));
                                    String fileName = itripUser.getId()+ "-" + System.currentTimeMillis() + "-" + ((int)(Math.random()*10000000)) + suffixString;
                                    //定义上传路径
                                    String path = systemConfig.getFileUploadPathString() + File.separator +fileName;
                                    logger.debug("uploadFile path : " + path);
                                    File localFile = new File(path);
                                    file.transferTo(localFile);
                                    dataList.add(systemConfig.getVisitImgUrlString()+fileName);
                                }
                            }
                        }catch (Exception e) {
                            continue;
                        }
                    }
                    dto = DtoUtil.returnSuccess("文件上传成功",dataList);
                }else{
                    dto = DtoUtil.returnFail("文件上传失败","100006");
                }
            }else{
                dto = DtoUtil.returnFail("上传的文件数不正确，必须是大于1小于等于4","100007");
            }
        }else{
            dto = DtoUtil.returnFail("请求的内容不是上传文件的类型","100008");
        }
        return dto;
    }

    @RequestMapping(value = "/delpic",produces="application/json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "图片删除接口", httpMethod = "POST",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class, notes = "删除传递图片名称"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100010 : 文件不存在，删除失败 </p>"+
            "<p>100000 : token失效，请重登录 </p>")
    public Dto<Object> delPic(@RequestParam String imgName,HttpServletRequest request) throws IllegalStateException, IOException {
/*	public Dto<Object> delPic(
    		@RequestBody InputDto inputDto) throws IllegalStateException, IOException {*/

        String tokenString  = request.getHeader("token");
        logger.debug("token name is from header : " + tokenString);
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        Dto<Object> dto = new Dto<Object>();
        if(null != currentUser){
            //获取物理路径
            /*		String path = systemConfig.getFileUploadPathString() + File.separator + inputDto.getParamString();*/
            String path = systemConfig.getFileUploadPathString() + File.separator + imgName;
            logger.debug("delete file path : " + path);
            File file = new File(path);
            if(file.exists()){
                file.delete();
                dto = DtoUtil.returnSuccess("删除成功");
            }else{
                dto = DtoUtil.returnFail("文件不存在，删除失败","100010");
            }
        }else{
            dto = DtoUtil.returnFail("token失效，请重登录","100000");
        }
        return dto;
    }


    @ApiOperation(value = "查询出游类型列表", httpMethod = "GET",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "查询出游类型列表"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100019 : 获取旅游类型列表错误 </p>")
    @RequestMapping(value = "/gettraveltype",method=RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto<Object> getTravelType(){
        Dto<Object> dto = new Dto<Object>();
        Long parentId = 107L;
        List<ItripLabelDicVO> itripLabelDicVOList = new ArrayList<ItripLabelDicVO>();
        try {
            itripLabelDicVOList =  itripLabelDicService.getItripLabelDicByParentId(parentId);
            dto = DtoUtil.returnSuccess("获取旅游类型列表成功",itripLabelDicVOList);
        } catch (Exception e) {
            e.printStackTrace();
            dto =  DtoUtil.returnFail("获取旅游类型列表错误","100019");
        }
        return dto;
    }

    @ApiOperation(value = "获取酒店相关信息（酒店名称、酒店星级）", httpMethod = "GET",
            protocols = "HTTP",produces = "application/json",
            response = Dto.class,notes = "新增评论信息页面内获取酒店相关信息（酒店名称、酒店星级）"+
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码：</p>"+
            "<p>100021 : 获取酒店相关信息错误 </p>")
    @RequestMapping(value = "/gethoteldesc/{hotelId}",method=RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto<Object> getHotelDesc(@ApiParam(required = true, name = "hotelId", value = "酒店ID")
                                    @PathVariable String hotelId){
        Dto<Object> dto = new Dto<Object>();
        logger.debug("hotelId : " + hotelId);
        ItripHotelDescVO itripHotelDescVO = null;
        try{
            if(null != hotelId && !"".equals(hotelId)){
                ItripHotel itripHotel = new ItripHotel();
                itripHotel = itripHotelService.getItripHotelById(Long.valueOf(hotelId));
                itripHotelDescVO = new ItripHotelDescVO();
                itripHotelDescVO.setHotelId(itripHotel.getId());
                itripHotelDescVO.setHotelName(itripHotel.getHotelName());
                itripHotelDescVO.setHotelLevel(itripHotel.getHotelLevel());
            }
            dto = DtoUtil.returnDataSuccess(itripHotelDescVO);
        }catch (Exception e){
            e.printStackTrace();
            dto = DtoUtil.returnFail("获取酒店相关信息错误","100021");
        }

        return dto;
    }


}
