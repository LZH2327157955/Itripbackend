package cn.itrip.search.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.hotel.SearchHotCityVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.search.beans.vo.hotel.ItripHotelVO;
import cn.itrip.search.service.SearchHotelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(value = "API", basePath = "/http://api.itrap.com/api")
@RequestMapping(value = "/api/hotellist")
public class HotelListController {

    @Resource
    private SearchHotelService searchHotelService;

    /***
     * 搜索酒店分页
     *
     * @param vo
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "查询酒店分页", httpMethod = "POST",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class, notes = "查询酒店分页(用于查询酒店列表)" +
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码: </p>" +
            "<p>20001: 系统异常,获取失败</p>" +
            "<p>20002: 目的地不能为空</p>")
    @RequestMapping(value = "/searchItripHotelPage", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Dto<Page<ItripHotelVO>> searchItripHotelPage(@RequestBody SearchHotelVO vo) {
        Page page = null;
        if (EmptyUtils.isEmpty(vo) || EmptyUtils.isEmpty(vo.getDestination())) {
            return DtoUtil.returnFail("目的地不能为空", "20002");
        }
        try {
            page = searchHotelService.searchItripHotelPage(vo, vo.getPageNo(), vo.getPageSize());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常,获取失败", "20001");
        }
        return DtoUtil.returnDataSuccess(page);
    }

    @ApiOperation(value = "根据热门城市查询酒店", httpMethod = "POST",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class, notes = "根据热门城市查询酒店" +
            "<p>成功：success = ‘true’ | 失败：success = ‘false’ 并返回错误码，如下：</p>" +
            "<p>错误码: </p>" +
            "<p>20003: 系统异常,获取失败</p>" +
            "<p>20004: 城市id不能为空</p>")
    @RequestMapping(value = "/searchItripHotelListByHotCity", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Dto<Page<ItripHotelVO>> searchItripHotelListByHotCity(@RequestBody SearchHotCityVO vo) throws Exception {
        if (EmptyUtils.isEmpty(vo) || EmptyUtils.isEmpty(vo.getCityId())) {
            return DtoUtil.returnFail("城市id不能为空", "20004");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("cityId", vo.getCityId());
        List list = searchHotelService.searchItripHotelListByHotCity(vo.getCityId(), vo.getCount());
        return DtoUtil.returnDataSuccess(list);
    }


}


