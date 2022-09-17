package puzz.xsliu.detection2.detection.service;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.mapper.BridgeMapper;
import puzz.xsliu.detection2.detection.mapper.TrafficMapper;
import puzz.xsliu.detection2.detection.model.ExportTrafficGroup;
import puzz.xsliu.detection2.detection.model.Traffic;
import puzz.xsliu.detection2.detection.repository.TrafficRepository;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.utils.ExcelUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficService {

    @Autowired
    TrafficRepository trafficRepository;

    @Resource
    private TrafficMapper trafficMapper;
    public Result<String> importTraffic(String filename, String rootNo) {
        List<Object[]> trafficObjectsList = ExcelUtil.importExcel(filename);
        List<Traffic> trafficList = new ArrayList<>();
        for (Object[] objects : trafficObjectsList) {
            if(objects.length > 0){
                Traffic traffic = Traffic.builder().rootNo(rootNo)
                        .year(objects[0].toString())
                        .xxhc(Long.parseLong(objects[1].toString()))
                        .zxhc(Long.parseLong(objects[2].toString()))
                        .dxhc(Long.parseLong(objects[3].toString()))
                        .tdhc(Long.parseLong(objects[4].toString()))
                        .jzxc(Long.parseLong(objects[5].toString()))
                        .zxkc(Long.parseLong(objects[6].toString()))
                        .dkc(Long.parseLong(objects[7].toString()))
                        .build();
                trafficList.add(traffic);
            }
        }
        if (trafficList.size() > 0) {
            trafficRepository.saveAll(trafficList);
            return Result.success("success");
        } else {
            return Result.failure("导入失败");
        }
    }

    public List<ExportTrafficGroup> list(String rootNo) {
        List<ExportTrafficGroup> traffics = trafficMapper.listExport(rootNo);
        if (CollectionUtil.isEmpty(traffics)) {
            return new ArrayList<>();
        }
//        traffics.forEach(trafficGroup -> {
//            if("交通量".equals(trafficGroup.getType())){
//                trafficGroup.setTotal(trafficGroup.getDkc() +trafficGroup.getDxhc());
//            }else{
//                trafficGroup.setTotal(trafficGroup.getDkc() +trafficGroup.getDxhc());
//            }
//        });
        return traffics;
    }
}
