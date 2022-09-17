package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

/**
 * @author lxs
 * @description <a href="mailto:xsl2011@outlook.com" />
 * 2022/1/27/12:20 PM
 */
public enum BridgeProcessEnum {
    /**
     * 上传中,桥梁初始化完毕,但是图像正在上传中
     */
    LOADING("LOADING", 1),
    /**
     * 检测中
     */
    DETECTING("DETECTING", 2),
    /**
     * 正在生成检测报告
     */
    GENERATING("GENERATING", 3),
    /**
     *  流程结束
     */
    FINISHED("FINISHED", 4),
    ;
    @Getter
    private final String code;
    @Getter
    private final int index;

    BridgeProcessEnum(String code, int index){
        this.code = code;
        this.index = index;
    }

    /**
     *  根据code找到对应的枚举
     */
    public static BridgeProcessEnum getProcessEnum(String code){
        if (code == null){
            return null;
        }
        for (BridgeProcessEnum processEnum : values()) {
            if (processEnum.getCode().equals(code)){
                return processEnum;
            }
        }
        return null;
    }


    public BridgeProcessEnum next(){
        for (BridgeProcessEnum process : values()) {
            if (process.getIndex() - 1 == this.getIndex()){
                return process;
            }
        }
        return this;
    }

    public BridgeProcessEnum previous(){
        for (BridgeProcessEnum process : values()) {
            if (process.getIndex() + 1 == this.getIndex()){
                return process;
            }
        }
        return this;
    }

    public boolean needSync(){
        return this == LOADING || this == DETECTING;
    }
}

