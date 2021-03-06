package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallLike;
import org.linlinjava.litemall.db.domain.LitemallLikeExample;

public interface LitemallLikeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    long countByExample(LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int insert(LitemallLike record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int insertSelective(LitemallLike record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    LitemallLike selectOneByExample(LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    LitemallLike selectOneByExampleSelective(@Param("example") LitemallLikeExample example, @Param("selective") LitemallLike.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    List<LitemallLike> selectByExampleSelective(@Param("example") LitemallLikeExample example, @Param("selective") LitemallLike.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    List<LitemallLike> selectByExample(LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    LitemallLike selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallLike.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    LitemallLike selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallLike record, @Param("example") LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallLike record, @Param("example") LitemallLikeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallLike record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_like
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallLike record);
}