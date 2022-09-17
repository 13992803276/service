package puzz.xsliu.detection2.detection.process.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import puzz.xsliu.detection2.detection.process.handler.DetectResultMessageHandler;
import puzz.xsliu.detection2.detection.process.handler.ExceptionResultMessageHandler;
import puzz.xsliu.detection2.detection.process.handler.QuantifyResultMessageHandler;
import puzz.xsliu.detection2.detection.process.handler.ResultMessageHandler;
import puzz.xsliu.detection2.detection.process.messages.DetectResultMessage;
import puzz.xsliu.detection2.detection.process.messages.Message;
import puzz.xsliu.detection2.detection.process.messages.QuantifyResultMessage;

import javax.annotation.Resource;

/**
 * 消息分发,分发给指定的处理器进行处理
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:03 PM
 * @author: lxs
 */
@Component
@Slf4j
public class MessageDispatcher {

    @Resource
    private DetectResultMessageHandler detectResultMessageHandler;
    @Resource
    private QuantifyResultMessageHandler quantifyResultMessageHandler;
    @Resource
    private ExceptionResultMessageHandler exceptionResultMessageHandler;

    public void dispatch(Message message){
        log.debug(message.toString());
        ResultMessageHandler handler = getMatchedHandler(message);
        if (handler == null){
            log.error("未找到匹配的结果消息处理器,消息体为{}", message);
            return;
        }
        handler.handle(message);
    }

    private ResultMessageHandler getMatchedHandler(Message message){
        if (message instanceof DetectResultMessage){
            return detectResultMessageHandler;
        }
        if (message instanceof QuantifyResultMessage){
            return quantifyResultMessageHandler;
        }
        return exceptionResultMessageHandler;
    }
}
