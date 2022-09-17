package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/7:16 PM
 * @author: lxs
 */
public enum FileTypeEnum {
    IMAGE("存储桥梁图像", "images"),
    ATTACH("存储反馈附件", "attaches"),
    HEADER("存储用户头像", "headers"),
    REPORT("存储检测报告", "reports")
    ;
    @Getter
    private final String code;
    @Getter
    private final String subFilePath;

    FileTypeEnum(String code, String subFilePath){
        this.code = code;
        this.subFilePath = subFilePath;
    }
}
