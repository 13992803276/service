package puzz.xsliu.detection2.detection.process;

import cn.hutool.core.collection.CollectionUtil;
import com.spire.doc.*;
import com.spire.doc.documents.*;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TextRange;
import lombok.extern.slf4j.Slf4j;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.Damage;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.entity.Struct;
import puzz.xsliu.detection2.detection.enums.BridgePartEnum;
import puzz.xsliu.detection2.detection.enums.DamageEnum;
import puzz.xsliu.detection2.detection.report.DamageStatistics;
import puzz.xsliu.detection2.detection.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/3/2:14 PM
 * @author: lxs
 */
@Slf4j
@SuppressWarnings("all")
public class ReportGenerator {
    private final Bridge bridge;
    private final Document document;
    private Section section;
    private int imageIndex = 1;
    private final Map<String, DamageStatistics.PartDamageStat> partDamageStats = new HashMap<>();
    private static final String[] TABLE_HEADERS = new String[]{"序号", "构件编号", "病害类型", "病害状况", "照片编号", "损伤编号"};

    public ReportGenerator(Bridge bridge) {
        this.bridge = bridge;
        document = new Document();
    }

    public void saveToLocalFile(File file) {
        document.saveToFile(file.getAbsolutePath(), FileFormat.Docx);
    }

    public void generate() {
        log.info("创建检测报告文档");
        // 为文档添加格式
        addStyle();
        section = document.addSection();
        // 设置页边距
        section.getPageSetup().getMargins().setTop(72f);
        section.getPageSetup().getMargins().setBottom(72f);
        section.getPageSetup().getMargins().setLeft(90f);
        section.getPageSetup().getMargins().setRight(90f);

        // 封皮
        generateCoverPage();
        // 正文
        try {
            generateMainBody();
        } catch (Exception e) {
            log.error("生成检测报告时出错!", e);
            throw new RuntimeException("生成检测报告时产生异常");
        }

    }


    private void generateMainBody() throws IOException {
        // 第二段
        generateParagraph2();
        // 第三段
        generateParagraph3();
        // 第四段
        addStatisticPic();

    }

