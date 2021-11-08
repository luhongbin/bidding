package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallQuoteModelMapper;
import org.linlinjava.litemall.db.domain.LitemallQuoteModel;
import org.linlinjava.litemall.db.domain.LitemallQuoteModelExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class LitemallQuoteModelService {
    private final LitemallQuoteModel.Column[] result = new LitemallQuoteModel.Column[]{LitemallQuoteModel.Column.id, LitemallQuoteModel.Column.name,
            LitemallQuoteModel.Column.version, LitemallQuoteModel.Column.excel, LitemallQuoteModel.Column.duty, LitemallQuoteModel.Column.code,
            LitemallQuoteModel.Column.status, LitemallQuoteModel.Column.creator, LitemallQuoteModel.Column.modify, LitemallQuoteModel.Column.updateTime};

    @Resource
    private LitemallQuoteModelMapper quoteModelMapper;

    public List<LitemallQuoteModel> queryAll() {
        LitemallQuoteModelExample example = new LitemallQuoteModelExample();
        example.or().andStatusEqualTo((int) 1).andDeletedEqualTo(false);
        return quoteModelMapper.selectByExampleWithBLOBs(example);
    }
    public List<LitemallQuoteModel> querySelective(String name, String version, Integer page, Integer limit, String sort, String order) {
        LitemallQuoteModelExample example = new LitemallQuoteModelExample();
        LitemallQuoteModelExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(version)) {
            criteria.andVersionLike("%" + version + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return quoteModelMapper.selectByExample(example);
    }

    public int updateById(LitemallQuoteModel ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return quoteModelMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        quoteModelMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallQuoteModel ad) {
        ad.setAddTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        quoteModelMapper.insertSelective(ad);
    }

    public LitemallQuoteModel findById(Integer id) {
        return quoteModelMapper.selectByPrimaryKey(id);
    }
}

