package puzz.xsliu.detection2.detection.mapper;

import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.entity.Damage;
import puzz.xsliu.detection2.detection.entity.param.DamageParam;

import java.util.List;

/**
 * @author lxs
 * @description <a href="mailto:xsl2011@outlook.com" />
 * 2022/1/26/5:18 PM
 */
@Mapper
public interface DamageMapper {
    int insert(Damage damageDO);
    int update(Damage damageDO);
    List<Damage> list(DamageParam param);
    Damage find(Long id);
    long count(DamageParam param);
}
