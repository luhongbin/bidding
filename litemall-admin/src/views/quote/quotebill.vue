<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.id" clearable class="filter-item" style="width: 160px;" placeholder="请输入询价单ID" />
      <el-input v-model="listQuery.adminid" clearable class="filter-item" style="width: 160px;" placeholder="请输入采购员ID" />
      <el-input v-model="listQuery.dutyid" clearable class="filter-item" style="width: 160px;" placeholder="请输入负责人ID" />
      <el-date-picker v-if="show1" v-model="listQuery.timeArray" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.status" multiple style="width: 200px" class="filter-item" placeholder="请选择询价单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/quoteBill/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
      <el-button v-permission="['GET /admin/quoteBill/listCeo']" class="filter-item" type="primary" icon="el-icon-search" @click="getListCeo">领导查询</el-button>
      <el-button v-permission="['GET /admin/quoteBill/search']" class="filter-item" type="primary" icon="el-icon-search" @click="handleSearch">分类查询</el-button>
      <!--<el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>-->
    </div>
    <div>
      <el-button v-permission="['POST /admin/quoteBill/create']" class="filter-item" align="left" type="primary" icon="el-icon-edit" @click="handleCreate">新建询价单</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" bquote fit highlight-current-row>
      <el-table-column align="center" min-width="50" label="单号" prop="id" />
      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status===0" v-permission="['POST /admin/quoteBill/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">修改</el-button>
          <!--          <el-button v-if="scope.row.status===0" v-permission="['POST /admin/quoteBill/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>-->
          <el-button type="success" round size="mini" @click="handleApprove(scope.row)">详情</el-button>
          <!--          <el-button v-if="scope.row.status===1||scope.row.status===2" v-permission="['POST /admin/quoteBill/cancle']" type="primary" size="mini" @click="handleRefund(scope.row)">撤销</el-button>-->
        </template>
      </el-table-column>
      <el-table-column align="left" label="询价单状态" prop="quoteStatusFilter">
        <template slot-scope="scope">
          <el-tag effect="dark" v-if = "scope.row.status == 2" type="danger">{{ scope.row.status | quoteStatusFilter }}</el-tag>
          <el-tag effect="dark" v-else-if = "scope.row.status == 6" type="error">{{ scope.row.status | quoteStatusFilter }}</el-tag>
          <el-tag effect="dark" v-else type="success">{{ scope.row.status | quoteStatusFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" v-if="(totalId===1)" label="采购员" prop="adminId">
        <template slot-scope="scope">
          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.adminId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="left" label="产品类别" prop="modelName">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.modelName!==null "> {{ formatModel(scope.row.modelName) }} </el-tag>
        </template>
      </el-table-column>
      <!--      <el-table-column align="center" label="负责人" prop="dutyCode">-->
      <!--        <template slot-scope="scope">-->
      <!--          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.dutyCode) }} </el-tag>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column align="left" label="主题" prop="purchaserNote" />
      <el-table-column align="left" label="供应商" prop="quoteSupplyCodeId">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.quoteSupplyCode" :key="roleId" type="primary" style="margin-left: 0px;"> {{ formatAdmin(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="show" align="left" label="供应商名称" prop="quoteSupplyCode" />
      <el-table-column align="center" property="quoteModelExcel" label="询价单附件" prop="quoteModelExcel">
        <template slot-scope="scope">
          <el-button v-if="scope.row.quoteModelExcel !== undefined && scope.row.quoteModelExcel !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcel,'询价'+scope.row.id.toString())">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" width="150" label="建档时间" prop="addTime" />
      <el-table-column align="center" width="150"  label="报价截止日期" prop="deadDate" />
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
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
import { uploadPath, readkey, readId } from '@/api/storage'
import { listQuote, deleteQuote, listCeo } from '@/api/quote'
import { openExcel } from '@/utils/quoteSubmit'
import { getToken } from '@/utils/auth'
import { currentuser } from '@/api/profile'

