package puzz.xsliu.detection2.detection.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/27/11:35 AM
 * @author: lxs
 */
@Configuration
public class LoginConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor interceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**")
                .excludePathPatterns("/dist/**")
                .excludePathPatterns("/plugins/**")
                .excludePathPatterns("/common/kaptcha")
                .excludePathPatterns("/common/register")
                .excludePathPatterns("/common/login")
                .excludePathPatterns("/common/register")
                .excludePathPatterns("/common/forget")
                .excludePathPatterns("/common/reset")
        ;
    }
}
