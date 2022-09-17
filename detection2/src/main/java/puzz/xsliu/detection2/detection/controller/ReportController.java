package puzz.xsliu.detection2.detection.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.service.BridgeService;
import puzz.xsliu.detection2.detection.service.CommonFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/5/6:51 PM
 * @author: lxs
 */
@Controller
@RequestMapping("/common/report")
public class ReportController {

    @Resource
    private BridgeService bridgeService;


    @Resource

    private HttpServletResponse response;

    @Resource
    private CommonFileService commonFileService;

    @GetMapping("/download/{bridgeId}")
    public void downloadReport(@PathVariable Long bridgeId) throws IOException {
        Bridge bridge = bridgeService.find(bridgeId);
        if (bridge == null){
            return;
        }
        File report = new File(bridge.getReportPath());
        commonFileService.send2Front(response, report);
    }
}
