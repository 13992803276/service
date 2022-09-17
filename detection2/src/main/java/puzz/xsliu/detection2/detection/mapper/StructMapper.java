package puzz.xsliu.detection2.detection.mapper;

import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.entity.Struct;
import puzz.xsliu.detection2.detection.entity.param.StructParam;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/4:51 PM
 * @author: lxs
 */
@Mapper
public interface StructMapper {
    int insert(Struct struct);
    int update(Struct struct);
    List<Struct> list(StructParam param);
    Struct find(long id);
    int count(StructParam param);
}
