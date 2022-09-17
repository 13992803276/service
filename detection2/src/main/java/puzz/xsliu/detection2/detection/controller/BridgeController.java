package puzz.xsliu.detection2.detection.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.param.BridgeParam;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.service.BridgeService;
import puzz.xsliu.detection2.detection.service.SessionService;
import puzz.xsliu.detection2.detection.utils.CommonUtil;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/11:04 AM
 * @author: lxs
 */
@RestController
@RequestMapping("/common/bridge")
public class BridgeController {
    @Resource
    private BridgeService bridgeService;
    @Resource
    private SessionService sessionService;

    @GetMapping("/list")
    public Object list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "process", required = false) String process){
        // 根据关键字来进行查询
        JSONObject obj = new JSONObject();
        BridgeParam param = new BridgeParam();
        param.setUserId(sessionService.getCurUserId());
        if (!CommonUtil.isBlank(keyword)){
            param.setKey(keyword);
        }
        if (!CommonUtil.isBlank(start)){
            param.setStart(CommonUtil.formatFrom(start));
        }
        if (!CommonUtil.isBlank(process)){
            param.setProcess(process);
        }
        List<Bridge> bridges = bridgeService.list(param);
        obj.put("rows", bridges);
        obj.put("counts", bridges.size());
        return obj;
    }

    @GetMapping("/detail/{id}")
    public Result<Bridge> detail(@PathVariable Long id){
        // 根据桥梁ID获取桥梁详情
        Bridge bridge = bridgeService.detail(id);
        if (bridge == null){
            return Result.failure("无对应桥梁数据");
        }
        return Result.success(bridge);
    }

    @PostMapping("/init")
    public Result<Bridge> init(Bridge bridge){
        // 初始化一座桥梁,需要将桥梁数据插入到MYSQL当中,并将信息添加到redis中用于同步
        return bridgeService.init(bridge);
    }


}
