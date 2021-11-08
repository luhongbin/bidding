<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="dataForm.name" maxlength="63" /><span class="info">压铸模具类,电子五金类...</span>
      </el-form-item>
      <el-form-item label="模板版本号" prop="version">
        <el-input v-model="dataForm.version" maxlength="10" /><span class="info">同样的类别存在不同样式单可能，用版本分开</span>
      </el-form-item>
      <el-form-item label="excel源文件" prop="excel">
        <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize" accept=".xlsx">
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
        <el-select v-model="dataForm.code" multiple placeholder="请选择">
          <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
        </el-select>
        <el-button style="float:left;" size="mini" type="primary" @click="purchaseCreate('code')">创建常用供应商</el-button>
        <span class="info">常用供应商，会在制作询价单时 自动填写到询价供应商中 作为默认值</span>
      </el-form-item>
      <el-form-item label="终审负责人" prop="ceoCode">
        <el-select v-model="dataForm.ceoCode" placeholder="请选择">
          <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
        </el-select>
        <span class="info">终审负责人ceo</span>
      </el-form-item>
      <el-form-item label="核价小组成员" prop="approveCode">
        <el-select v-model="dataForm.approveCode" multiple placeholder="请选择">
          <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
        </el-select>
        <el-button style="float:left;" size="mini" type="primary" @click="purchaseCreate('approveCode')">创建核价小组</el-button>
        <span class="info">核价小组，会在制作询价单时 自动填写到询价核价小组 作为默认值</span>
      </el-form-item>
      <el-form-item label="通知成员" prop="notice">
        <el-select v-model="dataForm.notice" multiple placeholder="请选择">
          <el-option v-for="item in UserList" :key="item.value" :label="item.deptname" :value="item.value" />
        </el-select>
        <el-button style="float:left;" size="mini" type="primary" @click="purchaseCreate('notice')">创建通知人员</el-button>
        <span class="info">ceo确认后 通知负责人,财务等人员名单,采购员不用写,系统会添加通知制单人</span>
      </el-form-item>
      <el-form-item label="详细信息">
        <editor v-model="dataForm.appendix" :init="editorInit" />
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
      <el-button type="primary" @click="updateData">确定</el-button>
    </div>
    <el-dialog :visible.sync="addVisiable" title="添加人员">
      <div class="search">
        <el-input v-model="listQuery.dept" clearable class="filter-item" style="width: 200px;" placeholder="请输入部门" />
        <el-input v-model="listQuery.nickname" clearable class="filter-item" style="width: 200px;" placeholder="请输入人员名称" />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilterAdmin">查找</el-button>
        <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column align="center" label="人员ID" prop="id" />
          <el-table-column align="center" label="部门" prop="dept" />
          <el-table-column align="center" label="人员名称" prop="nickname" />
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="confirmAdd">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style>

</style>

<script>
import { read, update } from '@/api/quotemodel'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import { optionsAdmin, listAdmin } from '@/api/admin'
import { openExcel } from '@/utils/quoteSubmit'
import Editor from '_@tinymce_tinymce-vue@3.0.1@@tinymce/tinymce-vue'

export default {
  name: 'QuoteModelEdit',
  components: { Pagination, Editor },
  data() {
    return {
      id: 0,
      uploadPath,
      addVisiable: false,
      fileList: [],
      model: [],
      list: [],
      UserList: [],
      total: 0,
      appendix: '',
      listLoading: false,
      selectedlist: [],
      bill: '',
      listQuery: {
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
        ceoCode: '',
        notice: [],
        approveCode: [],
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
      downloadLoading: false,
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false,
        plugins: [
          'advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'
        ],
        toolbar: [
          'searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample',
          'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'
        ]
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
    if (this.$route.query.id == null) {
      return
    }
    // this.id = this.$route.query.id
    this.getModel()
  },
  methods: {
    getModel() {
      this.listLoading = false
      read(this.$route.query.id)
        .then(response => {
          console.log(JSON.stringify(response))
          this.dataForm = response.data.data
          optionsAdmin().then(response => { console.log(JSON.stringify(response)); this.UserList = response.data.data.list }).catch(response => { this.$notify.error({ title: 'optionsAdmin失败', message: response.data.errmsg }) })
          this.listLoading = false
        })
        .catch(() => {
          console.log('无数据')
          this.dataForm = {}
          this.listLoading = false
        })
    },

    purchaseCreate(bill) {
      this.bill = bill
      this.listQuery = {
        page: 1,
        limit: 5,
        dept: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      }
      this.list = []
      this.total = 0
      this.selectedlist = []
      this.addVisiable = true
    },
    handleCancel: function() {
      this.$router.push({ path: '/config/quotemodel' })
    },
    getList() {
      this.listLoading = true
      listAdmin(this.listQuery).then(response => {
        console.log('list:' + JSON.stringify(response.data.data.list.data.list))
        this.list = response.data.data.list.data.list
        this.total = response.data.data.list.data.total
        this.listLoading = false
      }).catch(() => {
        console.log('无数据')
        this.list = []
        this.total = 0
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
      const newSupplyIds = []
      const newSupplyList = []
      this.selectedlist.forEach(item => {
        const id = item.id
        let found = false
        if (this.bill === 'code') {
          this.dataForm.code.forEach(goodsId => {
            if (id === goodsId) {
              found = true
            }
          })
        }
        if (this.bill === 'notice') {
          this.dataForm.notice.forEach(goodsId => {
            if (id === goodsId) {
              found = true
            }
          })
        }
        if (this.bill === 'approveCode') {
          this.dataForm.approveCode.forEach(goodsId => {
            if (id === goodsId) {
              found = true
            }
          })
        }

        if (!found) {
          newSupplyIds.push(id)
          newSupplyList.push(item)
        }
      })

      if (newSupplyIds.length > 0) {
        if (this.bill === 'approveCode') { this.dataForm.approveCode = this.dataForm.approveCode.concat(newSupplyIds) }
        if (this.bill === 'notice') { this.dataForm.notice = this.dataForm.notice.concat(newSupplyIds) }
        if (this.bill === 'code') { this.dataForm.code = this.dataForm.code.concat(newSupplyIds) }
        this.list = this.list.concat(newSupplyList)
      }
      this.addVisiable = false
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleFilterAdmin() {
      this.listQuery.page = 1
      this.getList()
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
          this.$router.push({ path: '/config/quotemodel' })
        }
      })
    }
  }
}
</script>
