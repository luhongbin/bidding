<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入模板名称" />
      <el-input v-model="listQuery.version" clearable class="filter-item" style="width: 200px;" placeholder="请输入版本号" />
      <el-button v-permission="['GET /admin/quoteModel/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <!--<el-button v-permission="['POST /admin/quoteModel/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">新建</el-button>-->
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="单号" prop="id" />
      <el-table-column align="center" label="模板名称" prop="name" />
      <el-table-column align="center" label="版本号" prop="version" />
      <el-table-column align="center" property="picUrl" label="询价单模板Excel样式" prop="excel">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-download" @click="openExcel(scope.row.excel)">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="核价负责人" prop="duty">
        <template slot-scope="scope">
          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.duty) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="默认供应商" prop="code">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.code" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="预审人员" prop="preApprove">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.preApprove" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="终审负责人" prop="ceoCode">
        <template slot-scope="scope">
          <el-tag style="margin-right: 20px;"> {{ formatRole(scope.row.ceoCode) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="核价小组" prop="approveCode">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.approveCode" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="通知人" prop="notice">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.notice" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.status ? '启用' : '不启用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="创建人" prop="creator">
        <template slot-scope="scope">
          <el-tag>{{ formatRole(scope.row.creator) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="更新人" prop="modify">
        <template slot-scope="scope">
          <el-tag>{{ formatRole(scope.row.modify) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="更新时间" prop="update_time" />

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/quoteModel/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <!-- <el-button v-permission="['POST /admin/quoteModel/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>-->
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
  </div>
</template>

<script>
import { list, quotedelete } from '@/api/quotemodel'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import { optionsAdmin } from '@/api/admin'
import { openExcel } from '@/utils/quoteSubmit'

export default {
  name: 'QuoteModel',
  components: { Pagination },
  data() {
    return {
      uploadPath,
      addVisiable: false,
      fileList: [],
      list: [],
      listAdmin: [],
      UserList: [],
      totalAdmin: 0,
      total: 0,
      listLoading: true,
      selectedlist: [],
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        version: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      rules: {
        name: [{ required: true, message: '必须有模板名称', trigger: 'blur' }],
        duty: [{ required: true, message: '必须有核价负责人', trigger: 'blur' }],
        code: [{ required: true, message: '必须有供应商', trigger: 'blur' }],
        ceoCode: [{ required: true, message: '必须有终审负责人', trigger: 'blur' }],
        approveCode: [{ required: true, message: '必须有核价小组成员', trigger: 'blur' }],
        notice: [{ required: true, message: '必须存在通知人员', trigger: 'blur' }]
      },
      downloadLoading: false
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
    optionsAdmin().then(response => { this.UserList = response.data.data.list }).catch(response => { this.$notify.error({ title: 'optionsAdmin失败', message: response.data.errmsg }) })
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      list(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      })
        .catch(() => { this.list = []; this.total = 0; this.listLoading = false })
        .catch(response => { this.$notify.error({ title: 'list失败', message: response.data.errmsg }) })
    },

    formatRole(roleId) {
      for (let i = 0; i < this.UserList.length; i++) {
        if (roleId === this.UserList[i].value) {
          return this.UserList[i].deptname
        }
      }
      return ''
    },

    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleFilterAdmin() {
      this.listQueryAdmin.page = 1
      this.getListAdmin()
    },

    openExcel(url, name) {
      openExcel(url, name)
    },

    beforeUpload(file) {
      const fileExt = file.name.split('.').pop().toLocaleLowerCase()
      if (fileExt === 'xlsx') {
        this.file = file
        this.readData(file)
      } else {
        this.$notify.error({ title: '失败', message: '请选择后缀为xlsx文件' })
      }
    },

    handleCreate() {
      this.$router.push({ path: '/config/quotemodel-create' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/config/quotemodel-edit', query: { id: row.id }})
    },
    handleDelete(row) {
      quotedelete(row).then(response => { this.$notify.success({ title: '成功', message: '删除成功' }); this.getList() })
        .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '品牌商ID',
          '品牌商名称',
          '介绍',
          '低价',
          '品牌商图片'
        ]
        const filterVal = ['id', 'name', 'desc', 'floorPrice', 'picUrl']
        excel.export_json_to_excel2(
          tHeader,
          this.list,
          filterVal,
          '品牌商信息'
        )
        this.downloadLoading = false
      })
    }
  }
}
</script>
