package puzz.xsliu.detection2.detection.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.Bridge;
import puzz.xsliu.detection2.detection.entity.Damage;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.entity.Struct;
import puzz.xsliu.detection2.detection.entity.param.BridgeParam;
import puzz.xsliu.detection2.detection.entity.param.DamageParam;
import puzz.xsliu.detection2.detection.entity.param.ImageParam;
import puzz.xsliu.detection2.detection.enums.BridgeProcessEnum;
import puzz.xsliu.detection2.detection.enums.DamageEnum;
import puzz.xsliu.detection2.detection.mapper.BridgeMapper;
import puzz.xsliu.detection2.detection.process.ReportGenerator;
import puzz.xsliu.detection2.detection.process.sync.BridgeSync;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 桥梁维度的服务
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/11:56 AM
 * @author: lxs
 */
@Slf4j
@Service
public class BridgeService{

    @Resource
    private RedisService redisService;
    @Resource
    private BridgeMapper bridgeMapper;

    @Resource
    private DamageService damageService;
    @Resource
    private StructService structService;
    @Resource
    private ImageService imageService;

    @Resource
    private CommonFileService commonFileService;

    @Resource
    private SessionService sessionService;

    public List<Bridge> list(BridgeParam param) {
        List<Bridge> bridges = bridgeMapper.list(param);
        // 对每个桥梁获取其构件数量和图像数量
        if (CollectionUtil.isEmpty(bridges)) {
            return new ArrayList<>();
        }
        bridges.forEach(bridge -> {
            bridge.setImageNum(imageService.count4Bridge(bridge.getId()));
            bridge.setStructNum(structService.count4Bridge(bridge.getId()));
        });
        return bridges;
    }

    public Bridge detail(Long id) {
        Bridge bridge = bridgeMapper.find(id);
        if (bridge == null) {
            return null;
        }
        long crack = 0, rebar = 0, spall = 0;
        List<Damage> damages = damageService.list4bridge(bridge.getId());
        if (!CollectionUtil.isEmpty(damages)){
            for (Damage damage : damages) {
                String type = damage.getType();
                if (Objects.equals(DamageEnum.REBAR.getCode(), type)){
                    rebar += 1;
                }else if (Objects.equals(type, DamageEnum.SPALL.getCode())){
                    spall += 1;
                }
                crack += 1;
            }
        }
        // 填充其他信息
        // 填充三种损伤的数量
        List<Long> counts = new ArrayList<>();
        counts.add(crack);
        counts.add(rebar);
        counts.add(spall);
        bridge.setCounts(counts);
        // 填构件数量
        bridge.setStructNum(structService.count4Bridge(id));
        // 填图像数量
        bridge.setImageNum(imageService.count4Bridge(bridge.getId()));
        return bridge;
    }

    /**
     * 检测流程的起始点,当图像上传完毕之后,调用detect方法进行检测
     * @param id 桥梁ID
     */
    public void detect(Long id) {
        // 获取对应的图像
        List<Image> images = imageService.listBridgeImages(id);
        if (CollectionUtil.isEmpty(images)){
            return;
        }
        images.forEach(image -> {
            // 进行检测
            imageService.detect(image);
        });

    }


    public Bridge find(Long id) {
        return bridgeMapper.find(id);
    }


    public Result<Bridge> init(Bridge bridge) {
        bridge.setGmtCreate(new Date());
        bridge.setUserId(sessionService.getCurUserId());
        bridge.setProcess(BridgeProcessEnum.LOADING.getCode());
        boolean success = bridgeMapper.insert(bridge) == 1;
        if (!success) {
            return Result.failure("插入数据库失败", "初始化桥梁信息失败!");
        }
        // 在缓存中新增同步信息
        BridgeSync sync = new BridgeSync();
        sync.setDone(0);
        sync.setTotal(bridge.getImageNum());
        sync.setProcess(BridgeProcessEnum.LOADING.getCode());
        sync.setId(bridge.getId());
        success = syncToCache(sync);
        if (!success) {
            return Result.failure("同步数据初始化错误", "初始化桥梁信息失败!");
        }
        return Result.success(bridge);
    }

    public void update(Bridge update) {
        bridgeMapper.update(update);
    }


