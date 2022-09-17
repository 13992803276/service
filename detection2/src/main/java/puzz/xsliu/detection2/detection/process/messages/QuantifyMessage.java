package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 量化消息
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/6:55 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuantifyMessage extends Message{

    /**
     *  裂缝的检测结果
     */
    private String boxes;
    /**
     *  面积型损伤结果
     */
    private String polygons;
    /**
     * 图像的ID
     */
    private Long id;
    /**
     * 原图的位置
     */
    private String path;

    private double shotDistance;

    private double focalLength;

}
