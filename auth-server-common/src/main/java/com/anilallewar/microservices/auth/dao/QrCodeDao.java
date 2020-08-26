
package com.anilallewar.microservices.auth.dao;

import com.neoframework.common.auth.model.QrCode;
import com.thinkgem.jeesite.common.persistence.CrudDao;

/**
 * Created by think on 2018-07-10.
 */
@MyBatisRepository
public interface QrCodeDao extends CrudDao<QrCode> {

    /**
     * 更新 token .
     *
     * @param entity 带 id 和 token
     * @return 影响条数 int
     */
    public int updateToken(QrCode entity);
}
