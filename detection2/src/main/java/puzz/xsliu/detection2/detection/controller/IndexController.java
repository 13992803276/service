package puzz.xsliu.detection2.detection.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * thymeleaf 模板位置解析
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/2/9:40 AM
 * @author: lxs
 */
@Controller
@RequestMapping("/common")
public class IndexController {

    @Resource
    HttpServletRequest request;


    @GetMapping("/images")
    public String toImages() {
        request.setAttribute("path", "images");
        return "images";
    }

    @GetMapping("/register")
    public String toRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String toLogin() {
        return "login";
    }

    @GetMapping("/index")
    public String toIndex() {
        request.setAttribute("path", "index");
        return "index";
    }

    @GetMapping("/reset")
    public String toReset() {
        return "reset";
    }

    @GetMapping("/forget")
    public String toForget() {
        return "forget";
    }

    @GetMapping("/detect")
    public String toDetect(){
        request.setAttribute("path", "detect");
        return "detect";
    }

    @GetMapping("/traffic")
    public String toTraffic() {
        request.setAttribute("path", "traffic");
        return "traffic";
    }


}
