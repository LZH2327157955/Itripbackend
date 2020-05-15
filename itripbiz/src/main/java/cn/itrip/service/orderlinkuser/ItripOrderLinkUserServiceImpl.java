package cn.itrip.service.orderlinkuser;

import cn.itrip.beans.vo.order.ItripOrderLinkUserVo;
import cn.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("ItripOrderLinkUserService")
public class ItripOrderLinkUserServiceImpl implements ItripOrderLinkUserService {

    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;

    public List<ItripOrderLinkUserVo> getItripOrderLinkUserListByMap(Map<String,Object> param)throws Exception{
        return itripOrderLinkUserMapper.getItripOrderLinkUserListByMap(param);
    }
    public List<Long> getItripOrderLinkUserIdsByOrder() throws Exception{
        return itripOrderLinkUserMapper.getItripOrderLinkUserIdsByOrder();
    }


}
