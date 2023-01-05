package com.czm.wormforwb.service.impl;

import com.czm.wormforwb.service.EmailSendService;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

/**
 * 邮件发送服务
 *
 * @author Slience
 * @date 2022/3/10 13:32
 **/
@Service
@Slf4j
public class EmailSendServiceImpl implements EmailSendService {

    @Value("${sender.email}")
    private Boolean emailSender;

    //邮箱服务
    @Value("${email.server}")
    private String emailServer;

    //邮箱使用的协议，通常为smtp
    @Value("${email.agreement}")
    private String emailAgreement;

    //发送者
    @Value("${email.sender.address}")
    private String senderEmail;

    //邮箱授权码
    @Value("${email.sender.authorization.code}")
    private String authorizationCode;

    @Value("${email.getter.address}")
    private String getterEmail;


    @Override
    public Boolean sendEmail(String title, String content) {
        // 未配置 或者配置是不发送
        if (ObjectUtils.isEmpty(emailSender) || !emailSender) {
            return false;
        }

        log.debug("------邮件服务开始------");

        //创建一个配置文件并保存
        Properties properties = new Properties();

        properties.setProperty("mail.host", emailServer);

        properties.setProperty("mail.transport.protocol", emailAgreement);

        properties.setProperty("mail.smtp.auth", "true");

        try {
            //QQ存在一个特性设置SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //创建一个session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, authorizationCode);
                }
            });

            //开启debug模式
            session.setDebug(true);

            //获取连接对象
            Transport transport = session.getTransport();

            //连接服务器
            transport.connect(emailServer, senderEmail, authorizationCode);

            //创建邮件对象
            MimeMessage mimeMessage = new MimeMessage(session);

            //邮件发送人
            mimeMessage.setFrom(new InternetAddress(senderEmail));

            //邮件接收人
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(getterEmail));

            //邮件标题
            mimeMessage.setSubject(title);

            //邮件内容
            mimeMessage.setContent(content, "text/html;charset=UTF-8");

            //发送邮件
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

            //关闭连接
            transport.close();
            log.debug("------邮件发送成功，服务结束------");
            return true;
        } catch (Exception e) {
            log.error("邮件发送抛出异常：" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }
}
