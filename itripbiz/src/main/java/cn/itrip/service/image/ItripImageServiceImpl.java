package cn.itrip.service.image;

import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.dao.image.ItripImageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ItripImageServiceImpl implements ItripImageService {

    @Resource
    private ItripImageMapper itripImageMapper;

    @Override
    public List<ItripImageVO> getItripImageListByMap(Map<String,Object> param)throws Exception{
        return itripImageMapper.getItripImageListByMap(param);
    }

}
