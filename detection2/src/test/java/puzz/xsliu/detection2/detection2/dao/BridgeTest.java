package puzz.xsliu.detection2.detection2.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import puzz.xsliu.detection2.detection.Detection2Application;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.service.BridgeService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/1/6:42 PM
 * @author: lxs
 */
@SpringBootTest(classes = Detection2Application.class)
public class BridgeTest {
    @Resource
    private BridgeService bridgeService;
    @Test
    public void testInsert(){
        for(int i = 0; i < 10; i++){
            Bridge bridge = new Bridge();
            bridge.setProcess(BridgeProcessEnum.LOADING.getCode());
            bridge.setUserId(5L);
            bridge.setName("测试桥梁" + (i+ 10));
            bridge.setSpan("跨境组合2" + (i+ 10));
            bridge.setStructure("结构形式" + (i+ 10));
            bridge.setGmtCreate(new Date());
            bridgeService.add(bridge);
        }
    }
}
