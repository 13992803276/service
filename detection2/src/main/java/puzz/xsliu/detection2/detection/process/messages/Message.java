package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:00 PM
 * @author: lxs
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = -7227313569743396744L;

    /**
     * 消息类型
     */
    private String messageType;
}
