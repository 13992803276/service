package puzz.xsliu.detection2.detection.entity.param;

import lombok.Data;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/5:19 PM
 * @author: lxs
 */
@Data
public class DamageParam {
    private Long id;
    private Long imageId;
    private String type;

    private Long bridgeId;
    private Long structId;
}
