package puzz.xsliu.detection2.detection.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.process.messages.ExceptionResultMessage;
import puzz.xsliu.detection2.detection.process.messages.Message;
import puzz.xsliu.detection2.detection.service.BridgeService;
import puzz.xsliu.detection2.detection.service.ImageService;

import javax.annotation.Resource;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:12 PM
 * @author: lxs
 */
@Slf4j
@Component
public class ExceptionResultMessageHandler implements ResultMessageHandler{

    @Resource
    private ImageService imageService;

    @Resource
    private BridgeService bridgeService;

    @Override
    public void handle(Message message) {
        log.error("调用外部系统出现异常!异常信息为{}", message.toString());
        try{
            ExceptionResultMessage exMessage = (ExceptionResultMessage) message;
            // 找到错误的桥梁
            Long imageId = exMessage.getId();
            Image image = imageService.find(imageId);
            if (image.isSingle()){
                // 单张图像出现异常,不做处理了
                return;
            }
            Bridge bridge = bridgeService.find(image.getBridgeId());
            // 更新桥梁
            Bridge update = new Bridge();
            update.setId(bridge.getId());
            // 标记一下,在流程中出现了异常情况,导致无法进行
            update.setExp(1);
            bridgeService.update(update);
            log.error("调用外部程序时出错,错误码为:{}\n具体的错误为:{}", exMessage.getCode(), exMessage.getDesc());

        }catch (Exception exception){
            // 如果无法转型
            log.error("调用外部系统时出错");
        }
    }

    @Override
    public boolean filter(Message message) {
        return ResultMessageHandler.super.filter(message);
    }
}
