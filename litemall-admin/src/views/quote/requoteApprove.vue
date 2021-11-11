<template>
  <div class="app-container">
    <!-- 报价单详情对话框 -->
    <el-card v-show="reQuote.status === 0" class="box-card">
      <label>
        <textarea v-model="approveNote" maxlength="200" style="width: 100%;" placeholder="建议" />
      </label>
      <el-button v-if="reQuote.status===0" class="filter-item" type="primary" @click="handleSubmit(reQuote,approveNote,'签收询价单',2,'报价单','提交报价单',1)">签收询价单</el-button>
      <h4>询价信息 (单号) {{ quote.id }}</h4>
      <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
        <el-form-item label="日期">
          <span>(询价发放日期) {{ quote.submitDate }}</span>
          <span>(报价截止时间) {{ quote.deadDate }}</span>
        </el-form-item>
        <el-form-item label="单据">
          <span>(类型) {{ formatModel(quote.modelName) }}</span>
          <el-button v-if="quote.quoteModelExcel !== undefined && quote.quoteModelExcel !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(quote.quoteModelExcel,'询价ID'+quote.id.toString()+'.xlsx')">下载</el-button>
        </el-form-item>
        <el-form-item label="询价概要">
          <span>{{ quote.purchaserNote }}</span>
        </el-form-item>
      </el-form>
    </el-card>
    <el-dialog :visible.sync="DialogVisiable" title="询价单明细">
<!--      <div v-html="goodsDetail" :init="editorInit" />-->
      <el-form status-icon label-position="left" style="margin-left:0px;">
        <el-form-item>
          <editor v-model="goodsDetail" :init="editorInit" />
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-card v-show="rubberCardVisiable && reQuote.status===0" class="box-card">
      <h3>塑料橡胶类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="electronicCardVisiable && reQuote.status===0" class="box-card">
      <h3>电子电器类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="quantityYear" label="年预估量" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="hardwareCardVisiable && reQuote.status===0" class="box-card">
      <h3>五金类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
