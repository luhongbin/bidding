package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallApproveInfoMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
@Service

public class LitemallApproveInfoService {
    @Resource
    private LitemallApproveInfoMapper approveInfoMapper;
    private LitemallApproveInfo.Column[] columns = new LitemallApproveInfo.Column[]{LitemallApproveInfo.Column.id, LitemallApproveInfo.Column.billCode, LitemallApproveInfo.Column.billName, LitemallApproveInfo.Column.billId, LitemallApproveInfo.Column.action,
            LitemallApproveInfo.Column.adminId, LitemallApproveInfo.Column.note, LitemallApproveInfo.Column.receiver, LitemallApproveInfo.Column.nextAction, LitemallApproveInfo.Column.addTime};

    public List<LitemallApproveInfo> query(Integer page, Integer limit, String sort, String order) {
        LitemallApproveInfoExample example = new LitemallApproveInfoExample();
        example.or().andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        return approveInfoMapper.selectByExampleSelective(example, columns);
    }
    public List<LitemallApproveInfo> queryRequoteApprove(Integer billId, Integer receiveId, Integer adminId, Integer billCode ) {

        LitemallApproveInfoExample example = new LitemallApproveInfoExample();
        example.setOrderByClause(LitemallRequote.Column.addTime.desc());

//        LitemallApproveInfoExample.Criteria c1 = example.createCriteria();
//        c1.andAdminIdEqualTo(adminId);
//        c1.andSourceIdEqualTo(sourceId);
//        c1.andBillCodeEqualTo(billId);
//        c1.andDeletedEqualTo(false);
//        LitemallApproveInfoExample.Criteria c2 = example.createCriteria();
//        c2.andReceiverEqualTo(receiveId);
//        c2.andSourceIdEqualTo(sourceId);
//        c1.andBillCodeEqualTo(billId);
//        c2.andDeletedEqualTo(false);
//        example.or(c2);

        return approveInfoMapper.selectByExample(example);
    }
    public List<LitemallApproveInfo> queryQuoteApprove(Integer billId, Integer billCode ) {

        LitemallApproveInfoExample example = new LitemallApproveInfoExample();
        example.setOrderByClause(LitemallRequote.Column.addTime.desc());
        LitemallApproveInfoExample.Criteria criteria = example.createCriteria();
        if (billCode > 0 ) {
            criteria.andBillIdEqualTo(billId);
            criteria.andBillCodeEqualTo(billCode);
        }
        if (billCode == 0 ) {
            criteria.andBillIdEqualTo(billId);
        }
        return approveInfoMapper.selectByExample(example);
    }
    public List<LitemallApproveInfo> query(Integer page, Integer limit) {
        return query(page, limit, null, null);
    }

    public LitemallApproveInfo findById(Integer id) {
        return approveInfoMapper.selectByPrimaryKey(id);
    }

    public List<LitemallApproveInfo> querySelective(Integer billCode, Integer billId, Integer page, Integer size, String sort, String order) {
        LitemallApproveInfoExample example = new LitemallApproveInfoExample();
        LitemallApproveInfoExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(billId)) {
            criteria.andBillIdEqualTo(billId);
        }
        if (!StringUtils.isEmpty(billCode)) {
            criteria.andBillCodeEqualTo(billCode);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return approveInfoMapper.selectByExample(example);
    }

    public int updateById(LitemallApproveInfo approveInfo) {
        approveInfo.setUpdateTime(LocalDateTime.now());
        return approveInfoMapper.updateByPrimaryKeySelective(approveInfo);
    }

    public void deleteById(Integer id) {
        approveInfoMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallApproveInfo approveInfo) {
        approveInfo.setAddTime(LocalDateTime.now());
        approveInfo.setUpdateTime(LocalDateTime.now());
        approveInfoMapper.insertSelective(approveInfo);
    }

    public List<LitemallApproveInfo> all() {
        LitemallApproveInfoExample example = new LitemallApproveInfoExample();
        example.or().andDeletedEqualTo(false);
        return approveInfoMapper.selectByExample(example);
    }
}