    /**
     * 第二部分,主要是提示说明部分
     */
    private void generateParagraph2() {
        log.info("正在创建检测报告的第二部分");
        // 报告第一页的标题，桥梁的名称
        Paragraph paragraph_2_1 = section.addParagraph();
        paragraph_2_1.setText(String.format("%s桥", bridge.getName()));
        paragraph_2_1.applyStyle("noteStyle");

        // 1.桥梁概况，中号标题
        Paragraph paragraph_2_2 = section.addParagraph();
        paragraph_2_2.setText("1 桥梁概况");
        paragraph_2_2.applyStyle("midTitle");

        // 桥梁概况，正文
        log.info("桥梁概况正文");
        Paragraph paragraph_2_3 = section.addParagraph();
        paragraph_2_3.appendText(String.format("%s桥结构形式为%s,跨径组合为%s", bridge.getName(), bridge.getStructure(), bridge.getSpan()));

        paragraph_2_3.applyStyle("normalStyle");

        // 2. 桥梁的编号规则， 中号标题
        Paragraph paragraph_2_4 = section.addParagraph();
        paragraph_2_4.setText("2 桥梁编号规则");
        paragraph_2_4.applyStyle("midTitle");
        // 2.1 方位描述， 小标题
        Paragraph paragraph_2_5 = section.addParagraph();
        paragraph_2_5.setText("2.1 方位描述");
        paragraph_2_5.applyStyle("smallTitle");

        // 方位描述正文
        log.info("方位描述正文");
        Paragraph paragraph_2_6 = section.addParagraph();
        paragraph_2_6.appendText("主线沿运营小桩号往运营大桩号方向为路线前进方向，沿路线前进方向为路线（右幅）（R），反之为左幅（L）。\n");
        paragraph_2_6.appendText("在同一横断面上，规定靠近道路中心线（中央分隔带）的位置为内侧，反之为外侧。\n");
        paragraph_2_6.appendText("在同一纵断面上，规定在小桩号一侧的位置标记为小桩号侧，大桩号一侧的位置标记为大桩号侧。\n");
        paragraph_2_6.appendText("桥梁纵向距离描述为默认由小桩号向大桩号方向，距小桩号的距离。");
        paragraph_2_6.applyStyle("normalStyle");

        // 2.2 编号规则，小标题
        Paragraph paragraph_2_7 = section.addParagraph();
        paragraph_2_7.setText("2.2 编号规则");
        paragraph_2_7.applyStyle("smallTitle");

        // 2.3 编号规则， 正文
        Paragraph paragraph_2_8 = section.addParagraph();
        paragraph_2_8.appendText("(1) 墩(台)、桥孔编号\n");
        paragraph_2_8.appendText("沿路线前进方向，主线桥小桩号方向的桥台为0#台，沿前进方向依次为1#墩、2#墩、……、m#台，相应的桥孔为第1孔、第2孔、……、第m孔。\n");
        paragraph_2_8.appendText("(2) T梁、湿接缝、横梁、横隔板编号\n");
        paragraph_2_8.appendText("第m孔主梁的编号方法：沿小桩号至大桩号方向，从左到右依次为m-1#梁、m-2#梁、……、m-n#梁。\n");
        paragraph_2_8.appendText("第m孔湿接缝的编号方法：沿小桩号至大桩号方向，从左到右以次为m-1#湿接缝、m-2#湿接缝、……、m-n#湿接缝。\n");
        paragraph_2_8.appendText("中(端)横梁编号方法为：沿小桩号至大桩号方向，当墩(台)上有1片横梁时，m#墩(台)上中(端)横梁从左往右依次为m-1#" + "中(端)横梁、m-2#中(端)横梁、……、m-n#中(端)横梁；当墩台上有2片横梁时，第m孔m#墩(台)上中(端)横梁从左往右依次为m-m-1#中(端)" + "横梁、m-m-2#中(端)横梁、……、m-m-n#中(端)横梁。\n");
        paragraph_2_8.appendText("横隔板编号方法为：沿小桩号至大桩号方向，依次为第1排、第2排、……、第n排横隔板，第m孔n排横隔板从左往右，依次为" + "m-n-1#横隔板、m-n-2#横隔板、……、m-n-f#横隔板；当桥跨仅有1排横隔板时，第m孔横隔板从左往右依次为m-1#横隔板、m-2#横隔板、" + "……、m-n#横隔板。\n");
        paragraph_2_8.appendText("(3) 支座编号\n");
        paragraph_2_8.appendText("第m孔n#墩柱上支座的编号方法：当墩顶为双排支座时，面向前进方向，从左到右依次为m-n-1#、m-n-2#、m-n-3#等支座" + "；当墩顶为单排支座时，面向前进方向，从左到右依次为n-1#、n-2#、n-3#等支座。\n");
        paragraph_2_8.appendText("(4) 墩柱编号\n");
        paragraph_2_8.appendText("主线桥m#墩的墩柱编号方法是面向前进方向，从左到右依次为m-1#、m-2#、m-3#墩柱。\n");
        paragraph_2_8.appendText("(5) 桥面系编号规则\n");
        paragraph_2_8.appendText("伸缩缝：沿小桩号至大桩号方向，从0#台开始依次为1#、2#、…、m#伸缩缝。\n");
        paragraph_2_8.appendText("护栏：沿小桩号至大桩号方向，直接描述为：“第m联左(右)侧护栏”。\n");
        paragraph_2_8.appendText("铺装：沿小桩号至大桩号方向，直接描述为：“第m联桥面铺装”。\n");
        paragraph_2_8.appendText("排水系统：沿小桩号至大桩号方向，直接描述为：“第m联排水系统”。\n");
        paragraph_2_8.applyStyle("normalStyle");
    }

    /**
     * 第三部分,图像和表
     */
    private void generateParagraph3() {
        log.info("正在创建检测报告的第三部分");
        // 第三段标题
        Paragraph paragraph_3_1 = section.addParagraph();
        paragraph_3_1.appendText("3 外观检查结果");
        paragraph_3_1.applyStyle("midTitle");
        generatePartInfo(BridgePartEnum.TOP);
        generatePartInfo(BridgePartEnum.BOTTOM);
        generatePartInfo(BridgePartEnum.DECK);
    }

