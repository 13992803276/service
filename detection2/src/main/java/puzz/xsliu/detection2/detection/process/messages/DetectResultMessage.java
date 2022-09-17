package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:00 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DetectResultMessage extends Message{
    private static final long serialVersionUID = -4908907254252864493L;

    private Long id;

    /**
     *  需要根据具体情况进行解析,也可以不在java程序中做解析
     */
    private String objects;

    /**
     *  检测网络会回传两种type,分别是polygon和box,代表的是json中标注的形式
     */
    private String type;
}
