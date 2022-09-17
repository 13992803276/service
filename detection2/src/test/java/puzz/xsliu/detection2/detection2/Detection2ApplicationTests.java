package puzz.xsliu.detection2.detection2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import puzz.xsliu.detection2.detection.Detection2Application;
import puzz.xsliu.detection2.detection.entity.User;
import puzz.xsliu.detection2.detection.mapper.UserMapper;

import javax.annotation.Resource;

@SpringBootTest(classes = Detection2Application.class)
class Detection2ApplicationTests {
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

}
