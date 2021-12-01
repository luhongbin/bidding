<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.adminId" clearable class="filter-item" style="width: 160px;" placeholder="请输入报价单ID" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择报价单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/requote/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['GET /admin/requote/listCeo']" class="filter-item" type="primary" icon="el-icon-search" @click="getListCeo">报价领导查询</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" bquote fit highlight-current-row>
      <el-table-column align="center" min-width="100" label="报价ID" prop="id" />
      <el-table-column align="center" min-width="100" label="询价ID" prop="quoteId" />
      <el-table-column align="center" label="询价单模板" prop="quoteModelExcelSupply">
        <template slot-scope="scope">
          <el-button size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcelSupply, '询价ID'+(scope.row.id).toString()+'.xlsx')">下载</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="简单描述" prop="note" />

      <el-table-column align="center" label="报价单状态" prop="quoteStatusFilter">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.status | quoteStatusFilter }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="供应商名称" prop="adminId">
        <template slot-scope="scope">
          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="定标通知时间" prop="quoteDate" />
      <el-table-column align="center" label="报价截止日期" prop="deadDate" />
      <el-table-column align="center" label="提交报价日期" prop="submitDate" />
      <el-table-column align="center" property="quoteModelExcel" label="报价单" prop="requoteExcel">
        <template slot-scope="scope">
          <el-button v-if="scope.row.requoteExcel !== '' && scope.row.requoteExcel !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel,'报价单ID' + scope.row.id + '.xlsx')">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status===0 && scope.row.status!==6" v-permission="['POST /admin/requote/update']" type="primary" size="mini" @click="getApprove(scope.row)">签收</el-button>
          <el-button v-if="(scope.row.status===1) && scope.row.status!==6" v-permission="['POST /admin/requote/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="scope.row.status >= 1 && scope.row.status!==6" v-permission="['POST /admin/requote/submit']" type="primary" size="mini" @click="getApprove(scope.row)">详情及审批</el-button>
        </template>
      </el-table-column>

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    <!-- 报价单详情对话框 -->
    <el-dialog :visible.sync="quoteDialogVisible" width="1200">
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
          <el-form-item label="询价主题">
            <span>{{ quote.purchaserNote }}</span>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card v-show="reQuote.status >0" class="box-card">
        <div v-show="reQuote.status<2" slot="header">
          <label>
            <textarea v-model="approveNote" maxlength="200" style="width: 100%;" placeholder="建议" />
          </label>
          <el-button v-if="reQuote.status===3" style="float: right" type="warning" @click="handleCancle(reQuote)">撤销提交</el-button>
          <el-button v-if="reQuote.status===0" class="filter-item" type="primary" @click="handleSubmit(reQuote,approveNote,'签收询价单',2,'报价单','提交报价单',1)">签收</el-button>
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
    </el-dialog>

    <!--修改详情对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <label>
        <textarea v-model="dataForm.note" maxlength="200" style="width: 100%;" placeholder="报价单描述" />
      </label>
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="200px">
        <el-card class="box-card">
          <el-form-item v-if="dataForm.requoteExcel !== '' && dataForm.requoteExcel !== null" label="上传报价单" prop="requoteExcel">
            <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize">
              <el-button style="margin-left: 10px;" size="small" type="success">重新询价单</el-button>
              <div slot="tip" class="el-upload__tip">只能上传一个,不超过20M</div>
            </el-upload>
          </el-form-item>

          <el-form-item v-if="dataForm.requoteExcel === '' || dataForm.requoteExcel === null" label="上传报价单" prop="requoteExcel">
            <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize">
              <el-button style="margin-left: 10px;" size="small" type="success">开始上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传一个,不超过20M</div>
            </el-upload>
          </el-form-item>
        </el-card>
      </el-form>
      <div class="op-container">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
      <!-- 制单对话框 -->
      <el-card class="box-card">
        <h4>询价信息</h4>
        <el-form ref="quote" :model="quote" status-icon label-position="left" label-width="100px">
          detailEdit
          <el-form-item label="询价主题">
            <span>{{ quote.purchaserNote }}</span>
          </el-form-item>
        </el-form>
      </el-card>
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
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { uploadPath } from '@/api/storage'
import { submitQuote } from '@/api/quote'
import { readQuote } from '@/api/requote'
import { listReQuote, listReCeo, updateRequote } from '@/api/requote'
import { openExcel } from '@/utils/quoteSubmit'
import { getToken } from '@/utils/auth'

