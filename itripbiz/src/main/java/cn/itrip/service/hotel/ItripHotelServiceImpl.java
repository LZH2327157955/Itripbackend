package cn.itrip.service.hotel;


import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.dao.hotel.ItripHotelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItripHotelServiceImpl implements ItripHotelService {

    @Resource
    private ItripHotelMapper itripHotelMapper;

    @Override
    public ItripHotel getItripHotelById(Long id)throws Exception{
        return itripHotelMapper.getItripHotelById(id);
    }

    public HotelVideoDescVO getVideoDescByHotelId(Long id)throws Exception{
        HotelVideoDescVO hotelVideoDescVO = new HotelVideoDescVO();
        List<ItripAreaDic> itripAreaDicList = new ArrayList<>();
        itripAreaDicList = itripHotelMapper.getHotelAreaByHotelId(id);
        List<String> tempList1 = new ArrayList<>();
        for (ItripAreaDic itripAreaDic:itripAreaDicList) {
            tempList1.add(itripAreaDic.getName());
        }
        hotelVideoDescVO.setTradingAreaNameList(tempList1);

        List<ItripLabelDic> itripLabelDicList = new ArrayList<>();
        itripLabelDicList = itripHotelMapper.getHotelFeatureByHotelId(id);
        List<String> tempList2 = new ArrayList<>();
        for (ItripLabelDic itripLabelDic:itripLabelDicList) {
            tempList2.add(itripLabelDic.getName());
        }
        hotelVideoDescVO.setHotelFeatureList(tempList2);

        hotelVideoDescVO.setHotelName(itripHotelMapper.getItripHotelById(id).getHotelName());
        return hotelVideoDescVO;
    }


    @Override
    public ItripSearchFacilitiesHotelVO getItripHotelFacilitiesById(Long id) throws Exception {
        return itripHotelMapper.getItripHotelFacilitiesById(id);
    }

    @Override
    public ItripSearchPolicyHotelVO queryHotelPolicy(Long id)throws Exception{
        return itripHotelMapper.queryHotelPolicy(id);
    }

    @Override
    public List<ItripSearchDetailsHotelVO> queryHotelDetails(Long id) throws Exception {
        List<ItripLabelDic> itripLabelDicList = new ArrayList<>();
        itripLabelDicList = itripHotelMapper.getHotelFeatureByHotelId(id);
        ItripSearchDetailsHotelVO vo = new ItripSearchDetailsHotelVO();
        List<ItripSearchDetailsHotelVO> list = new ArrayList<ItripSearchDetailsHotelVO>();
        vo.setName("酒店介绍");
        vo.setDescription(itripHotelMapper.getItripHotelById(id).getDetails());
        list.add(vo);
        for (ItripLabelDic itripLabelDic:itripLabelDicList) {
            ItripSearchDetailsHotelVO vo2 = new ItripSearchDetailsHotelVO();
            vo2.setName(itripLabelDic.getName());
            vo2.setDescription(itripLabelDic.getDescription());
            list.add(vo2);
        }
        return list;
    }



}