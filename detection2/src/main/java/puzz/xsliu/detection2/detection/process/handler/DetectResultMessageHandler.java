package puzz.xsliu.detection2.detection.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.enums.ImageProcessEnum;
import puzz.xsliu.detection2.detection.process.messages.DetectResultMessage;
import puzz.xsliu.detection2.detection.process.messages.Message;
import puzz.xsliu.detection2.detection.process.sync.ImageSync;
import puzz.xsliu.detection2.detection.service.ImageService;
import puzz.xsliu.detection2.detection.service.RedisService;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:07 PM
 * @author: lxs
 */
@Slf4j
@Component
public class DetectResultMessageHandler implements ResultMessageHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private ImageService imageService;

    @Override
    public void handle(Message message) {
        boolean f = filter(message);
        if (!f) {
            return;
        }
        DetectResultMessage dtMessage = (DetectResultMessage) message;
        ImageSync result = imageService.syncFromCache(dtMessage);
        if (result.isDone()) {
            // 说明已经收集到两个检测结果了
            // 找到原图像
            Image image = imageService.find(dtMessage.getId());
            // 添加检测结果
            image.setAreaJson(result.getPolygon());
            image.setCrackJson(result.getBoxes());
            image.setProcess(ImageProcessEnum.DETECTED.getCode());
            imageService.update(image);
            // 检测完成后不再更新桥梁进度,
            imageService.quantify(image);
        }
    }

    @Override
    public boolean filter(Message message) {
        boolean f = ResultMessageHandler.super.filter(message);
        // 获取key,检测结果需要对type进行区分
        DetectResultMessage dtMessage = (DetectResultMessage) message;
        String key = Constants.PROCESSING_IMAGE_PREFIX
                + BridgeProcessEnum.DETECTING.getCode() + Constants.SP +
                dtMessage.getId() + Constants.SP + dtMessage.getType();
        if (redisService.hasKey(key) || !f) {
            // 消息重复
            return false;
        }
        // 在缓存中添加key,设置过期时间为10分钟,同时满足去重和同步的需求
        redisService.set(key, dtMessage.getObjects(), 10 * 60);
        return true;
    }


}