const statusMap = {
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
  name: 'RequoteBill',
  components: { Pagination },
  filters: {
    quoteStatusFilter(status) {
      return statusMap[status]
    }
  },
  data() {
    return {
      uploadPath,
      quoteDialogVisible: false,
      editDialogVisible: false,
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
        dutyCode: undefined,
        ceoCode: undefined,
        notice: undefined,
        status: undefined
      },
      approve: [],
      reQuote: [],
      supply: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      dataForm: {
        id: 0,
        adminId: 0,
        adminName: undefined,
        quoteDate: undefined,
        note: undefined,
        requoteExcel: undefined,
        deadDate: undefined,
        submitDate: undefined,
        status: undefined
      },
      fileList: [],
      ListQuote: [],
      list: [],
      total: 0,
      getQuery: {
        quoteId: 0,
        billCode: 2 },
      listLoading: true,
      listQuery: {
        orderStatusArray: [],
        page: 1,
        limit: 20,
        sort: 'add_time',
        order: 'desc'
      },
      listCeoQuery: {
        adminId: 0,
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
  methods: {
    // checkPermission,
    getList() {
      this.listLoading = true
      listReQuote(this.listQuery).then(response => {
        this.list = response.data.data.list.data.list
        this.total = response.data.data.list.data.total
        this.current = Object.assign({}, response.data.data.currentUser)
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        sessionStorage.setItem('userid', this.current.id)
      }).catch(() => {
        this.list = []
        this.total = 0
        this.$notify.success({ title: '失败', message: '没取报价单出来数据' })
      })
      this.listLoading = false
    },

    getListCeo() {
      this.listLoading = true
      listReCeo(this.listCeoQuery).then(response => {
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
        this.$notify.success({ title: '失败', message: '管理者没取出来数据' })
      })
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
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    getApprove(row) {
      console.log('row:' + JSON.stringify(row))
      this.reQuote = row
      this.getQuery.quoteId = row.quoteId
      this.getQuery.billCode = 2
      readQuote(this.getQuery).then(response => {
        console.log(JSON.stringify(response))
        // this.reQuote = response.data.data.reQuote
        this.quote = response.data.data.Quote
        this.approve = response.data.data.ApproveInfoList
        console.log(JSON.stringify(this.reQuote))
        console.log(JSON.stringify(this.quote))
        console.log(JSON.stringify(this.approve))
      }).catch(() => { this.$notify.error('审批数据没取出来') })
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
    handleSubmit(row, approveNote, action, billcode, billname, nextaction, setstatus) {
      if (row.adminId !== parseInt(sessionStorage.getItem('userid'))) {
        this.$notify.error({ title: '失败', message: '必须官方指定的人签收' + sessionStorage.getItem('userid') })
        return
      }
      this.quoteDialogVisible = true
      if (row.status > 0) {
        if (row.requoteExcel === '' || row.requoteExcel === null || row.requoteExcel === undefined) {
          this.$notify.success({ title: '失败', message: '必须存在Excel报价单' })
          return
        }
      }
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
          receiver: this.quote.adminId }).then(response => { this.$message.success({ title: '成功', message: this.formatRole(row.adminId) + '[' + setstatus + ']成功' }) })
          .catch(response => { this.$notify.error({ title: '失败', message: '签收失败了' }) })
        this.quoteDialogVisible = false
      })
      this.getList()
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
