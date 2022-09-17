package puzz.xsliu.detection2.detection.service;

import org.springframework.stereotype.Service;
import puzz.xsliu.detection2.detection.entity.User;
import puzz.xsliu.detection2.detection.utils.Constants;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/28/7:31 PM
 * @author: lxs
 */
@Service
public class SessionService {
    @Resource
    private HttpSession session;

    public User getCurUser(){
        return (User) session.getAttribute(Constants.USER_FLAG);
    }

    public Long getCurUserId(){
        return getCurUser().getId();
    }

    public void set(String key, String val){
        session.setAttribute(key, val);
    }

    public Object get(String key){
        return session.getAttribute(key);
    }

}
