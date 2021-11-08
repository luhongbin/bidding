<template>
  <div class="app-container">
    <el-dialog :visible.sync="DialogVisiable" title="询价单明细">
      <el-form status-icon label-position="left" style="margin-left:0px;">
        <el-form-item>
          <editor v-model="goodsDetail" :init="editorInit" />
        </el-form-item>
      </el-form>

      <!--      <div v-html="goodsDetail" :init="editorInit" />-->
<!--      <editor v-html="goodsDetail" :init="editorInit" />-->
    </el-dialog>

    <el-card v-show="rubberCardVisiable && quote.status<2" class="box-card">
      <h3>塑料橡胶类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="weight" label="理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="electronicCardVisiable && quote.status<2" class="box-card">
      <h3>电子电器类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="status" label="状态" />
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="hardwareCardVisiable && quote.status<2" class="box-card">
      <h3>五金类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="明细" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="dieCastingCardVisiable && quote.status<2" class="box-card">
      <h3>压铸模具类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="size" label="产品尺寸(长宽高)" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="rubberCardVisiable && quote.status>=2" class="box-card">
      <h3>塑料橡胶类商品信息</h3>
      <el-table ref="multipleSelection" :data="reDetail" border fit highlight-current-row @selection-change="handleSelectionChange">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>(产品序号ID){{ props.row.id }} (报价单ID) {{ props.row.quoteId }}  (询价单ID) {{ props.row.mainId }}   (供应商) {{ formatRole(props.row.adminId) }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <span>(报价单状态) {{ props.row.status | quoteStatus1Filter }} (报价概要) {{ formatRole(props.row.note) }}</span>
                <el-button v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel.length !== 0" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(props.row.requoteExcel, '报价ID'+(props.row.id).toString()+'.xlsx')">下载报价单附件</el-button>
              </el-form-item>
              <el-form-item label="时间">
                <span>(定标通知时间){{ props.row.quoteDate }} (报价截止日期) {{ props.row.deadDate }}  (提交报价日期) {{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column v-if="quote.status !== 7 && quote.status !== 6" type="selection" :selectable="checkboxT" disabled="true" width="55" />
        <el-table-column property="adminId" label="供应商" sortable>
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="weight" label="理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="status" label="状态" sortable prop="quoteStatusFilter" >
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="isDuty" label="负责人选择"> prop="quoteStatusFilter">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDuty ? 'success' : 'error' ">{{ scope.row.isDuty ? '选中' : '未选中' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="deviceType" label="设备型号" />
        <el-table-column property="looseCore" label="抽芯数" />
        <el-table-column property="materialPrice" label="材料价" sortable />
        <el-table-column property="moldNumber" label="模穴数" />
        <el-table-column property="processingCostSingle" label="单个产品加工费" sortable />
        <el-table-column property="pieceWeight" label="单个产品克重价格" sortable />
        <el-table-column property="mouldCharge" label="模具费" sortable />
<!--        <el-table-column align="center" label="附件" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="electronicCardVisiable && quote.status>=2" class="box-card">
      <h3>电子电器类商品信息</h3>
      <el-table ref="multipleSelection2" :data="reDetail" border fit highlight-current-row @selection-change="handleSelectionChange2">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>(产品序号ID){{ props.row.id }} (报价单ID) {{ props.row.quoteId }}  (询价单ID) {{ props.row.mainId }}   (供应商) {{ formatRole(props.row.adminId) }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <span>(报价单状态) {{ props.row.status | quoteStatus1Filter }} (报价概要) {{ formatRole(props.row.note) }}</span>
                <el-button v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel.length !== 0" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(props.row.requoteExcel, '报价ID'+(props.row.id).toString()+'.xlsx')">下载报价单附件</el-button>
              </el-form-item>
              <el-form-item label="时间">
                <span>(定标通知时间){{ props.row.quoteDate }} (报价截止日期) {{ props.row.deadDate }}  (提交报价日期) {{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column v-if="quote.status !== 7 && quote.status !== 6" type="selection" :selectable="checkboxT" disabled="true" width="55" />
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="isDuty" label="负责人选择"> prop="quoteStatusFilter">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDuty ? 'success' : 'error' ">{{ scope.row.isDuty ? '选中' : '未选中' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="price" label="含税价" />
        <el-table-column property="delivery" label="交期" />
        <el-table-column property="moq" label="MOQ" />
        <el-table-column property="mpq" label="MPQ" />
        <el-table-column property="packageSize" label="包装方式" />
        <el-table-column property="brand" label="品牌'" />
        <el-table-column property="certificate" label="证书情况" />
<!--        <el-table-column align="center" label="附件" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="hardwareCardVisiable && quote.status>=2" class="box-card">
      <h3>五金类商品信息</h3>
      <el-table ref="multipleSelection3" :data="reDetail" border fit highlight-current-row @selection-change="handleSelectionChange3">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>(产品序号ID){{ props.row.id }} (报价单ID) {{ props.row.quoteId }}  (询价单ID) {{ props.row.mainId }}   (供应商) {{ formatRole(props.row.adminId) }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <span>(报价单状态) {{ props.row.status | quoteStatus1Filter }} (报价概要) {{ formatRole(props.row.note) }}</span>
                <el-button v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel.length !== 0" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(props.row.requoteExcel, '报价ID'+(props.row.id).toString()+'.xlsx')">下载报价单附件</el-button>
              </el-form-item>
              <el-form-item label="时间">
                <span>(定标通知时间){{ props.row.quoteDate }} (报价截止日期) {{ props.row.deadDate }}  (提交报价日期) {{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column v-if="quote.status !== 7 && quote.status !== 6" type="selection" :selectable="checkboxT" disabled="true" width="55" />
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" sortable prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="isDuty" label="负责人选择"> prop="quoteStatusFilter">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDuty ? 'success' : 'error' ">{{ scope.row.isDuty ? '选中' : '未选中' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="materialCharge" label="材料价格/吨" />
        <el-table-column property="materialPerCharge" label="单个产品材料价" />
        <el-table-column property="processingCharge" label="加工费" />
        <el-table-column property="electroplateCharge" label="电镀费" sortable />
        <el-table-column property="otherCharge" label="其它费用" sortable />
        <el-table-column property="price" label="产品报价" sortable />
<!--        <el-table-column align="center" label="附件" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="dieCastingCardVisiable && quote.status>=2" class="box-card">
      <h3>压铸模具类商品信息</h3>
      <el-table ref="multipleSelection4" :data="reDetail" border fit highlight-current-row @selection-change="handleSelectionChange4">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>(产品序号ID){{ props.row.id }} (报价单ID) {{ props.row.quoteId }}  (询价单ID) {{ props.row.mainId }}   (供应商) {{ formatRole(props.row.adminId) }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <span>(报价单状态) {{ props.row.status | quoteStatus1Filter }} (报价概要) {{ formatRole(props.row.note) }}</span>
                <el-button v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel.length !== 0" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(props.row.requoteExcel, '报价ID'+(props.row.id).toString()+'.xlsx')">下载报价单附件</el-button>
              </el-form-item>
              <el-form-item label="时间">
                <span>(定标通知时间){{ props.row.quoteDate }} (报价截止日期) {{ props.row.deadDate }}  (提交报价日期) {{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column v-if="quote.status !== 7 && quote.status !== 6" type="selection" :selectable="checkboxT" disabled="true" width="55" />
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="size" label="产品尺寸(长宽高)" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" sortable prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="isDuty" label="负责人选择"> prop="quoteStatusFilter">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDuty ? 'success' : 'error' ">{{ scope.row.isDuty ? '选中' : '未选中' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="moldNumber" label="模穴数(一出几）" />
        <el-table-column property="looseCore" label="抽芯数(一出几)" />
        <el-table-column property="feedingMode" label="进料方式" />
        <el-table-column property="deviceType" label="压铸机吨位" />
        <el-table-column property="deadline" label="模具设计寿命" />
        <el-table-column property="moldTime" label="开模时间" />
        <el-table-column property="mouldCharge" label="模具费(万)" sortable />
        <el-table-column property="note1" label="备注" />
        <el-table-column property="material" label="产品材料" />
        <el-table-column property="processingCharge" label="产品加工费" sortable />
<!--        <el-table-column align="center" label="附件" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <!-- 订单详情对话框 -->

    <el-card v-show="quote.status !== 6" class="box-card">
      <div slot="header">
        <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
          <el-form-item v-show="quote.status === 2 && dutyShow" label="是否会签" prop="toCeo">
            <el-radio-group v-model="quote.isCeo" @change="changeHandler1">
              <el-radio :label="true">提交评估小组</el-radio>
              <el-radio :label="false">直接总经理审批</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-show=" quote.status === 9 && dutyShow" label="重新报价" prop="toCeo">
            <el-radio-group v-model="quote.isReapprove" @change="changeHandler2">
              <el-radio :label="false">让采购员 通知供应商 重新报价</el-radio>
              <el-radio :label="true">直接总经理审批</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-show=" quote.status === 2 && dutyShow && chkgroup" label="会签人员">
            <span>(会签小组) <el-tag v-for="roleId in quote.approveCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag> </span>
          </el-form-item>
          <label v-show=" quote.status !== 6">
            <textarea v-model="approveNote" maxlength="200" style="width: 100%;" placeholder="我的看法" />
          </label>
          <el-button @click="handleCancel">取消</el-button>
          <el-button v-if="(quote.status === 0) && adminShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'提交询价单',1,'询价单','报价人签收',1)">提交询价单</el-button>
          <el-button v-if="(quote.status === 7) && adminShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'重新提交询价单',1,'询价单','报价人签收',8)">重新提交询价单</el-button>
          <el-button v-if="(quote.status === 2) && !chkgroup && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'提交总经理',1,'询价单','结案',3,true,quote.dutyChoice)">提交ceo</el-button>
          <el-button v-if="(quote.status === 2) && chkgroup && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'负责人提交会签',1,'询价单','责任人审批',4)">提交评估小组</el-button>
          <el-button v-if="(quote.status === 4 || quote.status === 9) && signShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'会审',1,'询价单','负责人提交ceo审批',9)">会签</el-button>
          <el-button v-if="(quote.status === 3 || quote.status === 5) && ceoShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'ceo审批',1,'询价单','结案',6,true,quote.ceoChoice)">ceo审批</el-button>
          <el-button v-if="quote.status === 9 && !chkceo && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'重新议价',1,'询价单','采购员报价',7,true,quote.dutyChoice)">重新议价</el-button>
          <el-button v-if="quote.status === 9 && chkceo && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'议价后提交ceo',1,'询价单','ceo审批',5,false,quote.dutyChoice)">议价后提交ceo</el-button>
<!--          <el-button v-if="quote.status===6" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'结案',1,'询价单','结案')">结案</el-button>-->
          <el-button style="float: right" type="warning" v-if="quote.status!=0 && quote.status !=6 && quote.status !=10 && dutyShow" @click="handleSubmit(quote,approveNote,'终止询价',1,'询价单','结束',10)">终止报价</el-button>
        </el-form>
      </div>
    </el-card>
    <!--    <el-card v-show="quote.status >= 2 && (ceoShow || signShow || dutyShow)" class="box-card">-->
    <!--      <h4>报价信息</h4>-->
    <!--      <el-table :data="reQuote" border fit highlight-current-row>-->
    <!--        <el-table-column align="center" label="状态" prop="status">-->
    <!--          <template slot-scope="scope">-->
    <!--            <el-tag>{{ scope.row.status | quoteStatus1Filter }}</el-tag>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column align="center" label="接收人" prop="adminId">-->
    <!--          <template slot-scope="scope">-->
    <!--            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column align="center" label="描述" prop="purchaserNote" />-->
    <!--        <el-table-column align="center" label="报价单" prop="quoteModelExcelSupply">-->
    <!--          <template slot-scope="scope">-->
    <!--            <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcelSupply,'报价单ID' + (scope.row.id).toString() + '.xlsx')">下载</el-button>-->
    <!--          </template>-->
    <!--        </el-table-column>-->
    <!--        <el-table-column align="center" label="提交时间" prop="submitDate" />-->
    <!--      </el-table>-->
    <!--    </el-card>-->
    <el-card v-show="quote.status!==0" class="box-card">
      <h4>审批信息</h4>
      <el-table :data="approve" border fit highlight-current-row>
        <el-table-column align="center" label="发起人" prop="adminId">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="接收人" prop="receiver">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.receiver) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="时间" prop="addTime" />
        <el-table-column align="center" label="留言" prop="note" />
        <el-table-column align="center" label="动作" prop="action" />
      </el-table>
    </el-card>
    <el-card class="box-card">
      <h3>询价单基本信息</h3>
      <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
        <el-form-item label="询价供应商">
          <el-tag v-for="roleId in quote.quoteSupplyCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag>
        </el-form-item>
        <el-form-item label="日期">
          <span>(询价发放日期) {{ quote.submitDate }}</span>
          <span>(报价截止时间) {{ quote.deadDate }}</span>
        </el-form-item>
        <el-form-item label="单据">
          <span>(状态) {{ quote.status | quoteStatusFilter }}</span>
          <span>(类型) {{ formatModel(quote.modelName) }}</span>
          <el-button v-if="quote.quoteModelExcel != undefined && quote.quoteModelExcel != null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(quote.quoteModelExcel,'询价ID'+(quote.id).toString()+'.xlsx')">下载</el-button>
        </el-form-item>
        <el-form-item label="询价概要">
          <span>{{ quote.purchaserNote }}</span>
        </el-form-item>
        <el-form-item label="审核人员">
          <span>(核价小组) <el-tag v-for="roleId in quote.approveCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag> </span>
          <span>(责任人) {{ formatRole(quote.dutyCode) }} </span>
          <span>(终审) {{ formatRole(quote.ceoCode) }} </span>
          <span>(通知人员) <el-tag v-for="roleId in quote.noticeCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag> </span>
        </el-form-item>
      </el-form>
    </el-card>

  </div>
</template>
<style>

.text-area textarea {
  width: 100%;
  margin: 0.75rem  0;
  border: none;
  outline: none;
  padding-left: 1.125rem;
  height: 6.5rem ;
}
.goods-detail-box img {
  width: 100%;
}
.text-area textarea::-webkit-input-placeholder {  color: #9E9E9E;}

</style>
<script>
import { uploadPath } from '@/api/storage'
import { updateQuote, submitQuote, readquote, find } from '@/api/quote'
import { openExcel } from '@/utils/quoteSubmit'
import { dingtalkSend } from '@/utils/senddingtalk'
import { getToken } from '@/utils/auth'
import Editor from '_@tinymce_tinymce-vue@3.0.1@@tinymce/tinymce-vue'
import _ from 'lodash'
import { readQuote } from '@/api/requote'

const statusMap = {
  0: '制单',
  1: '提交询价单',
  2: '等待初审',
  3: '提交ceo',
  4: '提交会审',
  5: '议价后提交ceo',
  6: 'ceo审批',
  7: '重新提交',
  8: '重新提交完毕',
  9: '会审中',
  10: '终止询价'
}
const statusMap1 = {
  0: '询价',
  1: '签收',
  2: '制作报价单',
  3: '提交报价单',
  4: '取消报价',
  5: '报价',
  6: '报价超时作废',
  8: '流标',
  9: '中标',
  10: '重新询价',
  11: '终止询价'
}
const statusMap2 = {
  0: '中标',
  1: '未中标',
  2: '要求重新报价',
  3: '未报价',
  4: '流标',
  5: '报价',
  6: '提交报价',
  7: '取消报价'
}
const statusMap3 = {
  0: '选中',
  1: '放弃'
}
export default {
  name: 'QuoteBill',
  components: { Editor },

  filters: {
    quoteStatusFilter(status) {
      return statusMap[status]
    },
    quoteStatus1Filter(status) {
      return statusMap1[status]
    },
    quoteStatus2Filter(status) {
      return statusMap2[status]
    },
    quoteStatus3Filter(status) {
      return statusMap3[status]
    }
  },
  data() {
    return {
      show: false,
      ceoShow: false,
      dutyShow: false,
      signShow: false,
      adminShow: false,
      chkgroup: false,
      chkceo: false,
      uploadPath,
      setstatus: 0,
      DialogVisiable: false,
      detailDialogVisible: false,
      quoteDialogVisible: true,
      rubberCardVisiable: false,
      electronicCardVisiable: false,
      hardwareCardVisiable: false,
      dieCastingCardVisiable: false,
      rubberVisiable: false,
      electronicVisiable: false,
      hardwareVisiable: false,
      dieCastingVisiable: false,
      multipleSelection: [],
      multipleSelection2: [],
      multipleSelection3: [],
      multipleSelection4: [],

      userid: 0,
      url: '',
      current: [],
      purchaser: '',
      goodsDetail: '',
      approveNote: '',
      reQuote: [],
      approve: [],
      quote: {
        id: 0,
        adminId: 0,
        purchaser: undefined,
        quoteModelExcel: undefined,
        quoteEndDate: undefined,
        quoteSupplyCode: undefined,
        quoteSupplyName: undefined,
        approveCode: undefined,
        dutyCode: undefined,
        noticeCode: undefined,
        ceoCode: undefined,
        notice: undefined,
        status: undefined,
        dutyChoice: undefined,
        ceoChoice: undefined,
        isCeo: undefined,
        isReapprove: undefined
      },
      hours: 0,
      supply: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      reDetail: [],
      detail: [],
      fileList: [],
      ListQuote: [],
      list: [],
      total: 0,
      listLoading: true,
      getapproveQuery: {
        billId: 0,
        billCode: 0 },
      getQuery: {
        quoteId: 0,
        billCode: 1 },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        fileList: [{ required: true, message: '必须长传询价单的Excel文件', trigger: 'blur' }],
        quoteSupplyCode: [{ required: true, message: '采购员必须选择报价的供应商', trigger: 'blur' }],
        ceoChoice: [{ required: true, message: 'ceo必须供应商', trigger: 'blur' }],
        dutyChoice: [{ required: true, message: '责任人必须选择供应商', trigger: 'blur' }]
      },
      dataForm: {
        id: 0,
        adminId: 0,
        purchaser: undefined,
        quoteModelExcel: undefined,
        quoteEndDate: undefined,
        quoteSupplyCode: undefined,
        quoteSupplyName: undefined,
        approveCode: undefined,
        modelName: undefined,
        dutyCode: undefined,
        noticeCode: undefined,
        ceoCode: undefined,
        notice: undefined,
        status: undefined
      },
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false
      },
      statusMap,
      shipDialogVisible: false,
      downloadLoading: false,
      channels: []
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },

  created() {
    this.getList()
  },
  mounted() {
  },

  methods: {
    getList() {
      const Id = this.$route.query.row.id
      readquote(Id).then(response => {
        console.log(response)
        this.current = Object.assign({}, response.data.data.currentUser)
        this.dataForm = Object.assign({}, response.data.data.quoteBills)
        this.detail = response.data.data.detail
        if (this.current.id === this.dataForm.adminId && this.current.id !== this.dataForm.dutyCode) { this.reDetail = response.data.data.redetailpart } else { this.reDetail = response.data.data.redetail }
        this.quoteModelId = response.data.data.quoteModelId
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel

        this.getQuery.quoteId = Id
        this.getQuery.billCode = 0
        readQuote(this.getQuery).then(response => {
          this.reQuote = response.data.data.reQuote
          this.approve = response.data.data.ApproveInfoList
          this.getApprove(this.$route.query.row)
          console.log(JSON.stringify(response))
        }).catch(() => { this.$notify.error('审批数据没取出来') })

        sessionStorage.setItem('userid', this.current.id)
        if (this.dataForm.status === 3 || this.dataForm.status === 5) {
          this.$nextTick(() => {
            for (let i = 0; i < this.reDetail.length; i++) {
              if (this.reDetail[i].isDuty === true) {
                if (this.dataForm.modelName === 3) {
                  this.$refs.multipleSelection.toggleRowSelection(this.reDetail[i])
                }
                if (this.dataForm.modelName === 6) {
                  this.$refs.multipleSelection2.toggleRowSelection(this.reDetail[i])
                }
                if (this.dataForm.modelName === 5) {
                  this.$refs.multipleSelection3.toggleRowSelection(this.reDetail[i])
                }
                if (this.dataForm.modelName === 4) {
                  this.$refs.multipleSelection4.toggleRowSelection(this.reDetail[i])
                }
              }
            }
          })
        }
      }).catch(() => { this.list = []; this.total = 0; this.$notify.error({ title: '失败', message: '基础数据没取出来数据' }) })
      this.listLoading = false
    },
    uploadUrl: function(response) {
      console.log(JSON.stringify(response))

      this.dataForm.quoteModelExcel = response.data.url
    },
    checkFileSize: function(file) {
      if (file.size > 20485760) {
        this.$notify.error('${file.name}文件大于20m，请选择小于20M大小的图片')
        return false
      }
      return true
    },
    handleClick() {
      if (this.tab === 'all') {
        this.listQuery.status = ''
      } else if (this.tab === 'uncheck') {
        this.listQuery.status = '1'
      } else if (this.tab === 'unrefund') {

        this.listQuery.status = '2'
      }
      this.getList()
    },
    checkboxT(row, index) {
      if (this.dataForm.status === 9) { return false } else { if (row.status === 5) { return true } else { return false } }
    },
    showDetail(row) {
      this.DialogVisiable = true
      this.goodsDetail = row
      // const modelId = this.dataForm.modelName
      // find(id, modelId)
      //   .then(response => {
      //     console.log(JSON.stringify(response.data.data.detail.appendix))
      //     this.goodsDetail = JSON.stringify(response.data.data.detail.appendix)
      //     this.DialogVisiable = true
      //   }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleSelectionChange2(val) {
      this.multipleSelection = val
    },
    handleSelectionChange3(val) {
      this.multipleSelection = val
    },
    handleSelectionChange4(val) {
      this.multipleSelection = val
    },
    getApprove(row) {
      this.quote = row
      if (row.ceoCode === parseInt(sessionStorage.getItem('userid'))) { this.ceoShow = true }
      if (row.dutyCode === parseInt(sessionStorage.getItem('userid'))) { this.dutyShow = true }
      if (row.adminId === parseInt(sessionStorage.getItem('userid'))) { this.adminShow = true }
      if ((row.approveCode).toString().indexOf(sessionStorage.getItem('userid')) >= 0) { this.signShow = true }
      if (this.signShow === true) {
        let count = 0
        let count1 = 0
        for (let i = 0; i < this.approve.length; i++) {
          if (this.approve[i].action === '负责人提交会签') { count++ }
          if (this.approve[i].action === '会审' && this.approve[i].adminId=== parseInt(sessionStorage.getItem('userid'))) { count1++ }
        }
        if (count1 >= count) { this.signShow = false }
      }
      this.quoteDialogVisible = true
      this.getQuery.quoteId = row.id
      this.getQuery.billCode = 0
      // readquote(this.getQuery).then(response => {
      //   console.log(JSON.stringify(response))
      //   this.reQuote = response.data.data.reQuote
      //   this.approve = response.data.data.ApproveInfoList
      // }).catch(() => { this.$notify.error('审批数据没取出来') })
      this.quoteDialogVisible = true
      const modelId = this.quote.modelName
      if (modelId === 3) { this.rubberCardVisiable = true }
      if (modelId === 4) { this.dieCastingCardVisiable = true }
      if (modelId === 5) { this.hardwareCardVisiable = true }
      if (modelId === 6) { this.electronicCardVisiable = true }
    },
    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dataForm.adminId = sessionStorage.getItem('userid')
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      console.log(JSON.stringify(this.dataForm))
      this.dataForm.modify = sessionStorage.getItem('userId')
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateQuote(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v); this.list.splice(index, 1, this.dataForm); break
                }
              }
              this.dialogFormVisible = false; this.$notify.success({ title: '成功', message: '更新成功' })
            })
            .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        }
      })
      this.getList()
    },
    formatModel(modelId) {
      for (let i = 0; i < this.modelNameList.length; i++) {
        if (modelId === this.modelNameList[i].value) {
          return this.modelNameList[i].label
        }
      }
      return ''
    },
    formatRole(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    handleCancel: function() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({ path: '/quoteManage/quotebill' })
    },
    formatdd(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].dd
        }
      }
      return ''
    },
    openExcel(url, name) {
      openExcel(url, name)
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    changeHandler1(value) {
      this.chkgroup = value
      console.log(value)
    },
    changeHandler2(value) {
      this.chkceo = value
    },
    handleCeoFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    handleSubmit(row, approveNote, action, billcode, billname, nextaction, setstatus, choiceid, choicevalue) {
      console.log(JSON.stringify(row))
      if (setstatus === 1 && this.reDetail.length === 0) {
        this.$notify.error({ title: '失败', message: '请至少输入一个产品,才能[' + action + '],重新输入' })
        return false
      }
      if (this.multipleSelection.length === 0) {
        if (row.status === 3 || row.status === 2 || row.status === 5 || row.status === 6) {
          this.$notify.error({ title: '失败', message: '请选择至少勾选一条记录,才能[' + action + '],重新选择' })

          // const gg = this.$confirm('请选择至少一条记录,确定重新选择吗？', '提示', {
          //   confirmButtonText: '确定',
          //   cancelButtonText: '取消',
          //   type: 'warning' })
          // }).then(() => {
          //   return
          // })
        }
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })

      if ((row.status === 9 || row.status === 10 || row.status === 4) && approveNote === '') {
        this.$notify.error({ title: '失败', message: '会签必须在录入区内 签署意见' })
        return false
      }
      if (row.status === 0 || row.status === 7) {
        this.quoteDialogVisible = true
        // if (row.quoteModelExcel === undefined || row.quoteModelExcel === null) {
        //   this.$notify.success({ title: '失败', message: '必须存在Excel询价单' })
        //   return false
        // }

        if (row.quoteSupplyCode === undefined || row.quoteSupplyCode === null) {
          this.$notify.error({ title: '失败', message: '必须指定接收询价的供应商' })
          return false
        }
        if (row.dutyCode !== row.adminId) {
          for (let i = 0; i < this.listAdmin.length; i++) {
            if (row.quoteSupplyCode.indexOf(this.listAdmin[i].value) >= 0) {
              if (this.listAdmin[i].capacity.indexOf('AB') < 0) {
                this.$notify.error({ title: '你不能提交审批', message: this.listAdmin[i].deptname + ':不是AB类供应商,需要[' + this.formatRole(row.dutyCode) + ']提交询价,并钉钉通知其本人' })
                var uid = ''
                if (row.dutyCode === row.adminId) {
                  uid = this.formatdd(row.dutyCode)
                } else {
                  uid = this.formatdd(row.dutyCode) + ',' + this.formatdd(row.adminId)
                }
                var infoSend = {
                  userid_list: uid,
                  agent_id: '1231569276',
                  msg: {
                    'msgtype': 'markdown',
                    'markdown': {
                      'title': 'LUTEC询价单提交',
                      'text': '询价单[' + (row.id).toString() + '号,' + this.listAdmin[i].deptname + ']:不是AB类供应商,需要[' + this.formatRole(row.dutyCode) + ']提交询价\n\n\n通知人:' + this.current.nickname
                    }
                  }
                }
                dingtalkSend(infoSend)
                this.quoteDialogVisible = false
                return false
              }
            }
          }
        }
      }
      if (row.adminId !== parseInt(sessionStorage.getItem('userid'))) {
        this.$notify.error('需要制单人[' + this.formatRole(row.adminId) + ']提交询价')
        return false
      }
      this.$confirm('您确定[' + action + ']吗？').then(_ => {
        // if (row.status === 3 || row.status === 4 || row.status === 5 || row.status === 6 || row.status === 7 ) {
        //   this.updateData()
        // }
        console.log('submit begin:')
        submitQuote({
          id: row.id,
          quoteId: row.id,
          approveNote: approveNote,
          excel: row.quoteModelExcel,
          action: action,
          billcode: billcode,
          billname: billname,
          nextaction: nextaction,
          adminId: row.adminId,
          adminName: this.formatRole(row.adminId),
          setstatus: setstatus,
          idcard: 1,
          receiver: 0,
          choiceid: choiceid,
          choicevalue: choicevalue,
          ids: ids
        }).then(response => { this.$notify.success({ title: '成功', message: '发放成功' }); this.getList(); this.quoteDialogVisible = false })
          .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        console.log('submit row:' + JSON.stringify(row))
        this.$message.success({ title: '询价单审批', message: '已经通知供应商报价,通知在钉钉 待办任务 里面' })
        this.$store.dispatch('tagsView/delView', this.$route)
        this.$router.push({ path: '/quoteManage/quotebill' })
        // this.list.status = setstatus
      })

      for (var i = 0; i < this.detail.length; i++) {
        const v = this.detail[i]
        if (v.id === this.detailForm.id) {
          this.detail.splice(i, 1, this.detailForm)
          break
        }
      }
    }
  }
}
</script>

