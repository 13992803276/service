package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

import java.util.Random;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/12:25 PM
 * @author: lxs
 */
public enum DamageEnum {
    CRACK("CRACK", "裂缝"),
    /**
     *  倾斜的裂缝
     */
    CRACK_BIAS("CRACK_BIAS", "倾斜裂缝"),
    /**
     *  横向的裂缝
     */
    CRACK_HORIZONTAL("CRACK_HORIZONTAL", "横向裂缝"),
    /**
     *  垂直的裂缝
     */
    CRACK_VERTICAL("CRACK_VERTICAL", "竖向裂缝"),
    /**
     *  混凝土剥落
     */
    SPALL("SPALL", "剥落"),
    /**
     *  钢筋锈蚀
     */
    REBAR("REBAR", "锈蚀");

    @Getter
    private final String code;

    @Getter
    private final String desc;

    DamageEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static DamageEnum randomGet(){
        Random random = new Random();
        int i = random.nextInt(10);
        if (i <= 3){
            return CRACK;
        }else if (i <= 6){
            return SPALL;
        }
        return REBAR;
    }

    public  static DamageEnum getByCode(String code){
        for (DamageEnum value : values()) {
            if (value.code.equals(code)){
                return value;
            }
        }
        // 没有匹配的情况下默认返回裂缝
        return CRACK;
    }
}