    /**
     * 统计各个部分损伤的信息,需要从图像中获取
     *
     * @param bridgePart 桥梁的一个部分
     */
    private void generatePartInfo(BridgePartEnum bridgePart) {
        String title;
        if (bridgePart == BridgePartEnum.TOP) {
            title = "3.1 上部结构";
        } else if (bridgePart == BridgePartEnum.BOTTOM) {
            title = "3.2 下部结构";
        } else {
            title = "3.3 桥面结构";
        }
        log.info("正在处理: " + title);
        Paragraph paragraph_3_2 = section.addParagraph();
        paragraph_3_2.setText(title);
        paragraph_3_2.applyStyle("smallTitle");

        List<DamageStatistics> statisticsList = calDamageInfo(bridgePart);
        //  将计算好的信息插入到文档当中
        int index = 1;
        for (DamageStatistics statistics : statisticsList) {

            generateTypeDamage(statistics, index++);
        }

    }

    /**
     * 一类构件,例如"梁"的病害详情,包含hi
     *
     * @param statistics 统计信息
     * @param index      此时的标题码
     */
    private void generateTypeDamage(DamageStatistics statistics, int index) {
        String title = statistics.getStructType();
        int structNum = statistics.getStructNum();
        Paragraph paragraph1 = section.addParagraph();
        paragraph1.appendText(index + "、" + title + "\n");
        paragraph1.appendText(String.format("本次检测的%s的主要病害为：\n", title));
        // 文字部分
        // 1 裂缝损伤
        int titleIndex = 1;
        if (statistics.getCrackNum() > 0) {
            paragraph1.appendText(String.format("（%d）%d个%s存在%d条裂缝，累计长度L=%s，裂缝宽度范围为%s~%s。" + "裂缝宽度≤0.15mm的裂缝有%d条，总长%s；裂缝宽度>0.15mm的裂缝有%d条，总长%s。\n", titleIndex++, structNum, title, statistics.getCrackNum(), CommonUtil.lengthFormat(statistics.getCrackTotalLength()), CommonUtil.lengthFormat(statistics.getCrackMinWidth()), CommonUtil.lengthFormat(statistics.getCrackMaxWidth()), statistics.getLessNum(), CommonUtil.lengthFormat(statistics.getLessTotalLength()), statistics.getMoreNum(), CommonUtil.lengthFormat(statistics.getMoreTotalLength())));
        }
        // 2 混凝土剥落
        if (statistics.getSpallNum() > 0) {
            paragraph1.appendText(String.format("(%d) %d个%s存在%d处破损，累计面积S=%s，破损范围为%s~%s。\n", titleIndex++, structNum, title, statistics.getSpallNum(), CommonUtil.areaFormat(statistics.getSpallTotalArea()), CommonUtil.areaFormat(statistics.getSpallMinArea() == Integer.MAX_VALUE ? 0 : statistics.getSpallMinArea()), CommonUtil.areaFormat(statistics.getSpallMaxArea())));
        }
        // 钢筋锈蚀
        if (statistics.getRebarNum() > 0) {
            String content3 = String.format("(%d)%d个%s存在%d处钢筋锈蚀，累计面积S=%s，钢筋锈蚀范围为%s~%s平方米。\n", titleIndex, structNum, title, statistics.getRebarNum(), CommonUtil.areaFormat(statistics.getRebarTotalArea()), CommonUtil.areaFormat(statistics.getRebarMinArea() == Integer.MAX_VALUE ? 0 : statistics.getRebarMinArea()), CommonUtil.areaFormat(statistics.getRebarNum()));
            paragraph1.appendText(content3);
        }
        paragraph1.appendText(String.format("具体病害见下表3-%d。", index));
        paragraph1.applyStyle("normalStyle");
        // 表部分
        addTable(statistics, index);
        // 图部分
        addPic(statistics);
    }

