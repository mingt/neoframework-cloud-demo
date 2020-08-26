
package com.anilallewar.microservices.auth.service;

import com.neoframework.common.auth.model.QrCode;
import java.util.List;

/**
 * QrCode service interface.
 */
public interface QrCodeService {

    /**
     * 获取单条数据.
     *
     * @param id the id
     * @return qr code
     */
    public QrCode get(String id);

    /**
     * 获取单条数据.
     *
     * @param entity the entity
     * @return qr code
     */
    public QrCode get(QrCode entity);

    /**
     * 查询数据列表.
     *
     * @param entity the entity
     * @return list
     */
    public List<QrCode> findList(QrCode entity);

    /**
     * 插入数据.
     *
     * @param entity the entity
     * @return int
     */
    public int insert(QrCode entity);

    /**
     * 更新数据.
     *
     * @param entity the entity
     * @return int
     */
    public int update(QrCode entity);

    /**
     * 更新 token .
     *
     * @param entity 带 id 和 token
     * @return 影响条数 int
     */
    public int updateToken(QrCode entity);

    /**
     * 删除数据.
     *
     * @param entity the entity
     * @return int
     */
    public int delete(QrCode entity);
}
