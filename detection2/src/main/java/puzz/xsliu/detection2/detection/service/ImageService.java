package puzz.xsliu.detection2.detection.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.entity.param.ImageParam;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.enums.MessageTypeEnum;
import puzz.xsliu.detection2.detection.mapper.ImageMapper;
import puzz.xsliu.detection2.detection.process.messages.DetectMessage;
import puzz.xsliu.detection2.detection.process.messages.DetectResultMessage;
import puzz.xsliu.detection2.detection.process.messages.QuantifyMessage;
import puzz.xsliu.detection2.detection.process.sync.ImageSync;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/11:56 AM
 * @author: lxs
 */
@Slf4j
@Service
public class ImageService {
    @Resource
    private SessionService sessionService;
    @Resource
    private RedisService redisService;

    @Resource
    private ImageMapper imageMapper;

    /**
     * 获取桥梁下图像的数量
     * @param bridgeId 桥梁ID
     * @return 桥梁下图像的数量
     */
    public int count4Bridge(Long bridgeId){
        ImageParam param = new ImageParam();
        param.setBridgeId(bridgeId);
        param.setUserId(sessionService.getCurUserId());
        param.setStatus(0);
        return imageMapper.count(param);
    }


    /**
     * 构件下的图像数量
     * @param structId 构件ID
     * @return 构件下的图像数量
     */
    public int count4Struct(Long structId){
        ImageParam param = new ImageParam();
        param.setStructId(structId);
        param.setStatus(0);
        param.setUserId(sessionService.getCurUserId());
        return imageMapper.count(param);
    }

    /**
     *  获取桥梁下的所有图像, 在检测之前获取
     * @param bridgeId 桥梁ID
     * @return 桥梁下的所有图像
     */
    public List<Image> listBridgeImages(Long bridgeId){
        // 获取未检测的桥梁所属图像
        ImageParam param = new ImageParam();
        param.setUserId(sessionService.getCurUserId());
        param.setBridgeId(bridgeId);
        List<Image> images = imageMapper.list(param);
        if (CollectionUtil.isEmpty(images)){
            return new ArrayList<>();
        }
        return images;
    }

    public List<Image> list(ImageParam param){
        return imageMapper.list(param);
    }

    /**
     *  获取所有的单张检测图像
     * @param param 筛选条件
     * @return 符合条件的单张图像
     */
    public List<Image> list4Single(ImageParam param){
        return imageMapper.list(param);
    }


    /**
     * 封装成
     * @param image 待量化的图像
     */
    @Async
    public void detect(Image image){
        if (image != null){
            // 生成检测消息
            DetectMessage message = new DetectMessage();
            message.setMessageType(MessageTypeEnum.DETECT.getCode());
            message.setId(image.getId());
            // 图像的原始路径
            message.setPath(image.getSrcPath());
            // 发送到消息队列当中
            redisService.publish(MessageTypeEnum.DETECT.getCode(), JSON.toJSONString(message));
        }else{
            log.error("待检测的图像信息为空!");
        }

    }

    public Image find(Long imageId){
        return imageMapper.find(imageId);
    }


    public boolean add(Image image){
        return imageMapper.insert(image) == 1;
    }

    @Async
    public void quantify(Image src) {
        if (src != null){
            QuantifyMessage message = new QuantifyMessage();
            message.setMessageType(MessageTypeEnum.QUANTIFY.getCode());
            message.setBoxes(src.getCrackJson());
            message.setPolygons(src.getAreaJson());
            message.setId(src.getId());
            message.setPath(src.getSrcPath());
            // 发送
            redisService.publish(MessageTypeEnum.QUANTIFY.getCode(), JSON.toJSONString(message));
        }else{
            log.error("待量化的图像信息为空!");
        }
    }

    public ImageSync syncFromCache(DetectResultMessage message){
        // 从缓存中对两个检测结果进行同步
        Long id = message.getId();
        String type = message.getType();
        ImageSync result = new ImageSync();
        // 从redis中获取到当前这张图像的同步情况
        String boxes;
        String polygon;
        String redisKey = Constants.PROCESSING_IMAGE_PREFIX
                + BridgeProcessEnum.DETECTING.getCode() + Constants.SP +
                message.getId() + Constants.SP;
        // 如果当前消息是box,则从redis中获取polygon
        redisKey += Objects.equals(type, Constants.BOXES) ? Constants.POLYGON: Constants.BOXES;
        if (redisService.hasKey(redisKey)){
            if (Objects.equals(type, Constants.BOXES)){
                polygon = (String) redisService.get(redisKey);
                boxes = message.getObjects();
            }else{
                polygon = message.getObjects();
                boxes = (String) redisService.get(redisKey);
            }
            result.setDone(true);
            result.setBoxes(boxes);
            result.setPolygon(polygon);
            return result;
        }
        return result;
    }


    public void update(Image image){
        imageMapper.update(image);
    }

    public boolean delete(Long imageId){
        if (imageId == null){
            return false;
        }
        // 删除一张图像
        // 检查图像是否存在
        // 检查图像是否不属于任何桥梁和构件
        Image image = find(imageId);
        if (Objects.equals(image.getUserId(), sessionService.getCurUserId())
            && image.getBridgeId() == 0 && image.getStructId() == 0){
            image.setStatus(1);
            update(image);
            return true;
        }
        return false;
    }


    public int count(ImageParam imageParam){
        return imageMapper.count(imageParam);
    }
}
