package puzz.xsliu.detection2.detection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/2/1/9:56 AM
 * @author: lxs
 */
@Service
@Slf4j
public class EmailService {
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static final String SUBJECT = "[FX|Detection] 验证码";
    /**
     *  异步发送邮件
     * @param to 收件人
     * @param text 内容
     */
    @Async
    public void sendMimeMail(String to, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(SUBJECT);
            // 设置邮件内容，第二个参数设置是否支持 text/html 类型
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件时出错,", e);
        }
    }
}