    private void addPic(DamageStatistics statistics) {
        List<DamageStatistics.DamageInfoInTable> damageInfos = statistics.getDamageInfos();
        // 将图片添加到文档中
        Paragraph paragraph3 = section.addParagraph();
        paragraph3.setText("病害照片如下图所示:");
        paragraph3.applyStyle("normalStyle");
        // 先对损伤信息进行筛选,因为多个损伤信息对应一张图像,而此时只需要图像,不需要额外的损伤信息,所以做一次筛选,只留下一个
        Set<Integer> imageIDSet = new HashSet<>();
        damageInfos = damageInfos.stream().filter(damageInfo -> {
            if (imageIDSet.contains(damageInfo.getImageIndex())) {
                return false;
            } else {
                imageIDSet.add(damageInfo.getImageIndex());
                return true;
            }
        }).collect(Collectors.toList());

        log.info("{}部分的图片数量为{}", statistics.getStructType(), damageInfos.size());
        // 根据图片的数量来计算一共需要多少行
        int rows = damageInfos.size() / 2;
        int cols = 2;
        if (rows != 0) {
            // 根据行数和列数来构件表
            Table table = section.addTable(false);
            // 先设置表头,rows等于0是会报异常
            table.resetCells(rows, cols);
            // 获取表头的那一行
            for (int r = 0; r < rows; r++) {
                TableRow row = table.getRows().get(r);
                for (int c = 0; c < cols; c++) {
                    int curIndex = r * cols + c;
                    TableCell cell = row.getCells().get(c);
                    addPictureInCell(cell, damageInfos.get(curIndex));
                }
            }
        }
        // 是否还有图片存在，判断依据是images数量是否为奇数
        if ((damageInfos.size() % 2) != 0) {
            // 单独添加一行
            DamageStatistics.DamageInfoInTable damageInfo = damageInfos.get(damageInfos.size() - 1);
            Image image = damageInfo.getImage();
            Paragraph paragraph = section.addParagraph();
            paragraph.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            DocPicture picture = paragraph.appendPicture(image.getResPath());
            picture.setWidth(212.5f);
            File srcFile = new File(image.getResPath());
            // 读取图片的尺寸
            double[] shape = CommonUtil.getImageSizeByBytes(srcFile);
            if (shape.length == 0) {
                shape = CommonUtil.getImageSizeByBufferedImage(srcFile);
            }
            double f = (Math.min(shape[0], shape[1])) / Math.max(shape[0], shape[1]);
            // 保持长宽比，设置图片的长边为212.5，短边相应缩小
            picture.setHeight((float) (212.5 * f));
            picture.setAllowOverlap(false);
            picture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            Paragraph paragraph1 = section.addParagraph();
            paragraph1.appendText(String.format("图3-%d %s %s", damageInfo.getImageIndex(), damageInfo.getStructSerialNumber(), image.generateDamageTypes()));
            paragraph1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph1.applyStyle("picCaption");
        }
    }

    private void addPictureInCell(TableCell cell, DamageStatistics.DamageInfoInTable damageInfo) {
        Paragraph paragraph = cell.addParagraph();
        Image image = damageInfo.getImage();
        DocPicture picture = paragraph.appendPicture(image.getResPath());
        picture.setWidth(212.5f);
        File srcFile = new File(image.getResPath());
        double[] shape = CommonUtil.getImageSizeByBytes(srcFile);
        if (shape.length == 0) {
            shape = CommonUtil.getImageSizeByBufferedImage(srcFile);
        }
        double f = (Math.min(shape[0], shape[1])) / Math.max(shape[0], shape[1]);
        // 保持长宽比，设置图片的长边为212.5，短边相应缩小
        picture.setHeight((float) (212.5 * f));
        picture.setAllowOverlap(false);
        picture.setVerticalAlignment(ShapeVerticalAlignment.Center);
        picture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
        // 在下面添加一个段，用于放题注
        paragraph.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        Paragraph paragraph1 = cell.addParagraph();
        // 图片对应的题注编号，
        paragraph1.appendText(String.format("图3-%d %s %s", damageInfo.getImageIndex(), damageInfo.getStructSerialNumber(), image.generateDamageTypes()));
        paragraph1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        paragraph1.applyStyle("picCaption");
    }

