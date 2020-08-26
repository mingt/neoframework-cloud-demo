
package com.neoframework.microservices.trace.dao;

import com.neoframework.biz.api.trace.model.StatCrashLog;
import com.neoframework.common.database.MyBatisRepository;

/**
 * The interface Stat crash log dao.
 */
@MyBatisRepository
public interface StatCrashLogDao {

    /**
     * 插入数据.
     *
     * @param statCrashLog 详情
     * @return int
     */
    public int insert(StatCrashLog statCrashLog);
}
