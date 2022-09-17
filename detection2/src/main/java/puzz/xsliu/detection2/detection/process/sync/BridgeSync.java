package puzz.xsliu.detection2.detection.process.sync;

import lombok.Data;

/**
 * 用于在整个流程当中同步桥梁的状态
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/11:42 AM
 * @author: lxs
 */
@Data
public class BridgeSync {
    private long id;
    private int total;
    private int done;
    private String process;
}
