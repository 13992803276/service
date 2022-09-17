package puzz.xsliu.detection2.detection.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import puzz.xsliu.detection2.detection.entity.Damage;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.enums.ImageProcessEnum;
import puzz.xsliu.detection2.detection.process.messages.Message;
import puzz.xsliu.detection2.detection.process.messages.QuantifyResultMessage;
import puzz.xsliu.detection2.detection.service.BridgeService;
import puzz.xsliu.detection2.detection.service.DamageService;
import puzz.xsliu.detection2.detection.service.ImageService;
import puzz.xsliu.detection2.detection.service.RedisService;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import java.util.List;

/**
 * NOTE 注意对于图像来讲,只有三个状态,DETECTING QUANTIFYING FINISHED
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:10 PM
 * @author: lxs
 */
@Slf4j
@Component
public class QuantifyResultMessageHandler implements ResultMessageHandler{
    @Resource
    private ImageService imageService;

    @Resource
    private BridgeService bridgeService;

    @Resource
    private DamageService damageService;

    @Resource
    private RedisService redisService;

    @Override
    public void handle(Message message) {
        boolean b = filter(message);
        if (!b){
            return;
        }
        QuantifyResultMessage qrMessage = (QuantifyResultMessage) message;
        // 创建图像对象,插入到数据库中
        Long imageId = qrMessage.getId();
        // 从数据库中查询得到原图像
        Image image = imageService.find(imageId);
        image.setProcess(ImageProcessEnum.QUANTIFIED.getCode());
        // 结果的路径
        image.setResPath(qrMessage.getPath());
        imageService.update(image);
        List<Damage> damages = qrMessage.getDamages();
        // 批量新增damages
        if (!CollectionUtils.isEmpty(damages)){
            damages.forEach(damage -> {
                damage.setImageId(image.getId());
                damage.setStructId(image.getStructId());
                damage.setBridgeId(image.getBridgeId());
            });
            boolean insert = damageService.batchInsert(damages);
            if (!insert){
                log.error("批量插入损伤数据时出现错误!");
                return;
            }
        }
        // 量化完成后更新桥梁进度
        if (!image.isSingle()){
            bridgeService.onProcess(image.getBridgeId(), BridgeProcessEnum.DETECTING);
        }
    }

    @Override
    public boolean filter(Message message) {
        boolean b = ResultMessageHandler.super.filter(message);
        // 通过redis进行去重
        QuantifyResultMessage qrMessage = (QuantifyResultMessage) message;
        String key = Constants.PROCESSING_IMAGE_PREFIX
                + ImageProcessEnum.QUANTIFIED.getCode() + Constants.SP
                + qrMessage.getId();
        if (redisService.hasKey(key) || !b){
            return false;
        }
        redisService.set(key, Constants.SP, 10 * 60);
        return true;
    }
}
