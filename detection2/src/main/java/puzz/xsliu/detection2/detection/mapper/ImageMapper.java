package puzz.xsliu.detection2.detection.mapper;

import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.entity.Image;
import puzz.xsliu.detection2.detection.entity.param.ImageParam;

import java.util.List;

/**
 * @author lxs
 * @description <a href="mailto:xsl2011@outlook.com" />
 * 2022/1/26/4:25 PM
 */
@Mapper
public interface ImageMapper {
    int insert(Image image);
    int update(Image image);
    List<Image> list(ImageParam param);
    int count(ImageParam param);
    Image find(Long imageId);
}
