package puzz.xsliu.detection2.detection2.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import puzz.xsliu.detection2.detection.Detection2Application;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.service.BridgeService;

import javax.annotation.Resource;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/5/11:08 AM
 * @author: lxs
 */
@SpringBootTest(classes = Detection2Application.class)
public class ReportGenerateTest {

    @Resource
    private BridgeService bridgeService;
    @Test
    public void testGenerate(){
        bridgeService.generateReport(23L);
    }
}
