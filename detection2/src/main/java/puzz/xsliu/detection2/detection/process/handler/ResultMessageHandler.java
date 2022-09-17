package puzz.xsliu.detection2.detection.process.handler;

import puzz.xsliu.detection2.detection.process.messages.Message;

/**
 * @author lxs
 * @description <a href="mailto:xsl2011@outlook.com" />
 * 2022/1/27/7:04 PM
 */
public interface ResultMessageHandler {

    void handle(Message message);

    default boolean filter(Message message){
        return message != null;
    }
}
