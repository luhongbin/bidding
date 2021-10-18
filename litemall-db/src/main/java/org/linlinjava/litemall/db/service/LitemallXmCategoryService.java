package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallXmcatalogMapper;
import org.linlinjava.litemall.db.domain.LitemallXmcatalog;
import org.linlinjava.litemall.db.domain.LitemallXmcatalogExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallXmCategoryService {
    @Resource
    private LitemallXmcatalogMapper xmcategoryMapper;
    private LitemallXmcatalog.Column[] CHANNEL = {LitemallXmcatalog.Column.id, LitemallXmcatalog.Column.name, LitemallXmcatalog.Column.iconUrl};

    public List<LitemallXmcatalog> queryL1WithoutRecommend(int offset, int limit) {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return xmcategoryMapper.selectByExample(example);
    }

    public List<LitemallXmcatalog> queryL1(int offset, int limit) {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return xmcategoryMapper.selectByExample(example);
    }

    public List<LitemallXmcatalog> queryL1() {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        return xmcategoryMapper.selectByExample(example);
    }

    public List<LitemallXmcatalog> queryByPid(Integer pid) {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        return xmcategoryMapper.selectByExample(example);
    }

    public List<LitemallXmcatalog> queryL2ByIds(List<Integer> ids) {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
        return xmcategoryMapper.selectByExample(example);
    }

    public LitemallXmcatalog findById(Integer id) {
        return xmcategoryMapper.selectByPrimaryKey(id);
    }

    public List<LitemallXmcatalog> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        LitemallXmcatalogExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return xmcategoryMapper.selectByExample(example);
    }

    public int updateById(LitemallXmcatalog category) {
        category.setUpdateTime(LocalDateTime.now());
        return xmcategoryMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteById(Integer id) {
        xmcategoryMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallXmcatalog category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        xmcategoryMapper.insertSelective(category);
    }

    public List<LitemallXmcatalog> queryChannel() {
        LitemallXmcatalogExample example = new LitemallXmcatalogExample();
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        return xmcategoryMapper.selectByExampleSelective(example, CHANNEL);
    }
}
