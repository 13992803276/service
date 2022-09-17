package puzz.xsliu.detection2.detection.entity;

import lombok.Data;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:15 AM
 * @author: lxs
 */
@Data
public class Struct {
    private Long id;
    private String part;
    private Long bridgeId;
    /**
     * 焦距
     */
    private Integer focalLength;
    /**
     * 拍摄距离
     */
    private Integer shotDistance;
    /**
     * 构件编号
     */
    private String serialNumber;

    private List<Image> images;

    public String getStructType() {
        return serialNumber.substring(serialNumber.lastIndexOf("#") + 1);
    }
}
