
package com.anilallewar.microservices.auth.service.impl;

// import com.monkeyk.sos.domain.dto.OauthClientDetailsDto;
// import com.monkeyk.sos.domain.oauth.OauthClientDetails;
// import com.monkeyk.sos.domain.oauth.OauthClientDetailsDao;
// import com.monkeyk.sos.service.OauthService;

import com.anilallewar.microservices.auth.dao.OauthClientDetailsDao;
import com.anilallewar.microservices.auth.model.OauthClientDetails;
import com.anilallewar.microservices.auth.model.OauthClientDetailsDto;
import com.anilallewar.microservices.auth.service.OauthService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OAuth 业务处理服务对象, 事务拦截也加在这一层
 *
 * @author Shengzhao Li
 */
@Service("oauthService")
public class OauthServiceImpl implements OauthService {

    @Autowired
    private OauthClientDetailsDao oauthRepository;

    @Override
    public OauthClientDetails loadOauthClientDetails(String clientId) {
        return oauthRepository.findOauthClientDetails(clientId);
    }

    @Override
    public List<OauthClientDetailsDto> loadAllOauthClientDetailsDtos() {
        List<OauthClientDetails> clientDetails = oauthRepository.findAllOauthClientDetails();
        return OauthClientDetailsDto.toDtos(clientDetails);
    }

    @Override
    public void archiveOauthClientDetails(String clientId) {
        OauthClientDetails entity = new OauthClientDetails();
        entity.clientId(clientId).archived(true);
        oauthRepository.updateOauthClientDetailsArchive(entity);
    }

    @Override
    public OauthClientDetailsDto loadOauthClientDetailsDto(String clientId) {
        final OauthClientDetails oauthClientDetails = oauthRepository.findOauthClientDetails(clientId);
        return oauthClientDetails != null ? new OauthClientDetailsDto(oauthClientDetails) : null;
    }

    @Override
    public void registerClientDetails(OauthClientDetailsDto formDto) {
        OauthClientDetails clientDetails = formDto.createDomain();
        oauthRepository.saveOauthClientDetails(clientDetails);
    }

}
