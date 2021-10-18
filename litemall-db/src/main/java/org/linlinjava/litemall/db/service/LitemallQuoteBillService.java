package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.dao.LitemallQuoteBillMapper;
import org.linlinjava.litemall.db.dao.QuoteMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LitemallQuoteBillService {
    private final Log logger = LogFactory.getLog(LitemallQuoteBillService.class);

    @Autowired
    private LitemallQuoteDieCastingService quoteDieCastingService;
    @Autowired
    private LitemallQuoteElectronicService quoteElectronicService;
    @Autowired
    private LitemallQuoteHardwareService quoteHardwareService;
    @Autowired
    private LitemallQuoteRubberService quoteRubberService;
    @Autowired
    private LitemallSystemConfigService systemConfigService;
    @Resource
    private LitemallQuoteBillMapper quoteBillMapper;
    @Resource
    private QuoteMapper quoteMapper;

    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<String>();
        if(roleIds.length == 0){
            return roles;
        }

        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
        List<LitemallQuoteBill> roleList = quoteBillMapper.selectByExample(example);

        for(LitemallQuoteBill role : roleList){
            roles.add(role.getPurchaser());
        }

        return roles;

    }
    public int updateWithSubmit(LitemallQuoteBill quoteBill) {
        LocalDateTime preUpdateTime = quoteBill.getUpdateTime();
        quoteBill.setUpdateTime(LocalDateTime.now());
        return quoteMapper.updateWithOptimisticLocker(preUpdateTime, quoteBill);
    }
    public List<LitemallQuoteBill> querySelective(Integer id,Integer adminId, String purchaser, String quoteSupplyName, LocalDateTime start, LocalDateTime end, List<Short> quoteStatusArray, Integer page, Integer limit, String sort, String order) {

        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.setOrderByClause(LitemallQuoteBill.Column.addTime.desc());
        LitemallQuoteBillExample.Criteria criteria = example.createCriteria();

        if (adminId != null && adminId > 0) {
            criteria.andAdminIdEqualTo(adminId);
        }

        if (id != null && id > 0) {
            criteria.andIdEqualTo(id);
        }
        if (!com.alibaba.druid.util.StringUtils.isEmpty(quoteSupplyName)) {
            criteria.andQuoteSupplyNameLike("%" + quoteSupplyName + "%");
        }

        if (!com.alibaba.druid.util.StringUtils.isEmpty(purchaser)) {
            criteria.andPurchaserLike("%" + purchaser+ "%");
        }

        if (start != null) {
            criteria.andAddTimeGreaterThanOrEqualTo(start);
        }
        if (end != null) {
            criteria.andAddTimeLessThanOrEqualTo(end);
        }

        criteria.andDeletedEqualTo(false);

        if (quoteStatusArray != null && quoteStatusArray.size() != 0) {
            criteria.andStatusIn(quoteStatusArray);
        }

        criteria.andDeletedEqualTo(false);

        if (!org.springframework.util.StringUtils.isEmpty(sort) && !org.springframework.util.StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        logger.info("----querySelective--------------"+ example);

        return quoteBillMapper.selectByExample(example);
    }
    public List<LitemallQuoteBill> quoteBillCeoService(Integer id,Integer adminId, String purchaser, String quoteSupplyName, LocalDateTime start, LocalDateTime end, List<Short> quoteStatusArray, Integer page, Integer limit, String sort, String order) {

        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.setOrderByClause(LitemallQuoteBill.Column.addTime.desc());
        LitemallQuoteBillExample.Criteria criteria = example.createCriteria();

        if (adminId != null && adminId > 0) {
            criteria.andAdminIdEqualTo(adminId);
        }
        if (id != null && id > 0) {
            criteria.andIdEqualTo(id);
        }
        if (!com.alibaba.druid.util.StringUtils.isEmpty(quoteSupplyName)) {
            criteria.andQuoteSupplyNameLike("%" + quoteSupplyName + "%");
        }

        if (!com.alibaba.druid.util.StringUtils.isEmpty(purchaser)) {
            criteria.andPurchaserLike("%" + purchaser+ "%");
        }

        if (start != null) {
            criteria.andAddTimeGreaterThanOrEqualTo(start);
        }
        if (end != null) {
            criteria.andAddTimeLessThanOrEqualTo(end);
        }

        criteria.andDeletedEqualTo(false);

        if (quoteStatusArray != null && quoteStatusArray.size() != 0) {
            criteria.andStatusIn(quoteStatusArray);
        }

        criteria.andDeletedEqualTo(false);

        if (!org.springframework.util.StringUtils.isEmpty(sort) && !org.springframework.util.StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        logger.info("----querySelective--------------"+ example);

        return quoteBillMapper.selectByExample(example);
    }

    public LitemallQuoteBill findById(Integer id) {
        System.out.print("Quot  ");
        return quoteBillMapper.selectByPrimaryKey(id);
    }

    public void add(LitemallQuoteBill role) {
        role.setAddTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        quoteBillMapper.insertSelective(role);
    }

    public void deleteById(Integer id) {
        quoteBillMapper.logicalDeleteByPrimaryKey(id);
        quoteDieCastingService.deleteByGid(id);
        quoteElectronicService.deleteByGid(id);
        quoteHardwareService.deleteByGid(id);
        quoteRubberService.deleteByGid(id);
    }

    public int updateById(LitemallQuoteBill quoteBill) {
        quoteBill.setUpdateTime(LocalDateTime.now());
        return quoteBillMapper.updateByPrimaryKeySelective(quoteBill);
    }

    public boolean checkExist(String name) {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false);
        return quoteBillMapper.countByExample(example) != 0;
    }

    public List<LitemallQuoteBill> queryAll() {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false);
        return quoteBillMapper.selectByExample(example);
    }

    public List<LitemallQuoteBill> queryExpired() {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andStatusEqualTo((short) 1).andDeadDateLessThan(LocalDateTime.now()).andStatusLessThan((short) 2).andDeletedEqualTo(false);
        return quoteBillMapper.selectByExample(example);
    }
    public List<LitemallQuoteBill> query8Expired() {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andStatusEqualTo((short) 8).andDeadDateLessThan(LocalDateTime.now()).andDeletedEqualTo(false);
        return quoteBillMapper.selectByExample(example);
    }
    public int totalCount() {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false);

        return (int) quoteBillMapper.countByExample(example);
    }
    public int overCount() {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false).andStatusEqualTo((short) 6);
        return (int) quoteBillMapper.countByExample(example);
    }
    public int purTotalCount(Integer adminId) {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false).andAdminIdEqualTo(adminId);
        return (int) quoteBillMapper.countByExample(example);
    }

    public int purOverCount(Integer adminId) {
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andDeletedEqualTo(false).andStatusEqualTo((short) 8).andAdminIdEqualTo(adminId);
        return (int) quoteBillMapper.countByExample(example);
    }
}
