package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 图像在流程中的状态
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/6:51 PM
 * @author: lxs
 */
public enum ImageProcessEnum {
    WAITING("WAITING"),
    DETECTED("DETECTED"),
    QUANTIFIED("QUANTIFIED");

    @Getter
    private final String code;
    ImageProcessEnum(String code){
        this.code = code;
    }

    public static ImageProcessEnum getByCode(String code){
        for (ImageProcessEnum processEnum : values()) {
            if (Objects.equals(processEnum.code, code)){
                return processEnum;
            }
        }
        return null;
    }


}
