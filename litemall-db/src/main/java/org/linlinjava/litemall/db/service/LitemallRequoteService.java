package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.dao.LitemallRequoteMapper;

import org.linlinjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LitemallRequoteService {
    private final Log logger = LogFactory.getLog(LitemallRequoteService.class);
    LitemallRequote.Column[] columns = new LitemallRequote.Column[]{LitemallRequote.Column.id, LitemallRequote.Column.quoteId,LitemallRequote.Column.adminId, LitemallRequote.Column.adminName,
            LitemallRequote.Column.status, LitemallRequote.Column.requoteExcel, LitemallRequote.Column.submitDate, LitemallRequote.Column.deadDate,LitemallRequote.Column.addTime,
            LitemallRequote.Column.note,  LitemallRequote.Column.quoteDate, LitemallRequote.Column.quoteModelExcelSupply,LitemallRequote.Column.updateTime,LitemallRequote.Column.deleted
    };

    @Resource
    private LitemallRequoteMapper reQuoteMapper;

    public Set<Integer> queryByIds(Integer[] roleIds) {
        Set<Integer> roles = new HashSet<Integer>();
        if(roleIds.length == 0){
            return roles;
        }
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
        List<LitemallRequote> roleList = reQuoteMapper.selectByExample(example);
        for(LitemallRequote role : roleList){
            roles.add(role.getAdminId());
        }
        return roles;
    }
    public int update(LitemallRequote couponUser) {
        couponUser.setUpdateTime(LocalDateTime.now());
        return reQuoteMapper.updateByPrimaryKeySelective(couponUser);
    }

    public List<LitemallRequote> querySelectivelist(Integer adminId, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        LitemallRequoteExample.Criteria criteria = example.createCriteria();

        if (orderStatusArray != null && orderStatusArray.size() != 0) {
//            logger.info(orderStatusArray);

            criteria.andStatusIn(orderStatusArray);
        }
        if (!StringUtils.isEmpty(adminId) && adminId != 0) {
            criteria.andAdminIdEqualTo(adminId);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        logger.info("-reQuoteMapper---columns--------------");
        System.out.print("columns");
        PageHelper.startPage(page, limit);
        return reQuoteMapper.selectByExampleSelective(example, columns);
    }
    public List<LitemallRequote> querySelectiveCeolist(Integer adminId, List<Short> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        LitemallRequoteExample.Criteria criteria = example.createCriteria();

        if (orderStatusArray != null && orderStatusArray.size() != 0) {
//            logger.info(orderStatusArray);

            criteria.andStatusIn(orderStatusArray);
        }
        if (!StringUtils.isEmpty(adminId) && adminId != 0) {
            criteria.andAdminIdEqualTo(adminId);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        logger.info("-reQuoteMapper---columns--------------");
        System.out.print("columns");
        PageHelper.startPage(page, limit);
        return reQuoteMapper.selectByExampleSelective(example, columns);
    }

    public LitemallRequote findById2(Integer id) {
        logger.info("----querySelective--------------");
        logger.info(reQuoteMapper.selectByPrimaryKey(id));
        return reQuoteMapper.selectByPrimaryKeySelective(id, columns);
    }
    public LitemallRequote findById(Integer id) {
//        System.out.print("Quot  ");
        return reQuoteMapper.selectByPrimaryKey(id);
    }
    public int add(LitemallRequote role) {
        role.setAddTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        return reQuoteMapper.insertSelective(role);
    }
    public List<LitemallRequote> readQuote(Integer quoteId) {
        logger.info("quoteId readQuote ");
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andQuoteIdEqualTo(quoteId).andDeletedEqualTo(false);
        return reQuoteMapper.selectByExample(example);
    }

    public List<LitemallRequote> winQuote(Integer quoteId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        LitemallRequoteExample.Criteria criteria = example.createCriteria();
        criteria.andQuoteIdEqualTo(quoteId).andStatusEqualTo((short) 9);
        criteria.andDeletedEqualTo(false);

        return reQuoteMapper.selectByExample(example);
    }
    public void deleteById(Integer id) {
        reQuoteMapper.logicalDeleteByPrimaryKey(id);
    }

    public int updateById(LitemallRequote requote) {
        requote.setUpdateTime(LocalDateTime.now());
        return reQuoteMapper.updateByPrimaryKeySelective(requote);
    }

    public boolean checkExist(String name) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false);
        return reQuoteMapper.countByExample(example) != 0;
    }

    public List<LitemallRequote> queryAll() {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false);
        return reQuoteMapper.selectByExample(example);
    }
    public List<LitemallRequote> queryExpired() {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeadDateLessThan(LocalDateTime.now()).andStatusLessThan((short) 2).andDeletedEqualTo(false);
        return reQuoteMapper.selectByExample(example);
    }

    public List<LitemallRequote> query10Expired() {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeadDateLessThan(LocalDateTime.now()).andStatusEqualTo((short) 10).andDeletedEqualTo(false);
        return reQuoteMapper.selectByExample(example);
    }
    public int totalCount() {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false);

        return (int) reQuoteMapper.countByExample(example);
    }
    public int overCount() {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andStatusEqualTo((short) 9);
        return (int) reQuoteMapper.countByExample(example);
    }
    public int purTotalCount(Integer adminId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andAdminIdEqualTo(adminId);

        return (int) reQuoteMapper.countByExample(example);
    }

    public int purOverCount(Integer adminId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andStatusEqualTo((short) 9).andAdminIdEqualTo(adminId);
        return (int) reQuoteMapper.countByExample(example);
    }
    public int waitpurTotalCount(Integer adminId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andStatusLessThan((short)3).andAdminIdEqualTo(adminId);
        return (int) reQuoteMapper.countByExample(example);
    }
    public int mywaitTotalCount(Integer adminId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andStatusLessThan((short)6).andAdminIdEqualTo(adminId);
        return (int) reQuoteMapper.countByExample(example);
    }
    public int waitTotalCount(Integer adminId) {
        LitemallRequoteExample example = new LitemallRequoteExample();
        example.or().andDeletedEqualTo(false).andAdminIdEqualTo(adminId);
        return (int) reQuoteMapper.countByExample(example);
    }
}
