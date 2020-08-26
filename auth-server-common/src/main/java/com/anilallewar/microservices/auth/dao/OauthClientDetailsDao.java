
package com.anilallewar.microservices.auth.dao;

import com.anilallewar.microservices.auth.model.OauthClientDetails;
import com.neoframework.common.database.MyBatisRepository;
import java.util.List;

/**
 * 处理 OAuth 相关业务的 Repository.
 *
 * <p>TODO: ahming marks: 这事实系 OauthClientDetail 的.</p>
 *
 * @author Shengzhao Li
 */
@MyBatisRepository
public interface OauthClientDetailsDao {

    /**
     * Find oauth client details oauth client details.
     *
     * @param clientId the client id
     * @return the oauth client details
     */
    OauthClientDetails findOauthClientDetails(String clientId);

    /**
     * Find all oauth client details list.
     *
     * @return the list
     */
    List<OauthClientDetails> findAllOauthClientDetails();

    /**
     * String clientId, boolean archive.
     *
     * @param clientDetails the client details
     * @return the int
     */
    int updateOauthClientDetailsArchive(OauthClientDetails clientDetails);

    /**
     * Save oauth client details int.
     *
     * @param clientDetails the client details
     * @return the int
     */
    int saveOauthClientDetails(OauthClientDetails clientDetails);
}
