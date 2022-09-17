package puzz.xsliu.detection2.detection.enums;

import lombok.Getter;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/3/2:19 PM
 * @author: lxs
 */
public enum BridgePartEnum {
    /**
     *  桥梁上部
     */
    TOP("TOP"),
    /**
     *  桥面
     */
    DECK("DECK"),
    /**
     *  桥梁底部
     */
    BOTTOM("BOTTOM");

    @Getter
    private final String code;

    BridgePartEnum(String code){
        this.code = code;
    }
}