    private void addTable(DamageStatistics damageStatistics, int index) {
        // 设置表头
        Paragraph paragraph2 = section.addParagraph();
        paragraph2.setText(String.format("表3-%d %s的病害详表", index, damageStatistics.getStructType()));
        paragraph2.applyStyle("tableTitle");
        List<DamageStatistics.DamageInfoInTable> damageInfos = damageStatistics.getDamageInfos();

        // 在section上添加表
        Table table = section.addTable(true);
        // 先设置好表头
        table.resetCells(damageInfos.size() + 1, TABLE_HEADERS.length);
        // 获取表头的那一行
        TableRow headerRow = table.getFirstRow();
        // 设置为表头
        headerRow.isHeader(true);
        // 设置高度
        headerRow.setHeight(40);
        // 设置高度的属性
        headerRow.setHeightType(TableRowHeightType.Exactly);
        // 获取表头行中的Cell，并设置其中的值
        for (int i = 0; i < TABLE_HEADERS.length; i++) {
            headerRow.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
            Paragraph p = headerRow.getCells().get(i).addParagraph();
            p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            TextRange range1 = p.appendText(TABLE_HEADERS[i]);
            range1.getCharacterFormat().setFontName("宋体");
            range1.getCharacterFormat().setFontSize(10f);
            range1.getCharacterFormat().setBold(true);
        }

        for (int i = 0; i < damageInfos.size(); i++) {
            DamageStatistics.DamageInfoInTable damageInfo = damageInfos.get(i);
            // 先获取表的行
            TableRow row = table.getRows().get(i + 1);
            // 损伤序号
            fillCellContent(row, 0, String.valueOf(i + 1));
            // 构件编号
            fillCellContent(row, 1, damageInfo.getStructSerialNumber());
            // 病害的类型
            fillCellContent(row, 2, damageInfo.getDamageType());
            fillCellContent(row, 3, damageInfo.getDamageStatus());
            // 图像编号
            fillCellContent(row, 4, "图 3-" + damageInfo.getImageIndex());
            // 损伤编号
            fillCellContent(row, 5, String.valueOf(damageInfo.getDamageIndex()));
        }
    }

    private void fillCellContent(TableRow row, int cellIndex, String content) {
        TableCell cell = row.getCells().get(cellIndex);
        cell.getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
        Paragraph paragraph = cell.addParagraph();
        paragraph.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        TextRange range2 = paragraph.appendText(content);
        range2.getCharacterFormat().setFontName("宋体");
        range2.getCharacterFormat().setFontSize(10f);
    }

    private void generateCoverPage() {
        Paragraph coverParagraph1 = section.addParagraph();
        log.info("正在创建报告封皮");
        coverParagraph1.appendText("\n\n\n\n");
        coverParagraph1.appendText(String.format("%s桥梁检测报告", bridge.getName()));
        coverParagraph1.applyStyle("title");

        // 设置封面的题注
        Paragraph coverParagraph2 = section.addParagraph();
        coverParagraph2.appendText("\n\n\n\n\n\n");
        coverParagraph2.appendText("中交第一公路勘察设计研究院有限公司\n");
        // 获取此时的年份和月份
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        String dateString = dateTimeFormatter.format(date);
        coverParagraph2.appendText(dateString);
        coverParagraph2.applyStyle("noteStyle");
        // 添加分页符进行分页
        coverParagraph2.appendBreak(BreakType.Page_Break);
    }

    private void addStyle() {
        log.info("添加文档格式");
        // 设置报告封皮标题的样式
        ParagraphStyle titleStyle = new ParagraphStyle(document);
        // 设置中间对齐
        titleStyle.getParagraphFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        // 设置字体
        titleStyle.getCharacterFormat().setFontSize(40f);
        titleStyle.getCharacterFormat().setFontName("宋体");
        titleStyle.getCharacterFormat().setBold(true);
        // 设置样式的名称
        titleStyle.setName("title");
        document.getStyles().add(titleStyle);

        // 设置题注的样式
        ParagraphStyle noteStyle = new ParagraphStyle(document);
        noteStyle.getCharacterFormat().setFontName("宋体");
        noteStyle.getCharacterFormat().setFontSize(18f);
        noteStyle.getParagraphFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        noteStyle.getCharacterFormat().setBold(true);
        noteStyle.getParagraphFormat().setAfterSpacing(20f);
        noteStyle.getParagraphFormat().setBeforeSpacing(20f);
        noteStyle.setName("noteStyle");
        document.getStyles().add(noteStyle);

        // 设置中号标题的样式,宋体14榜加粗
        ParagraphStyle midStyle = new ParagraphStyle(document);
        midStyle.getCharacterFormat().setFontSize(14);
        midStyle.getCharacterFormat().setBold(true);
        midStyle.getCharacterFormat().setFontName("宋体");
        midStyle.getParagraphFormat().setBeforeSpacing(20f);
        midStyle.getParagraphFormat().setAfterSpacing(20f);
        midStyle.setName("midTitle");
        document.getStyles().add(midStyle);

        // 设置正文格式，12榜宋体，行首缩进，行间距为15
        ParagraphStyle commonStyle = new ParagraphStyle(document);
        commonStyle.setName("normalStyle");
        commonStyle.getCharacterFormat().setFontName("宋体");
        commonStyle.getCharacterFormat().setFontSize(12f);
        commonStyle.getParagraphFormat().setLineSpacing(15f);
        commonStyle.getParagraphFormat().setFirstLineIndent(26f);
        commonStyle.getParagraphFormat().setHorizontalAlignment(HorizontalAlignment.Justify);
        document.getStyles().add(commonStyle);

        // 设置小标题格式12镑加粗
        ParagraphStyle smallStyle = new ParagraphStyle(document);
        smallStyle.setName("smallTitle");
        smallStyle.getParagraphFormat().setBeforeSpacing(10f);
        smallStyle.getParagraphFormat().setAfterSpacing(10f);
        smallStyle.getCharacterFormat().setFontSize(12f);
        smallStyle.getCharacterFormat().setFontName("宋体");
        smallStyle.getCharacterFormat().setBold(true);
        document.getStyles().add(smallStyle);

        // 设置表头的格式，11磅加粗
        ParagraphStyle tableTitle = new ParagraphStyle(document);
        tableTitle.setName("tableTitle");
        tableTitle.getCharacterFormat().setFontName("宋体");
        tableTitle.getCharacterFormat().setFontSize(11);
        tableTitle.getCharacterFormat().setBold(true);
        tableTitle.getParagraphFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        document.getStyles().add(tableTitle);

        // 设置图片题注的格式
        ParagraphStyle picCapStyle = new ParagraphStyle(document);
        picCapStyle.setName("picCaption");
        picCapStyle.getCharacterFormat().setFontSize(10.5f);
        picCapStyle.getCharacterFormat().setFontName("宋体");
        picCapStyle.getParagraphFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        document.getStyles().add(picCapStyle);
    }


