package puzz.xsliu.detection2.detection.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/6:23 PM
 * @author: lxs
 */
@Slf4j
public class CommonUtil {

    private static final Font SONG = new Font("宋体", Font.BOLD, 18);
    private static final Font HEI = new Font("微软雅黑", Font.BOLD, 18);
    /**
     *  1厘米 = 10毫米
     */
    private static final int CM = 10;
    /**
     *  1分米 = 10毫米
     */
    private static final int DM = 10 * CM;
    /**
     * ... 同上
     */
    private static final int M = 10 * DM;

    /**
     * 平方厘米
     */
    private static final int CM2 = CM  * CM;
    /**
     * ... 同上
     */
    private static final int DM2 = DM * DM;
    private static final int M2 = M * M;

    private static final String FOCAL_LENGTH_FLAG = "Focal Length";
    private static final String FOCAL_PLANE_X_RESOLUTION_FLAG = "Focal Plane X Resolution";
    private static final String FOCAL_PLANE_Y_RESOLUTION_FLAG = "Focal Plane Y Resolution";
    private static final String FOCAL_PLANE_RESOLUTION_UNIT_FLAG = "Focal Plane Resolution Unit";

    private static final int SALT_LENGTH = 5;
    private static final String SP = ";";
    private static final String FORMAT = "yyyy-MM-dd HH:mm";
    private static final String[] SUPPORT_FORMAT = {"jpg", "png", "jpeg"};

    private static final String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int IMAGE_MIN_SIDE = 768;

    /**
     * 原始字符串 + 噪声 之后进行MD5编码
     * @param original 原始字符串,即前端用户的输入
     * @param salt 噪声
     * @return 编码后的密码
     */
    public static String MD5Encoding(String original, String salt) {
        original += salt;
        return MD5.create().digestHex(original);
    }

