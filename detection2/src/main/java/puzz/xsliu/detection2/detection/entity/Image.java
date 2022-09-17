package puzz.xsliu.detection2.detection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import puzz.xsliu.detection2.detection.enums.DamageEnum;

import java.util.*;

/**
 * 图像
 *
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:06 AM
 * @author: lxs
 */
@Data
public class Image {
    private Long id;
    /**
     * 数据状态,默认0,删除1
     */
    private Integer status;
    /**
     * 裂缝损伤的json
     */
    private String crackJson;
    /**
     * 名称
     */
    private String name;
    /**
     * 面积型损伤的json
     */
    private String areaJson;
    // 外键
    /**
     * 所属桥梁
     */
    private Long bridgeId;
    /**
     * 所属构件,单张图像是为0
     */
    private Long structId;
    /**
     * 所属用户,单张图像时为0
     */
    private Long userId;
    /**
     * 原始图像的保存路径
     */
    private String srcPath;
    /**
     * 结果图像的保存路径
     */
    private String resPath;
    /**
     * 当前流程,对于图像而言,具有等待中,检测完成,量化完成 三个状态
     */
    private String process;
    /**
     * 拍摄距离, 单位毫米
     */
    private Integer shotDistance;
    /**
     * 焦距,单位毫米
     */
    private Integer focalLength;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
    /**
     * 是否出现了异常,默认为空
     */
    private String exception;




    // DO属性
    private List<Damage> damages;

    public boolean isSingle() {
        return this.bridgeId == 0 && this.structId == 0;
    }


    public String generateDamageTypes(){
        Set<String> typeSet = new HashSet<>();
        for (Damage damage : damages) {
            String type = damage.getType();
            if (type.startsWith(DamageEnum.CRACK.getCode())){
                typeSet.add("裂缝");
            }else if(type.equals(DamageEnum.REBAR.getCode())){
                typeSet.add("钢筋锈蚀");
            }else{
                typeSet.add("剥落");
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : typeSet) {
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString();
    }


}
