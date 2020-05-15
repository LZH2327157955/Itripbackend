package cn.itrip.service.labeldic;

import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ItripLabelDicServiceImpl implements ItripLabelDicService {

    @Resource
    private ItripLabelDicMapper itripLabelDicMapper;

    public List<ItripLabelDic> getItripLabelDicListByMap(Map<String, Object> param) throws Exception {
        return itripLabelDicMapper.getItripLabelDicListByMap(param);
    }

    /**
     * 根据parentId查询数据字典
     * @param parentId
     */
    public List<ItripLabelDicVO> getItripLabelDicByParentId(Long parentId)throws Exception{
        return itripLabelDicMapper.getItripLabelDicByParentId(parentId);
    }


}