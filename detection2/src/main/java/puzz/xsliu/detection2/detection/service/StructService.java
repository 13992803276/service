package puzz.xsliu.detection2.detection.service;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.Struct;
import puzz.xsliu.detection2.detection.entity.param.StructParam;
import puzz.xsliu.detection2.detection.mapper.StructMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/12:19 PM
 * @author: lxs
 */
@Service
@Slf4j
public class StructService {
    @Resource
    private StructMapper structMapper;

    public int count4Bridge(Long bridgeId) {
        StructParam param = new StructParam();
        param.setBridgeId(bridgeId);
        return structMapper.count(param);
    }

    public List<Struct> list(Long bridgeId){
        StructParam param = new StructParam();
        param.setBridgeId(bridgeId);
        return structMapper.list(param);
    }

    public Struct find(Long bridgeId, String serialNumber) {
        StructParam param = new StructParam();
        param.setBridgeId(bridgeId);
        param.setSerialNumber(serialNumber);
        List<Struct> list = structMapper.list(param);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() != 1) {
            log.error("同一桥梁下出现了两个相同编号的构件");
        }
        return list.get(0);
    }

    public void insert(Struct struct) {
        structMapper.insert(struct);
    }

    public Struct touch(Struct struct) {
        // 查一查是否存在
        Struct ifExist = find(struct.getBridgeId(), struct.getSerialNumber());
        if (ifExist != null) {
            // 说明已经存在该构件
            return ifExist;
        }
        insert(struct);
        return struct;
    }

}
