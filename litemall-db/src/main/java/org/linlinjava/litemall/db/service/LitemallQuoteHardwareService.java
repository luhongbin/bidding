package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallQuoteHardwareMapper;
import org.linlinjava.litemall.db.domain.LitemallQuoteHardware;
import org.linlinjava.litemall.db.domain.LitemallQuoteHardwareExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallQuoteHardwareService {
    @Resource
    private LitemallQuoteHardwareMapper quoteHardwareMapper;

    public List<LitemallQuoteHardware> queryByGid(Integer quoteId,Boolean reId) {
        LitemallQuoteHardwareExample example = new LitemallQuoteHardwareExample();
        example.or().andQuoteIdEqualTo(quoteId).andBillIdEqualTo(reId).andDeletedEqualTo(false);
        return quoteHardwareMapper.selectByExampleWithBLOBs(example);
    }

    public void add(LitemallQuoteHardware quoteHardware) {
        quoteHardware.setAddTime(LocalDateTime.now());
        quoteHardware.setUpdateTime(LocalDateTime.now());
        quoteHardwareMapper.insertSelective(quoteHardware);
    }

    public LitemallQuoteHardware findById(Integer id) {
        return quoteHardwareMapper.selectByPrimaryKey(id);
    }

    public void deleteByGid(Integer gid) {
        LitemallQuoteHardwareExample example = new LitemallQuoteHardwareExample();
        example.or().andQuoteIdEqualTo(gid);
        quoteHardwareMapper.logicalDeleteByExample(example);
    }

    public void deleteById(Integer id) {
        quoteHardwareMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(LitemallQuoteHardware quoteHardware) {
        quoteHardware.setUpdateTime(LocalDateTime.now());
        quoteHardwareMapper.updateByPrimaryKeySelective(quoteHardware);
    }
}