    /**
     * 每次检测并量化完一张图像,会调用这个方法进行同步
     *
     * @param bridgeId 桥梁ID
     * @param process  当前进程, 仅三个进程需要进行同步,即LOADED, DETECTED, QUANTIFIED
     */
    public synchronized void onProcess(Long bridgeId, BridgeProcessEnum process) {
        if (!process.needSync()) {
            log.error("该进程不需要进行同步:{},桥梁ID={}", process, bridgeId);
            return;
        }
        // 从缓存中获取同步对象
        BridgeSync sync = syncFromCache(bridgeId);
        if (sync == null) {
            // 说明桥梁没有被正确的初始化,初始化时会向缓存中写入同步对象
            log.error("桥梁没有正确初始化!桥梁ID={}", bridgeId);
            return;
        }
        // 查看状态,是否和当前状态一致
        if (!Objects.equals(sync.getProcess(), process.getCode())) {
            log.error("同步消息和当前状态不一致, 桥梁ID={}", bridgeId);
            log.error("同步状态为{}, 入参传入的进程状态为{}", sync.getProcess(), process.getCode());
            return;
        }
        sync.setDone(sync.getDone() + 1);
        if (sync.getDone() == sync.getTotal()) {
            process = process.next();
            // 当前流程已经结束,更新数据库
            Bridge bridge = new Bridge();
            bridge.setId(bridgeId);
            bridge.setProcess(process.getCode());
            update(bridge);
            // 更新同步信息
            sync.setProcess(process.getCode());
            sync.setDone(0);
            // 开始检测
            if (process == BridgeProcessEnum.DETECTING){
                detect(bridgeId);
            }
            // 开始生成检测报告
            if (process == BridgeProcessEnum.GENERATING){
                generateReport(bridgeId);
            }
        }
        syncToCache(sync);

    }


    /**
     * 获取桥梁同步信息在redis中的key
     */
    private String getRedisKey(@NotNull Long id) {
        return Constants.PROCESSING_BRIDGE_PREFIX + id;
    }

    /**
     * 从缓存当中获取同步对象
     *
     * @param id 桥梁ID
     */
    public BridgeSync syncFromCache(@NotNull Long id) {
        String key = getRedisKey(id);
        Map<Object, Object> map = redisService.hmget(key);
        if (CollectionUtil.isEmpty(map)) {
            return null;
        }
        BridgeSync sync = new BridgeSync();
        sync.setDone(Convert.toInt(map.get(Constants.DONE_FIELD), 0));
        sync.setId(id);
        sync.setTotal(Convert.toInt(map.get(Constants.TOTAL_FIELD), 0));
        sync.setProcess(Convert.toStr(map.get(Constants.PROCESS_FIELD)));
        return sync;
    }

    /**
     * 将同步对象修改到缓存当中
     *
     * @param sync 同步对象
     */
    public boolean syncToCache(BridgeSync sync) {
        Map<String, Object> map = new HashMap<>();
        String key = getRedisKey(sync.getId());
        if (Objects.equals(sync.getProcess(), BridgeProcessEnum.GENERATING.getCode())) {
            // 同步已经结束,在缓存中删除同步信息
            redisService.del(key);
            // 开始生成检测报告

            return true;
        }
        map.put(Constants.TOTAL_FIELD, sync.getTotal());
        map.put(Constants.DONE_FIELD, sync.getDone());
        map.put(Constants.PROCESS_FIELD, sync.getProcess());

        return redisService.hmset(key, map);
    }


    public void generateReport(@NotNull Long bridgeId){
        // 首先查找桥梁
        Bridge bridge = bridgeMapper.find(bridgeId);
        if (bridge == null){
            log.error("待生成报告的桥梁不存在,ID为{}", bridgeId);
            return;
        }
        // 向bridge中填充图像和构件,注意,需要看图像受存在损伤,不存在损伤直接过滤掉
        prepare(bridge);
        ReportGenerator generator = new ReportGenerator(bridge);
        generator.generate();
        File reportFile = new File(commonFileService.getReportFolder(), UUID.randomUUID() + ".docx");
        generator.saveToLocalFile(reportFile);
        // 更新桥
        bridge.setProcess(BridgeProcessEnum.FINISHED.getCode());
        bridge.setReportPath(reportFile.getAbsolutePath());
        update(bridge);
    }

    public void add(Bridge bridge){
        bridgeMapper.insert(bridge);
    }


    public void prepare(Bridge bridge){
        if (bridge == null){
            return;
        }
        // 获取所有的构件
        List<Struct> structs = structService.list(bridge.getId());
        List<Struct> structList = new ArrayList<>();
        List<Image> imageList = new ArrayList<>();
        if (CollectionUtil.isEmpty(structs)){
            bridge.setStructs(structList);
        }
        ImageParam param = new ImageParam();
        param.setBridgeId(bridge.getId());
        for (Struct struct: structs){
            // 获取对应的图像
            param.setStructId(struct.getId());
            List<Image> images = imageService.list(param);
            if (CollectionUtil.isEmpty(images)){
                // 说明这个构件下没有图像
                log.error("ID为{}的构件下无对应的图像", struct.getId());
                continue;
            }
            // 对所有的图像设置对应的损伤
            images.forEach(image ->image.setDamages(damageService.list4image(image.getId())));
            // 按照损伤的数量对图像进行过滤
            List<Image> tmp = images.stream()
                    .filter(image -> !CollectionUtil.isEmpty(image.getDamages()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(tmp)){
                continue;
            }
            imageList.addAll(tmp);
            struct.setImages(tmp);
            structList.add(struct);
        }

        bridge.setStructs(structList);
        bridge.setImages(imageList);
    }

    public int count(BridgeParam param){
        return bridgeMapper.count(param);
    }
}
