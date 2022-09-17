package puzz.xsliu.detection2.detection2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import puzz.xsliu.detection2.detection.Detection2Application;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.service.ImageService;

import javax.annotation.Resource;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/2/3:15 PM
 * @author: lxs
 */
@SpringBootTest(classes = Detection2Application.class)
public class RedisTest {

    @Resource
    private ImageService imageService;

    @Test
    public void testSend(){
        Image image = imageService.find(14L);
        imageService.detect(image);
    }

}
