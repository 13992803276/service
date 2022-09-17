package puzz.xsliu.detection2.detection.service;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import puzz.xsliu.detection2.detection.enums.FileTypeEnum;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/7:15 PM
 * @author: lxs
 */
@Slf4j
@Component
@SuppressWarnings("javadoc")
public class CommonFileService {

    private static final String SAVE_DIR;
    static {
        SAVE_DIR = FileUtil.file(SystemUtil.get("user.dir"), "app_folder").getAbsolutePath();
        touch(SAVE_DIR);
    }


    /**
     * 将前端上传的文件保存到服务器本地
     *
     * @param multipartFile
     * @param fileType      文件类型
     * @return
     * @throws IOException
     * @see FileTypeEnum
     */
    public File save2File(MultipartFile multipartFile, FileTypeEnum fileType) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        File saveFile = new File(SAVE_DIR);
        log.info("存储路径为{}", saveFile.getAbsolutePath());
        check(saveFile);
        saveFile = new File(saveFile.getAbsolutePath(), fileType.getSubFilePath());
        check(saveFile);
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;
        String ext = "." + FileUtil.extName(fileName);
        fileName = UUID.randomUUID() + ext;
        saveFile = new File(saveFile.getAbsolutePath(), fileName);
        log.info("文件存储路径为{}", saveFile.getAbsolutePath());
        multipartFile.transferTo(saveFile);
        return saveFile;
    }

    /**
     * 文件权限检查
     *
     * @param file
     */
    private void check(File file) {
        if (!file.exists()) {
            boolean b = file.mkdir();
            if (!b) {
                throw new RuntimeException("无法创建文件" + "\t" + file.getAbsolutePath());
            }
        }
    }

    /**
     * 获取文件输入流
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public FileInputStream getFileInputStream(String filePath) throws FileNotFoundException {
        if (filePath == null || !new File(filePath).exists()) {
            throw new FileNotFoundException("不存在对应的文件路径: " + filePath);
        }

        File file = new File(filePath);
        return new FileInputStream(file);
    }

    /**
     * 获取应用统一的文件保存路径
     *
     * @return
     */
    public File getAppFolder() {
        return new File(SAVE_DIR);
    }

    public File getImageFolder(){
        File file = new File(SAVE_DIR, FileTypeEnum.IMAGE.getSubFilePath());
        touch(file);
        return file;
    }

    /**
     * 发送到前端
     *
     * @param response 响应
     * @param file     待发送的文件
     * @throws IOException
     */
    public void send2Front(HttpServletResponse response, File file) throws IOException {
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new IOException("文件路径异常");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        send2Front(response, fileInputStream);
    }

    /**
     * 发送文件输入流到前端
     *
     * @param response    响应
     * @param inputStream 文件输入流
     * @throws IOException
     */
    public void send2Front(HttpServletResponse response, InputStream inputStream) throws IOException {
        send2Front(response, inputStream.readAllBytes());
    }

    /**
     * 发送字节数组到前端
     *
     * @param response 响应
     * @param bytes    字节数组
     * @throws IOException
     */
    public void send2Front(HttpServletResponse response, byte[] bytes) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pram", "no-cache");
        response.setDateHeader("Expires", 0);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(bytes);
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    /**
     * 删除指定的文件
     *
     * @param path
     */
    public void deleteFile(String path) {
        cn.hutool.core.io.FileUtil.del(path);
    }

    /**
     * 获取预览图,如果已经存在,则不会重复生成,判断是存在的依据为源图像名称 + _thumbnail_.jpg
     * @param srcPath 源图像路径
     * @return
     */
    public File generateThumbnail(String srcPath){
        File imageFolder = getImageFolder();
        String name = FileUtil.mainName(srcPath)  + Constants.THUMBNAIL_FIX;
        File thumbnail = new File(imageFolder, name);
        if (FileUtil.exist(thumbnail)){
            return thumbnail;
        }
        BufferedImage bfImage = ImgUtil.read(srcPath);
        java.awt.Image scale = ImgUtil.scale(bfImage, 200, 200);
        ImgUtil.write(scale, thumbnail);
        return thumbnail;
    }


    public static void touch(String dir){
        if (!FileUtil.exist(dir)){
            FileUtil.mkdir(dir);
        }
    }

    public static void touch(File file){
        touch(file.getAbsolutePath());
    }

    public File getReportFolder(){
        File file = new File(getAppFolder(), FileTypeEnum.REPORT.getSubFilePath());
        touch(file);
        return file;
    }
}