const statusMap = {
  0: '制单',
  1: '提交询价单',
  2: '等待初审',
  3: '提交ceo',
  4: '提交会审',
  5: '议价后提交ceo',
  6: '已结束',
  7: '重新提交',
  8: '重新提交完毕',
  9: '会审中',
  10: '终止询价',
  11: '整单流标',
  12: 'ceo退回负责人',
  13: '通知预审人会签',
  14: '预审供应商',
  15: '预审后提交询价单'
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
  9: '已开标',
  10: '重新询价',
  11: '终止询价',
  12: '签收重新询价'
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
      show1: false,
      show: false,
      ceoShow: false,
      dutyShow: false,
      signShow: false,
      adminShow: false,
      chkgroup: false,
      chkceo: false,
      totalId: 0,
      uploadPath,
      setstatus: 0,
      quoteDialogVisible: false,
      userid: 0,
      url: '',
      current: [],
      purchaser: '',
      approveNote: '',
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
        page: 1,
        limit: 20,
        id: undefined,
        adminid: undefined,
        dutyid: undefined,
        timeArray: [],
        status: [],
        sort: 'add_time',
        order: 'desc'
      },
      listCeoQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        adminId: undefined,
        dutyid: undefined,
        timeArray: [],
        status: [],
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
    this.begin()
  },
  mounted() {
  },
  methods: {
    begin() {
      currentuser().then(response => {
        this.current = Object.assign({}, response.data.data.currentUser)
        // alert('asd')
        if (this.current.capacity.indexOf('ceo') >= 0) {
          this.listCeoQuery.status = [3, 5]
          listCeo(this.listCeoQuery).then(response => {
            this.totalId = 1
            console.log(response)
            this.list = response.data.data.list.data.list
            this.total = response.data.data.list.data.total
            this.current = Object.assign({}, response.data.data.currentUser)
            this.listAdmin = response.data.data.optionsAdmin
            this.modelNameList = response.data.data.quoteModel
            sessionStorage.setItem('userid', this.current.id)
            this.listLoading = false
          })
        } else if (this.current.capacity.indexOf('采购员') >= 0) {
          this.listCeoQuery.status = [0, 1, 2, 7, 15]
          listQuote(this.listCeoQuery).then(response => {
            this.totalId = 0
            this.list = response.data.data.list.data.list
            this.total = response.data.data.list.data.total
            this.current = Object.assign({}, response.data.data.currentUser)
            this.listAdmin = response.data.data.optionsAdmin
            this.modelNameList = response.data.data.quoteModel
            sessionStorage.setItem('userid', this.current.id)
            console.log(response)
            this.listLoading = false
          })
        } else {
          this.listCeoQuery.status = [2, 3, 4, 5, 6, 8, 9, 10, 13, 14]
          listCeo(this.listCeoQuery).then(response => {
            this.totalId = 1
            console.log(response)
            this.list = response.data.data.list.data.list
            this.total = response.data.data.list.data.total
            this.current = Object.assign({}, response.data.data.currentUser)
            this.listAdmin = response.data.data.optionsAdmin
            this.modelNameList = response.data.data.quoteModel
            sessionStorage.setItem('userid', this.current.id)
            this.listLoading = false
          })
        }
      })
      this.listLoading = false
    },
    getList() {
      this.listLoading = true
      if (this.listQuery.timeArray && this.listQuery.timeArray.length === 2) {
        this.listQuery.start = this.listQuery.timeArray[0]; this.listQuery.end = this.listQuery.timeArray[1]
      } else { this.listQuery.start = null; this.listQuery.end = null }
      listQuote(this.listQuery).then(response => {
        this.totalId = 0
        this.list = response.data.data.list.data.list
        this.total = response.data.data.list.data.total
        this.current = Object.assign({}, response.data.data.currentUser)
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        sessionStorage.setItem('userid', this.current.id)
        console.log(response)
        this.listLoading = false
      }).catch(() => {
        this.list = []; this.total = 0
        this.listLoading = false
        this.$store.dispatch('tagsView/delView', this.$route)
        this.$router.push({ path: '/quoteManage/quotebill' })
      })
      this.listLoading = false

        // this.$notify.error({ title: '失败', message: '询价单没取出来数据' }) })
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
      this.listCeoQuery=this.listQuery
      listCeo(this.listCeoQuery).then(response => {
        this.totalId = 1
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

    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },

    handleCreate() {
      this.$router.push({ path: '/quoteManage/quotebill-create' })
    },
    handleSearch() {
      this.$router.push({ path: '/quoteManage/quotelist' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/quoteManage/quotebill-edit', query: { id: row.id }})
    },
    handleApprove(row) {
      this.$router.push({ path: '/quoteManage/quotebill-approve', query: { row: row }})
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
      readkey(url).then(response => { openExcel(url, name+'_'+ response.data.data.name) }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
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

