package puzz.xsliu.detection2.detection.service;

import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.User;
import puzz.xsliu.detection2.detection.mapper.UserMapper;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.utils.CommonUtil;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Objects;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/6:16 PM
 * @author: lxs
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private EmailService emailService;

    @Resource
    private SessionService sessionService;
    public Result<User> login(String password, String email){
        User user = userMapper.findByEmail(email);
        if (user == null){
            return Result.failure("该邮箱未注册");
        }
        String str = CommonUtil.MD5Encoding(password, user.getSalt());
        if (str.equals(user.getPassword())){
            return Result.success(user);
        }else{
            return Result.failure("账号密码不匹配");
        }
    }

    public Result<Void> register(User toRegister){
        // 首先根据email来查找是否存在重复
        String email = toRegister.getEmail();
        User user = userMapper.findByEmail(email);
        if (user != null){
            return Result.failure("该邮箱已注册请直接登录!");
        }
        String salt = CommonUtil.getSalt();
        toRegister.setPassword(CommonUtil.MD5Encoding(toRegister.getPassword(), salt));
        toRegister.setSalt(salt);
        boolean success = userMapper.insert(toRegister) == 1;
        if (success){
            return Result.success();
        }
        return Result.failure("注册失败请稍后重试");
    }

    private String generateMailContent(String email, String verifyCode){
        // 问候行
        return "亲爱的用户" + "「<strong>" + email + "</strong>」" + "<br>" +
                // 文本行
                "        " + "您好!您现在正在重置密码,如果操作不是您发起的,请忽略此邮件.您的验证码为: " +
                "<strong>" + verifyCode + "</strong>,此验证码15分钟内有效." + "<br>" +
                "        " + "感谢您使用我们的产品,祝好!";
    }

    /**
     * 忘记密码,使用邮箱重设密码
     * @param email 邮箱
     * @return
     */
    public Result<Void> forgetPass(String email){
        // 找这个用户是否存在
        User user = userMapper.findByEmail(email);
        if (user == null){
            return Result.failure("该邮箱尚未注册");
        }
        String key = Constants.FORGET_USER_EMAIL + email;
        // 生成验证码
        String verifyCode = CommonUtil.getRandomString(6).toUpperCase(Locale.ROOT);
        // 将验证码和key存储到redis中,验证码15分钟有效
        redisService.set(key, verifyCode, 15 * 60);
        sessionService.set("email", email);
        String content = generateMailContent(email, verifyCode);
        // 发邮件
        emailService.sendMimeMail(email, content);
        return Result.success();
    }

    public Result<Void> resetPass(String verifyCode, String newPass){
        String email = (String) sessionService.get("email");
        // 先查用户
        User user = userMapper.findByEmail(email);
        if (user == null){
            return Result.failure("该邮箱尚未注册");
        }
        // 查验证码
        String key = Constants.FORGET_USER_EMAIL + email;
        String code = (String) redisService.get(key);
        if (!Objects.equals(verifyCode, code)){
            return Result.failure("验证码无效");
        }
        String salt = user.getSalt();
        user.setPassword(CommonUtil.MD5Encoding(newPass, salt));
        userMapper.update(user);
        return Result.success();
    }



}