<!--        <el-table-column align="center" label="明细" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>
    <el-card v-show="dieCastingCardVisiable && reQuote.status===0" class="box-card">
      <h3>压铸模具类商品信息</h3>
      <el-table :data="detail">
        <el-table-column property="id" label="id" />
        <el-table-column property="code" label="品号" />
        <el-table-column property="name" label="品名" />
        <el-table-column property="spec" label="规格" />
        <el-table-column property="size" label="产品尺寸(长宽高)" />
        <el-table-column property="weight" label="产品理论重量(克)" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>

    <el-card v-show="rubberCardVisiable && reQuote.status>0" class="box-card">
      <h3>塑料橡胶类商品信息</h3>
      <el-table ref="multipleSelection" :data="detail" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="单号">
                <span>(产品序号单号){{ props.row.id }} (报价单单号) {{ props.row.quoteId }} (产品状态) {{ props.row.status | quoteStatus2Filter }} </span>
              </el-form-item>
              <el-form-item label="报价产品">
                <span>(品号){{ props.row.code }}  (品名) {{ props.row.name }}  (规格) {{ props.row.spec }}  (材质) {{ props.row.material }} </span>
              </el-form-item>
              <el-form-item label="数量">
                <span>(理论重量){{ props.row.weight }} (年预估量){{ props.row.weight }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column property="name" width="200" label="品名" />
        <el-table-column property="spec" width="200" label="规格" />
        <el-table-column property="weight" label="理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="deviceType" label="设备型号(注塑机)" />
        <el-table-column property="looseCore" label="抽芯数(一出几)" />
        <el-table-column property="materialPrice" label="材料价(元/克)" />
        <el-table-column property="moldNumber" label="模穴数(一出几)" />
        <el-table-column property="processingCostSingle" label="单个产品加工费(元/克)" />
        <el-table-column property="pieceWeight" label="单个产品克重价格(元/克)" />
        <el-table-column property="mouldCharge" label="模具费" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag effect="dark" v-if = "scope.row.status == 0 && endBill==9" type="danger">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status == 2 && endBill==9" type="info">历史报价</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status != 2 && scope.row.status != 0 && endBill==9" type="info">流标</el-tag>
            <el-tag effect="dark" v-else type="'error'">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="electronicCardVisiable && reQuote.status>0" class="box-card">
      <h3>电子电器类商品信息</h3>
      <el-table ref="multipleSelection2" :data="detail" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="单号">
                <span>(产品序号单号){{ props.row.id }} (报价单单号) {{ props.row.quoteId }} (产品状态) {{ props.row.status | quoteStatus2Filter }} </span>
              </el-form-item>
              <el-form-item label="报价产品">
                <span>(品号){{ props.row.code }}  (品名) {{ props.row.name }}  (规格) {{ props.row.spec }} </span>
              </el-form-item>
              <el-form-item label="数量">
                <span>(理论重量){{ props.row.weight }} (年预估量){{ props.row.quantityYear }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column property="name" width="200" label="品名" />
        <el-table-column property="spec" width="200" label="规格" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="price" label="含税价" />
        <el-table-column property="delivery" label="交期" />
        <el-table-column property="moq" label="MOQ" />
        <el-table-column property="mpq" label="MPQ" />
        <el-table-column property="packageSize" label="包装方式" />
        <el-table-column property="brand" label="品牌'" />
        <el-table-column property="certificate" label="证书情况" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag effect="dark" v-if = "scope.row.status == 0 && endBill==9" type="danger">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status == 2 && endBill==9" type="info">历史报价</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status != 2 && scope.row.status != 0 && endBill==9" type="info">流标</el-tag>
            <el-tag effect="dark" v-else type="'error'">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="hardwareCardVisiable && reQuote.status>0" class="box-card">
      <h3>五金类商品信息</h3>
      <el-table ref="multipleSelection3" :data="detail" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="单号">
                <span>(产品序号单号){{ props.row.id }} (报价单单号) {{ props.row.quoteId }} (产品状态) {{ props.row.status | quoteStatus2Filter }} </span>
              </el-form-item>
              <el-form-item label="报价产品">
                <span>(品号){{ props.row.code }}  (品名) {{ props.row.name }}  (规格) {{ props.row.spec }} </span>
              </el-form-item>
              <el-form-item label="数量">
                <span>(材质){{ props.row.material }} (产品理论重量(克)){{ props.row.weight }} (年预估量){{ props.row.quantityYear }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column property="name" width="200" label="品名" />
        <el-table-column property="spec" width="200" label="规格" />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="materialCharge" label="材料价格/吨" />
        <el-table-column property="materialPerCharge" label="单个产品材料价" />
        <el-table-column property="processingCharge" label="加工费" />
        <el-table-column property="electroplateCharge" label="电镀费" />
        <el-table-column property="otherCharge" label="其它费用" />
        <el-table-column property="price" label="产品报价" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag effect="dark" v-if = "scope.row.status == 0 && endBill==9" type="danger">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status == 2 && endBill==9" type="info">历史报价</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status != 2 && scope.row.status != 0 && endBill==9" type="info">流标</el-tag>
            <el-tag effect="dark" v-else type="'error'">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="明细" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-show="dieCastingCardVisiable && reQuote.status>0" class="box-card">
      <h3>压铸模具类商品信息</h3>
      <el-table ref="multipleSelection4" :data="detail" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="单号">
                <span>(产品序号单号){{ props.row.id }} (报价单单号) {{ props.row.quoteId }} (产品状态) {{ props.row.status | quoteStatus2Filter }} </span>
              </el-form-item>
              <el-form-item label="报价产品">
                <span>(品号){{ props.row.code }}  (品名) {{ props.row.name }}  (规格) {{ props.row.spec }} </span>
              </el-form-item>
              <el-form-item label="数量">
                <span>(产品尺寸(长宽高)){{ props.row.size }} (产品理论重量(克){{ props.row.weight }} (年预估量){{ props.row.quantityYear }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column property="name" width="200" label="品名" />
        <el-table-column property="spec" width="200" label="规格" />
        <el-table-column property="size" label="产品尺寸" />
        <el-table-column property="weight" label="产品理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column property="moldNumber" label="模穴数" />
        <el-table-column property="looseCore" label="抽芯数" />
        <el-table-column property="feedingMode" label="进料方式" />
        <el-table-column property="deviceType" label="压铸机吨位" />
        <el-table-column property="deadline" label="模具设计寿命" />
        <el-table-column property="moldTime" label="开模时间" />
        <el-table-column property="mouldCharge" label="模具费(万)" />
        <el-table-column property="note1" label="备注" />
        <el-table-column property="material" label="产品材料" />
        <el-table-column property="processingCharge" label="产品加工费" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag effect="dark" v-if = "scope.row.status == 0 && endBill==9" type="danger">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status == 2 && endBill==9" type="info">历史报价</el-tag>
            <el-tag effect="dark" v-else-if="scope.row.status != 2 && scope.row.status != 0 && endBill==9" type="info">流标</el-tag>
            <el-tag effect="dark" v-else type="'error'">{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="备注" prop="id">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-show="reQuote.status >0" class="box-card">
      <div v-show="reQuote.status<2 || reQuote.status===10" slot="header">
        <label>
          <textarea v-model="approveNote" maxlength="200" style="width: 100%;" placeholder="建议" />
        </label>
        <el-button v-if="reQuote.status===3" style="float: right" type="warning" @click="handleCancle(reQuote)">撤销提交</el-button>
        <el-button v-if="reQuote.status===0 || reQuote.status===10" class="filter-item" type="primary" @click="handleSubmit(reQuote,approveNote,'签收询价单',2,'报价单','提交报价单',1)">签收</el-button>
        <el-button v-if="reQuote.status===1" class="filter-item" type="primary" @click="handleSubmit(reQuote,approveNote,'提交报价单',2,'报价单','等待中标结果',2)">提交报价单</el-button>
      </div>
      <el-form v-show="reQuote.status!==0" ref="reQuote" :model="reQuote" status-icon label-position="left" label-width="100px">
        <!-- 发货对话框 -->
        <el-form-item label="报价商家">
          <template slot-scope="scope">
            <el-tag type="primary" style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
          </template>
        </el-form-item>
        <el-form-item label="日期">
          <span>(报价截止日期) {{ reQuote.deadDate }}</span>
          <span>(提交报价日期) {{ reQuote.submitDate }}</span>
          <span>(甲方答复日期) {{ reQuote.quoteDate }}</span>
        </el-form-item>
        <el-form-item label="报价概要">
          <span>{{ reQuote.note }}</span>
        </el-form-item>
        <el-form-item label="单据状态">
          <span>{{ reQuote.status | quoteStatusFilter }}</span>
          <el-button v-if="reQuote.requoteExcel !== '' && reQuote.requoteExcel !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(reQuote.requoteExcel,'报价ID'+reQuote.id.toString()+'.xlsx')">下载</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-show="reQuote.status > 0" class="box-card">
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

.text-area textarea::-webkit-input-placeholder {  color: #9E9E9E;}

</style>
<script>
import { createStorage, uploadPath } from '@/api/storage'
import { find, submitQuote } from '@/api/quote'
import { myRead, updateRequote } from '@/api/requote'
import { openExcel } from '@/utils/quoteSubmit'
import { getToken } from '@/utils/auth'
import Editor from '_@tinymce_tinymce-vue@3.0.1@@tinymce/tinymce-vue'
import _ from 'lodash'

const statusMap = {
  0: '询价',
  1: '签收',
  2: '制作报价单',
  3: '提交报价单',
  4: '甲方审批中',
  5: '报价',
  6: '报价超时作废',
  8: '流标',
  9: '已开标',
  10: '重新报价',
  11: '终止询价'
}
const statusMap2 = {
  0: '中标',
  1: '未中标',
  2: '历史报价',
  3: '未报价',
  4: '流标',
  5: '报价',
  6: '提交报价',
  7: '取消报价',
  8: '待报价',
  9: '最新报价'
}
const statusMap3 = {
  0: '选中',
  1: '放弃'
}
export default {
  name: 'RequoteBill',
  components: { Editor },
  filters: {
    quoteStatusFilter(status) {
      return statusMap[status]
    },
    quoteStatus2Filter(status) {
      return statusMap2[status]
    },
    quoteStatus3Filter(status) {
      return statusMap3[status]
    },
    filters: {
      quoteStatusFilter(status) {
        return statusMap[status]
      },
      quoteStatus2Filter(status) {
        return statusMap2[status]
      },
      quoteStatus3Filter(status) {
        return statusMap3[status]
      }
    }
  },
  data() {
    return {
      uploadPath,
      goodsDetail: '',
      multipleSelection: [],
      multipleSelection2: [],
      multipleSelection3: [],
      multipleSelection4: [],
      quoteDialogVisible: false,
      editDialogVisible: false,
      DialogVisiable: false,
      detailDialogVisible: false,
      rubberCardVisiable: false,
      electronicCardVisiable: false,
      hardwareCardVisiable: false,
      dieCastingCardVisiable: false,
      rubberVisiable: false,
      electronicVisiable: false,
      hardwareVisiable: false,
      dieCastingVisiable: false,
      endBill: 0,
      purchaser: '',
      approveNote: '',
      quote: {
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
        ceoCode: undefined,
        notice: undefined,
        status: undefined,
        dutyChoice: undefined,
        ceoChoice: undefined,
        isCeo: undefined,
        isReapprove: undefined
      },
      approve: [],
      reQuote: {},
      detail: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      fileList: [],
      ListQuote: [],
      list: [],
      total: 0,
      listLoading: true,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        fileList: [
          { required: true, message: '必须上传报价单的Excel文件', trigger: 'blur' }

        ],
        quoteSupplyCode: [{ required: true, message: '必须选择报价的供应商', trigger: 'blur' }]
      },
      statusMap,
      shipDialogVisible: false,
      refundForm: {
        quoteId: undefined,
        refundMoney: undefined
      },
      refundDialogVisible: false,
      downloadLoading: false,
      channels: [],
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false
      }
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
    // this.init()
    // setTimeout(function() {}, 500)
    this.getList()
  },
  methods: {
    // checkPermission,
    getList() {
      this.listLoading = true
      const Id = this.$route.query.row.id

      myRead(Id).then(response => {
        console.log('myRead:' + JSON.stringify(response))
        this.current = Object.assign({}, response.data.data.currentUser)
        this.detail = response.data.data.redetail
        this.reQuote = response.data.data.reQuote
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        this.quote = response.data.data.Quote
        this.approve = response.data.data.ApproveInfoList
        const modelId = response.data.data.Quote.modelName
        this.detail.forEach(item => {
          item.allname = item.code + ':' + item.name + ':' + item.spec
        })
        if (response.data.data.reQuote.status === 9) { this.endBill = 9 }
        if (response.data.data.reQuote.status === 8) { this.endBill = 8 }
        console.log(JSON.stringify(this.detail))
        if (modelId === 3) { this.rubberCardVisiable = true }
        if (modelId === 4) { this.dieCastingCardVisiable = true }
        if (modelId === 5) { this.hardwareCardVisiable = true }
        if (modelId === 6) { this.electronicCardVisiable = true }
        this.$nextTick(() => {
          for (let i = 0; i < this.detail.length; i++) {
            if (this.detail[i].isDuty === true) {
              if (this.dataForm.modelName === 3) {
                this.$refs.multipleSelection.toggleRowSelection(this.detail[i])
              }
              if (this.dataForm.modelName === 6) {
                this.$refs.multipleSelection2.toggleRowSelection(this.detail[i])
              }
              if (this.dataForm.modelName === 5) {
                this.$refs.multipleSelection3.toggleRowSelection(this.detail[i])
              }
              if (this.dataForm.modelName === 4) {
                this.$refs.multipleSelection4.toggleRowSelection(this.detail[i])
              }
            }
          }
        })
        sessionStorage.setItem('userid', this.current.id)
      }).catch(() => { this.list = []; this.total = 0; this.$notify.error({ title: '失败', message: '基础数据没取出来数据' }) })
      this.listLoading = false
    },
    uploadUrl: function(response) {
      console.log(JSON.stringify(response))
      this.dataForm.requoteExcel = response.data.url
      // this.$notify({ message: '上传成功!' + this.dataForm.requoteExcel })
    },
    checkFileSize: function(file) {
      if (file.size > 20485760) {
        this.$notify.error('${file.name}文件大于20m，请选择小于20M大小的文件')
        return false
      }
      return true
    },
    selectSupply(val) {
      console.log(val)
      for (let i = 0; i < this.modelNameList.length; i++) {
        if (this.modelNameList[i]['value'] === val) { this.$set(this.dataForm, 'quoteSupplyCode', this.modelNameList[i].supply) }
      }
    },
    showDetail(row) {
      // alert(row)
      this.DialogVisiable = true
      this.$nextTick(() => {
        this.goodsDetail = row
      });
      // const modelId = this.quote.modelName
      // find(id, modelId)
      //   .then(response => {
      //     console.log(JSON.stringify(response.data.data.detail.appendix))
      //     this.goodsDetail = JSON.stringify(response.data.data.detail.appendix)
      //     this.DialogVisiable = true
      //   }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
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
      if (row.adminId !== parseInt(sessionStorage.getItem('userid'))) {
        this.$notify.error({ title: '失败', message: '必须官方指定的人提供报价' + sessionStorage.getItem('userid') })
        return
      }
      // this.getQuote(row.quoteId)
      this.dataForm = Object.assign({}, row)
      this.dataForm.adminId = parseInt(sessionStorage.getItem('userid'))
      this.dataForm.adminName = sessionStorage.getItem('nickname')
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.dataForm.modify = parseInt(sessionStorage.getItem('userId'))

      this.$refs['dataForm'].validate(valid => {
        console.log('dataform:' + JSON.stringify(this.dataForm))
        if (valid) {
          this.dataForm.status = 2
          updateRequote(this.dataForm).then(() => {
            for (const v of this.list) {
              if (v.id === this.dataForm.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.dataForm)
                break
              }
            }
            this.dialogFormVisible = false
            this.$message.success({
              title: '成功',
              message: '更新成功'
            })
            submitQuote({
              id: this.dataForm.id,
              quoteId: this.dataForm.quoteId,
              approveNote: this.approveNote,
              action: '制作报价单',
              billcode: 2,
              billname: '报价单',
              nextaction: '提交报价单',
              adminId: this.dataForm.adminId,
              adminName: this.formatRole(this.dataForm.adminId),
              setstatus: 1,
              idcard: 3,
              receiver: this.quote.adminId })
              .then(response => { this.$message.success({ title: '成功', message: '提交报价单成功' }) })
              .catch(response => { this.$notify.error({ title: '失败', message: '提交报价单失败了' + response.data.errmsg }) })
          }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        }
      })
      // this.getList()
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
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    getApprove(row) {
      console.log('row:' + JSON.stringify(row))
      this.reQuote = row
      this.getQuery.quoteId = row.quoteId
      this.getQuery.billCode = 2
      // readQuote(this.getQuery).then(response => {
      //   console.log(JSON.stringify(response))
      //   // this.reQuote = response.data.data.reQuote
      //   this.quote = response.data.data.Quote
      //   this.approve = response.data.data.ApproveInfoList
      //   console.log(JSON.stringify(this.reQuote))
      //   console.log(JSON.stringify(this.quote))
      //   console.log(JSON.stringify(this.approve))
      // }).catch(() => { this.$notify.error('审批数据没取出来') })
      this.quoteDialogVisible = true
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
    handleSubmit(row, approveNote, action, billcode, billname, nextaction, setstatus, choiceid, choicevalue) {

      if (this.getcurrtime() > this.reQuote.deadDate) {
        this.$notify.error({ title: '提交失败', message: '超过报价截止时间:'+this.reQuote.deadDate })
        return false
      }
      if (row.adminId !== parseInt(sessionStorage.getItem('userid'))) {
        this.$notify.error({ title: '失败', message: '必须官方指定的人签收' + sessionStorage.getItem('userid') })
        return
      }
      if (this.multipleSelection.length === 0 && this.reQuote.status > 0) {
        if (this.reQuote.status > 0) {
          this.$confirm('请选择至少一条记录,确定重新选择吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(_ => { return })
        }
      }
      if (row.status > 0) {
        // if (row.requoteExcel === '' || row.requoteExcel === null || row.requoteExcel === undefined) {
        //   this.$notify.success({ title: '失败', message: '必须存在Excel报价单' })
        //   return
        // }
      }
      const ids = []
      _.forEach(this.detail.id, function(item) {
        ids.push(item.id)
      })

      this.$confirm('您确定[' + action + ']吗？').then(_ => {
        console.log('submit begin:')
        submitQuote({
          id: row.id,
          quoteId: row.quoteId,
          approveNote: approveNote,
          excel: row.requoteExcel,
          action: action,
          billcode: billcode,
          billname: billname,
          nextaction: nextaction,
          adminId: row.adminId,
          adminName: this.formatRole(row.adminId),
          setstatus: setstatus,
          idcard: 2,
          receiver: this.quote.adminId,
          choiceid: choiceid,
          choicevalue: choicevalue,
          ids: ids }).then(response => { this.$message.success({ title: '提交成功', message: this.formatRole(row.adminId) + '[' + setstatus + ']成功' }) })
          .catch(response => { this.$notify.error({ title: '失败', message: '签收失败了' }) })
        this.$store.dispatch('tagsView/delView', this.$route)
        this.$router.push({ path: '/supplyManage/requote' })
      })
    },
    getcurrtime() {
      var date = new Date()

      var year = date.getFullYear();       //年 ,从 Date 对象以四位数字返回年份
      var month = date.getMonth() + 1      //月 ,从 Date 对象返回月份 (0 ~ 11) ,date.getMonth()比实际月份少 1 个月
      var day = date.getDate();            //日 ,从 Date 对象返回一个月中的某一天 (1 ~ 31)

      var hours = date.getHours();         //小时 ,返回 Date 对象的小时 (0 ~ 23)
      var minutes = date.getMinutes()      //分钟 ,返回 Date 对象的分钟 (0 ~ 59)
      var seconds = date.getSeconds()      //秒 ,返回 Date 对象的秒数 (0 ~ 59)

      //获取当前系统时间
      var currentDate = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds

      //修改月份格式
      if (month >= 1 && month <= 9) {
        month = "0" + month
      }

      //修改日期格式
      if (day >= 0 && day <= 9) {
        day = "0" + day
      }

      //修改小时格式
      if (hours >= 0 && hours <= 9) {
        hours = "0" + hours
      }

      //修改分钟格式
      if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes
      }

      //修改秒格式
      if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds
      }

      //获取当前系统时间  格式(yyyy-mm-dd hh:mm:ss)
      var currentFormatDate = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds
      return currentFormatDate
    },
    printOrder() {
      this.$print(this.$refs.print)
      this.quoteDialogVisible = false
    }
  }
}
</script>
