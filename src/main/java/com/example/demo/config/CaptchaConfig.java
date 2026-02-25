package com.example.demo.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Google Captcha验证码配置
 */
@Configuration
public class CaptchaConfig {

    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 是否有边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 验证码文本字符颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 验证码图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 验证码图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 验证码文本字符大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // 验证码文本字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 验证码文本字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        // 验证码文本字符字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        // 验证码噪点颜色
        properties.setProperty("kaptcha.noise.color", "black");
        // 验证码背景渐变色开始
        properties.setProperty("kaptcha.background.clear.from", "lightGray");
        // 验证码背景渐变色结束
        properties.setProperty("kaptcha.background.clear.to", "white");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}