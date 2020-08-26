/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */

package com.thinkgem.jeesite.common.persistence;

import java.util.List;

/**
 * DAO支持类实现
 *
 * @param <T> 类型
 * @author ThinkGem
 * @version 2014 -05-16
 */
public interface CrudDao<T> extends BaseDao {

    /**
     * 获取单条数据
     *
     * @param id the id
     * @return t
     */
    public T get(String id);

    /**
     * 获取单条数据
     *
     * @param entity the entity
     * @return t
     */
    public T get(T entity);

    /**
     * 获取单条数据
     *
     * @param entity the entity
     * @return one
     */
    public T getOne(T entity);

    /**
     * 根据id获取单条数据
     *
     * @param id the id
     * @return one by id
     */
    public T getOneById(String id);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page&lt;T&gt;());
     *
     * @param entity the entity
     * @return list
     */
    public List<T> findList(T entity);

    /**
     * 查询所有数据列表
     *
     * @param entity the entity
     * @return list
     */
    public List<T> findAllList(T entity);

    /**
     * 查询所有数据列表
     *
     * @return list
     *
     * @see public List&lt;T&gt; findAllList(T entity)
     */
    @Deprecated
    public List<T> findAllList();

    /**
     * 插入数据
     *
     * @param entity the entity
     * @return int
     */
    public int insert(T entity);

    /**
     * 更新数据
     *
     * @param entity the entity
     * @return int
     */
    public int update(T entity);

    /**
     * 删除数据（一般为逻辑删除，更新del_flag字段为1）
     *
     * @param id the id
     * @return int
     *
     * @see publicint delete(T entity)
     */
    @Deprecated
    public int delete(String id);

    /**
     * 删除数据（一般为逻辑删除，更新del_flag字段为1）
     *
     * @param entity the entity
     * @return int
     */
    public int delete(T entity);

}
