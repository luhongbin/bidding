package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallKnowledgeMapper;
import org.linlinjava.litemall.db.domain.LitemallKnowledge;
import org.linlinjava.litemall.db.domain.LitemallKnowledge.Column;
import org.linlinjava.litemall.db.domain.LitemallKnowledgeExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LitemallKnowledgeService {
    Column[] columns = new Column[]{Column.id, Column.title, Column.readCount, Column.status, Column.lang, Column.isPopular, Column.knowledgeCatalogId};
    @Resource
    private LitemallKnowledgeMapper knowledgeMapper;

    /**
     * 获取热卖商品
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallKnowledge> queryByIsPopular(int offset, int limit) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andIsPopularEqualTo(true).andStatusEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return knowledgeMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallKnowledge> queryByStatus(int offset, int limit) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andIsPopularEqualTo(true).andStatusEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return knowledgeMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的商品
     *
     * @param catList
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallKnowledge> queryByCategory(List<Integer> catList, int offset, int limit) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andKnowledgeCatalogIdIn(catList).andStatusEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time  desc");
        PageHelper.startPage(offset, limit);

        return knowledgeMapper.selectByExampleSelective(example, columns);
    }


    /**
     * 获取分类下的商品
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    public List<LitemallKnowledge> queryByCategory(Integer catId, int offset, int limit) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andKnowledgeCatalogIdEqualTo(catId).andStatusEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return knowledgeMapper.selectByExampleSelective(example, columns);
    }


    public List<LitemallKnowledge> querySelective(Integer knowledgeCatalogId,  Boolean status, Boolean isPopular, Integer offset, Integer limit, String sort, String order) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        LitemallKnowledgeExample.Criteria criteria1 = example.or();
        LitemallKnowledgeExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(knowledgeCatalogId) && knowledgeCatalogId != 0) {
            criteria1.andKnowledgeCatalogIdEqualTo(knowledgeCatalogId);
            criteria2.andKnowledgeCatalogIdEqualTo(knowledgeCatalogId);
        }
        if (!StringUtils.isEmpty(status)) {
            criteria1.andStatusEqualTo(status);
            criteria2.andStatusEqualTo(status);
        }
        if (!StringUtils.isEmpty(isPopular)) {
            criteria1.andIsPopularEqualTo(isPopular);
            criteria2.andIsPopularEqualTo(isPopular);
        }

        criteria1.andStatusEqualTo(true);
        criteria2.andStatusEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(offset, limit);

        return knowledgeMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallKnowledge> querySelective(Integer Id, String title, Integer readCount, Integer status, Integer isPopular, Integer page, Integer size, String sort, String order) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        LitemallKnowledgeExample.Criteria criteria = example.createCriteria();

        if (Id != null) {
            criteria.andIdEqualTo(Id);
        }
        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return knowledgeMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    public LitemallKnowledge findById(Integer id) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return knowledgeMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    public LitemallKnowledge findByIdVO(Integer id) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andIdEqualTo(id).andStatusEqualTo(true).andDeletedEqualTo(false);
        return knowledgeMapper.selectOneByExampleSelective(example, columns);
    }


    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    public Integer queryStatus() {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andStatusEqualTo(true).andDeletedEqualTo(false);
        return (int) knowledgeMapper.countByExample(example);
    }

    public int updateById(LitemallKnowledge goods) {
        goods.setUpdateTime(LocalDateTime.now());
        return knowledgeMapper.updateByPrimaryKeySelective(goods);
    }

    public void deleteById(Integer id) {
        knowledgeMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallKnowledge goods) {
        goods.setAddTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.insertSelective(goods);
    }

    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    public int count() {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andDeletedEqualTo(false);
        return (int) knowledgeMapper.countByExample(example);
    }

    public List<Integer> getCatIds(String keywords, Boolean ispopular, Boolean status) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        LitemallKnowledgeExample.Criteria criteria1 = example.or();
        LitemallKnowledgeExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(status)) {
            criteria1.andStatusEqualTo(status);
            criteria2.andStatusEqualTo(status);
        }
        if (!StringUtils.isEmpty(ispopular)) {
            criteria1.andIsPopularEqualTo(ispopular);
            criteria2.andIsPopularEqualTo(ispopular);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria2.andTitleLike("%" + keywords + "%");
        }
        criteria1.andStatusEqualTo(true);
        criteria2.andStatusEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<LitemallKnowledge> knowledgeList = knowledgeMapper.selectByExampleSelective(example, Column.knowledgeCatalogId);
        List<Integer> cats = new ArrayList<Integer>();
        for (LitemallKnowledge knowledge : knowledgeList) {
            cats.add(knowledge.getKnowledgeCatalogId());
        }
        return cats;
    }

    public boolean checkExistByTitle(String title) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andTitleEqualTo(title).andStatusEqualTo(true).andDeletedEqualTo(false);
        return knowledgeMapper.countByExample(example) != 0;
    }

    public List<LitemallKnowledge> queryByIds(Integer[] ids) {
        LitemallKnowledgeExample example = new LitemallKnowledgeExample();
        example.or().andIdIn(Arrays.asList(ids)).andStatusEqualTo(true).andDeletedEqualTo(false);
        return knowledgeMapper.selectByExampleSelective(example, columns);
    }
}

