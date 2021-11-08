<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.adminId" clearable class="filter-item" style="width: 160px;" placeholder="请输入报价单ID" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择报价单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/requote/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['GET /admin/requote/listCeo']" class="filter-item" type="primary" icon="el-icon-search" @click="getListCeo">领导查询</el-button>
      <el-button v-permission="['GET /admin/quoteBill/search']" class="filter-item" type="primary" icon="el-icon-search" @click="handleSearch">商品浏览</el-button>
      <!-- <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>-->
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" bquote fit highlight-current-row>
      <el-table-column align="center" min-width="100" label="报价ID" prop="id" />
      <el-table-column align="center" min-width="100" label="询价ID" prop="quoteId" />
      <el-table-column align="center" label="询价单附件" prop="quoteModelExcelSupply">
        <template slot-scope="scope">
          <el-button v-if="scope.row.quoteModelExcelSupply !== undefined && scope.row.quoteModelExcelSupply !== null" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.quoteModelExcelSupply, '询价ID'+(scope.row.quoteId).toString()+'.xlsx')">下载</el-button>
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
      <el-table-column align="center" label="报价单附件" prop="requoteExcel">
        <template slot-scope="scope">
          <el-button v-if="scope.row.requoteExcel !== undefined && scope.row.requoteExcel !== null && scope.row.requoteExcel.length !== 0" size="mini" type="info" icon="el-icon-download" plain @click="openExcel(scope.row.requoteExcel, '报价ID'+(scope.row.id).toString()+'.xlsx')">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="定标通知时间" prop="quoteDate" />
      <el-table-column align="center" label="报价截止日期" prop="deadDate" />
      <el-table-column align="center" label="提交报价日期" prop="submitDate" />
      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="(scope.row.status===0 || scope.row.status===10) && scope.row.status!==6" v-permission="['POST /admin/requote/update']" type="primary" size="mini" @click="getApprove(scope.row)">签收</el-button>
          <el-button v-if="(scope.row.status===1) && scope.row.status!==6" v-permission="['POST /admin/requote/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">报价</el-button>
          <el-button v-if="scope.row.status >= 1" v-permission="['POST /admin/requote/submit']" type="primary" size="mini" @click="getApprove(scope.row)">详情</el-button>
        </template>
      </el-table-column>
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
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { uploadPath } from '@/api/storage'
import { listReQuote, listReCeo } from '@/api/requote'
import { openExcel } from '@/utils/quoteSubmit'
import { getToken } from '@/utils/auth'

const statusMap = {
  0: '询价',
  1: '签收',
  2: '制作报价单',
  3: '提交报价单',
  4: '甲方审批中',
  5: '报价',
  6: '报价超时作废',
  8: '流标',
  9: '开标',
  10: '重新报价',
  11: '终止询价'
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
      approve: [],
      reQuote: [],
      supply: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],

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
    handleSearch() {
      this.$router.push({ path: '/supplyManage/requotelist' })
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
    handleUpdate(row) {
      this.$router.push({ path: '/supplyManage/requote-edit', query: { id: row.id, row: row }})
    },
    getApprove(row) {
      this.$router.push({ path: '/supplyManage/requote-approve', query: { row: row }})
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
