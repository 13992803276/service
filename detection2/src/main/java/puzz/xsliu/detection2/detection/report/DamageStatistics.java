package puzz.xsliu.detection2.detection.report;

import lombok.Data;
import puzz.xsliu.detection2.detection.entity.Image;

import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/3/2:21 PM
 * @author: lxs
 */
@Data
public class DamageStatistics {
    public static final double WIDTH_THRESHOLD = 0.15;
    /**
     *  构件的类型
     */
    private String structType;
    /**
     *  裂缝的数量
     */
    private int crackNum;

    /**
     *  裂缝的总长度,单位毫米
     */
    private double crackTotalLength;

    /**
     *  裂缝的最小宽度
     */
    private double crackMinWidth = Integer.MAX_VALUE;

    /**
     *  裂缝的最大宽度
     */
    private double crackMaxWidth;

    /**
     *  宽度消息0.15mm的裂缝数量
     */
    private int lessNum;

    /**
     *  宽度小于0.15mm的裂缝总长度
     */
    private double lessTotalLength;

    /**
     *  宽度大于阈值的裂缝总长度
     */
    private double moreTotalLength;

    /**
     *  宽度大于阈值的裂缝总数
     */
    private int moreNum;

    /**
     * 锈蚀的个数
     */
    private int rebarNum;

    /**
     *  锈蚀的总面积,单位mm^2
     */
    private double rebarTotalArea;

    /**
     *  锈蚀的最小面积,单位mm^2
     */
    private double rebarMinArea = Integer.MAX_VALUE;

    /**
     *  锈蚀的最大面积,单位mm^2
     */
    private double rebarMaxArea;

    /**
     *  剥落的数量
     */
    private int spallNum;

    /**
     *  剥落的总面积,单位mm^2
     */
    private double spallTotalArea;

    /**
     *  剥落的最小面积,单位mm^2
     */
    private double spallMinArea = Integer.MAX_VALUE;
    /**
     * 剥落的最大面积,,单位mm^2
     */
    private double spallMaxArea;

    /**
     *  这类构件的数量
     */
    private int structNum;


    private List<DamageInfoInTable> damageInfos;


    @Data
    public static class DamageInfoInTable{
        private int imageIndex;
        private String structSerialNumber;
        private String damageType;
        private String damageStatus;
        private int damageIndex;
        private Image image;
    }

    /**
     * 每个部分损伤的信息
     */
    @Data
    public static class PartDamageStat {
        /**
         *  裂缝的数量
         */
        private int crackNum;

        /**
         *  锈蚀的数量
         */
        private int rebarNum;
        /**
         *  剥落的数量
         */
        private int spallNum;
        /**
         *  小于阈值的数量
         */
        private int lessNum;
        /**
         *  大于阈值的数量
         */
        private int moreNum;
    }
}

