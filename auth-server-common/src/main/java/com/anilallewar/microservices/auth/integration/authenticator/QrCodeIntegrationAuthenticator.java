
package com.anilallewar.microservices.auth.integration.authenticator;

import com.anilallewar.microservices.auth.dao.UserDao;
import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.anilallewar.microservices.auth.service.QrCodeService;
import com.neoframework.common.auth.model.QrCode;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

/**
 * 二维码扫描登录认证.
 *
 * @author ahming
 * @since 2018-7-11
 **/
@Component
public class QrCodeIntegrationAuthenticator extends UsernamePasswordAuthenticator {

    /** 本类型 auth_type */
    public static final String QR_CODE_AUTH_TYPE = "qr";

    /** 安全验证的 code , 从调用 oauth/token 时传入 */
    public static final String QR_CODE_SECURITY_CODE = "s_code";

    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private UserDao userDao;

    @Override
    public void prepare(IntegrationAuthentication integrationAuthentication, ServletRequest servletRequest) {
        String qrCodeId = integrationAuthentication.getAuthParameter("password"); // 保存了 qrcode 的 id uuid
        String code = integrationAuthentication.getAuthParameter(QR_CODE_SECURITY_CODE);

        if (StringUtils.isBlank(qrCodeId) || StringUtils.isBlank(code)) {
            throw new OAuth2Exception("二维码验证有误"); // TODO: 使用更详细、准备的描述
        }
    }

    @Override
    public User authenticate(IntegrationAuthentication integrationAuthentication) {
        String qrCodeId = integrationAuthentication.getAuthParameter("password"); // 保存了 qrcode 的 id uuid
        String code = integrationAuthentication.getAuthParameter(QR_CODE_SECURITY_CODE);

        // 用户名即login_name
        String username = integrationAuthentication.getUsername();

        // 验证
        QrCode qrCode = qrCodeService.get(qrCodeId);
        if (null == qrCode || qrCode.getCode() == null || !qrCode.getCode().equals(code) || qrCode.getUsername() == null
                || !qrCode.getUsername().equals(username)) {
            throw new OAuth2Exception("二维码验证有误"); // TODO: 使用更详细、准备的描述
        }

        User queryInfo = new User();
        queryInfo.setLoginName(username);
        User user = userDao.getByLoginName(queryInfo);
        if (user == null || user.isDeleted()) { // user.archived()
            throw new UsernameNotFoundException("Not found any user for username[" + username + "] or deleted");
        }

        return user;
    }

    @Override
    public boolean support(IntegrationAuthentication integrationAuthentication) {
        return QR_CODE_AUTH_TYPE.equals(integrationAuthentication.getAuthType());
    }
}
