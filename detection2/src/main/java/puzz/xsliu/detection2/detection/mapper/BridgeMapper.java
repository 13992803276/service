package puzz.xsliu.detection2.detection.mapper;

import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.param.BridgeParam;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/4:41 PM
 * @author: lxs
 */
@Mapper
public interface BridgeMapper {
    /**
     *  插入一条桥梁信息
     */
    int insert(Bridge bridge);

    /**
     *  修改一座桥梁的信息，按照ID为条件查找
     */
    int update(Bridge bridge);

    /**
     *  按照条件查询
     */
    List<Bridge> list(BridgeParam param);

    /**
     *  根据ID获取到某一个
     */
    Bridge find(long id);

    int count(BridgeParam param);
}
