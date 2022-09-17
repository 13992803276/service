package puzz.xsliu.detection2.detection.process;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import puzz.xsliu.detection2.detection.enums.MessageTypeEnum;
import puzz.xsliu.detection2.detection.process.dispatcher.MessageDispatcher;
import puzz.xsliu.detection2.detection.process.messages.DetectResultMessage;
import puzz.xsliu.detection2.detection.process.messages.ExceptionResultMessage;
import puzz.xsliu.detection2.detection.process.messages.QuantifyResultMessage;
import puzz.xsliu.detection2.detection.utils.Constants;
import puzz.xsliu.detection2.detection.utils.SpringContextHolder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 消息监听器,用于监听redis的订阅消息
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/2/1:38 PM
 * @author: lxs
 */
@Component
@Slf4j
public class RedisMessageListener implements MessageListener {
    @Resource
    private MessageDispatcher messageDispatcher;

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        if (Objects.equals(channel, MessageTypeEnum.DETECT.getCode()) ||
            Objects.equals(channel, MessageTypeEnum.QUANTIFY.getCode())
        ){
            // 向通道中发送的, for debug
            log.debug("channel: " + channel);
            log.debug("body: " + body);
        }else{
            if (messageDispatcher == null){
                messageDispatcher = SpringContextHolder.getBean("messageDispatcher");
            }
            messageDispatcher.dispatch(transferTo(channel, body));
        }
    }

    private puzz.xsliu.detection2.detection.process.messages.Message transferTo(String channel, String content){
        MessageTypeEnum messageType = MessageTypeEnum.getByCode(channel);
        if (messageType == null){
            ExceptionResultMessage resultMessage = new ExceptionResultMessage();
            resultMessage.setMessageType(MessageTypeEnum.EXCEPTION.getCode());
            resultMessage.setDesc("消息转型失败");
            return resultMessage;
        }
        switch (messageType){
            case DETECT_RESULT:
                // 那就转换成对应的消息种类
                return JSON.parseObject(content, DetectResultMessage.class);
            case QUANTIFY_RESULT:
                // 转换成量化的结果
                return JSON.parseObject(content, QuantifyResultMessage.class);
            case EXCEPTION:
            default:
                return JSON.parseObject(content, ExceptionResultMessage.class);
        }
    }
}
