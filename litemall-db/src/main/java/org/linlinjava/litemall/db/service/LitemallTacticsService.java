package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallPackingTacticsMapper;
import org.linlinjava.litemall.db.domain.LitemallPackingTactics;
import org.linlinjava.litemall.db.domain.LitemallPackingTacticsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallTacticsService {
    @Resource
    private LitemallPackingTacticsMapper tacticsMapper;

    public List<LitemallPackingTactics> queryIndex() {
        LitemallPackingTacticsExample example = new LitemallPackingTacticsExample();
        return tacticsMapper.selectByExample(example);
    }

    public List<LitemallPackingTactics> querySelective(Integer fileId, Integer page, Integer limit, String sort, String order) {
        LitemallPackingTacticsExample example = new LitemallPackingTacticsExample();
        LitemallPackingTacticsExample.Criteria criteria = example.createCriteria();


        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return tacticsMapper.selectByExample(example);
    }

    public int updateById(LitemallPackingTactics tactics) {
        tactics.setUpdateTime(LocalDateTime.now());
        return tacticsMapper.updateByPrimaryKeySelective(tactics);
    }

    public void deleteById(Integer id) {
        tacticsMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallPackingTactics tactics) {
        tactics.setAddTime(LocalDateTime.now());
        tactics.setUpdateTime(LocalDateTime.now());
        tacticsMapper.insertSelective(tactics);
    }

    public LitemallPackingTactics findById(Integer id) {
        return tacticsMapper.selectByPrimaryKey(id);
    }
}
