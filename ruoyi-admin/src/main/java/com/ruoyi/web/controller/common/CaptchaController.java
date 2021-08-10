package com.ruoyi.web.controller.common;

import com.google.code.kaptcha.Producer;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.sign.Base64;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 生成的验证信息存放到redis缓存中
     */
    @Autowired
    private RedisCache redisCache;

    // 验证码类型
    @Value("${ruoyi.captchaType}")
    private String captchaType;

    @Autowired
    private ISysConfigService configService;

    /**
     * 生成验证码
     * 验证码接口：
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException {
        //创建一个响应信息对象
        AjaxResult ajax = AjaxResult.success();
        //获取验证码开关
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        ajax.put("captchaOnOff", captchaOnOff);
        //如果验证码开关是false（则表示没有使用验证码）,则直接返回
        if (!captchaOnOff) {
            return ajax;
        }

        // 生成一个uuid
        String uuid = IdUtils.simpleUUID();
        // 拼接前缀
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 如果设置的验证码类型是math数组计算类型
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            System.out.println("captchaProducerMath.createText()方法生成的字符串:" + capText);
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            //生成图片
            image = captchaProducerMath.createImage(capStr);
        }
        // 如果设置的验证码类型是char字符验证类型
        else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }
}
