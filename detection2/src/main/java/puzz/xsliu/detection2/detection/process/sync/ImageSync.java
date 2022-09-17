package puzz.xsliu.detection2.detection.process.sync;

import lombok.Data;

/**
 * 用于在检测时同步图像的检测结果
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/11:43 AM
 * @author: lxs
 */
@Data
public class ImageSync {
    boolean done;
    String boxes;
    String polygon;
}
