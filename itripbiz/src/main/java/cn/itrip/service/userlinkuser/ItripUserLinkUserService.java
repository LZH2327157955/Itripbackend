package cn.itrip.service.userlinkuser;

import cn.itrip.beans.pojo.ItripUserLinkUser;

import java.util.List;
import java.util.Map;

public interface ItripUserLinkUserService {
    public Integer modifyItripUserLinkUser(ItripUserLinkUser itripUserLinkUser)throws Exception;
    public Integer deleteItripUserLinkUserByIds(Long[] ids)throws Exception;
    public Integer addItripUserLinkUser(ItripUserLinkUser itripUserLinkUser)throws Exception;
    public List<ItripUserLinkUser> getItripUserLinkUserListByMap(Map<String, Object> param)throws Exception;



}

