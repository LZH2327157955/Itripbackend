package cn.itrip.service.hotelorder;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripOrderLinkUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripListHotelOrderVO;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.hoteltempstore.ItripHotelTempStoreMapper;
import cn.itrip.dao.hotelroom.ItripHotelRoomMapper;
import cn.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import static java.math.BigDecimal.ROUND_DOWN;

@Service
public class ItripHotelOrderServiceImpl implements ItripHotelOrderService {
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;

    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;

    @Resource
    private ItripHotelRoomMapper itripHotelRoomMapper;

    @Resource
    private ItripHotelTempStoreMapper itripHotelTempStoreMapper;


    public BigDecimal getOrderPayAmount(int count, Long roomId) throws Exception {
        BigDecimal payAmount = null;
        BigDecimal roomPrice = itripHotelRoomMapper.getItripHotelRoomById(roomId).getRoomPrice();
        payAmount = BigDecimalUtil.OperationASMD(count, roomPrice,
                BigDecimalUtil.BigDecimalOprations.multiply,
                2, ROUND_DOWN);
        return payAmount;
    }

    public Map<String, String> itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder, List<ItripUserLinkUser> linkUserList) throws Exception {
        //定义变量map，里面存放订单的id和orderNo返回给前端
        Map<String, String> map = new HashMap<String, String>();
        if (null != itripHotelOrder) {
            int flag = 0;
            if (EmptyUtils.isNotEmpty(itripHotelOrder.getId())) {
                //删除联系人
                itripOrderLinkUserMapper.deleteItripOrderLinkUserByOrderId(itripHotelOrder.getId());
                itripHotelOrder.setModifyDate(new Date());
                flag = itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
            } else {
                itripHotelOrder.setCreationDate(new Date());
                flag = itripHotelOrderMapper.insertItripHotelOrder(itripHotelOrder);
            }
            if (flag > 0) {
                Long orderId = itripHotelOrder.getId();
                //添加订单之后还需要往订单与常用联系人关联表中添加记录
                if (orderId > 0) {
                    for (ItripUserLinkUser itripUserLinkUser : linkUserList) {
                        ItripOrderLinkUser itripOrderLinkUser = new ItripOrderLinkUser();
                        itripOrderLinkUser.setOrderId(orderId);
                        itripOrderLinkUser.setLinkUserId(itripUserLinkUser.getId());
                        itripOrderLinkUser.setLinkUserName(itripUserLinkUser.getLinkUserName());
                        itripOrderLinkUser.setCreationDate(new Date());
                        itripOrderLinkUser.setCreatedBy(itripHotelOrder.getCreatedBy());
                        itripOrderLinkUserMapper.insertItripOrderLinkUser(itripOrderLinkUser);
                    }
                }
                map.put("id", itripHotelOrder.getId().toString());
                map.put("orderNo", itripHotelOrder.getOrderNo());
                return map;
            }
        }
        return map;
    }

    public ItripHotelOrder getItripHotelOrderById(Long id) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderById(id);
    }

    public ItripPersonalOrderRoomVO getItripHotelOrderRoomInfoById(Long orderId) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderRoomInfoById(orderId);
    }

    public Page<ItripListHotelOrderVO> queryOrderPageByMap(Map<String, Object> param, Integer pageNo, Integer pageSize) throws Exception {
        Integer total = itripHotelOrderMapper.getOrderCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripListHotelOrderVO> itripHotelOrderList = itripHotelOrderMapper.getOrderListByMap(param);
        page.setRows(itripHotelOrderList);
        return page;
    }

    @Override
    public boolean flushOrderStatus(Integer type) throws Exception {
        Integer flag;
        if (type == 1) {
            flag = itripHotelOrderMapper.flushCancelOrderStatus();
        } else {
            flag = itripHotelOrderMapper.flushSuccessOrderStatus();
        }
        return flag > 0 ? true : false;
    }

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder) throws Exception {
        ItripHotelOrder modifyItripHotelOrder = itripHotelOrderMapper.getItripHotelOrderById(itripHotelOrder.getId());
        //更新临时表的库存
        Map<String, Object> roomStoreMap = new HashMap<String, Object>();
        roomStoreMap.put("startTime", modifyItripHotelOrder.getCheckInDate());
        roomStoreMap.put("endTime", modifyItripHotelOrder.getCheckOutDate());
        roomStoreMap.put("count", modifyItripHotelOrder.getCount());
        roomStoreMap.put("roomId", modifyItripHotelOrder.getRoomId());
        itripHotelTempStoreMapper.updateRoomStore(roomStoreMap);
        return itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
    }

    public List<ItripHotelOrder> getItripHotelOrderListByMap(Map<String, Object> param) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderListByMap(param);
    }
}
