package com.demo.sys.controller;

import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.ResetRootPassword;
import com.demo.sys.service.AuthUserService;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 认证/用户相关
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthUserService authUserService;

    @PostMapping("/resetRootPassword")
    public ApiHttpResponse<AuthUserCache> resetRootPassword(@RequestBody ResetRootPassword resetRootPassword) {
        authUserService.resetRootPassword(resetRootPassword);
        return ApiHttpResponse.success(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }

//    @Resource
//    private Producer captchaProducer;
//
//    /**
//     * 使用kaptcha生成一个4位数的验证码并绑定在session中，并返回给前端图片
//     * kaptcha 有一个安全漏洞，生成随机数时没有用安全的Random，不过这个是验证码函数，我觉得没有影响
//     *
//     * @param req
//     * @param resp
//     * @throws IOException
//     */
//    @RequestMapping("/captcha")
//    public void captcha(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        // 生成验证码文本并存储到session
//        String capText = captchaProducer.createText();
//        req.getSession().setAttribute(WebSecurityConfig.VERIFICATION_CODE_SESSION_KEY, capText);
//        // 设定返回给客户端的内容类型
//        resp.setContentType("image/jpg");
//        // 禁止浏览器缓存此图片
//        resp.setHeader("Pragma", "No-cache");
//        resp.setHeader("Cache-Control", "no-cache");
//        resp.setDateHeader("Expires", 0);
//        // 将验证码图片写入到response的输出流中
//        ServletOutputStream out = resp.getOutputStream();
//        // 生成图片验证码
//        BufferedImage image = captchaProducer.createImage(capText);
//        ImageIO.write(image, "jpg", out);
//        try {
//            out.flush();
//        } finally {
//            out.close();
//        }
//    }

}
