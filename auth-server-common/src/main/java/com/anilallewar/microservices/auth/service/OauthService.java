
package com.anilallewar.microservices.auth.service;

// import com.monkeyk.sos.domain.dto.OauthClientDetailsDto;
// import com.monkeyk.sos.domain.oauth.OauthClientDetails;

import com.anilallewar.microservices.auth.model.OauthClientDetails;
import com.anilallewar.microservices.auth.model.OauthClientDetailsDto;
import java.util.List;

/**
 * The interface Oauth service.
 *
 * @author Shengzhao Li
 */
public interface OauthService {

    /**
     * Load oauth client details oauth client details.
     *
     * @param clientId the client id
     * @return the oauth client details
     */
    OauthClientDetails loadOauthClientDetails(String clientId);

    /**
     * Load all oauth client details dtos list.
     *
     * @return the list
     */
    List<OauthClientDetailsDto> loadAllOauthClientDetailsDtos();

    /**
     * Archive oauth client details.
     *
     * @param clientId the client id
     */
    void archiveOauthClientDetails(String clientId);

    /**
     * Load oauth client details dto oauth client details dto.
     *
     * @param clientId the client id
     * @return the oauth client details dto
     */
    OauthClientDetailsDto loadOauthClientDetailsDto(String clientId);

    /**
     * Register client details.
     *
     * @param formDto the form dto
     */
    void registerClientDetails(OauthClientDetailsDto formDto);
}
