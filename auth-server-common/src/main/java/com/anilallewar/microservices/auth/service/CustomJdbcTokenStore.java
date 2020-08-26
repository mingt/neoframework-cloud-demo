
package com.anilallewar.microservices.auth.service;

import com.neoframework.common.api.ApiErrorConstant;
import com.neoframework.common.api.exception.InternalErrorException;
import com.thinkgem.jeesite.common.utils.Reflections;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

// import static com.monkeyk.sos.infrastructure.CacheConstants.ACCESS_TOKEN_CACHE;
// import static com.monkeyk.sos.infrastructure.CacheConstants.REFRESH_TOKEN_CACHE;

/**
 * 扩展默认的 TokenStore, 增加对缓存的支持。
 *
 * @author Shengzhao Li 2016/7/26
 * @author ahming 暂时去掉 cache, 针对并发问题和refresh问题做处理 2018/7/18
 */
// @Service("tokenStore")
public class CustomJdbcTokenStore extends JdbcTokenStore {

    Logger logger = LoggerFactory.getLogger(CustomJdbcTokenStore.class);

    public static final String ACCESS_TOKEN_CACHE = "accessToken";
    public static final String REFRESH_TOKEN_CACHE = "refreshToken";

    public CustomJdbcTokenStore(DataSource dataSource) {
        super(dataSource);
    }

    private static final String SELECT_INVALID_REFRESH_TOKEN =
            "SELECT ort.token_id,ort.token,ort.authentication FROM oauth_refresh_token ort "
                + "LEFT JOIN oauth_access_token oat ON ort.token_id = oat.refresh_token WHERE oat.create_time IS NULL";

    private String selectInvalidRefreshTokenSql = SELECT_INVALID_REFRESH_TOKEN;

    @Override
    // 不需要也不能加 //@CacheEvict(cacheNames = ACCESS_TOKEN_CACHE, key="#token.getValue()")
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        logger.info("storeAccessToken");
        try {
            super.storeAccessToken(token, authentication);
        } catch (Exception e) {
            logger.warn("storeAccessToken maybe Duplicate entry", e);

            String msg = e.getMessage();
            if (StringUtils.contains(msg, "Duplicate entry")) {
                // TODO： 暂定：这个直接忽略就可以，因为重复，也未过期，不需要额外处理
                // TODO： ---> 设法改为 update，待测试确认
                // 因为父类不开放 JdbcTemplate ，所以使用它来操作数据库不可行，试先 remove 再插入；而且要清楚，例如 refresh_token
                // 时，是不带有旧的已有 token 的信息，所以要去查出来
                String clientId = authentication.getOAuth2Request().getClientId();
                String userName = authentication.isClientOnly() ? null : authentication.getName();
                if (StringUtils.isBlank(userName) || StringUtils.isBlank(clientId)) {
                    // 因为无法处理，所以仍然向上抛出异常
                    throw new InternalErrorException(ApiErrorConstant.CommonError.INTERNAL_ERROR,
                            ApiErrorConstant.CommonError.INTERNAL_ERROR_MSG);
                }
                Collection<OAuth2AccessToken> tokens = findTokensByClientIdAndUserName(clientId, userName);
                for (OAuth2AccessToken tokenOne : tokens) {
                    removeAccessToken(tokenOne);
                }
                try {
                    super.storeAccessToken(token, authentication); // 重试
                    logger.warn("storeAccessToken solved ok");
                } catch (Exception e1) {
                    logger.warn("storeAccessToken solved not ok");
                    // 因为无法处理，所以仍然向上抛出异常
                    throw new InternalErrorException(ApiErrorConstant.CommonError.INTERNAL_ERROR,
                            ApiErrorConstant.CommonError.INTERNAL_ERROR_MSG);
                }

            }
            // else {
            // // 其他的暂时不处理，直接丢弃
            // }
        }
    }

    @Override
    // 不需要也不能加 //@CacheEvict(cacheNames = REFRESH_TOKEN_CACHE, key="#refreshToken.getValue()")
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        logger.info("storeRefreshToken");
        try {
            super.storeRefreshToken(refreshToken, authentication);
        } catch (Exception e) {
            logger.warn("storeRefreshToken maybe Duplicate entry", e);

            String msg = e.getMessage();
            if (StringUtils.contains(msg, "Duplicate entry")) {
                logger.warn("storeRefreshToken do Duplicate entry");
            }
            // else {
            // // 其他的暂时不处理，直接丢弃
            // }
        }
    }

    // @Cacheable(cacheNames = ACCESS_TOKEN_CACHE, key="#tokenValue")
    // public OAuth2AccessToken readAccessToken(String tokenValue) {
    // logger.info("readAccessToken");
    // return super.readAccessToken(tokenValue);
    // }
    //
    // @CacheEvict(value = ACCESS_TOKEN_CACHE, key = "#tokenValue")
    // public void removeAccessToken(String tokenValue) {
    // logger.info("removeAccessToken");
    // super.removeAccessToken(tokenValue);
    // }
    //
    // @Cacheable(value = REFRESH_TOKEN_CACHE, key = "#token")
    // public OAuth2RefreshToken readRefreshToken(String token) {
    // logger.info("readRefreshToken");
    // return super.readRefreshToken(token);
    // }
    //
    // @CacheEvict(value = REFRESH_TOKEN_CACHE, key = "#token")
    // public void removeRefreshToken(String token) {
    // logger.info("removeRefreshToken");
    // super.removeRefreshToken(token);
    // }

    /**
     * 获取oauth_refresh_token表的所有垃圾数据
     *
     * @return
     */
    public List<OAuth2RefreshToken> getInvalidRefreshTokens() {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (null != jdbcTemplate) {
            List<OAuth2RefreshToken> refreshTokens =
                    jdbcTemplate.query(selectInvalidRefreshTokenSql, new RowMapper<OAuth2RefreshToken>() {

                        public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return deserializeRefreshToken(rs.getBytes(2));
                        }
                    });

            return refreshTokens;
        }

        return null;
    }

    /**
     * 通过反射获取jdbcStore里的jdbcTemplate
     *
     * @return
     */
    private JdbcTemplate getJdbcTemplate() {
        try {
            Field field = Reflections.getAccessibleField(this, "jdbcTemplate");
            JdbcTemplate jdbcTemplate = (JdbcTemplate) field.get(this);
            return jdbcTemplate;
        } catch (Exception e) {
            logger.error("getJdbcTemplate error=>{}", e);
        }

        return null;
    }
}
