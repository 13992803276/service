package puzz.xsliu.detection2.detection2.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import puzz.xsliu.detection2.detection.Detection2Application;
import puzz.xsliu.detection2.detection.entity.User;
import puzz.xsliu.detection2.detection.mapper.UserMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/4:09 PM
 * @author: lxs
 */
@SpringBootTest(classes = Detection2Application.class)
public class UserTest  {
    @Resource
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testInsert(){
        User user = new User();
        user.setEmail("xml@gmail.com");
        user.setNick("nickname");
        user.setPassword("ppgod1234");
        user.setSalt("123456");
        int result = userMapper.insert(user);
        System.out.println(result);
    }

    @Test
    void testUpdate(){
        User user = new User();
        user.setId(1L);
        user.setPassword("123456");
        int res = userMapper.update(user);
        System.out.println(res);
    }

    @Test
    void testSelect(){
        User u = userMapper.findByEmail("xml@gmail.com");
        System.out.println(u);
    }

}
