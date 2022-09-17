package puzz.xsliu.detection2.detection.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.Damage;
import puzz.xsliu.detection2.detection.entity.param.DamageParam;
import puzz.xsliu.detection2.detection.enums.DamageEnum;
import puzz.xsliu.detection2.detection.mapper.DamageMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/12:18 PM
 * @author: lxs
 */
@Service
public class DamageService {

    @Resource
    private DamageMapper damageMapper;

    public boolean batchInsert(List<Damage> damages){
        for (Damage damage : damages) {
            boolean insert = damageMapper.insert(damage) == 1;
            if (!insert){
                return false;
            }
        }
        return true;
    }


    public long count4struct(@NotNull Long structId, DamageEnum damageType){
        DamageParam param = new DamageParam();
        param.setType(damageType.getCode());
        param.setStructId(structId);
        return damageMapper.count(param);
    }

    public long count4image(@NotNull Long imageId, DamageEnum damageType){
        DamageParam param = new DamageParam();
        param.setType(damageType.getCode());
        param.setImageId(imageId);
        return damageMapper.count(param);
    }


    public List<Damage> list4image(Long imageId){
        DamageParam param = new DamageParam();
        param.setImageId(imageId);
        return damageMapper.list(param);
    }

    public List<Damage> list4bridge(Long bridgeId){
        DamageParam damageParam = new DamageParam();
        damageParam.setBridgeId(bridgeId);
        return damageMapper.list(damageParam);
    }
}
