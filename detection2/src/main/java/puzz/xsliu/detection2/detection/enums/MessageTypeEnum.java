package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/8:16 PM
 * @author: lxs
 */
public enum MessageTypeEnum {
    DETECT("DETECT", "检测消息"),
    DETECT_RESULT("DETECT_RESULT", "检测结果消息"),
    QUANTIFY("QUANTIFY", "量化消息"),
    QUANTIFY_RESULT("QUANTIFY_RESULT", "量化结果消息"),
    EXCEPTION("EXCEPTION", "通用异常消息"),
            ;
    @Getter
    private final String desc;
    @Getter
    private final String code;

    MessageTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static MessageTypeEnum getByCode(String code){
        for (MessageTypeEnum typeEnum : values()) {
            if (typeEnum.code.equals(code)){
                return typeEnum;
            }
        }
        return null;
    }

    public static List<String> listChannels(){
        List<String> channels = new ArrayList<>();
        for (MessageTypeEnum value : values()) {
            if (value == EXCEPTION){
                continue;
            }
            channels.add(value.code);
        }
        return channels;
    }
}
