package puzz.xsliu.detection2.detection.entity.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 图像的筛选条件
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/4:27 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageParam extends PageParam{
    // 图像ID
    private Long id;
    // 当前进程
    private String process;
    // 当前状态 0-正常 1- 删除
    private Integer status;
    // 桥梁ID
    private Long bridgeId;
    // 构件ID
    private Long structId;
    // 图像名称
    private String name;
    // 用户ID
    private Long userId;
    // 开始时间
    private Date start;

    private String exception;

}
