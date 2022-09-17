package puzz.xsliu.detection2.detection.mapper;

import org.apache.ibatis.annotations.Mapper;
import puzz.xsliu.detection2.detection.entity.User;

import java.util.List;


/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/3:18 PM
 * @author: lxs
 */
@Mapper
public interface UserMapper {
    int insert(User user);
    int update(User user);
    User findById(Long userId);
    User findByEmail(String email);
}
