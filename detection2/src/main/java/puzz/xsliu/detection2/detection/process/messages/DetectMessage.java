package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 检测消息
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/6:53 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DetectMessage extends Message{
    private Long id;
    private String path;
}
