package puzz.xsliu.detection2.detection.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.param.BridgeParam;
import puzz.xsliu.detection2.detection.model.ExportTrafficGroup;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.service.TrafficService;
import puzz.xsliu.detection2.detection.utils.CommonUtil;

import java.util.List;


@RestController
@RequestMapping("/traffic")
public class TrafficController {

    @Autowired
    TrafficService trafficService;

    @Value("${file.upload.path}")
    private String path;

    @PostMapping("/import")
    public Result<String> importTraffic(String fileName, String rootNo){
        fileName = fileName + path;
        return trafficService.importTraffic(fileName, rootNo);
    }

    @GetMapping("/list")
    public Object list(@RequestParam(value = "rootNo", required = true) String rootNo){
        JSONObject obj = new JSONObject();
        if(!rootNo.isEmpty()){
            List<ExportTrafficGroup> traffics = trafficService.list(rootNo);
            obj.put("rows", traffics);
            obj.put("counts", traffics.size());
        }
        return obj;
    }
}
