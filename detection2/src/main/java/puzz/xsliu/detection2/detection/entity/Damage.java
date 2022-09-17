package puzz.xsliu.detection2.detection.entity;

import lombok.Data;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:14 AM
 * @author: lxs
 */
@Data
public class Damage {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 类型
     */
    private String type;
    /**
     * 图像的ID
     */
    private Long imageId;
    /**
     * 构件的ID
     */
    private Long structId;
    /**
     * 桥梁的ID
     */
    private Long bridgeId;

    /**
     *  长度型损伤的宽度和长度,单位都是mm
     */
    private Double width;
    private Double length;

    /**
     *  长度型损伤的面积,单位是mm^2
     */
    private Double area;
}
