package puzz.xsliu.detection2.detection.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import puzz.xsliu.detection2.detection.enums.DamageEnum;
import puzz.xsliu.detection2.detection.utils.CommonUtil;

import java.util.List;
import java.util.Objects;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/2/10:28 AM
 * @author: lxs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageVO extends Image{
    private int crack;
    private int rebar;
    private int spall;

    public void generateDamageInfos() {
        List<Damage> damages = getDamages();
        if (damages == null) {
            return;
        }
        crack = rebar = spall = 0;
        for (Damage damage : damages) {
            String type = damage.getType();
            if (StrUtil.contains(type, DamageEnum.CRACK.getCode())) {
                crack += 1;

            } else if (Objects.equals(type, DamageEnum.REBAR.getCode())) {
                rebar += 1;

            } else {
                spall += 1;

            }
        }
    }
}
