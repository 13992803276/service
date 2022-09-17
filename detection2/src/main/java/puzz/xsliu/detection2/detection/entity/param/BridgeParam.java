package puzz.xsliu.detection2.detection.entity.param;

import lombok.Data;

import java.util.Date;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/4:43 PM
 * @author: lxs
 */
@Data
public class BridgeParam {
    /**
     *  模糊查询，可以对应到桥梁名称，桥梁跨境组合或者桥梁结构
     */
    private String key;
    private Long id;
    private Long userId;
    private Date start;
    private String process;
}
