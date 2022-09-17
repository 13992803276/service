package puzz.xsliu.detection2.detection.utils;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/7:40 PM
 * @author: lxs
 */
public class Constants {
    public static final String USER_FLAG = "user";
    public static final String SP = "#";

    public static final String BOXES = "box";
    public static final String POLYGON = "polygon";

    /**
     * 摄像头的默认拍摄距离,为500毫米,即0.5米
     */
    public static final int DEFAULT_SHOT_DISTANCE = 500;
    /**
     * 摄像头的默认焦距,为6毫米
     */
    public static final int DEFAULT_FOCAL_LENGTH = 6;

    public static final String DONE_FIELD = "done";
    public static final String TOTAL_FIELD = "total";
    public static final String PROCESS_FIELD = "process";




    /**
     * redis中的桥梁key,构件key,正在根据邮件找回密码的用户等等.
     */

    public static final String PROCESSING_BRIDGE_PREFIX = "PROCESSING_BRIDGE#";


    public static final String PROCESSING_IMAGE_PREFIX = "PROCESSING_IMAGE#";

    public static final String FORGET_USER_EMAIL = "FORGET_USER_EMAIL#";

    public static final String THUMBNAIL_FIX = "_thumbnail_.jpg";
}
