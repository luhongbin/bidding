<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入模板名称" />
      <el-input v-model="listQuery.version" clearable class="filter-item" style="width: 200px;" placeholder="请输入版本号" />
      <el-button v-permission="['GET /admin/quoteModel/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/quoteModel/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">新建</el-button>
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
      <el-table-column align="center" label="终审负责人" prop="duty">
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
          <el-button v-permission="['POST /admin/quoteModel/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px">

        <el-form-item label="模板名称" prop="name">
          <el-input v-model="dataForm.name" maxlength="63" /><span class="info">压铸模具类,电子五金类...</span>
        </el-form-item>
        <el-form-item label="模板版本号" prop="version">
          <el-input v-model="dataForm.version" maxlength="10" /><span class="info">同样的类别存在不同样式单可能，用版本分开</span>
        </el-form-item>
        <el-form-item label="excel源文件" prop="excel">
          <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize">
            <el-button style="margin-left: 10px;" size="small" type="success">上传到服务器</el-button>
            <div slot="tip" class="el-upload__tip">xlsx/xls文件，且不超过5M</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="核价负责人" prop="duty">
          <el-select v-model="dataForm.duty" placeholder="请选择">
            <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <span class="info">每个类别的核价单，存在不同的核价负责人</span>
        </el-form-item>
        <el-form-item label="常用供应商" prop="code">
          <el-button style="float:right;" size="mini" type="primary" @click="purchaseCreate()">创建常用供应商</el-button>
          <el-select v-model="dataForm.code" multiple placeholder="请选择">
            <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <span class="info">常用供应商，会在制作询价单时 自动填写到询价供应商中 作为默认值</span>
        </el-form-item>
        <el-form-item label="核价负责人" prop="ceoCode">
          <el-select v-model="dataForm.ceoCode" placeholder="请选择">
            <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <span class="info">终审负责人</span>
        </el-form-item>
        <el-form-item label="核价小组成员" prop="approveCode">
          <el-select v-model="dataForm.approveCode" multiple placeholder="请选择">
            <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <span class="info">核价小组，会在制作询价单时 自动填写到询价核价小组 作为默认值</span>
        </el-form-item>
        <el-form-item label="通知成员" prop="notice">
          <el-select v-model="dataForm.notice" multiple placeholder="请选择">
            <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <span class="info">ceo确认后 通知负责人,财务等人员名单,采购员不用写,系统会添加通知制单人</span>
        </el-form-item>
        <el-form-item label="是否启用" prop="status">
          <el-select v-model="dataForm.status" placeholder="请选择">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="不启用" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="addVisiable" title="添加人员">
      <div class="search">
        <el-input v-model="listQueryAdmin.dept" clearable class="filter-item" style="width: 200px;" placeholder="请输入部门" />
        <el-input v-model="listQueryAdmin.nickname" clearable class="filter-item" style="width: 200px;" placeholder="请输入人员名称" />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilterAdmin">查找</el-button>
        <el-table v-loading="listLoading" :data="listAdmin" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column align="center" label="人员ID" prop="value" />
          <el-table-column align="center" label="部门" prop="dept" />
          <el-table-column align="center" label="人员名称" prop="label" />
        </el-table>
        <pagination v-show="totalAdmin>0" :total="totalAdmin" :page.sync="listQueryAdmin.page" :limit.sync="listQueryAdmin.limit" @pagination="getListAdmin" />

      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addVisiable = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style>

</style>

<script>
import { list, create, update, quotedelete } from '@/api/quotemodel'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import { optionsAdmin, listAdmin } from '@/api/admin'
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
      listQueryAdmin: {
        page: 1,
        limit: 5,
        dept: undefined,
        nickname: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      dataForm: {
        id: 0,
        name: '',
        version: '',
        excel: '',
        code: [],
        status: 1,
        creator: 0,
        modify: 0,
        update_time: undefined,
        duty: 0
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ],
        duty: [
          { required: true, message: '核价负责人不能为空', trigger: 'blur' }
        ],
        version: [
          { required: true, message: '版本不能为空', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '常用询价供应商不能为空', trigger: 'blur' }
        ],
        ceoCode: [
          { required: true, message: '终审不能为空', trigger: 'blur' }
        ],
        notice: [
          { required: true, message: '通知人员不能为空', trigger: 'blur' }
        ],
        approveCode: [
          { required: true, message: '核价小组不能为空', trigger: 'blur' }
        ]
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
        this.dataForm.id = response.data.data.id
        this.dataForm.name = response.data.data.name
        this.dataForm.version = response.data.data.version
        this.dataForm.excel = response.data.data.excel
        this.dataForm.code = response.data.data.code
        this.dataForm.status = response.data.data.status
        this.dataForm.creator = response.data.data.creator
        this.dataForm.modify = response.data.data.modify
        this.dataForm.update_time = response.data.data.update_time
        this.dataForm.duty = response.data.data.duty
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      })
        .catch(() => { this.list = []; this.total = 0; this.listLoading = false })
        .catch(response => { this.$notify.error({ title: 'list失败', message: response.data.errmsg }) })
    },
    getListAdmin() {
      this.listLoading = true
      listAdmin(this.listQuery).then(response => {
        this.listAdmin = response.data.data.list
        this.totalAdmin = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.listAdmin = []
        this.totalAdmin = 0
        this.listLoading = false
      })
    },
    handleSelectionChange(val) {
      this.selectedlist = val
    },
    formatRole(roleId) {
      for (let i = 0; i < this.UserList.length; i++) {
        if (roleId === this.UserList[i].value) {
          return this.UserList[i].deptname
        }
      }
      return ''
    },
    uploadUrl: function(response) {
      console.log(JSON.stringify(response))
      this.dataForm.excel = response.data.url
    },
    checkFileSize: function(file) {
      if (file.size > 2048576) {
        this.$notify.error({ title: '失败', message: '${file.name}文件大于2m，请选择小于2M大小的图片' })
        return false
      }
      return true
    },
    confirmAdd() {
      const newGoodsIds = []
      const newGoodsList = []
      this.selectedlist.forEach(item => {
        const id = item.id
        let found = false
        this.topic.goods.forEach(goodsId => {
          if (id === goodsId) {
            found = true
          }
        })
        if (!found) {
          newGoodsIds.push(id)
          newGoodsList.push(item)
        }
      })

      if (newGoodsIds.length > 0) {
        this.topic.goods = this.topic.goods.concat(newGoodsIds)
        this.goodsList = this.goodsList.concat(newGoodsList)
      }
      this.addVisiable = false
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleFilterAdmin() {
      this.listQueryAdmin.page = 1
      this.getListAdmin()
    },
    resetForm() {
      this.dataForm = {
        id: 0,
        name: '',
        version: '',
        excel: '',
        code: [],
        status: 0,
        creator: 0,
        modify: 0,
        update_time: undefined,
        duty: 0
      }
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
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    purchaseCreate() {
      this.listQueryAdmin = {
        page: 1,
        limit: 5,
        id: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      }
      this.listAdmin = []
      this.totalAdmin = 0
      this.selectedlist = []
      this.addVisiable = true
    },

    createData() {
      // this.dataForm.code = JSON.stringify(this.dataForm.code)
      this.dataForm.creator = parseInt(sessionStorage.getItem('userid'))
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          create(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$message.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              this.$message.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      console.log(JSON.stringify(this.dataForm))
      this.dataForm.modify = parseInt(sessionStorage.getItem('userid'))

      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          update(this.dataForm)
            .then(() => {
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
            })
            .catch(response => {
              this.$message.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
      this.getList()
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
