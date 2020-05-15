package cn.itrip.service.image;

import cn.itrip.beans.vo.ItripImageVO;

import java.util.List;
import java.util.Map;

public interface ItripImageService {
    public List<ItripImageVO> getItripImageListByMap(Map<String, Object> param)throws Exception;


}
