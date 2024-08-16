package com.demo.core.authentication;

import com.google.code.kaptcha.Producer;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * token验证
 */
@Getter
@Setter
public class TokenSecurityContextHolderFilter extends GenericFilterBean {

    private final TokenManager tokenManager;
    private final String captchaUrl;
    private final Producer captchaProducer;

    public TokenSecurityContextHolderFilter(TokenManager tokenManager, String captchaUrl, Producer captchaProducer) {
        this.tokenManager = tokenManager;
        this.captchaUrl = captchaUrl;
        this.captchaProducer = captchaProducer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = ((HttpServletRequest) request).getRequestURI();
        //验证码请求
        if (uri.startsWith(captchaUrl) && !uri.replaceAll(captchaUrl, "").contains("/")) {
            // 生成验证码文本并存储到session
            String capText = captchaProducer.createText();
            httpRequest.getSession().setAttribute(WebSecurityConfig.VERIFICATION_CODE_SESSION_KEY, capText.toLowerCase());
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            // 设定返回给客户端的内容类型
            httpResponse.setContentType("image/jpg");
            // 禁止浏览器缓存此图片
            httpResponse.setHeader("Pragma", "No-cache");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setDateHeader("Expires", 0);
            // 将验证码图片写入到response的输出流中
            ServletOutputStream out = httpResponse.getOutputStream();
            // 生成图片验证码
            BufferedImage image = captchaProducer.createImage(capText);
            ImageIO.write(image, "jpg", out);
            try {
                out.flush();
            } finally {
                out.close();
            }
            return;
        }
        // 从请求头中获取Token
        String reqToken = httpRequest.getHeader(WebSecurityConfig.HTTP_HEADER_AUTHORIZATION);

        // 如果没有Token或Token为空，则继续过滤器链
        if (StringUtils.isBlank(reqToken)) {
            chain.doFilter(request, response);
            return;
        }

        // 尝试通过Token获取Authentication对象
        Authentication authentication = tokenManager.getAuthenticationByToken(reqToken);
        if (authentication == null || !checkAuthentication(request, authentication)) {
            chain.doFilter(request, response);
            return;
        } else {
            //有效token，为其续期
            tokenManager.delayExpired(reqToken);
        }

        request.setAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_AUTHENTICATION, authentication);
        request.setAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS, authentication.getDetails());
        // 设置SecurityContextHolder中的Authentication对象

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    /**
     * 详细校验权限
     * 预留给重写
     *
     * @param request
     * @param authentication
     * @return
     */
    protected boolean checkAuthentication(ServletRequest request, Authentication authentication) {
        return true;
    }

}