    private List<DamageStatistics> calDamageInfo(BridgePartEnum bridgePart) {
        DamageStatistics.PartDamageStat partDamageStat = partDamageStats.getOrDefault(bridgePart.getCode(), new DamageStatistics.PartDamageStat());
        List<DamageStatistics> res = new ArrayList<>();
        // 首先获取这个部分的构件
        Map<String, DamageStatistics> map = new HashMap<>();
        List<Struct> structs = bridge.getStructs(bridgePart);
        // 统计各个构件类型的损伤
        for (Struct struct : structs) {
            String structType = struct.getStructType();
            DamageStatistics statistics;
            if (map.containsKey(structType)) {
                statistics = map.get(structType);
            } else {
                statistics = new DamageStatistics();
                statistics.setStructType(structType);
                statistics.setDamageInfos(new ArrayList<>());
                res.add(statistics);
            }
            statistics.setStructNum(statistics.getStructNum() + 1);
            List<DamageStatistics.DamageInfoInTable> damageInfos = statistics.getDamageInfos();

            // 获取构件对应的图像
            List<Image> images = struct.getImages();
            if (CollectionUtil.isEmpty(images)) {
                continue;
            }
            for (Image image : images) {
                List<Damage> damages = image.getDamages();
                if (CollectionUtil.isEmpty(damages)) {
                    continue;
                }
                int damageIndex = 1;
                for (Damage damage : damages) {

                    // 三种不同的裂缝类型
                    DamageEnum damageType = DamageEnum.getByCode(damage.getType());
                    String damageStatus;
                    if (damageType == DamageEnum.CRACK_BIAS || damageType == DamageEnum.CRACK_HORIZONTAL || damageType == DamageEnum.CRACK_VERTICAL) {
                        // 当前损害是裂缝,更新裂缝的相关信息
                        double width = damage.getWidth();
                        double length = damage.getLength();
                        statistics.setCrackNum(statistics.getCrackNum() + 1);
                        statistics.setCrackMaxWidth(Math.max(width, statistics.getCrackMaxWidth()));
                        statistics.setCrackMinWidth(Math.min(width, statistics.getCrackMinWidth()));
                        statistics.setCrackTotalLength(statistics.getLessTotalLength() + length);

                        damageStatus = String.format("L=%s,W=%s", CommonUtil.lengthFormat(length), CommonUtil.lengthFormat(width));
                        partDamageStat.setCrackNum(partDamageStat.getCrackNum() + 1);
                        if (damage.getWidth() <= DamageStatistics.WIDTH_THRESHOLD) {
                            statistics.setLessNum(statistics.getLessNum() + 1);
                            statistics.setLessTotalLength(statistics.getLessTotalLength() + length);
                            partDamageStat.setLessNum(partDamageStat.getLessNum() + 1);
                        } else {
                            statistics.setMoreNum(statistics.getMoreNum() + 1);
                            statistics.setMoreTotalLength(statistics.getMoreTotalLength() + length);
                            partDamageStat.setMoreNum(partDamageStat.getMoreNum() + 1);
                        }
                    } else if (DamageEnum.REBAR == damageType) {
                        double area = damage.getArea();
                        partDamageStat.setRebarNum(partDamageStat.getRebarNum() + 1);
                        statistics.setRebarMaxArea(Math.max(statistics.getRebarMaxArea(), area));
                        statistics.setRebarMinArea(Math.min(statistics.getRebarMinArea(), area));
                        statistics.setRebarNum(statistics.getRebarNum() + 1);
                        statistics.setRebarTotalArea(statistics.getSpallTotalArea() + area);

                        damageStatus = String.format("S=%s", CommonUtil.areaFormat(area));
                    } else {
                        double area = damage.getArea();
                        partDamageStat.setSpallNum(partDamageStat.getSpallNum() + 1);
                        statistics.setSpallMaxArea(Math.max(statistics.getRebarMaxArea(), area));
                        statistics.setSpallMinArea(Math.min(statistics.getRebarMinArea(), area));
                        statistics.setSpallNum(statistics.getRebarNum() + 1);
                        statistics.setSpallTotalArea(statistics.getSpallTotalArea() + area);

                        damageStatus = String.format("S=%s", CommonUtil.areaFormat(area));
                    }

                    // 收集损伤信息
                    DamageStatistics.DamageInfoInTable damageInfo = new DamageStatistics.DamageInfoInTable();
                    damageInfo.setDamageIndex(damageIndex++);
                    damageInfo.setDamageType(damageType.getDesc());
                    damageInfo.setStructSerialNumber(struct.getSerialNumber());
                    damageInfo.setDamageStatus(damageStatus);
                    damageInfo.setImageIndex(imageIndex);
                    damageInfo.setImage(image);
                    damageInfos.add(damageInfo);

                }
                imageIndex++;
            }

            // 将更新好的信息添加回去
            map.put(structType, statistics);
        }
        partDamageStats.put(bridgePart.getCode(), partDamageStat);
        return res;
    }


