package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallXmcatalog;
import org.linlinjava.litemall.db.domain.LitemallXmcatalogExample;

public interface LitemallXmcatalogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    long countByExample(LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int insert(LitemallXmcatalog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int insertSelective(LitemallXmcatalog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    LitemallXmcatalog selectOneByExample(LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    LitemallXmcatalog selectOneByExampleSelective(@Param("example") LitemallXmcatalogExample example, @Param("selective") LitemallXmcatalog.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    List<LitemallXmcatalog> selectByExampleSelective(@Param("example") LitemallXmcatalogExample example, @Param("selective") LitemallXmcatalog.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    List<LitemallXmcatalog> selectByExample(LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    LitemallXmcatalog selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallXmcatalog.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    LitemallXmcatalog selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    LitemallXmcatalog selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallXmcatalog record, @Param("example") LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallXmcatalog record, @Param("example") LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallXmcatalog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallXmcatalog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallXmcatalogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_xmcatalog
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}