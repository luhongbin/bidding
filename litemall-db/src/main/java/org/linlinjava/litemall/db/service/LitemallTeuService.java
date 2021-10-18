package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallTeuMapper;
import org.linlinjava.litemall.db.domain.LitemallTeu;
import org.linlinjava.litemall.db.domain.LitemallTeuExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallTeuService {
    @Resource
    private LitemallTeuMapper teuMapper;

    public List<LitemallTeu> queryByOid(Integer userId) {
        LitemallTeuExample example = new LitemallTeuExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return teuMapper.selectByExample(example);
    }

    public List<LitemallTeu> querySelective(String username, String teuTypeCn, String teuNameCn,Integer page, Integer limit, String sort, String order) {
        LitemallTeuExample example = new LitemallTeuExample();
        LitemallTeuExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(teuNameCn)) {
            criteria.andTeuTypeCnLike("%" + teuTypeCn + "%");
        }
        if (!StringUtils.isEmpty(teuNameCn)) {
            criteria.andTeuNameCnLike("%" + teuNameCn + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return teuMapper.selectByExample(example);
    }

    public int updateById(LitemallTeu teu) {
        teu.setUpdateTime(LocalDateTime.now());
        return teuMapper.updateByPrimaryKeySelective(teu);
    }

    public void deleteById(Integer id) {
        teuMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallTeu teu) {
        teu.setAddTime(LocalDateTime.now());
        teu.setUpdateTime(LocalDateTime.now());
        teuMapper.insertSelective(teu);
    }

    public LitemallTeu findById(Integer id) {
        return teuMapper.selectByPrimaryKey(id);
    }
}
