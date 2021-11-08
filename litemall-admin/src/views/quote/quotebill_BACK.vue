<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.id" clearable class="filter-item" style="width: 160px;" placeholder="请输入询价单ID" />
      <el-input v-model="listQuery.purchaser" clearable class="filter-item" style="width: 160px;" placeholder="请输入采购员名字" />
      <el-input v-model="listQuery.quoteSupplyName" clearable class="filter-item" style="width: 160px;" placeholder="请输入供应商名称" />
      <el-date-picker v-model="listQuery.timeArray" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择询价单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/quoteBill/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">询价员查询</el-button>
      <el-button v-permission="['GET /admin/quoteBill/listCeo']" class="filter-item" type="primary" icon="el-icon-search" @click="getListCeo">询价领导查询</el-button>
      <el-button v-permission="['POST /admin/quoteBill/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">新建</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" bquote fit highlight-current-row>
      <el-table-column align="center" min-width="100" label="ID" prop="id" />
      <el-table-column align="center" label="采购员" prop="adminId">
        <template slot-scope="scope">
          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="模板" prop="modelName">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.modelName!==null "> {{ formatModel(scope.row.modelName) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="描述" prop="purchaserNote" />
      <el-table-column align="center" label="询价单状态" prop="quoteStatusFilter">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.status | quoteStatusFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="供应商" prop="quoteSupplyCodeId">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.quoteSupplyCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" property="quoteModelExcel" label="询价单模板Excel样式" prop="quoteModelExcel">
        <template slot-scope="scope">
          <el-button v-if="scope.row.quoteModelExcel !== undefined && scope.row.quoteModelExcel !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcel,'询价ID'+(scope.row.id).toString()+'.xlsx')">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="show" align="center" label="供应商名称" prop="quoteSupplyCode" />
      <el-table-column align="center" label="建档时间" prop="addTime" />
      <el-table-column align="center" label="报价截止日期" prop="deadDate" />
      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status===0 || scope.row.status===7" v-permission="['POST /admin/quoteBill/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="scope.row.status===0" v-permission="['POST /admin/quoteBill/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-permission="['POST /admin/quoteBill/submit']" type="primary" size="mini" @click="getApprove(scope.row)">详情及审批</el-button>
          <!--          <el-button v-if="scope.row.status===1||scope.row.status===2" v-permission="['POST /admin/quoteBill/cancle']" type="primary" size="mini" @click="handleRefund(scope.row)">撤销</el-button>-->
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="quoteDialogVisible" width="1200">
      <el-card v-show="quote.status !== 6" class="box-card">
        <div slot="header">
          <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
            <el-form-item v-show="(quote.status===3 || quote.status === 5) " label="负责人选择">
              <el-tag v-for="roleId in quote.dutyChoice" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag>
            </el-form-item>
            <!--            <el-form-item v-show="quote.status === 6" label="ceo选择">-->
            <!--              <el-tag v-for="roleId in quote.ceoChoice" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatAdmin(roleId) }} </el-tag>-->
            <!--            </el-form-item>-->
            <el-form-item v-show="(quote.status === 2 || quote.status === 4) && dutyShow" label="负责人选择" prop="dutyChoice">
              <el-select v-model="quote.dutyChoice" multiple placeholder="请选择" clear @change="selectCeo($event)">
                <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item v-show="(quote.status===3 || quote.status === 5) && ceoShow" label="ceo选定" prop="ceoChoice">
              <el-select v-model="quote.ceoChoice" multiple placeholder="请选择" clear>
                <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item v-show=" quote.status === 2 && dutyShow" label="是否会签" prop="toCeo">
              <el-radio-group v-model="quote.isCeo" @change="changeHandler1">
                <el-radio :label="true">提交评估小组</el-radio>
                <el-radio :label="false">直接总经理审批</el-radio>
              </el-radio-group>
            </el-form-item>
            <!--            <el-form-item v-show=" quote.status === 4 && dutyShow" label="会审" prop="toCeo">-->
            <!--              <el-radio-group v-model="quote.isReapprove" @change="changeHandler2">-->
            <!--                <el-radio :label="false">让采购员 通知供应商 重新报价</el-radio>-->
            <!--                <el-radio :label="true">直接总经理审批</el-radio>-->
            <!--              </el-radio-group>-->
            <!--            </el-form-item>-->
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
            <!--<el-button style="float: right" type="warning" @click="handleCancle(quote)" v-if="quote.status!=0 && quote.status != 1 && quote.status !=8 ">撤销提交</el-button>&ndash;&gt;-->
            <el-button v-if="(quote.status === 0) && adminShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'提交询价单',1,'询价单','报价人签收',1)">提交询价单</el-button>
            <el-button v-if="(quote.status === 7) && adminShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'重新提交询价单',1,'询价单','报价人签收',1)">重新提交询价单</el-button>
            <el-button v-if="(quote.status === 2) && !chkgroup && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'提交总经理',1,'询价单','结案',3,true,quote.dutyChoice)">提交ceo</el-button>
            <el-button v-if="(quote.status === 2) && chkgroup && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'负责人提交会签',1,'询价单','责任人审批',4)">提交会签</el-button>
            <el-button v-if="(quote.status === 4 || quote.status === 9) && signShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'会审',1,'询价单','负责人提交ceo审批',9)">会签</el-button>
            <el-button v-if="(quote.status === 3 || quote.status === 5) && ceoShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'ceo审批',1,'询价单','结案',6,true,quote.ceoChoice)">ceo审批</el-button>
            <el-button v-if="quote.status === 9 && !chkceo && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'重新议价',1,'询价单','采购员报价',7,true,quote.dutyChoice)">重新议价</el-button>
            <el-button v-if="quote.status === 9 && chkceo && dutyShow" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'议价后提交ceo',1,'询价单','ceo审批',5,false,quote.dutyChoice)">议价后提交ceo</el-button>
            <el-button v-if="quote.status===8" v-permission="['POST /admin/quoteBill/submit']" class="filter-item" type="primary" @click="handleSubmit(quote,approveNote,'结案',1,'询价单','结案')">结案</el-button>
          </el-form>
        </div>
      </el-card>

      <el-card v-show="quote.status < 2" class="box-card">
        <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
          <el-form-item label="询价商家">
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
          </el-form-item>
        </el-form>
      </el-card>

      <el-card v-show="quote.status >= 2  && (ceoShow || signShow || dutyShow)" class="box-card">
        <h4>报价信息</h4>
        <el-table :data="reQuote" border fit highlight-current-row>
          <el-table-column align="center" label="状态" prop="status">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.status | quoteStatus1Filter }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="接收人" prop="adminId">
            <template slot-scope="scope">
              <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="描述" prop="purchaserNote" />
          <el-table-column align="center" label="报价单" prop="quoteModelExcelSupply">
            <template slot-scope="scope">
              <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcelSupply,'报价单ID' + (scope.row.id).toString() + '.xlsx')">下载</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" label="提交时间" prop="submitDate" />
        </el-table>
      </el-card>
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

    </el-dialog>

    <!--修改详情对话框 -->

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="200px">
        <!-- 发货对话框 -->
        <el-form-item label="选择模板" prop="modelName">
          <el-select v-model="dataForm.modelName" clearable @change="selectSupply($event)">
            <el-option v-for="item in modelNameList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报价单需求" prop="version">
          <label>
            <textarea v-model="dataForm.purchaserNote" maxlength="200" style="width: 70%;" placeholder="描述" />
          </label>
        </el-form-item>
        <el-card class="box-card">
          <el-form-item v-if="dataForm.quoteModelExcel !== undefined && dataForm.quoteModelExcel !== null" label="上传询价单" prop="quoteModelExcel">
            <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize" accept=".xlsx">
              <el-button style="margin-left: 10px;" size="small" type="success">重新询价单</el-button>
              <div slot="tip" class="el-upload__tip">xlsx文件，且不超过20M</div>
            </el-upload>
          </el-form-item>
          <el-form-item v-if="dataForm.quoteModelExcel === undefined || dataForm.quoteModelExcel === null" label="上传询价单" prop="quoteModelExcel">
            <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize" accept=".xlsx">
              <el-button style="margin-left: 10px;" size="small" type="success">开始上传(上传之后 要按[确定]才会生效)</el-button>
              <div slot="tip" class="el-upload__tip">xlsx文件，且不超过20M</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="选择询价供应商" prop="quoteSupplyCode">
            <el-select v-model="dataForm.quoteSupplyCode" multiple placeholder="请选择" clear @change="selectDuty($event)">
              <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="添加核价小组成员" prop="approveCode">
            <el-select v-model="dataForm.approveCode" disabled multiple placeholder="请选择" clear>
              <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="核价负责人" prop="dutyCode">
            <el-select v-model="dataForm.dutyCode" disabled placeholder="请选择">
              <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
            </el-select>
            <span class="info">每个类别的核价单，存在不同的核价负责人</span>
          </el-form-item>
          <el-form-item label="终审人" prop="ceoCode">
            <el-select v-model="dataForm.ceoCode" disabled placeholder="请选择">
              <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
            </el-select>
            <span class="info">默认是CEO,最后敲定价格</span>
          </el-form-item>
          <el-form-item label="通知人" prop="notice">
            <el-select v-model="dataForm.notice" multiple disabled placeholder="请选择">
              <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
            </el-select>
            <span class="info">价格确认后 通知的人员</span>
          </el-form-item>
        </el-card>
      </el-form>
      <div class="op-container">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="detailEdit" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>
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
import { listQuote, createQuote, deleteQuote, updateQuote, listCeo, submitQuote } from '@/api/quote'
import { readQuote } from '@/api/requote'
import { openExcel } from '@/utils/quoteSubmit'
import { dingtalkSend } from '@/utils/senddingtalk'
import { getToken } from '@/utils/auth'

