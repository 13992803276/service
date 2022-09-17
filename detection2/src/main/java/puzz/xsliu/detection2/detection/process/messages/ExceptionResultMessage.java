package puzz.xsliu.detection2.detection.process.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检测或者量化过程中捕捉到的问题,反馈到服务端
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:12 PM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExceptionResultMessage extends Message{

    /**
     *  全大写,单词之间用下划线的形式来简略描述出现的问题
     *  例如:FILE_NOT_FOUND_ERROR,IMAGE_FORMAT_ERROR等等
     */
    private String code;

    /**
     *  详细描述出现的问题
     */
    private String desc;

    /**
     *  关联的图像ID
     *  融合进程也要传关联的图像ID,这样可以统一定位所属的桥梁
     */
    private Long id;

}
