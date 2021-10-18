package org.linlinjava.litemall.admin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.linlinjava.litemall.admin.dto.Quoteinone;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminQuoteBillService {
    private final Log logger = LogFactory.getLog(AdminQuoteBillService.class);

    @Autowired
    private LitemallQuoteDieCastingService quoteDieCastingService;
    @Autowired
    private LitemallQuoteElectronicService quoteElectronicService;
    @Autowired
    private LitemallQuoteHardwareService quoteHardwareService;
    @Autowired
    private LitemallQuoteRubberService quoteRubberService;
    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LitemallApproveInfoService approveInfoService;
    @Autowired
    private LitemallRequoteService reQuoteService;
    @Autowired
    private LitemallQuoteModelService quoteModelService;
    @Autowired
    private LitemallQuoteBillService quoteBillService;

    private Object validate(Quoteinone quoteinone) {
        LitemallQuoteBill quoteBill = quoteinone.getQuoteBill();
        Integer name = quoteBill.getModelName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        LitemallQuoteDieCasting[] attributes = quoteinone.getQuoteDieCasting();
        for (LitemallQuoteDieCasting attribute : attributes) {
            String attr = attribute.getName();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = attribute.getCode();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        LitemallQuoteElectronic[] specifications = quoteinone.getQuoteElectronic();
        for (LitemallQuoteElectronic specification : specifications) {
            String attr = specification.getName();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = specification.getCode();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        LitemallQuoteHardware[] products = quoteinone.getQuoteHardware();
        for (LitemallQuoteHardware product : products) {
            String attr = product.getName();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = product.getCode();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        LitemallQuoteRubber[] quoteRubbers = quoteinone.getQuoteRubber();
        for (LitemallQuoteRubber quoteRubber : quoteRubbers) {
            String attr = quoteRubber.getName();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = quoteRubber.getCode();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }
        return null;
    }

    /**
     * 编辑商品
     *
     * NOTE：
     * 由于商品涉及到四个表，特别是litemall_goods_product表依赖litemall_goods_specification表，
     * 这导致允许所有字段都是可编辑会带来一些问题，因此这里商品编辑功能是受限制：
     * （1）litemall_goods表可以编辑字段；
     * （2）litemall_goods_specification表只能编辑pic_url字段，其他操作不支持；
     * （3）litemall_goods_product表只能编辑price, number和url字段，其他操作不支持；
     * （4）litemall_goods_attribute表支持编辑、添加和删除操作。
     *
     * NOTE2:
     * 前后端这里使用了一个小技巧：
     * 如果前端传来的update_time字段是空，则说明前端已经更新了某个记录，则这个记录会更新；
     * 否则说明这个记录没有编辑过，无需更新该记录。
     *
     * NOTE3:
     * （1）购物车缓存了一些商品信息，因此需要及时更新。
     * 目前这些字段是goods_sn, goods_name, price, pic_url。
     * （2）但是订单里面的商品信息则是不会更新。
     * 如果订单是未支付订单，此时仍然以旧的价格支付。
     */
    @Transactional
    public Object update(Quoteinone quoteinone) {
        LitemallQuoteBill quoteBill = quoteinone.getQuoteBill();
        Integer modelId = quoteBill.getModelName();
//        logger.info("modelIdmodelId:"+modelId);
        if (quoteBillService.updateById(quoteBill) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        if (modelId == 4) {
            LitemallQuoteDieCasting[] quoteDieCastings = quoteinone.getQuoteDieCasting();
            for (LitemallQuoteDieCasting quoteDieCasting : quoteDieCastings) {
                if (quoteDieCasting.getId() == null || quoteDieCasting.getId().equals(0)){
                    quoteDieCasting.setQuoteId(quoteBill.getId());
                    quoteDieCasting.setBillId(false);
                    quoteDieCasting.setStatus((short) 3);
                    quoteDieCastingService.add(quoteDieCasting);
                }
                else if(quoteDieCasting.getDeleted()){
                    quoteDieCastingService.deleteById(quoteDieCasting.getId());
                }
                else if(quoteDieCasting.getUpdateTime() == null){
                    quoteDieCasting.setStatus((short) 3);
                    quoteDieCastingService.updateById(quoteDieCasting);
                }
            }
        }
        if (modelId == 6) {
            LitemallQuoteElectronic[] quoteElectronics = quoteinone.getQuoteElectronic();
            for (LitemallQuoteElectronic quoteElectronic : quoteElectronics) {
                if (quoteElectronic.getId() == null || quoteElectronic.getId().equals(0)){
                    quoteElectronic.setQuoteId(quoteBill.getId());
                    quoteElectronic.setBillId(false);
                    quoteElectronic.setStatus((short) 3);
                    quoteElectronicService.add(quoteElectronic);
                }
                else if(quoteElectronic.getDeleted()){
                    quoteElectronicService.deleteById(quoteElectronic.getId());
                }
                else if(quoteElectronic.getUpdateTime() == null){
                    quoteElectronic.setStatus((short) 3);
                    quoteElectronicService.updateById(quoteElectronic);
                }
            }
        }
        if (modelId == 5) {
            LitemallQuoteHardware[] quoteHardwares = quoteinone.getQuoteHardware();
            for (LitemallQuoteHardware quoteHardware : quoteHardwares) {
                if (quoteHardware.getId() == null || quoteHardware.getId().equals(0)){
                    quoteHardware.setQuoteId(quoteBill.getId());
                    quoteHardware.setBillId(false);
                    quoteHardware.setStatus((short) 3);
                    quoteHardwareService.add(quoteHardware);
                }
                else if(quoteHardware.getDeleted()){
                    quoteHardwareService.deleteById(quoteHardware.getId());
                }
                else if(quoteHardware.getUpdateTime() == null){
                    quoteHardware.setStatus((short) 3);
                    quoteHardwareService.updateById(quoteHardware);
                }
            }
        }
        if (modelId == 3) {
            LitemallQuoteRubber[] quoteRubbers = quoteinone.getQuoteRubber();
            for (LitemallQuoteRubber quoteRubber : quoteRubbers) {
                if (quoteRubber.getId() == null || quoteRubber.getId().equals(0)){
                    quoteRubber.setQuoteId(quoteBill.getId());
                    quoteRubber.setBillId(false);
                    quoteRubber.setStatus((short) 3);
                    quoteRubberService.add(quoteRubber);
                }
                else if(quoteRubber.getDeleted()){
                    quoteRubberService.deleteById(quoteRubber.getId());
                }
                else if(quoteRubber.getUpdateTime() == null){
                    quoteRubberService.updateById(quoteRubber);
                }
            }
        }


        // 商品基本信息表litemall_goods
        if (quoteBillService.updateById(quoteBill) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        return ResponseUtil.ok();
    }

    @Transactional
    public Object updateRequote(Quoteinone quoteinone) {
        LitemallRequote requote = quoteinone.getReQuote();
        if (reQuoteService.updateById(requote) == 0) {
            throw new RuntimeException("更新数据失败");
        }
        LitemallQuoteBill quoteBill = quoteinone.getQuoteBill();
        Integer modelId = quoteBill.getModelName();
//        logger.info("modelIdrequote:"+modelId);

        BigDecimal a = new BigDecimal (0);
        if (modelId == 4) {
            LitemallQuoteDieCasting[] quoteDieCastings = quoteinone.getQuoteDieCasting();
            for (LitemallQuoteDieCasting quoteDieCasting : quoteDieCastings) {
                if (quoteDieCasting.getProcessingCharge().compareTo(a) == 1) {
                    quoteDieCasting.setStatus((short) 5);
                    quoteDieCastingService.updateById(quoteDieCasting);
                }
            }
        }
        if (modelId == 6) {
            LitemallQuoteElectronic[] quoteElectronics = quoteinone.getQuoteElectronic();
            for (LitemallQuoteElectronic quoteElectronic : quoteElectronics) {
                if (quoteElectronic.getPrice().compareTo(a) == 1) {
                    quoteElectronic.setStatus((short) 5);
                    quoteElectronicService.updateById(quoteElectronic);
                }
            }
        }
        if (modelId == 5) {
            LitemallQuoteHardware[] quoteHardwares = quoteinone.getQuoteHardware();
            for (LitemallQuoteHardware quoteHardware : quoteHardwares) {
               if (quoteHardware.getPrice().compareTo(a) == 1) {
                   quoteHardware.setStatus((short) 5);
                   quoteHardwareService.updateById(quoteHardware);
               }
            }
        }
        if (modelId == 3) {
            LitemallQuoteRubber[] quoteRubbers = quoteinone.getQuoteRubber();
            for (LitemallQuoteRubber quoteRubber : quoteRubbers) {
              if (quoteRubber.getMouldCharge().compareTo(a) == 1) {
                  quoteRubber.setStatus((short) 5);
                  quoteRubberService.updateById(quoteRubber);
              }
            }
        }
        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(LitemallQuoteBill quoteBill) {
        Integer id = quoteBill.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = quoteBill.getId();
        quoteDieCastingService.deleteByGid(gid);
        quoteElectronicService.deleteByGid(gid);
        quoteHardwareService.deleteByGid(gid);
        quoteRubberService.deleteByGid(gid);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(Quoteinone quoteinone) {
        LitemallQuoteBill quoteBill = quoteinone.getQuoteBill();
        Integer modelId = quoteBill.getModelName();

        quoteBillService.add(quoteBill);
        if (modelId == 4) {
            LitemallQuoteDieCasting[] quoteDieCastings = quoteinone.getQuoteDieCasting();
            for (LitemallQuoteDieCasting quoteDieCasting : quoteDieCastings) {
                quoteDieCasting.setBillId(false);
                quoteDieCasting.setStatus((short) 3);
                quoteDieCasting.setQuoteId(quoteBill.getId());
                quoteDieCastingService.add(quoteDieCasting);
            }
        }
        if (modelId == 6) {
            LitemallQuoteElectronic[] quoteElectronics = quoteinone.getQuoteElectronic();
            for (LitemallQuoteElectronic quoteElectronic : quoteElectronics) {
                quoteElectronic.setBillId(false);
                quoteElectronic.setQuoteId(quoteBill.getId());
                quoteElectronic.setStatus((short) 3);
                quoteElectronicService.add(quoteElectronic);
            }
        }
        if (modelId == 5) {
            LitemallQuoteHardware[] quoteHardwares = quoteinone.getQuoteHardware();
            for (LitemallQuoteHardware quoteHardware : quoteHardwares) {
                quoteHardware.setBillId(false);
                quoteHardware.setQuoteId(quoteBill.getId());
                quoteHardware.setStatus((short) 3);
                quoteHardwareService.add(quoteHardware);
            }
        }
        if (modelId == 3) {
            LitemallQuoteRubber[] quoteRubbers = quoteinone.getQuoteRubber();
            for (LitemallQuoteRubber quoteRubber : quoteRubbers) {
                quoteRubber.setBillId(false);
                quoteRubber.setStatus((short) 3);
                quoteRubber.setQuoteId(quoteBill.getId());
                quoteRubberService.add(quoteRubber);
            }
        }
        return ResponseUtil.ok();
    }
    public Object find(Integer id, Integer modelId) {
//        logger.info("find:"+String.valueOf(id)+String.valueOf(modelId));

        Map<String, Object> data = new HashMap<>();
        if (modelId == 4) {
            LitemallQuoteDieCasting quoteDieCastings = quoteDieCastingService.findById(id);
            data.put("detail", quoteDieCastings);
        }
        if (modelId == 6) {
            LitemallQuoteElectronic quoteElectronics = quoteElectronicService.findById(id);
            data.put("detail", quoteElectronics);
        }
        if (modelId == 5) {
            LitemallQuoteHardware quoteHardwares = quoteHardwareService.findById(id);
            data.put("detail", quoteHardwares);
        }
        if (modelId == 3) {
            LitemallQuoteRubber quoteRubbers = quoteRubberService.findById(id);
            data.put("detail", quoteRubbers);
        }
        return ResponseUtil.ok(data);
    }
    public Object detail(Integer id) {
        Map<String, Object> data = new HashMap<>();

        LitemallQuoteBill quoteBills = quoteBillService.findById(id);
        Integer modelId = quoteBills.getModelName();
//        logger.info("modelId:"+String.valueOf(modelId));

        if (modelId == 4) {
            List<LitemallQuoteDieCasting> quoteDieCastings = quoteDieCastingService.queryByGid(id, false);
            data.put("detail", quoteDieCastings);

            List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);
            List<Map<String, Object>> redetail = new ArrayList<>();
            List<Map<String, Object>> redetailpart = new ArrayList<>();

            for (LitemallRequote reQuote : reQuotes) {
                List<LitemallQuoteDieCasting> quoteDieCastingsr  = quoteDieCastingService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteDieCasting role : quoteDieCastingsr) {
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode()+':'+role.getName() + ':'+role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("size", role.getSize());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("moldNumber", role.getMoldNumber());
                    quoteModel.put("cavityMaterial", role.getCavityMaterial());
                    quoteModel.put("looseCore", role.getLooseCore());
                    quoteModel.put("feedingMode", role.getFeedingMode());
                    quoteModel.put("deviceType", role.getDeviceType());
                    quoteModel.put("deadline", role.getDeadline());
                    quoteModel.put("moldTime", role.getMoldTime());
                    quoteModel.put("mouldCharge", role.getMouldCharge());
                    quoteModel.put("note1", role.getNote1());
                    quoteModel.put("material", role.getMaterial());
                    quoteModel.put("processingCharge", role.getProcessingCharge());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("adminId", reQuote.getAdminId());
                    quoteModel.put("quoteDate", reQuote.getQuoteDate());
                    quoteModel.put("qnote", reQuote.getNote());
                    quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("quoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("submitDate", reQuote.getSubmitDate());
                    quoteModel.put("deadDate", reQuote.getDeadDate());
                    quoteModel.put("qstatus", reQuote.getStatus());
                    redetail.add(quoteModel);
                    if (role.getStatus() == 2 || role.getStatus() == 0) {
                        redetailpart.add(quoteModel);
                    }
                }
            }
            data.put("redetail", redetail);
            data.put("redetailpart", redetailpart);
        }
        if (modelId == 6) {
            List<LitemallQuoteElectronic> quoteElectronics = quoteElectronicService.queryByGid(id, false);
            data.put("detail", quoteElectronics);

            List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);
            List<Map<String, Object>> redetail = new ArrayList<>();
            List<Map<String, Object>> redetailpart = new ArrayList<>();

            for (LitemallRequote reQuote : reQuotes) {
                List<LitemallQuoteElectronic> quoteElectronicr  = quoteElectronicService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteElectronic role : quoteElectronicr) {
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode()+':'+role.getName() + ':'+role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("price", role.getPrice());
                    quoteModel.put("delivery", role.getDelivery());
                    quoteModel.put("moq", role.getMoq());
                    quoteModel.put("mpq", role.getMpq());
                    quoteModel.put("packageSize", role.getPackageSize());
                    quoteModel.put("brand", role.getBrand());
                    quoteModel.put("certificate", role.getCertificate());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("adminId", reQuote.getAdminId());
                    quoteModel.put("quoteDate", reQuote.getQuoteDate());
                    quoteModel.put("qnote", reQuote.getNote());
                    quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("quoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("submitDate", reQuote.getSubmitDate());
                    quoteModel.put("deadDate", reQuote.getDeadDate());
                    quoteModel.put("qstatus", reQuote.getStatus());
                    redetail.add(quoteModel);
                    if (role.getStatus() == 2 || role.getStatus() == 0) {
                        redetailpart.add(quoteModel);
                    }
                }
            }
            data.put("redetail", redetail);
            data.put("redetailpart", redetailpart);
        }
        if (modelId == 5) {
            List<LitemallQuoteHardware> quoteHardwares = quoteHardwareService.queryByGid(id, false);
            data.put("detail", quoteHardwares);

            List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);
            List<Map<String, Object>> redetail = new ArrayList<>();
            List<Map<String, Object>> redetailpart = new ArrayList<>();

            for (LitemallRequote reQuote : reQuotes) {
                List<LitemallQuoteHardware> quoteHardwarer  = quoteHardwareService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteHardware role : quoteHardwarer) {
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode()+':'+role.getName() + ':'+role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("materialCharge", role.getMaterialCharge());
                    quoteModel.put("materialPerCharge", role.getMaterialPerCharge());
                    quoteModel.put("processingCharge", role.getProcessingCharge());
                    quoteModel.put("electroplateCharge", role.getElectroplateCharge());
                    quoteModel.put("otherCharge", role.getOtherCharge());
                    quoteModel.put("price", role.getPrice());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("adminId", reQuote.getAdminId());
                    quoteModel.put("quoteDate", reQuote.getQuoteDate());
                    quoteModel.put("qnote", reQuote.getNote());
                    quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("quoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("submitDate", reQuote.getSubmitDate());
                    quoteModel.put("deadDate", reQuote.getDeadDate());
                    quoteModel.put("qstatus", reQuote.getStatus());
                    redetail.add(quoteModel);
                    if (role.getStatus() == 2 || role.getStatus() == 0) {
                        redetailpart.add(quoteModel);
                    }
                }
            }
            data.put("redetail", redetail);
            data.put("redetailpart", redetailpart);
        }
        if (modelId == 3) {
            List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(id, false);
            data.put("detail", quoteRubbers);
//            logger.info("reQuotes:");

            List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);

            List<Map<String, Object>> redetail = new ArrayList<>();
            List<Map<String, Object>> redetailpart = new ArrayList<>();

            for (LitemallRequote reQuote : reQuotes) {
                List<LitemallQuoteRubber> quoteRubberr  = quoteRubberService.queryByGid(reQuote.getId(), true);
//                logger.info(reQuote);
//                logger.info(quoteRubberr);

                for (LitemallQuoteRubber role : quoteRubberr) {
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode()+':'+role.getName() + ':'+role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("material", role.getMaterial());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("deviceType", role.getDeviceType());
                    quoteModel.put("looseCore", role.getLooseCore());
                    quoteModel.put("materialPrice", role.getMaterialPrice());
                    quoteModel.put("moldNumber", role.getMoldNumber());
                    quoteModel.put("processingCostSingle", role.getProcessingCostSingle());
                    quoteModel.put("pieceWeight", role.getPieceWeight());
                    quoteModel.put("mouldCharge", role.getMouldCharge());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("adminId", reQuote.getAdminId());
                    quoteModel.put("quoteDate", reQuote.getQuoteDate());
                    quoteModel.put("qnote", reQuote.getNote());
                    quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("quoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("submitDate", reQuote.getSubmitDate());
                    quoteModel.put("deadDate", reQuote.getDeadDate());
                    quoteModel.put("qstatus", reQuote.getStatus());
                    redetail.add(quoteModel);
                    if (role.getStatus() == 2 || role.getStatus() == 0) {
                        redetailpart.add(quoteModel);
                    }
                }
            }
            data.put("redetail", redetail);
            data.put("redetailpart", redetailpart);
        }
//        logger.info("detail:");

        LitemallQuoteModel quoteModelId = quoteModelService.findById(modelId);
        data.put("quoteModelId", quoteModelId.getAppendix());

        LitemallAdmin admin = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();

        List<LitemallAdmin> adminlist = adminService.queryAll();
        List<Map<String, Object>> adminlists = new ArrayList<>(adminlist.size());
        for (LitemallAdmin adminname : adminlist) {
            Map<String, Object> adminnames = new HashMap<>();
            adminnames.put("value", adminname.getId());
            adminnames.put("label", adminname.getNickname());
            adminnames.put("username", adminname.getUsername());
            adminnames.put("dept", adminname.getDept());
            adminnames.put("deptname", adminname.getDept()+':'+adminname.getNickname()+':'+adminname.getId());
            adminnames.put("dd", adminname.getUsername());
            adminnames.put("jobNumber", adminname.getJobnumber());
            adminnames.put("capacity", adminname.getCapacity());
            adminlists.add(adminnames);
        }

        List<LitemallQuoteModel> rolist = quoteModelService.queryAll();
        List<Map<String, Object>> quoteModels = new ArrayList<>(rolist.size());
        for (LitemallQuoteModel role : rolist) {
            Map<String, Object> quoteModel = new HashMap<>();
            quoteModel.put("value", role.getId());
            quoteModel.put("label", role.getName()+':'+role.getId());
            quoteModel.put("note", role.getAppendix());
            quoteModel.put("supply", role.getCode());
            quoteModel.put("notice", role.getNotice());
            quoteModel.put("approveCode", role.getApproveCode());
            quoteModel.put("ceoCode", role.getCeoCode());
            quoteModel.put("duty", role.getDuty());
            quoteModels.add(quoteModel);
        }
        data.put("currentUser", admin);
        data.put("optionsAdmin", adminlists);
        data.put("quoteBills", quoteBills);
        data.put("quoteModel", quoteModels);
        return ResponseUtil.ok(data);
    }

    public Object totalGoogs(Integer id,Integer adminId, Integer supplyId, String codeId,  List<Short> status) {
        Map<String, Object> data = new HashMap<>();
        LitemallAdmin admin = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();
        data.put("currentUser", admin);
        List<Map<String, Object>> quoteDieCastinglist = new ArrayList<>();
        List<Map<String, Object>> quoteElectroniclist = new ArrayList<>();
        List<Map<String, Object>> quoteHardwarelist = new ArrayList<>();
        List<Map<String, Object>> quoteRubberlist = new ArrayList<>();
        List<LitemallQuoteBill> quoteBillList = quoteBillService.queryAll();
        data.put("quoteBills", quoteBillList);

        for (LitemallQuoteBill quoteBills : quoteBillList) {
            if (!StringUtils.isEmpty(adminId) && adminId != 0) {
                if (quoteBills.getAdminId() != adminId) { continue; }
            }
            Integer modelId = quoteBills.getModelName();
//            System.out.print(quoteBills.toString());
//            System.out.print(modelId);

//            if (id == 1 && quoteBills.getAdminId() != admin.getId()) { continue; }
            List<LitemallRequote> reQuotes = reQuoteService.readQuote(quoteBills.getId());
//            System.out.print("readQuote:" + reQuotes.toString());

//            System.out.print(reQuotes.toString());

            for (LitemallRequote reQuote : reQuotes) {
                if (!StringUtils.isEmpty(supplyId) && supplyId != 0) {
                    if (reQuote.getAdminId() != supplyId) { continue; }
                }

//                logger.info(reQuote.getId());
                if (modelId == 4) {
//                    System.out.print("4");

//                    List<LitemallQuoteDieCasting> quoteDieCastings = quoteDieCastingService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteDieCasting", quoteDieCastings);
                    List<LitemallQuoteDieCasting> quoteDieCastingsr = quoteDieCastingService.queryByGid(reQuote.getId(), true);
                    for (LitemallQuoteDieCasting role : quoteDieCastingsr) {
                        if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                        if (id == 2 && role.getStatus()!=0) { continue; }
                        if (status != null && status.size() != 0) {
                            if (status.contains(Short.valueOf(role.getStatus()))==false) { continue; }
                        }
                        if (!StringUtils.isEmpty(codeId)) {
                            if (!codeId.equals(role.getCode()) ) { continue; }
                        }
                        Map<String, Object> quoteModel = new HashMap<>();
                        quoteModel.put("id", role.getId());
                        quoteModel.put("quoteId", role.getQuoteId());
                        quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                        quoteModel.put("code", role.getCode());
                        quoteModel.put("name", role.getName());
                        quoteModel.put("spec", role.getSpec());
                        quoteModel.put("size", role.getSize());
                        quoteModel.put("isDuty", role.getIsDuty());
                        quoteModel.put("isCeo", role.getIsCeo());
                        quoteModel.put("weight", role.getWeight());
                        quoteModel.put("quantityYear", role.getQuantityYear());
                        quoteModel.put("moldNumber", role.getMoldNumber());
                        quoteModel.put("cavityMaterial", role.getCavityMaterial());
                        quoteModel.put("looseCore", role.getLooseCore());
                        quoteModel.put("feedingMode", role.getFeedingMode());
                        quoteModel.put("deviceType", role.getDeviceType());
                        quoteModel.put("deadline", role.getDeadline());
                        quoteModel.put("moldTime", role.getMoldTime());
                        quoteModel.put("mouldCharge", role.getMouldCharge());
                        quoteModel.put("note1", role.getNote1());
                        quoteModel.put("material", role.getMaterial());
                        quoteModel.put("processingCharge", role.getProcessingCharge());
                        quoteModel.put("status", role.getStatus());
                        quoteModel.put("note", role.getNote());
                        quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                        quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                        quoteModel.put("reQuotenote", reQuote.getNote());
                        quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                        quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                        quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                        quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                        quoteModel.put("reQuotestatus", reQuote.getStatus());
                        quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                        quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                        quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                        quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                        quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                        quoteDieCastinglist.add(quoteModel);
                    }
                }
                if (modelId == 6) {
//                    System.out.print("6");

//                    List<LitemallQuoteElectronic> quoteElectronics = quoteElectronicService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteElectronic", quoteElectronics);
                    List<LitemallQuoteElectronic> quoteElectronicr = quoteElectronicService.queryByGid(reQuote.getId(), true);
                    for (LitemallQuoteElectronic role : quoteElectronicr)  {
                        if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                        if (id == 2 && role.getStatus() !=0 ) { continue; }
                        if (status != null && status.size() != 0) {
                            if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                        }
                        if (!StringUtils.isEmpty(codeId)) {
                            if (role.getCode() != codeId) { continue; }
                        }
                        Map<String, Object> quoteModel = new HashMap<>();
                        quoteModel.put("id", role.getId());
                        quoteModel.put("quoteId", role.getQuoteId());
                        quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                        quoteModel.put("code", role.getCode());
                        quoteModel.put("name", role.getName());
                        quoteModel.put("spec", role.getSpec());
                        quoteModel.put("isDuty", role.getIsDuty());
                        quoteModel.put("isCeo", role.getIsCeo());
                        quoteModel.put("quantityYear", role.getQuantityYear());
                        quoteModel.put("price", role.getPrice());
                        quoteModel.put("delivery", role.getDelivery());
                        quoteModel.put("moq", role.getMoq());
                        quoteModel.put("mpq", role.getMpq());
                        quoteModel.put("packageSize", role.getPackageSize());
                        quoteModel.put("brand", role.getBrand());
                        quoteModel.put("certificate", role.getCertificate());
                        quoteModel.put("status", role.getStatus());
                        quoteModel.put("note", role.getNote());
                        quoteModel.put("adminId", reQuote.getAdminId());
                        quoteModel.put("quoteDate", reQuote.getQuoteDate());
                        quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                        quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                        quoteModel.put("reQuotenote", reQuote.getNote());
                        quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                        quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                        quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                        quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                        quoteModel.put("reQuotestatus", reQuote.getStatus());
                        quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                        quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                        quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                        quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                        quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                        quoteElectroniclist.add(quoteModel);
                    }
                }
                if (modelId == 5) {
//                    System.out.print("5");

//                    List<LitemallQuoteHardware> quoteHardwares = quoteHardwareService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteHardware", quoteHardwares);
                    List<LitemallQuoteHardware> quoteHardwarer = quoteHardwareService.queryByGid(reQuote.getId(), true);
                    for (LitemallQuoteHardware role : quoteHardwarer)  {
                        if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                        if (id == 2 && role.getStatus() !=0 ) { continue; }
                        if (status != null && status.size() != 0) {
                            if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                        }
                        if (!StringUtils.isEmpty(codeId)) {
                            if (!codeId.equals(role.getCode()) ) { continue; }
                        }
                        Map<String, Object> quoteModel = new HashMap<>();
                        quoteModel.put("id", role.getId());
                        quoteModel.put("quoteId", role.getQuoteId());
                        quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                        quoteModel.put("code", role.getCode());
                        quoteModel.put("name", role.getName());
                        quoteModel.put("spec", role.getSpec());
                        quoteModel.put("weight", role.getWeight());
                        quoteModel.put("materialCharge", role.getMaterialCharge());
                        quoteModel.put("materialPerCharge", role.getMaterialPerCharge());
                        quoteModel.put("processingCharge", role.getProcessingCharge());
                        quoteModel.put("electroplateCharge", role.getElectroplateCharge());
                        quoteModel.put("otherCharge", role.getOtherCharge());
                        quoteModel.put("price", role.getPrice());
                        quoteModel.put("isDuty", role.getIsDuty());
                        quoteModel.put("isCeo", role.getIsCeo());
                        quoteModel.put("status", role.getStatus());
                        quoteModel.put("note", role.getNote());
                        quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                        quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                        quoteModel.put("reQuotenote", reQuote.getNote());
                        quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                        quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                        quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                        quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                        quoteModel.put("reQuotestatus", reQuote.getStatus());
                        quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                        quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                        quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                        quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                        quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                        quoteHardwarelist.add(quoteModel);
                    }
                }
                if (modelId == 3) {
//                    List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteRubber", quoteRubbers);
                    List<LitemallQuoteRubber> quoteRubberr = quoteRubberService.queryByGid(reQuote.getId(), true);
                    for (LitemallQuoteRubber role : quoteRubberr) {
                        if (id == 1 && quoteBills.getAdminId() != admin.getId() && role.getStatus() !=0) { continue; }
                        if (id == 2 && role.getStatus() !=0 ) { continue; }
                        if (status != null && status.size() != 0) {
                            if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                        }
                        if (!StringUtils.isEmpty(codeId)) {
                            if (!codeId.equals(role.getCode()) ) { continue; }
                        }
                        Map<String, Object> quoteModel = new HashMap<>();
                        quoteModel.put("id", role.getId());
                        quoteModel.put("quoteId", role.getQuoteId());
                        quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                        quoteModel.put("code", role.getCode());
                        quoteModel.put("name", role.getName());
                        quoteModel.put("spec", role.getSpec());
                        quoteModel.put("isDuty", role.getIsDuty());
                        quoteModel.put("isCeo", role.getIsCeo());
                        quoteModel.put("weight", role.getWeight());
                        quoteModel.put("material", role.getMaterial());
                        quoteModel.put("quantityYear", role.getQuantityYear());
                        quoteModel.put("deviceType", role.getDeviceType());
                        quoteModel.put("looseCore", role.getLooseCore());
                        quoteModel.put("materialPrice", role.getMaterialPrice());
                        quoteModel.put("moldNumber", role.getMoldNumber());
                        quoteModel.put("processingCostSingle", role.getProcessingCostSingle());
                        quoteModel.put("pieceWeight", role.getPieceWeight());
                        quoteModel.put("mouldCharge", role.getMouldCharge());
                        quoteModel.put("status", role.getStatus());
                        quoteModel.put("note", role.getNote());
                        quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                        quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                        quoteModel.put("reQuotenote", reQuote.getNote());
                        quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                        quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                        quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                        quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                        quoteModel.put("reQuotestatus", reQuote.getStatus());
                        quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                        quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                        quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                        quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                        quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                        quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                        quoteRubberlist.add(quoteModel);
                    }
                }
            }
        }
        data.put("DieCastings", ResponseUtil.okList(quoteDieCastinglist));
        data.put("Electronics", ResponseUtil.okList(quoteElectroniclist));
        data.put("Hardwares", ResponseUtil.okList(quoteHardwarelist));
        data.put("Rubbers", ResponseUtil.okList(quoteRubberlist));

        List<LitemallAdmin> adminlist = adminService.queryAll();
        List<Map<String, Object>> adminlists = new ArrayList<>(adminlist.size());
        for (LitemallAdmin adminname : adminlist) {
            Map<String, Object> adminnames = new HashMap<>();
            adminnames.put("value", adminname.getId());
            adminnames.put("label", adminname.getNickname());
            adminnames.put("username", adminname.getUsername());
            adminnames.put("dept", adminname.getDept());
            adminnames.put("deptname", adminname.getDept()+':'+adminname.getNickname()+':'+adminname.getId());
            adminnames.put("dd", adminname.getUsername());
            adminnames.put("jobNumber", adminname.getJobnumber());
            adminnames.put("capacity", adminname.getCapacity());
            adminlists.add(adminnames);
        }

        List<LitemallQuoteModel> rolist = quoteModelService.queryAll();
        List<Map<String, Object>> quoteModels = new ArrayList<>(rolist.size());
        for (LitemallQuoteModel role : rolist) {
            Map<String, Object> quoteModel = new HashMap<>();
            quoteModel.put("value", role.getId());
            quoteModel.put("label", role.getName()+':'+role.getId());
            quoteModel.put("note", role.getAppendix());
            quoteModel.put("supply", role.getCode());
            quoteModel.put("notice", role.getNotice());
            quoteModel.put("approveCode", role.getApproveCode());
            quoteModel.put("ceoCode", role.getCeoCode());
            quoteModel.put("duty", role.getDuty());
            quoteModels.add(quoteModel);
        }
        data.put("optionsAdmin", adminlists);
        data.put("quoteModel", quoteModels);

        return ResponseUtil.ok(data);
    }

    public Object supplyGoogs(Integer id,Integer adminId, String codeId, List<Short> status) {
        Map<String, Object> data = new HashMap<>();
        LitemallAdmin admin = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();
        data.put("currentUser", admin);
        List<Map<String, Object>> quoteDieCastinglist = new ArrayList<>();
        List<Map<String, Object>> quoteElectroniclist = new ArrayList<>();
        List<Map<String, Object>> quoteHardwarelist = new ArrayList<>();
        List<Map<String, Object>> quoteRubberlist = new ArrayList<>();
        List<LitemallRequote> reQuoteList = reQuoteService.queryAll();
        data.put("reQuotes", reQuoteList);

        for (LitemallRequote reQuote : reQuoteList) {
            if (!StringUtils.isEmpty(adminId) && adminId != 0) {
                if (reQuote.getAdminId() != adminId) { continue; }
            }
            LitemallQuoteBill quoteBills = quoteBillService.findById(reQuote.getQuoteId());
            Integer modelId = quoteBills.getModelName();
                if (modelId == 4) {
//                    System.out.print("4");

//                    List<LitemallQuoteDieCasting> quoteDieCastings = quoteDieCastingService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteDieCasting", quoteDieCastings);
                List<LitemallQuoteDieCasting> quoteDieCastingsr = quoteDieCastingService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteDieCasting role : quoteDieCastingsr) {
                    if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                    if (id == 2 && role.getStatus()!=0) { continue; }
                    if (status != null && status.size() != 0) {
                        if (status.contains(Short.valueOf(role.getStatus()))==false) { continue; }
                    }
                    if (!StringUtils.isEmpty(codeId)) {
                        if (!codeId.equals(role.getCode()) ) { continue; }
                    }
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("size", role.getSize());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("moldNumber", role.getMoldNumber());
                    quoteModel.put("cavityMaterial", role.getCavityMaterial());
                    quoteModel.put("looseCore", role.getLooseCore());
                    quoteModel.put("feedingMode", role.getFeedingMode());
                    quoteModel.put("deviceType", role.getDeviceType());
                    quoteModel.put("deadline", role.getDeadline());
                    quoteModel.put("moldTime", role.getMoldTime());
                    quoteModel.put("mouldCharge", role.getMouldCharge());
                    quoteModel.put("note1", role.getNote1());
                    quoteModel.put("material", role.getMaterial());
                    quoteModel.put("processingCharge", role.getProcessingCharge());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                    quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                    quoteModel.put("reQuotenote", reQuote.getNote());
                    quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                    quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                    quoteModel.put("reQuotestatus", reQuote.getStatus());
                    quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                    quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                    quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                    quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                    quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                    quoteDieCastinglist.add(quoteModel);
                }
            }
            if (modelId == 6) {
//                    System.out.print("6");

//                    List<LitemallQuoteElectronic> quoteElectronics = quoteElectronicService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteElectronic", quoteElectronics);
                List<LitemallQuoteElectronic> quoteElectronicr = quoteElectronicService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteElectronic role : quoteElectronicr)  {
                    if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                    if (id == 2 && role.getStatus() !=0 ) { continue; }
                    if (status != null && status.size() != 0) {
                        if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                    }
                    if (!StringUtils.isEmpty(codeId)) {
                        if (!codeId.equals(role.getCode()) ) { continue; }
                    }
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("price", role.getPrice());
                    quoteModel.put("delivery", role.getDelivery());
                    quoteModel.put("moq", role.getMoq());
                    quoteModel.put("mpq", role.getMpq());
                    quoteModel.put("packageSize", role.getPackageSize());
                    quoteModel.put("brand", role.getBrand());
                    quoteModel.put("certificate", role.getCertificate());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("adminId", reQuote.getAdminId());
                    quoteModel.put("quoteDate", reQuote.getQuoteDate());
                    quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                    quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                    quoteModel.put("reQuotenote", reQuote.getNote());
                    quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                    quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                    quoteModel.put("reQuotestatus", reQuote.getStatus());
                    quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                    quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                    quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                    quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                    quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                    quoteElectroniclist.add(quoteModel);
                }
            }
            if (modelId == 5) {
//                    System.out.print("5");

//                    List<LitemallQuoteHardware> quoteHardwares = quoteHardwareService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteHardware", quoteHardwares);
                List<LitemallQuoteHardware> quoteHardwarer = quoteHardwareService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteHardware role : quoteHardwarer)  {
                    if (id == 1 && quoteBills.getAdminId() != admin.getId() &&  role.getStatus() !=0) { continue; }
                    if (id == 2 && role.getStatus() !=0 ) { continue; }
                    if (status != null && status.size() != 0) {
                        if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                    }
                    if (!StringUtils.isEmpty(codeId)) {
                        if (!codeId.equals(role.getCode()) ) { continue; }
                    }
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("material", role.getMaterial());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("materialCharge", role.getMaterialCharge());
                    quoteModel.put("materialPerCharge", role.getMaterialPerCharge());
                    quoteModel.put("processingCharge", role.getProcessingCharge());
                    quoteModel.put("electroplateCharge", role.getElectroplateCharge());
                    quoteModel.put("otherCharge", role.getOtherCharge());
                    quoteModel.put("price", role.getPrice());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                    quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                    quoteModel.put("reQuotenote", reQuote.getNote());
                    quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                    quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                    quoteModel.put("reQuotestatus", reQuote.getStatus());
                    quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                    quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                    quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                    quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                    quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                    quoteHardwarelist.add(quoteModel);
                }
            }
            if (modelId == 3) {
//                    List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(quoteBills.getId(), false);
//                    data.put("quoteRubber", quoteRubbers);
                List<LitemallQuoteRubber> quoteRubberr = quoteRubberService.queryByGid(reQuote.getId(), true);
                for (LitemallQuoteRubber role : quoteRubberr) {
                    if (id == 1 && quoteBills.getAdminId() != admin.getId() && role.getStatus() !=0) { continue; }
                    if (id == 2 && role.getStatus() !=0 ) { continue; }
                    if (status != null && status.size() != 0) {
                        if (status.contains(Short.valueOf( role.getStatus()))==false) { continue; }
                    }
                    if (!StringUtils.isEmpty(codeId)) {
                        if (!codeId.equals(role.getCode()) ) { continue; }
                    }
                    Map<String, Object> quoteModel = new HashMap<>();
                    quoteModel.put("id", role.getId());
                    quoteModel.put("quoteId", role.getQuoteId());
                    quoteModel.put("allname", role.getCode() + ':' + role.getName() + ':' + role.getSpec());
                    quoteModel.put("code", role.getCode());
                    quoteModel.put("name", role.getName());
                    quoteModel.put("spec", role.getSpec());
                    quoteModel.put("isDuty", role.getIsDuty());
                    quoteModel.put("isCeo", role.getIsCeo());
                    quoteModel.put("weight", role.getWeight());
                    quoteModel.put("material", role.getMaterial());
                    quoteModel.put("quantityYear", role.getQuantityYear());
                    quoteModel.put("deviceType", role.getDeviceType());
                    quoteModel.put("looseCore", role.getLooseCore());
                    quoteModel.put("materialPrice", role.getMaterialPrice());
                    quoteModel.put("moldNumber", role.getMoldNumber());
                    quoteModel.put("processingCostSingle", role.getProcessingCostSingle());
                    quoteModel.put("pieceWeight", role.getPieceWeight());
                    quoteModel.put("mouldCharge", role.getMouldCharge());
                    quoteModel.put("status", role.getStatus());
                    quoteModel.put("note", role.getNote());
                    quoteModel.put("reQuoteadminId", reQuote.getAdminId());
                    quoteModel.put("reQuotequoteDate", reQuote.getQuoteDate());
                    quoteModel.put("reQuotenote", reQuote.getNote());
                    quoteModel.put("reQuoterequoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("reQuotequoteModelExcelSupply", reQuote.getQuoteModelExcelSupply());
                    quoteModel.put("reQuotesubmitDate", reQuote.getSubmitDate());
                    quoteModel.put("reQuotedeadDate", reQuote.getDeadDate());
                    quoteModel.put("reQuotestatus", reQuote.getStatus());
                    quoteModel.put("requoteExcel", reQuote.getRequoteExcel());
                    quoteModel.put("reQuoteAdminId", reQuote.getAdminId());
                    quoteModel.put("quoteBillstatus", quoteBills.getStatus());
                    quoteModel.put("quoteBillModelName", quoteBills.getModelName());
                    quoteModel.put("quoteBilladminId", quoteBills.getAdminId());
                    quoteModel.put("quotePurchaserNote", quoteBills.getPurchaserNote());
                    quoteRubberlist.add(quoteModel);
                }
            }
        }
        data.put("DieCastings", ResponseUtil.okList(quoteDieCastinglist));
        data.put("Electronics", ResponseUtil.okList(quoteElectroniclist));
        data.put("Hardwares", ResponseUtil.okList(quoteHardwarelist));
        data.put("Rubbers", ResponseUtil.okList(quoteRubberlist));

        List<LitemallAdmin> adminlist = adminService.queryAll();
        List<Map<String, Object>> adminlists = new ArrayList<>(adminlist.size());
        for (LitemallAdmin adminname : adminlist) {
            Map<String, Object> adminnames = new HashMap<>();
            adminnames.put("value", adminname.getId());
            adminnames.put("label", adminname.getNickname());
            adminnames.put("username", adminname.getUsername());
            adminnames.put("dept", adminname.getDept());
            adminnames.put("deptname", adminname.getDept()+':'+adminname.getNickname()+':'+adminname.getId());
            adminnames.put("dd", adminname.getUsername());
            adminnames.put("jobNumber", adminname.getJobnumber());
            adminnames.put("capacity", adminname.getCapacity());
            adminlists.add(adminnames);
        }

        List<LitemallQuoteModel> rolist = quoteModelService.queryAll();
        List<Map<String, Object>> quoteModels = new ArrayList<>(rolist.size());
        for (LitemallQuoteModel role : rolist) {
            Map<String, Object> quoteModel = new HashMap<>();
            quoteModel.put("value", role.getId());
            quoteModel.put("label", role.getName()+':'+role.getId());
            quoteModel.put("note", role.getAppendix());
            quoteModel.put("supply", role.getCode());
            quoteModel.put("notice", role.getNotice());
            quoteModel.put("approveCode", role.getApproveCode());
            quoteModel.put("ceoCode", role.getCeoCode());
            quoteModel.put("duty", role.getDuty());
            quoteModels.add(quoteModel);
        }
        data.put("optionsAdmin", adminlists);
        data.put("quoteModel", quoteModels);

        return ResponseUtil.ok(data);
    }
}
