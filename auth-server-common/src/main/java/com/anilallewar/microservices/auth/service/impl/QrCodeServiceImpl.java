
package com.anilallewar.microservices.auth.service.impl;

import com.anilallewar.microservices.auth.dao.QrCodeDao;
import com.anilallewar.microservices.auth.service.QrCodeService;
import com.neoframework.common.auth.model.QrCode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * QrCode service interface.
 */
@Service("qrCodeService")
public class QrCodeServiceImpl implements QrCodeService {

    /**
     * The Qr code dao.
     */
    @Autowired
    QrCodeDao qrCodeDao;

    /**
     * 获取单条数据.
     *
     * @param id ID
     * @return QrCode
     */
    public QrCode get(String id) {
        return qrCodeDao.get(id);
    }

    /**
     * 获取单条数据.
     *
     * @param entity 条件
     * @return QrCode
     */
    public QrCode get(QrCode entity) {
        return qrCodeDao.get(entity);
    }

    /**
     * 查询数据列表.
     *
     * @param entity 条件
     * @return QrCode列表
     */
    public List<QrCode> findList(QrCode entity) {
        return qrCodeDao.findList(entity);
    }

    /**
     * 插入数据.
     *
     * @param entity 信息
     * @return int
     */
    public int insert(QrCode entity) {
        return qrCodeDao.insert(entity);
    }

    /**
     * 更新数据.
     *
     * @param entity 信息
     * @return int
     */
    public int update(QrCode entity) {
        return qrCodeDao.update(entity);
    }

    /**
     * 更新数据.
     *
     * @param entity 信息
     * @return int
     */
    public int updateToken(QrCode entity) {
        return qrCodeDao.updateToken(entity);
    }

    /**
     * 删除数据.
     *
     * @param entity 信息
     * @return int
     */
    public int delete(QrCode entity) {
        return qrCodeDao.delete(entity);
    }
}
