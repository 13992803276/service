package puzz.xsliu.detection2.detection.controller;

import cn.hutool.core.img.ImgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.entity.ImageVO;
import puzz.xsliu.detection2.detection.entity.Struct;
import puzz.xsliu.detection2.detection.entity.param.ImageParam;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.enums.FileTypeEnum;
import puzz.xsliu.detection2.detection.enums.ImageProcessEnum;
import puzz.xsliu.detection2.detection.result.PageResult;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.service.*;
import puzz.xsliu.detection2.detection.utils.CommonUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 图像接口,向前端发送图像
 *
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/11:04 AM
 * @author: lxs
 */
@Slf4j
@Controller
@RequestMapping("/common/image")
public class ImageController {

    @Resource
    private CommonFileService commonFileService;

    @Resource
    private ImageService imageService;

    @Resource
    private DamageService damageService;

    @Resource
    private SessionService sessionService;

    @Resource
    private StructService structService;

    @Resource
    private BridgeService bridgeService;

    /**
     * 单张图像检测上传接口
     *
     * @param file         图片文件
     * @param shotDistance 拍摄距离
     * @param focalLength  焦距
     */
    @PostMapping("/detect")
    @ResponseBody
    public Result<Void> detect(@RequestBody MultipartFile file,
                               @RequestParam Integer shotDistance,
                               @RequestParam Integer focalLength) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.failure("参数异常", "请传输正确的图像文件");
        }
        String originalFilename = file.getOriginalFilename();
        if (!CommonUtil.isSupportImage(originalFilename)) {
            return Result.failure("参数异常", "仅支持" + CommonUtil.getSupportFormat() + "类型的图像!");
        }
        File saveFile = commonFileService.save2File(file, FileTypeEnum.IMAGE);
        Image image = new Image();
        image.setProcess(ImageProcessEnum.WAITING.getCode());
        image.setBridgeId(0L);
        image.setStructId(0L);
        image.setStatus(0);
        image.setSrcPath(saveFile.getAbsolutePath());
        image.setFocalLength(focalLength);
        image.setShotDistance(shotDistance);
        image.setName(file.getOriginalFilename());
        image.setUserId(sessionService.getCurUserId());
        image.setGmtCreate(new Date());
        // 存储数据
        boolean success = imageService.add(image);
        if (success) {
            // 存储成功,调用检测功能
            imageService.detect(image);
            return Result.success();
        }
        return Result.failure("系统异常");
    }

    /**
     * 单张图像的删除接口
     *
     * @param id 图像ID
     */
    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = imageService.delete(id);
        if (success) {
            return Result.success();
        }
        return Result.failure("删除图像失败");
    }

    /**
     * 获取属于图像的损伤信息,各种损伤的数量几何,以及详细的细节信息
     *
     * @param id 图像ID
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public Result<ImageVO> damages(@PathVariable Long id) {
        Image image = imageService.find(id);
        ImageVO imageVO = new ImageVO();
        BeanUtils.copyProperties(image, imageVO);
        imageVO.setDamages(damageService.list4image(id));
        imageVO.generateDamageInfos();
        return Result.success(imageVO);
    }

    /**
     * 获取图像,如果检测完毕则展示结果图,否则展示原始图
     *
     * @param id 图像ID
     */
    @GetMapping(value = "/blob/{id}")
    public void blob(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Image image = imageService.find(id);
        String path = image.getResPath() == null ? image.getSrcPath() : image.getResPath();
        ImgUtil.read(path);
        commonFileService.send2Front(response, new File(path));
    }

    /**
     * 获取图像的预览图,如果没有相应的预览图,则生成一个
     *
     * @param id 图像的ID
     */
    @GetMapping(value = "/thumbnail/{id}")
    public void thumbnail(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Image image = imageService.find(id);
        String srcPath = image.getResPath() == null ? image.getSrcPath() : image.getResPath();
        File file = commonFileService.generateThumbnail(srcPath);
        commonFileService.send2Front(response, file);
    }


    /**
     * 核心接口
     *
     * @param page    当前页码
     * @param name    图像名称,提供模糊搜索
     * @param start   起始时间
     * @param process 流程过滤
     * @return 返回符合查询条件的图像
     */
    @GetMapping("/list/{page}")
    @ResponseBody
    public PageResult<Image> list(@PathVariable int page,
                                  @RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "start", required = false) String start,
                                  @RequestParam(value = "process", required = false) String process) {
        Long userId = sessionService.getCurUserId();
        ImageParam imageParam = new ImageParam();
        imageParam.buildPage(page, 12);
        imageParam.setUserId(userId);
        imageParam.setStructId(0L);
        imageParam.setBridgeId(0L);
        imageParam.setStatus(0);
        if (!CommonUtil.isBlank(name)) {
            imageParam.setName(name);
        }
        if (!CommonUtil.isBlank(start)) {
            imageParam.setStart(CommonUtil.formatFrom(start));
        }
        if (!CommonUtil.isBlank(process)) {
            imageParam.setProcess(process.trim());
        }
        // 查找
        List<Image> images = imageService.list4Single(imageParam);
        int count = imageService.count(imageParam);
        return PageResult.ofSuccess(images, count, page, 12);
    }


    /**
     * 处理桥梁维度的图像上传,每次上传会带来构件信息,
     * 如果这个图片所属的构件并未创建,则创建然后插入到数据库中,构件维度不做同步,同步在桥梁维度处理即可
     *
     * @param     图像文件
     * @param struct 构件信息
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public Result<Void> upload(MultipartFile file , Struct struct) throws IOException {
        File saveFile = commonFileService.save2File(file, FileTypeEnum.IMAGE);
        // 处理这个构件
        struct = structService.touch(struct);
        if (struct.getId() == null){
            log.error("服务器内部错误");
            return Result.failure("服务器内部错误");
        }
        // 向数据库中添加图像数据
        Image image = new Image();
        image.setSrcPath(saveFile.getAbsolutePath());
        image.setUserId(sessionService.getCurUserId());
        image.setStatus(0);
        image.setShotDistance(struct.getShotDistance());
        image.setFocalLength(struct.getFocalLength());
        image.setName(file.getOriginalFilename());
        image.setBridgeId(struct.getBridgeId());
        image.setStructId(struct.getId());
        image.setProcess(ImageProcessEnum.WAITING.getCode());
        image.setGmtCreate(new Date());
        imageService.add(image);
        // 桥梁同步
        bridgeService.onProcess(struct.getBridgeId(), BridgeProcessEnum.LOADING);
        return Result.success();
    }


}
