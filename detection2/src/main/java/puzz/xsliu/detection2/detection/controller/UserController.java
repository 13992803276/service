package puzz.xsliu.detection2.detection.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import puzz.xsliu.detection2.detection.entity.User;
import puzz.xsliu.detection2.detection.entity.param.BridgeParam;
import puzz.xsliu.detection2.detection.entity.param.ImageParam;
import puzz.xsliu.detection2.detection.result.Result;
import puzz.xsliu.detection2.detection.service.BridgeService;
import puzz.xsliu.detection2.detection.service.ImageService;
import puzz.xsliu.detection2.detection.service.SessionService;
import puzz.xsliu.detection2.detection.service.UserService;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 用户相关
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/10:10 AM
 * @author: lxs
 */
@RestController
@RequestMapping("/common")
public class UserController {

    @Resource
    private ImageService imageService;

    @Resource
    private BridgeService bridgeService;

    @Resource
    private SessionService sessionService;

    @Resource
    HttpSession session;
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(String password, String email){
        Result<User> res = userService.login(password, email);
        if (res.isSuccess()){
            session.setAttribute("user", res.getData());
        }
        return res;
    }


    @PostMapping("/register")
    public Result<Void> register(User user){
        return userService.register(user);
    }


    @PostMapping("/reset")
    public Result<Void> reset(@RequestParam("code") String code, @RequestParam("pass") String pass){
        return userService.resetPass(code, pass.trim());
    }

    @PostMapping("/forget")
    public Result<Void> forget(@RequestParam("email") String email){
        return userService.forgetPass(email);
    }

    @GetMapping("/logout")
    public Result<Void> logout(){
        session.removeAttribute(Constants.USER_FLAG);
        return Result.success();
    }

    @GetMapping("/indexData")
    public Object getIndexData(){
        // 获取检测的图像数量
        ImageParam param = new ImageParam();
        param.setUserId(sessionService.getCurUserId());
        param.setStructId(0L);
        param.setBridgeId(0L);
        int imgNum = imageService.count(param);
        BridgeParam bridgeParam = new BridgeParam();
        bridgeParam.setUserId(sessionService.getCurUserId());
        int bridgeNum = bridgeService.count(bridgeParam);
        JSONObject obj = new JSONObject();
        obj.put("imageNum", imgNum);
        obj.put("bridgeNum", bridgeNum);
        return obj;
    }

}
