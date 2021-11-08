<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.supplyId" clearable class="filter-item" style="width: 160px;" placeholder="请输入供应商ID" />
      <el-input v-model="listQuery.codeId" clearable class="filter-item" style="width: 160px;" placeholder="请输入商品代码" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择产品状态">
        <el-option v-for="(key, value) in statusMap2" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/requote/listBrowser']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
      <el-button v-permission="['GET /admin/requote/listBrowserCeo']" class="filter-item" type="primary" icon="el-icon-search" @click="getListCeo">全部查询</el-button>
      <el-button v-permission="['GET /admin/requote/listBrowserOk']" class="filter-item" type="primary" icon="el-icon-search" @click="getListOK">查询中标商品</el-button>
      <!-- <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>-->
    </div>
    <el-tabs v-model="tab" @tab-click="handleClick">
      <el-tab-pane label="电子电器类" name="dz" />
      <el-tab-pane label="塑料橡胶类" name="sl" />
      <el-tab-pane label="五金类报价单" name="wj" />
      <el-tab-pane label="压铸模具类" name="yz" />
    </el-tabs>
    <!-- 查询结果 -->
    <el-dialog :visible.sync="DialogVisiable" title="询价单明细">
      <el-form status-icon label-position="left" style="margin-left:0px;">
        <el-form-item>
          <editor v-model="goodsDetail" :init="editorInit" />
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-card v-show="rubberCardVisiable" class="box-card">
      <h3>塑料橡胶类商品信息</h3>
      <el-table v-loading="listLoading" :data="quoteRubberslist" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>{{ props.row.id }}</span>
              </el-form-item>
              <el-form-item label="主表ID">
                <span>{{ props.row.quoteId }}</span>
              </el-form-item>
              <el-form-item label="报价概要">
                <span>{{ props.row.reQuotenote }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <template slot-scope="scope">
                  <el-tag>{{ scope.row.reQuotestatus | quoteStatus1Filter }}</el-tag>
                </template>
              </el-form-item>
              <el-form-item label="定标通知时间">
                <span>{{ props.row.reQuotequoteDate }}</span>
              </el-form-item>
              <el-form-item label="报价截止日期">
                <span>{{ props.row.reQuotedeadDate }}</span>
              </el-form-item>
              <el-form-item label="提交报价日期">
                <span>{{ props.row.reQuotesubmitDate }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel !== ''" label="报价单附件">
                <template slot-scope="scope">
                  <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel,'报价单ID' + (scope.row.quoteId).toString() + '.xlsx')">下载</el-button>
                </template>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购员">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.quoteBilladminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="报价商家">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.reQuoteadminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="weight" label="理论重量" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="deviceType" label="设备型号" />
        <el-table-column property="looseCore" label="抽芯数" />
        <el-table-column property="materialPrice" label="材料价" />
        <el-table-column property="moldNumber" label="模穴数" />
        <el-table-column property="processingCostSingle" label="单个产品加工费" />
        <el-table-column property="pieceWeight" label="单个产品克重价格(元/克)" />
        <el-table-column property="mouldCharge" label="模具费" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
      <pagination v-show="quoteRubberstotal>0" :total="quoteRubberstotal" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>
    <el-card v-show="electronicCardVisiable" class="box-card">
      <h3>电子电器类商品信息</h3>
      <el-table v-loading="listLoading" :data="quoteElectronicslist" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>{{ props.row.id }}</span>
              </el-form-item>
              <el-form-item label="主表ID">
                <span>{{ props.row.quoteId }}</span>
              </el-form-item>
              <el-form-item label="报价概要">
                <span>{{ props.row.note }}</span>
              </el-form-item>
              <el-form-item label="定标通知时间">
                <span>{{ props.row.quoteDate }}</span>
              </el-form-item>
              <el-form-item label="报价截止日期">
                <span>{{ props.row.deadDate }}</span>
              </el-form-item>
              <el-form-item label="提交报价日期">
                <span>{{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel !== ''" label="报价单附件">
                <template slot-scope="scope">
                  <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel,'报价单ID' + (scope.row.quoteId).toString() + '.xlsx')">下载</el-button>
                </template>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购员">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.quoteBilladminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="报价商家">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.reQuoteadminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="price" label="含税价" />
        <el-table-column property="delivery" label="交期" />
        <el-table-column property="moq" label="MOQ" />
        <el-table-column property="mpq" label="MPQ" />
        <el-table-column property="packageSize" label="包装方式" />
        <el-table-column property="brand" label="品牌'" />
        <el-table-column property="certificate" label="证书情况" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" />
    </el-card>
    <el-card v-show="hardwareCardVisiable" class="box-card">
      <h3>五金类商品信息</h3>
      <el-table v-loading="listLoading" :data="quoteHardwareslist" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>{{ props.row.id }}</span>
              </el-form-item>
              <el-form-item label="主表ID">
                <span>{{ props.row.quoteId }}</span>
              </el-form-item>
              <el-form-item label="报价概要">
                <span>{{ props.row.note }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <template slot-scope="scope">
                  <el-tag>{{ scope.row.status | quoteStatus1Filter }}</el-tag>
                </template>
              </el-form-item>
              <el-form-item label="定标通知时间">
                <span>{{ props.row.quoteDate }}</span>
              </el-form-item>
              <el-form-item label="报价截止日期">
                <span>{{ props.row.deadDate }}</span>
              </el-form-item>
              <el-form-item label="提交报价日期">
                <span>{{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel !== ''" label="报价单附件">
                <template slot-scope="scope">
                  <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel,'报价单ID' + (scope.row.quoteId).toString() + '.xlsx')">下载</el-button>
                </template>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购员">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.quoteBilladminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="报价商家">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.reQuoteadminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="material" label="材质" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="materialCharge" label="材料价格/吨" />
        <el-table-column property="materialPerCharge" label="单个产品材料价" />
        <el-table-column property="processingCharge" label="加工费" />
        <el-table-column property="electroplateCharge" label="电镀费" />
        <el-table-column property="otherCharge" label="其它费用" />
        <el-table-column property="price" label="产品报价" />
<!--        <el-table-column align="center" label="明细" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.row.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
      <pagination v-show="quoteHardwarestotal>0" :total="quoteHardwarestotal" :page.sync="listQuery.page" :limit.sync="listQuery.limit" />
    </el-card>
    <el-card v-show="dieCastingCardVisiable" class="box-card">
      <h3>压铸模具类商品信息</h3>
      <el-table v-loading="listLoading" :data="quoteDieCastingslist" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" class="table-expand">
              <el-form-item label="ID">
                <span>{{ props.row.id }}</span>
              </el-form-item>
              <el-form-item label="主表ID">
                <span>{{ props.row.quoteId }}</span>
              </el-form-item>
              <el-form-item label="报价概要">
                <span>{{ props.row.note }}</span>
              </el-form-item>
              <el-form-item label="报价单状态">
                <template slot-scope="scope">
                  <el-tag>{{ scope.row.status | quoteStatus1Filter }}</el-tag>
                </template>
              </el-form-item>
              <el-form-item label="定标通知时间">
                <span>{{ props.row.quoteDate }}</span>
              </el-form-item>
              <el-form-item label="报价截止日期">
                <span>{{ props.row.deadDate }}</span>
              </el-form-item>
              <el-form-item label="提交报价日期">
                <span>{{ props.row.submitDate }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.requoteExcel !== undefined && props.row.requoteExcel !== null && props.row.requoteExcel !== ''" label="报价单附件">
                <template slot-scope="scope">
                  <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel,'报价单ID' + (scope.row.quoteId).toString() + '.xlsx')">下载</el-button>
                </template>
              </el-form-item>
              <el-form-item>
                <editor v-model="props.row.appendix" :init="editorInit" />
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column align="center" label="采购员">
          <template slot-scope="scope">
            <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.quoteBilladminId) }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="allname" label="产品名称" sortable />
        <el-table-column property="size" label="产品尺寸(长宽高)" />
        <el-table-column property="weight" label="产品理论重量(克)" />
        <el-table-column property="quantityYear" label="年预估量" />
        <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column property="moldNumber" label="模穴数(一出几）" />
        <el-table-column property="looseCore" label="抽芯数(一出几)" />
        <el-table-column property="feedingMode" label="进料方式" />
        <el-table-column property="deviceType" label="压铸机吨位" />
        <el-table-column property="deadline" label="模具设计寿命" />
        <el-table-column property="moldTime" label="开模时间" />
        <el-table-column property="mouldCharge" label="模具费(万)" />
        <el-table-column property="note1" label="备注" />
        <el-table-column property="material" label="产品材料" />
        <el-table-column property="processingCharge" label="产品加工费" />
<!--        <el-table-column align="center" label="备注" prop="id">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="primary" size="mini" @click="showDetail(scope.appendix)">查看</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
      <pagination v-show="quoteDieCastingstotal>0" :total="quoteDieCastingstotal" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
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
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination import { getToken } from '@/utils/auth'
import { uploadPath } from '@/api/storage'
import { listBrowserOK, find, listBrowser, listBrowserCeo } from '@/api/quote'
import { openExcel } from '@/utils/quoteSubmit'
import { getToken } from '@/utils/auth'
import Editor from '_@tinymce_tinymce-vue@3.0.1@@tinymce/tinymce-vue'

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
  9: '开标',
  10: '重新报价',
  11: '终止询价'
}
const statusMap2 = {
  0: '开标',
  1: '流标',
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
  name: 'Quotebillliist',
  components: { Editor, Pagination },

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
      DialogVisiable: false,
      detailDialogVisible: false,
      quoteDialogVisible: true,
      rubberVisiable: false,
      rubberCardVisiable: false,
      electronicCardVisiable: false,
      hardwareCardVisiable: false,
      dieCastingCardVisiable: false,
      electronicVisiable: false,
      hardwareVisiable: false,
      dieCastingVisiable: false,
      uploadPath,
      tab: 'dz',
      setstatus: 0,
      userid: 0,
      url: '',
      goodsDetail: '',
      current: {},
      purchaser: '',
      approveNote: '',
      hours: 0,
      reDetail: [],
      quote: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      fileList: [],
      ListQuote: [],
      quoteRubberslist: [],
      quoteRubberstotal: 0,
      quoteHardwareslist: [],
      quoteHardwarestotal: 0,
      quoteElectronicslist: [],
      quoteElectronicstotal: 0,
      quoteDieCastingslist: [],
      quoteDieCastingstotal: 0,
      list: [],
      total: 0,
      listLoading: true,
      getapproveQuery: {
        billId: 0,
        billCode: 0 },
      getQuery: {
        quoteId: 0,
        billCode: 1 },
      listQuery: {
        adminId: undefined,
        codeId: undefined,
        orderStatusArray: [],
        page: undefined,
        limit: undefined,
        sort: undefined,
        order: undefined
      },
      listCeoQuery: {
        id: undefined,
        adminId: undefined,
        codeId: undefined,
        orderStatusArray: [],
        page: undefined,
        limit: undefined,
        sort: undefined,
        order: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      statusMap,
      statusMap2,
      statusMap1,
      statusMap3,
      shipDialogVisible: false,
      downloadLoading: false,
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false
      },
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
    // this.init()
    // setTimeout(function() {}, 500)
    this.getList()
  },
  mounted() {
  },

  methods: {
    getList() {
      this.listLoading = true
      // alert(JSON.stringify(this.listQuery))
      listBrowser(this.listQuery).then(response => {
        // console.log(response)
        // console.log(JSON.stringify(response.data.data.data.Rubbers.data.list))
        this.current = Object.assign({}, response.data.data.data.currentUser)
        this.quote = Object.assign({}, response.data.data.data.quoteBills)
        this.listAdmin = response.data.data.data.optionsAdmin
        this.modelNameList = response.data.data.data.quoteModel
        this.quoteRubberslist = response.data.data.data.Rubbers.data.list
        this.quoteRubberstotal = response.data.data.data.Rubbers.data.total
        this.quoteHardwareslist = response.data.data.data.Hardwares.data.list
        this.quoteHardwarestotal = response.data.data.data.Hardwares.data.total
        this.quoteElectronicslist = response.data.data.data.Electronics.data.list
        this.quoteElectronicstotal = response.data.data.data.Electronics.data.total
        this.quoteDieCastingslist = response.data.data.data.DieCastings.data.list
        this.quoteDieCastingstotal = response.data.data.data.DieCastings.data.total
        sessionStorage.setItem('userid', this.current.id)
        this.rubberVisiable = true
        this.rubberCardVisiable = true
        this.listLoading = false
        this.handleClick()
      }).catch(() => {
        this.quoteRubberslist = []
        this.quoteRubberstotal = 0
        this.quoteHardwareslist = []
        this.quoteHardwarestotal = 0
        this.quoteElectronicslist = []
        this.quoteElectronicstotal = 0
        this.quoteDieCastingslist = []
        this.quoteDieCastingstotal = 0
        this.$notify.error({ title: '失败', message: '采購員数据没取出来数据' })
        this.listLoading = false
      })
    },
    showDetail(row) {
      this.DialogVisiable = true
      this.goodsDetail = row
    },
    getListCeo() {
      this.listLoading = true
      listBrowserCeo(this.listQuery).then(response => {
        console.log(response)
        this.current = Object.assign({}, response.data.data.data.currentUser)
        this.quote = Object.assign({}, response.data.data.data.quoteBills)
        this.listAdmin = response.data.data.data.optionsAdmin
        this.modelNameList = response.data.data.data.quoteModel
        this.quoteRubberslist = response.data.data.data.Rubbers.data.list
        this.quoteRubberstotal = response.data.data.data.Rubbers.data.total
        this.quoteHardwareslist = response.data.data.data.Hardwares.data.list
        this.quoteHardwarestotal = response.data.data.data.Hardwares.data.total
        this.quoteElectronicslist = response.data.data.data.Electronics.data.list
        this.quoteElectronicstotal = response.data.data.data.Electronics.data.total
        this.quoteDieCastingslist = response.data.data.data.DieCastings.data.list
        this.quoteDieCastingstotal = response.data.data.data.DieCastings.data.total
        sessionStorage.setItem('userid', this.current.id)
        this.handleClick()
      })
      this.listLoading = false
    },
    getListOK() {
      this.listLoading = true
      listBrowserOK(this.listQuery).then(response => {
        console.log(response)
        this.current = Object.assign({}, response.data.data.data.currentUser)
        this.quote = Object.assign({}, response.data.data.data.quoteBills)
        this.listAdmin = response.data.data.data.optionsAdmin
        this.modelNameList = response.data.data.data.quoteModel
        this.quoteRubberslist = response.data.data.data.Rubbers.data.list
        this.quoteRubberstotal = response.data.data.data.Rubbers.data.total
        this.quoteHardwareslist = response.data.data.data.Hardwares.data.list
        this.quoteHardwarestotal = response.data.data.data.Hardwares.data.total
        this.quoteElectronicslist = response.data.data.data.Electronics.data.list
        this.quoteElectronicstotal = response.data.data.data.Electronics.data.total
        this.quoteDieCastingslist = response.data.data.data.DieCastings.data.list
        this.quoteDieCastingstotal = response.data.data.data.DieCastings.data.total
        sessionStorage.setItem('userid', this.current.id)
        this.handleClick()
      }).catch(() => {
        this.quoteRubberslist = []
        this.quoteRubberstotal = 0
        this.quoteHardwareslist = []
        this.quoteHardwarestotal = 0
        this.quoteElectronicslist = []
        this.quoteElectronicstotal = 0
        this.quoteDieCastingslist = []
        this.quoteDieCastingstotal = 0
        this.$notify.error({ title: '失败', message: '中标数据没取出来数据' })
      })
      this.listLoading = false
    },
    handleClick() {
      this.falseVisiable()
      if (this.tab === 'dz') {
        this.trueVisiable(6)
      } else if (this.tab === 'sl') {
        this.trueVisiable(3)
      } else if (this.tab === 'wj') {
        this.trueVisiable(5)
      } else if (this.tab === 'yz') {
        this.trueVisiable(4)
      }
    },
    falseVisiable() {
      this.rubberCardVisiable = false
      this.electronicCardVisiable = false
      this.hardwareCardVisiable = false
      this.dieCastingCardVisiable = false
    },
    trueVisiable(modelId) {
      if (modelId === 3) { this.rubberCardVisiable = true }
      if (modelId === 4) { this.dieCastingCardVisiable = true }
      if (modelId === 5) { this.hardwareCardVisiable = true }
      if (modelId === 6) { this.electronicCardVisiable = true }
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
    selectSupply(val) {
      console.log(val)
      for (let i = 0; i < this.modelNameList.length; i++) {
        if (this.modelNameList[i]['value'] === val) {
          this.$set(this.dataForm, 'quoteSupplyCode', this.modelNameList[i].supply)
          this.$set(this.dataForm, 'approveCode', this.modelNameList[i].approveCode)
          this.$set(this.dataForm, 'dutyCode', this.modelNameList[i].duty)
          this.$set(this.dataForm, 'ceoCode', this.modelNameList[i].ceoCode)
          this.$set(this.dataForm, 'notice', this.modelNameList[i].notice)
          this.$set(this.dataForm, 'ceoChoice', this.modelNameList[i].supply)
          this.$set(this.dataForm, 'dutyChoice', this.modelNameList[i].supply)
        }
      }
    },
    selectDuty(val) {
      this.$set(this.dataForm, 'dutyChoice', this.quote.dutyChoice)
    },
    selectCeo(val) {
      this.$set(this.quote, 'ceoChoice', this.quote.dutyChoice)
    },

    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
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

    handleCeoFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    isSign() {
      for (let i = 0; i < this.reQuote.length; i++) {
        if (this.reQuote[i].status > 0) {
          return this.formatRole(this.reQuote[i].adminId)
        }
      }
      return ''
    },

    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '是否删除', '收货人', '收货联系电话', '收货地址']
        const filterVal = ['id', 'quoteSn', 'userId', 'quoteStatus', 'isDelete', 'consignee', 'mobile', 'address']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
        this.downloadLoading = false
      })
    },

    printOrder() {
      this.$print(this.$refs.print)
      this.quoteDialogVisible = false
    }
  }
}
</script>