const statusMap = {
  0: '制单',
  1: '提交询价单',
  2: '等待初审',
  3: '提交ceo',
  4: '提交会审',
  5: '议价后提交ceo',
  6: 'ceo审批',
  7: '重新提交',
  // 8: 'ceo审批完成',
  9: '会审中'
}
const statusMap1 = {
  0: '询价',
  1: '签收',
  2: '制作报价单',
  3: '提交报价单',
  4: '甲方审批中',
  5: '撤销报价',
  6: '报价超时作废',
  8: '未中标',
  9: '中标'
}

export default {
  name: 'QuoteBill',
  components: { Pagination },
  filters: {
    quoteStatusFilter(status) {
      return statusMap[status]
    },
    quoteStatus1Filter(status) {
      return statusMap1[status]
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
      quoteDialogVisible: false,
      userid: 0,
      url: '',
      current: [],
      purchaser: '',
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
      listQuery: {
        id: undefined,
        purchaser: '',
        quoteSupplyName: '',
        status: undefined,
        orderStatusArray: undefined,
        page: undefined,
        limit: undefined,
        sort: undefined,
        order: undefined
      },
      listCeoQuery: {
        id: undefined,
        adminId: undefined,
        purchaser: '',
        quoteSupplyName: '',
        timeArray: [],
        orderStatusArray: [],
        page: 1,
        limit: 20,
        sort: 'add_time',
        order: 'desc'
      },
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
        dutyCode: undefined,
        ceoCode: undefined,
        notice: undefined,
        status: undefined
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
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
    // this.init()
    // setTimeout(function() {}, 500)
    this.getList()
  },
  mounted() {
  },

  methods: {
    getList() {
      this.listLoading = true
      if (this.listQuery.timeArray && this.listQuery.timeArray.length === 2) {
        this.listQuery.start = this.listQuery.timeArray[0]; this.listQuery.end = this.listQuery.timeArray[1]
      } else { this.listQuery.start = null; this.listQuery.end = null }
      listQuote(this.listQuery).then(response => {
        console.log(response)
        this.list = response.data.data.list.data.list
        this.total = response.data.data.list.data.total
        this.current = Object.assign({}, response.data.data.currentUser)
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        sessionStorage.setItem('userid', this.current.id)
      }).catch(() => { this.list = []; this.total = 0; this.$notify.error({ title: '失败', message: '询价单没取出来数据' }) })
      this.listLoading = false
    },
    changeHandler1(value) {
      this.chkgroup = value
      console.log(value)
    },
    changeHandler2(value) {
      this.chkceo = value
    },
    getListCeo() {
      this.listLoading = true
      if (this.listCeoQuery.timeArray && this.listCeoQuery.timeArray.length === 2) {
        this.listQuery.start = this.listCeoQuery.timeArray[0]
        this.listCeoQuery.end = this.listCeoQuery.timeArray[1]
      } else {
        this.listCeoQuery.start = null
        this.listCeoQuery.end = null
      }
      listCeo(this.listCeoQuery).then(response => {
        console.log(response)
        this.list = response.data.data.list.data.list
        this.total = response.data.data.list.data.total
        this.current = Object.assign({}, response.data.data.currentUser)
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        sessionStorage.setItem('userid', this.current.id)
      }).catch(() => {
        this.list = []
        this.total = 0
        this.$notify.error({ title: '失败', message: 'ceo操作没取出来数据' })
      })
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

    getApprove(row) {
      this.quote = row
      if (row.ceoCode === parseInt(sessionStorage.getItem('userid'))) { this.ceoShow = true }
      if (row.dutyCode === parseInt(sessionStorage.getItem('userid'))) { this.dutyShow = true }
      if (row.adminId === parseInt(sessionStorage.getItem('userid'))) { this.adminShow = true }
      if ((row.approveCode).toString().indexOf(sessionStorage.getItem('userid')) >= 0) { this.signShow = true }
      this.quoteDialogVisible = true
      this.getQuery.quoteId = row.id
      this.getQuery.billCode = 0
      readQuote(this.getQuery).then(response => {
        console.log(JSON.stringify(response))
        this.reQuote = response.data.data.reQuote
        this.approve = response.data.data.ApproveInfoList
      }).catch(() => { this.$notify.error('审批数据没取出来') })
      this.quoteDialogVisible = true
    },
    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    resetForm() {
      this.dataForm = {
        id: 0,
        adminId: 0,
        purchaser: undefined,
        quoteModelExcel: undefined,
        quoteEndDate: undefined,
        quoteSupplyCode: undefined,
        quoteSupplyName: undefined,
        approveCode: undefined,
        dutyCode: undefined,
        ceoCode: undefined,
        notice: undefined,
        status: undefined
      }
    },
    handleCreate() {
      // this.resetForm()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => { this.$refs['dataForm'].clearValidate() })
    },
    createData() {
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dataForm.adminId = sessionStorage.getItem('userid')
      this.dataForm.status = 0
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createQuote(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({ title: '成功', message: '新增询价单成功' })
            }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        }
      })
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
    handleDelete(row) {
      this.$confirm('您确定删除吗？').then(_ => {
        deleteQuote(row).then(response => {
          this.$notify.success({ title: '成功', message: response.data.ok })
          this.getList()
        }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
      })
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
    handleCancle(row) {
      // let sj = this.isSign()
      // if (sj != '') { this.$notify.success({ title: '失败:',  message: sj+'已签收报价单,只能对付撤销签收 才能修改询价单' }); return }
      //
      // this.$confirm('您确定撤销已经发放的询价单吗？').then(_ => {
      //   console.log('submit begin:')
      //   this.$refs['quote'].validate(valid => {
      //     if (valid) { this.quote.status = 0
      //       updateQuote(this.quote).then(() => {
      //         for (const v of this.list) { if (v.id === this.quote.id) { const index = this.list.indexOf(v); this.list.splice(index, 1, this.quote); break } }
      //         this.$notify.success({ title: '成功', message: '更新成功' }) })
      //         .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
      //       }
      //   })
      //   cancleQuote({ id: this.quote.id, quoteId: this.quote.quoteId, quoteSupplyCode: this.quote.quoteSupplyCode })
      //     .then(response => { this.$notify.success({ title: '成功', message: '发放成功' }) })
      //     .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
      //   this.getList()
      //   this.$notify.success({ title: '询价单', message: '已经通知供应商报价,通知在钉钉 待办任务 里面' })
      // })
    },
    handleSubmit(row, approveNote, action, billcode, billname, nextaction, setstatus, choiceid, choicevalue) {
      if (row.status === 9 && approveNote === '') {
        this.$notify.error({ title: '失败', message: '会签必须在录入区内 签署意见' })
        return false
      }
      if (row.status === 0 || row.status === 7) {
        this.quoteDialogVisible = true
        if (row.quoteModelExcel === undefined || row.quoteModelExcel === null) {
          this.$notify.success({ title: '失败', message: '必须存在Excel询价单' })
          return false
        }
        if (row.quoteSupplyCode === undefined || row.quoteSupplyCode === null) {
          this.$notify.error({ title: '失败', message: '必须指定接收询价的供应商' })
          return false
        }

        if (row.dutyCode !== row.adminId) {
          for (let i = 0; i < this.listAdmin.length; i++) {
            if (row.quoteSupplyCode.indexOf(this.listAdmin[i].value) >= 0) {
              if (this.listAdmin[i].capacity.indexOf('AB') < 0) {
                this.$notify.error({ title: '你不能提交审批', message: this.listAdmin[i].deptname + ':不是AB类供应商,需要[' + this.formatRole(row[i].dutyCode) + ']提交询价,并钉钉通知其本人' })
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
          choicevalue: choicevalue
        }).then(response => { this.$notify.success({ title: '成功', message: '发放成功' }); this.getList(); this.quoteDialogVisible = false })
          .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        console.log('submit row:' + JSON.stringify(row))
        this.$message.success({ title: '询价单审批', message: '已经通知供应商报价,通知在钉钉 待办任务 里面' })
        // this.list.status = setstatus
      })
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