    private void addStatisticPic() throws IOException {
        log.info("正在添加统计图片");
        Paragraph paragraph4_1 = section.addParagraph();
        paragraph4_1.setText("4 病害统计分析");
        paragraph4_1.applyStyle("midTitle");
        Paragraph paragraph4_2 = section.addParagraph();
        paragraph4_2.setText("4.1 上部结构病害统计分析");
        paragraph4_2.applyStyle("smallTitle");
        Paragraph paragraph4_3 = section.addParagraph();
        paragraph4_3.appendText("1、病害类型分布柱状图");
        paragraph4_3.applyStyle("normalStyle");
        List<File> files = generateStatisticPics();
        // 添加上部的柱状图
        log.info("添加上部的柱状图");
        Paragraph paragraph4_4 = section.addParagraph();
        // 先检查是否生成了统计图片
        File topHist = files.get(0);
        if (topHist.exists()) {
            DocPicture topHistPicture = paragraph4_4.appendPicture(topHist.getAbsolutePath());
            //设置宽度和高度
            topHistPicture.setWidth(340f);
            topHistPicture.setHeight(255f);
            topHistPicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            // 添加题注
            Paragraph paragraph4_4_2 = section.addParagraph();
            paragraph4_4_2.appendText("图4-1 上部病害类型分布柱状图");
            paragraph4_4.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph4_4_2.applyStyle("picCaption");
        }
        // 添加上部的饼状图
        log.info("添加上部的饼状图");
        Paragraph paragraph4_5 = section.addParagraph();
        paragraph4_5.setText("2、裂缝分布饼状图");
        log.info("添加上部的裂缝分布图");
        paragraph4_5.applyStyle("normalStyle");
        Paragraph paragraph4_6 = section.addParagraph();
        File topPie = files.get(1);
        if (topPie.exists()) {
            DocPicture topPiePicture = paragraph4_6.appendPicture(topPie.getAbsolutePath());
            // 设置宽度和高度
            topPiePicture.setWidth(340f);
            topPiePicture.setHeight(220f);
            topPiePicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            // 添加题注
            Paragraph paragraph4_6_2 = section.addParagraph();
            paragraph4_6_2.appendText("图4-2 上部裂缝分布饼状图");
            paragraph4_6.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph4_6_2.applyStyle("picCaption");
        }
        // 添加下部
        log.info("添加下部的柱状图");
        Paragraph paragraph4_7 = section.addParagraph();
        paragraph4_7.setText("4.2 下部结构病害统计分析");
        paragraph4_7.applyStyle("smallTitle");
        Paragraph paragraph4_8 = section.addParagraph();
        paragraph4_8.appendText("1、病害类型分布柱状图");
        paragraph4_8.applyStyle("normalStyle");
        File bottomHist = files.get(2);
        Paragraph paragraph4_9 = section.addParagraph();
        if (bottomHist.exists()) {
            DocPicture bottomHistPicture = paragraph4_9.appendPicture(bottomHist.getAbsolutePath());
            bottomHistPicture.setHeight(255f);
            bottomHistPicture.setWidth(340f);
            bottomHistPicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            Paragraph paragraph4_9_2 = section.addParagraph();
            paragraph4_9_2.appendText("图4-3 下部病害类型分布柱状图");
            paragraph4_9.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph4_9_2.applyStyle("picCaption");
        }
        log.info("添加下部裂缝饼状图");
        Paragraph paragraph4_10 = section.addParagraph();
        paragraph4_10.setText("2、裂缝分布饼状图");
        paragraph4_10.applyStyle("normalStyle");
        Paragraph paragraph4_11 = section.addParagraph();
        File bottomPie = files.get(3);
        if (bottomPie.exists()) {
            DocPicture bottomPiePicture = paragraph4_11.appendPicture(bottomPie.getAbsolutePath());
            bottomPiePicture.setHeight(220f);
            bottomPiePicture.setWidth(340f);
            bottomPiePicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            Paragraph paragraph4_11_2 = section.addParagraph();
            paragraph4_11_2.appendText("图4-4 下部裂缝分布饼状图");
            paragraph4_11.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph4_11_2.applyStyle("picCaption");
        }
        log.info("添加桥面的柱状图");
        // 添加桥面
        File deckHist = files.get(4);
        Paragraph paragraph4_12 = section.addParagraph();
        paragraph4_12.setText("4.3 桥面结构病害统计分析");
        paragraph4_12.applyStyle("smallTitle");
        Paragraph paragraph4_13 = section.addParagraph();
        paragraph4_13.setText("桥面病害类型分布柱状图");
        paragraph4_13.applyStyle("normalStyle");
        Paragraph paragraph4_14 = section.addParagraph();
        if (deckHist.exists()) {
            DocPicture deckHistPicture = paragraph4_14.appendPicture(deckHist.getAbsolutePath());
            deckHistPicture.setWidth(340f);
            deckHistPicture.setHeight(220f);
            deckHistPicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
            Paragraph paragraph4_14_2 = section.addParagraph();
            paragraph4_14_2.appendText("图4-5 桥面病害类型分布柱状图");
            paragraph4_14.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            paragraph4_14_2.applyStyle("picCaption");
        }
    }


    private List<File> generateStatisticPics() throws IOException {
        List<File> res = new ArrayList<>();
        // 顶部

        for (BridgePartEnum partEnum : BridgePartEnum.values()) {
            DamageStatistics.PartDamageStat part = partDamageStats.get(partEnum.getCode());
            // 先化hist
            int[] nums = new int[]{part.getCrackNum(), part.getRebarNum(), part.getSpallNum()};
            String[] labels = new String[]{"裂缝", "锈蚀", "剥落"};
            File tmp = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + ".jpg");
            CommonUtil.drawHist(nums, labels, tmp);
            res.add(tmp);
            // 再画pie
            nums = new int[]{part.getLessNum(), part.getMoreNum()};
            labels = new String[]{"小于" + DamageStatistics.WIDTH_THRESHOLD + "mm", "大于" + DamageStatistics.WIDTH_THRESHOLD + "mm"};
            tmp = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + ".jpg");
            CommonUtil.drawPie(nums, labels, tmp);
            res.add(tmp);
        }
        return res;
    }
}

