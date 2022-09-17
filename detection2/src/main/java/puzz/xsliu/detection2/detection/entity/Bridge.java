package puzz.xsliu.detection2.detection.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import puzz.xsliu.detection2.detection.enums.BridgePartEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:15 AM
 * @author: lxs
 */
@Data
public class Bridge {
    private Long id;
    private String name;
    private String span;
    private String structure;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
    private String process;
    private String reportPath;

    private Integer exp;

    // DO属性
    private List<Image> images;
    private List<Damage> damages;
    private List<Long> counts;
    private List<Struct> structs;

    // 构件数
    private int structNum;
    // 图像数
    private int imageNum;


    public List<Struct> getStructs(BridgePartEnum bridgePartEnum) {
        if (CollectionUtil.isEmpty(structs)){
            return new ArrayList<>();
        }
        // 获取这个部分的构件
        String part = bridgePartEnum.getCode();
        return structs.stream().filter(struct -> StrUtil.equalsIgnoreCase(struct.getPart(), bridgePartEnum.getCode())).collect(Collectors.toList());
    }
}
