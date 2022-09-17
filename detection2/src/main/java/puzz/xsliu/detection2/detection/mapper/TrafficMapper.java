package puzz.xsliu.detection2.detection.mapper;


import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.model.ExportTrafficGroup;
import puzz.xsliu.detection2.detection.model.Traffic;

import java.util.List;

@Mapper
public interface TrafficMapper {

    /**
     *  按照条件查询
     */
    List<Traffic> list(String rootNo);

    /**
     *  按照条件查询
     */
    List<ExportTrafficGroup> listExport(String rootNo);

}