    public static String getSalt() {
        return getRandomString(SALT_LENGTH);
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(STR.charAt(number));
        }
        return sb.toString();
    }


    public static boolean isBlank(String str) {
        return str == null || str.isBlank() || str.isEmpty();
    }

    /**
     * 正则表达式判断是否是邮箱
     *
     * @param str 待判断字符串
     */
    public static boolean isEmail(String str) {
        String check = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        return matcher.matches();
    }

    /**
     * 正则表达式判断是否是数字字符串
     *
     * @param str 待判断字符串
     */
    public static boolean isNum(String str) {
        String check = "^-?[0-9]+";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        return matcher.matches();
    }

    /**
     * 是否是支持的图像类型
     */
    public static boolean isSupportImage(String fileName) {
        if (isBlank(fileName)) {
            return false;
        }
        String tmp = fileName.toLowerCase(Locale.ROOT);
        for (String ext : SUPPORT_FORMAT) {
            if (tmp.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     *  获取支持的图像类型
     */
    public static String getSupportFormat() {
        return Arrays.toString(SUPPORT_FORMAT);
    }


    /**
     * 字符串到日期
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date formatFrom(String dateStr) {
        return DateUtil.parse(dateStr, FORMAT);
    }

    /**
     * 日期到字符串
     * @param date 日期
     * @return 日期字符串
     */
    public static String format(Date date) {
        return DateUtil.format(date, FORMAT);
    }

    /**
     * 长度单位修正,将毫米数修正成合适的长度单位,保留两位数字
     *
     * @param lengthOfMM 毫米单位的长度
     * @return
     */
    public static String lengthFormat(double lengthOfMM) {
        if (lengthOfMM < CM) {
            return String.format("%.2f毫米", lengthOfMM);
        }

        if (lengthOfMM < DM) {
            return String.format("%.2f厘米", lengthOfMM / CM);
        }

        if (lengthOfMM < M) {
            return String.format("%.2f分米", lengthOfMM / DM);
        }

        return String.format("%.2f米", lengthOfMM / M);
    }

    /**
     * 面积单位修正,将毫米数修正成合适的面积单位,保留两位数字
     *
     * @param num 平方毫米
     * @return
     */
    public static String areaFormat(double num) {
        if (num < CM2) {
            // 单位是平方毫米,
            return String.format("%.2f平方毫米", num);
        }

        if (num < DM2) {
            return String.format("%.2f平方厘米", num / CM2);
        }

        if (num < M2) {
            return String.format("%.2f平方分米", num / DM2);
        }

        return String.format("%.2f平方米", num / M2);
    }

    public static double[] getImageSizeByBufferedImage(File imageFile) {
        try{
            BufferedImage image = ImageIO.read(new FileInputStream(imageFile));
            return new double[]{image.getWidth(), image.getHeight()};
        }catch (IOException e){
            log.error("读取图像尺寸出错!");
            return new double[0];
        }
    }

    public static double[] getImageSizeByBytes(File imageFile) {
        try {
            InputStream is = new FileInputStream(imageFile);
            int c1 = is.read();
            int c2 = is.read();
            int c3 = is.read();

            String mimeType = null;
            double width = -1;
            double height = -1;

            if (c1 == 'G' && c2 == 'I' && c3 == 'F') { // GIF
                is.skip(3);
                width = readInt(is, 2, false);
                height = readInt(is, 2, false);
                mimeType = "image/gif";
            } else if (c1 == 0xFF && c2 == 0xD8) { // JPG
                while (c3 == 255) {
                    int marker = is.read();
                    int len = readInt(is, 2, true);
                    if (marker == 192 || marker == 193 || marker == 194) {
                        is.skip(1);
                        height = readInt(is, 2, true);
                        width = readInt(is, 2, true);
                        mimeType = "image/jpeg";
                        break;
                    }
                    is.skip(len - 2);
                    c3 = is.read();
                }
            } else if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
                is.skip(15);
                width = readInt(is, 2, true);
                is.skip(2);
                height = readInt(is, 2, true);
                mimeType = "image/png";
            } else if (c1 == 66 && c2 == 77) { // BMP
                is.skip(15);
                width = readInt(is, 2, false);
                is.skip(2);
                height = readInt(is, 2, false);
                mimeType = "image/bmp";
            } else if (c1 == 'R' && c2 == 'I' && c3 == 'F') { // WEBP
                byte[] bytes = is.readNBytes(27);
                width = ((int) bytes[24] & 0xff) << 8 | ((int) bytes[23] & 0xff);
                height = ((int) bytes[26] & 0xff) << 8 | ((int) bytes[25] & 0xff);
                mimeType = "image/webp";
            } else {
                int c4 = is.read();
                if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                        || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) { //TIFF
                    boolean bigEndian = c1 == 'M';
                    int ifd = 0;
                    int entries;
                    ifd = readInt(is, 4, bigEndian);
                    is.skip(ifd - 8);
                    entries = readInt(is, 2, bigEndian);
                    for (int i = 1; i <= entries; i++) {
                        int tag = readInt(is, 2, bigEndian);
                        int fieldType = readInt(is, 2, bigEndian);
                        int valOffset;
                        if ((fieldType == 3 || fieldType == 8)) {
                            valOffset = readInt(is, 2, bigEndian);
                            is.skip(2);
                        } else {
                            valOffset = readInt(is, 4, bigEndian);
                        }
                        if (tag == 256) {
                            width = valOffset;
                        } else if (tag == 257) {
                            height = valOffset;
                        }
                        if (width != -1 && height != -1) {
                            mimeType = "image/tiff";
                            break;
                        }
                    }
                }
            }
            if (mimeType == null) {
                throw new RuntimeException("Unsupported image type");
            }
            return new double[]{width, height};
        } catch (IOException e) {
            log.info("读取出错!");
            return new double[0];
        }
    }

    private static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    public static void drawPie(int[] nums, String[] labels, File saveFile) throws IOException {
        commonValidation(nums, labels);
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < nums.length; i++) {
            dataset.setValue(labels[i], nums[i]);
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "裂缝的宽度分布饼状图", // chart title
                dataset, // data
                true, // include legend
                true,
                false);
        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        chart.setBackgroundPaint(new ChartColor(255, 255, 255));
        chart.getTitle().setFont(SONG);
        // 得到图块,准备设置标签的字体
        PiePlot plot = (PiePlot) chart.getPlot();
        // 设置标签字体
        plot.setLabelFont(SONG);
        plot.setStartAngle(3.14f / 2f);
        // 设置plot的前景色透明度
        plot.setForegroundAlpha(0.7f);
        // 设置plot的背景色透明度
        plot.setBackgroundAlpha(0.0f);
        // 设置标签生成器(默认{0})
        // {0}:key {1}:value {2}:百分比 {3}:sum
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({1}占{2})"));
        chart.getLegend().setItemFont(SONG);
        ChartUtils.saveChartAsJPEG(saveFile, chart, width, height);
    }

    public static void drawHist(int[] nums, String[] labels, File saveFile) throws IOException {
        commonValidation(nums, labels);

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int max = 0;
        String damage = "损伤";
        for (int i = 0; i < nums.length; i++) {
            dataset.addValue(nums[i], damage, labels[i]);
            max = Math.max(nums[i], max);
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "三种损伤的数量",
                "种类", "数量",
                dataset, PlotOrientation.VERTICAL,
                false, false, false);
        // barChart.getLegend().setItemFont(font);

        barChart.getTitle().setFont(SONG);
        CategoryPlot categoryPlot = barChart.getCategoryPlot();
        CategoryAxis domainAxis = categoryPlot.getDomainAxis();//X轴
        domainAxis.setLabelFont(SONG);//下标
        domainAxis.setTickLabelFont(SONG);//X轴标题

        ValueAxis rangeAxis = categoryPlot.getRangeAxis();//Y轴
        rangeAxis.setRange(0, max + 3);
        rangeAxis.setLabelFont(SONG);//下标
        rangeAxis.setTickLabelFont(SONG);//Y轴标题
        // 获取图形
        // 设置plot的前景色透明度
        categoryPlot.setForegroundAlpha(0.7f);
        // 设置plot的背景色透明度
        categoryPlot.setBackgroundAlpha(0.0f);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        // 设置柱子上的数值显示
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(HEI);
        // 设置颜色
        renderer.setSeriesItemLabelFont(0, HEI);
        StandardBarPainter standardBarPainter = new StandardBarPainter();
        renderer.setBarPainter(standardBarPainter);
        Paint paint = new ChartColor(0, 153, 153);
        renderer.setSeriesPaint(0, paint);
        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        ChartUtils.saveChartAsJPEG(saveFile, barChart, width, height);
    }

    private static void commonValidation(int[] nums, String[] labels) {
        if (ArrayUtil.isEmpty(nums) || ArrayUtil.isEmpty(labels)) {
            throw new RuntimeException("label和数据不能为空!");
        }
        if (labels.length != nums.length) {
            throw new RuntimeException( "label的尺寸和数据尺寸不匹配");
        }
    }
}
