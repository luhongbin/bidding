<template>
  <div class="app-container">
    <el-card class="box-card">
    <!--      <h3>用户信息</h3>-->
      <el-form ref="user" :rules="rules" :model="user" label-width="150px">
        <el-form-item label="用户昵称">
          <span>{{ user.nickname }} [ {{ user.id }} ]</span>
        </el-form-item>
       </el-form>
    </el-card>
    <!-- 查询和其他操作 -->

    <el-card class="box-card">
      <h3>箱体信息</h3>
      <el-button type="primary" @click="handleTeuShow">添加</el-button>
      <el-table :data="teuData">
        <el-table-column align="center" label="TEU ID" prop="id" />
        <el-table-column align="center" label="TEU类型" prop="teuTypeCn" />
        <el-table-column align="center" label="TEU名称" prop="teuNameCn"/>
        <el-table-column align="center" label="长(米)" prop="length" />
        <el-table-column align="center" label="宽(米)" prop="width" />
        <el-table-column align="center" label="高(米)" prop="height"/>
        <el-table-column align="center" label="体积(立方米)" prop="cube" />
        <el-table-column align="center" label="空箱自重(吨)" prop="tareWeight" />
        <el-table-column align="center" label="毛重(吨)" prop="grossWeight"/>
        <el-table-column align="center" label="最大载重(吨)" prop="maxPayload"/>
        <el-table-column align="center" label="是否启用" prop="status">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/teu/update']" type="primary" size="mini" @click="handleTeuEdit(scope.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/teu/delete']" type="danger" size="mini" @click="handleTeuDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="teuVisiable" :title="teuAdd ? '添加箱体' : '编辑箱体'">
        <el-form ref="teuForm" :rules="rules" :model="teuForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <el-form-item align="center" label="TEU类型" prop="teuTypeCn">
            <el-input v-model="teuForm.teuTypeCn" />
          </el-form-item>
          <el-form-item align="center" label="TEU名称" prop="teuNameCn">
            <el-input v-model="teuForm.teuNameCn" />
          </el-form-item>
          <el-form-item align="center" label="长(米)" prop="length">
            <el-input v-model="teuForm.length" />
          </el-form-item>
          <el-form-item align="center" label="宽(米)" prop="width">
            <el-input v-model="teuForm.width" placeholder="0.00" />
          </el-form-item>
          <el-form-item align="center" label="高(米)" prop="height">
            <el-input v-model="teuForm.height" placeholder="0.00" />
          </el-form-item>
          <el-form-item align="center" label="体积" prop="cube">
            <el-input v-model="cube" placeholder="0.00" disabled >
              <template slot="append">立方米</template>
            </el-input>
          </el-form-item>
          <el-form-item align="center" label="空箱自重(吨)" prop="tareWeight">
            <el-input v-model="teuForm.tareWeight" placeholder="0.00" />
          </el-form-item>
          <el-form-item align="center" label="毛重(吨)" prop="grossWeight">
            <el-input v-model="teuForm.grossWeight" placeholder="0.00" />
          </el-form-item>
          <el-form-item align="center" label="最大载重(吨)" prop="maxPayload">
            <el-input v-model="teuForm.maxPayload" placeholder="0.00" />
          </el-form-item>
          <el-form-item label="是否启用" prop="status">
            <el-radio-group v-model="teuForm.status">
              <el-radio :label="false">未启用</el-radio>
              <el-radio :label="true">已启用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="teuVisiable = false">取消</el-button>
          <el-button v-if="teuAdd" type="primary" @click="handleTeuAdd">确定</el-button>
          <el-button v-else type="primary" @click="handleTeuUpdate">确定</el-button>
        </div>
      </el-dialog>
    </el-card>
    <!-- 查询和其他操作 -->

      <el-card class="box-card">
        <h3>装箱文件</h3>
<!--        <el-button type="primary" @click="handlePackagefileShow">添加</el-button>-->

        <el-upload
          class="upload-demo"
          ref="upload"
          action="doUpload"
          :on-change="handleChange"
          multiple
          accept=".txt, .csv"
          :limit="1"
          :file-list="fileList"
          :before-upload="beforeUpload">
<!--          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>-->
          <el-button style="margin-left: 10px;" size="small" type="success" >上传到服务器</el-button>
          <div slot="tip" class="el-upload__tip">只能上传txt和csv文件，且不超过10K</div>
<!--          <div slot="tip" class="el-upload-list__item-name">{{fileName}}</div>-->
        </el-upload>

        <el-table :data="fileData">
          <el-table-column align="center" label="文件ID" prop="fileid" sortable />
          <el-table-column align="center" label="创建时间" prop="addTime" />
          <el-table-column align="center" label="装箱文件名称" prop="packingFilename"/>
          <el-table-column v-if=false align="center" label="装箱文件" prop="packingFile"/>
          <el-table-column align="center" label="查看" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-permission="['POST /admin/teu/update']" type="primary" size="mini" @click="handlePackagefiledo(scope.row)">装箱</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" label="体积(立方米)" prop="cube"/>
          <el-table-column align="center" label="重量(kg)" prop="weight"/>
          <el-table-column align="center" label="总件数" prop="quanlity"/>
          <el-table-column align="center" label="总行数" prop="rowcount"/>
          <el-table-column align="center" label="是否启用" prop="status">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-permission="['POST /admin/teu/update']" type="primary" size="mini" @click="handlePackagefileEdit(scope.row)">编辑</el-button>
              <el-button v-permission="['POST /admin/teu/delete']" type="danger" size="mini" @click="handlePackagefileDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="packagefileVisiable" :title="packagefileAdd ? '添加文件' : '编辑文件'">
          <el-form ref="packagefileForm" :rules="rules" :model="packagefileForm" status-icon label-position="left" label-width="150px">
            <el-form-item align="center" label="创建时间" prop="addTime">
              <el-input v-model="packagefileForm.addTime" disabled />
            </el-form-item>
            <el-form-item align="center" label="装箱文件名称" prop="packingFilename">
              <el-input v-model="packagefileForm.packingFilename" />
            </el-form-item>
            <el-form-item align="center" label="文件内容">
              <div class="text-area">
                <textarea v-model="packagefileForm.packingFile"></textarea>
              </div>
            </el-form-item>
            <el-form-item label="是否启用" prop="status">
              <el-radio-group v-model="packagefileForm.status">
                <el-radio :label="0">未启用</el-radio>
                <el-radio :label="1">已启用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="packagefileVisiable = false">取消</el-button>
            <el-button v-if="packagefileAdd" type="primary" @click="handlePackagefileAdd">确定</el-button>
            <el-button v-else type="primary" @click="handlePackagefileupdate">确定</el-button>
          </div>
        </el-dialog>
    </el-card>
  </div>
</template>

<style>
.el-card {
  margin-bottom: 10px;
}
.el-tag + .el-tag {
  margin-left: 10px;
}

.input-new-keyword {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
/*文本域*/
.text-area{
  width: 100%;
  border-top:1px solid gainsboro;
  border-bottom:1px solid gainsboro;

}
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
import { listTeu, listFile, deleteTeu, publishTeu, editTeu, detailTeu, publishFile, editFile } from '@/api/teu'
import { createStorage, uploadPath } from '@/api/storage'
// import Editor from '@tinymce/tinymce-vue'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'
import _ from 'lodash'

export default {
  name: 'Teuedit',

  data() {
    return {
      uploadPath,
      detailDialogVisible: false,
      list: [],
      uploadList: [],
      total: 0,
      listLoading: true,
      user: { },
      file: [],
      teu: [],
      teuForm: { id: 0, teuTypeCn: '集装箱', teuNameCn: '', length: 0.00, width: 0.00, height: 0.00, cube: 0.00, tareWeight: 0.00, grossWeight: 0.00, maxPayload: 0.00, status: true },
      fileto: [],
      filetoForm: { id: 0, fileId: 0, name: '', quantity: 0.00, length: 0.00, width: 0.00, height: 0.00, cube: 0.00 },
      teuAdd: true,
      teuVisiable: false,
      packagefileForm: { id: 0, userId: 0, addTime: '', updateTime: '', packingFilename: '', packingFile: '', cube: 0.00, weight: 0.00, quanlity: 0, rowcount: 0, status: true },
      packagefileAdd: true,
      packagefileVisiable: false,
      newid: true,
      dialogFormVisible: false,
      dialogStatus: '',
      filename: '',
      fileList: [],
      textMap: {
        update: '编辑',
        create: '创建'
      },
      goodsDetail: '',
      rules: {
        teuTypeCn: [{ required: true, message: '箱体类型不能为空', trigger: 'blur' }],
        teuNameCn: [{ required: true, message: '箱体名称不能为空', trigger: 'blur' }],
        length: [{ required: true, message: '长不能为空', trigger: 'blur' }],
        width: [{ required: true, message: '宽不能为空', trigger: 'blur' }],
        height: [{ required: true, message: '高不能为空', trigger: 'blur' }]
      },
      downloadLoading: false,
      editorInit: {
        language: 'zh_CN',
        height: 500,
        convert_urls: false,
        plugins: [],
        toolbar: [],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    },
    cube() { return (this.teuForm.length * this.teuForm.width * this.teuForm.height).toFixed(2) },
    fileData: function() { return _.orderBy(this.file1, ['id'], ['desc']) },
    teuData() {
      var teuData = []
      for (var i = 0; i < this.teu.length; i++) {
        if (this.teu[i].deleted) {
          continue
        }
        teuData.push(this.teu[i])
      }
      return teuData
    },
    file1() {
      var fileData = []
      for (var i = 0; i < this.file.length; i++) {
        if (this.file[i].deleted) {
          continue
        }
        fileData.push(this.file[i])
      }
      return fileData
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      if (this.$route.query.id == null) {
        return
      }
      const userId = this.$route.query.id
      detailTeu(userId).then(response => {
        this.user = response.data.data.user
        this.teu = response.data.data.teu
        this.file = response.data.data.file
      })
    },
    getTeuList() {
      this.listLoading = true
      listTeu(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.teu = response.data.data.teu
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    getFileList() {
      this.listLoading = true
      listFile(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.file = response.data.data.file
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getTeuList()
    },
    handleTeuShow(row) {
      if (row.id) {
        this.teuForm = Object.assign({}, row)
        this.teuAdd = false
      } else {
        this.teuAdd = true
      }
      this.teuVisiable = true
    },
    handleTeuAdd() {
      this.teu.unshift(this.teuForm)
      this.teuForm.userId = this.user.id
      this.teuForm.username = this.user.username
      this.teuForm.cube = Number(this.cube)
      this.teuForm.width = Number(this.teuForm.width)
      this.teuForm.length = Number(this.teuForm.length)
      this.teuForm.height = Number(this.teuForm.height)
      this.teuForm.status = 1
      this.$refs['teuForm'].validate(valid => {
        if (valid) {
          publishTeu(this.teuForm)
            .then(response => {
              // this.list.unshift(response.data.data)
              this.teuVisiable = false
              this.$notify.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleTeuEdit(row) {
      this.teuForm = Object.assign({}, row)
      this.teuAdd = false
      this.teuVisiable = true
      this.$nextTick(() => {
        this.$refs['teuForm'].clearValidate()
      })
    },

    handleTeuUpdate() {
      this.teuForm.cube = Number(this.cube)
      this.teuForm.width = Number(this.teuForm.width)
      this.teuForm.length = Number(this.teuForm.length)
      this.teuForm.height = Number(this.teuForm.height)
      this.teuForm.status = 1
      this.$refs['teuForm'].validate(valid => {
        if (valid) {
          editTeu(this.teuForm)
            .then(() => {
              for (const v of this.teu) {
                if (v.id === this.teuForm.id) {
                  const index = this.teu.indexOf(v)
                  this.teu.splice(index, 1, this.teuForm)
                  break
                }
              }
              this.teuVisiable = false
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.teu.errmsg
              })
            })
        }
      })
    },
    handleTeuDelete(row) {
      deleteTeu(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getTeuList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handlePackagefileShow(row) {
      if (row.id) {
        this.packagefileForm = Object.assign({}, row)
        this.packagefileAdd = false
      } else {
        this.packagefileForm = {}
        this.packagefileAdd = true
      }
      this.packagefileVisiable = true
    },
    handlePackagefileAdd() {
      this.file.unshift(this.packagefileForm)
      this.packagefileForm.userId = this.user.id
      this.packagefileForm.status = 1
      this.$refs['packagefileForm'].validate(valid => {
        if (valid) {
          publishFile(this.packagefileForm)
            .then(response => {
              // this.list.unshift(response.data.data)
              this.packagefileVisiable = false
              this.$notify.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handlePackagefiledo(row) {
      // alert(JSON.stringify(row))
      this.$router.push({ path: '/package/select', query: { userId: this.user.id, nickname: this.user.nickname, tuedata: this.teuData, filename: row.packingFilename, fileData: row.packingFile }})
    },
    handlePackagefileEdit(row) {
      this.packagefileForm = Object.assign({}, row)
      this.packagefileAdd = false
      this.packagefileVisiable = true
      this.$nextTick(() => {
        this.$refs['packagefileForm'].clearValidate()
      })
    },
    handlePackagefileupdate() {
      this.packagefileForm.status = 1

      this.$refs['packagefileForm'].validate(valid => {
        if (valid) {
          editFile(this.packagefileForm)
            .then(() => {
              for (const v of this.file) {
                if (v.id === this.packagefileForm.id) {
                  const index = this.file.indexOf(v)

                  this.file.splice(index, 1, this.packagefileForm)
                  break
                }
              }
              this.packagefileVisiable = false

              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.file.errmsg
              })
            })
        }
      })
    },
    handlePackagefileDelete(row) {
      row.deleted = true
    },
    resetForm() {
      this.tue = {
        id: undefined,
        username: undefined,
        teuTypeCn: undefined,
        teuNameCn: undefined,
        length: undefined,
        width: undefined,
        height: undefined,
        cube: undefined,
        tareWeight: undefined,
        grossWeight: undefined,
        maxPayload: undefined,
        status: true
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

    beforeUpload(file) {
      const fileExt = file.name.split('.').pop().toLocaleLowerCase()
      if (fileExt === 'txt' || fileExt === 'csv') {
        this.file = file
        this.readData(file)
      } else {
        MessageBox.alert('请选择后缀为"txt"或"csv"的文件', {
          confirmButtonText: '确定',
          type: 'error'
        })
      }
    },
    readData(file) {
      const fileName = file.name
      const reader = new FileReader()
      reader.readAsText(file)
      reader.onerror = e => {
        this.$notify.success({
          title: 'error',
          message: '读取文件错误'
        })
      }
      reader.onload = e => {
        this.uploadList.push({
          name: fileName,
          data: e.target.result
        })
      }
    },
    handleChange(file) {
      const reader = new FileReader()
      if (typeof FileReader === 'undefined') {
        this.$notify.success({ title: '失败', message: '您的浏览器不支持文件读取。' })
        return
      }
      reader.readAsText(file.raw, 'gb2312')
      // reader.readAsArrayBuffer(file.raw)
      reader.onload = function(e) {
        // var ints = new Uint8Array(e.target.result)
        // ints = ints.slice(0, 5000)
        // const snippets = new TextDecoder('gb2312').decode(ints)
        console.log('读取的内容如下：')
        console.log(this.result)

        var array = this.result.toString().split('\n')

        var fileto = []
        var temp = []
        var w = 0.00
        var c = 0.00
        var q = 0
        var r = 0
        for (var i in array) {
          fileto[i] = array[i].split('|')
          temp[i] = fileto[i]
          // fileto[i] = temp[i].join().trim().replace(/\s+/ig, ' ').split(' ')
          console.log('arrs', fileto[i])
          if (isNaN(fileto[i][1])) {
            MessageBox.alert('第' + i.toString() + '行，二列长度 要求是数字类型' + fileto[i][1], '错误', {
              confirmButtonText: '确定',
              type: 'error'
            })
            console.log('arr1', fileto[i][1])
            return
          }
          if (isNaN(fileto[i][2])) {
            MessageBox.alert('第' + i.toString() + '行，第三列宽度 要求是数字类型' + fileto[i][1], '错误', {
              confirmButtonText: '确定',
              type: 'error'
            })
            console.log('arr1', fileto[i][1])
            return
          }
          if (isNaN(fileto[i][3])) {
            MessageBox.alert('第' + i.toString() + '行，第四列高度 要求是数字类型', '错误', {
              confirmButtonText: '确定',
              type: 'error'
            })
            return
          }
          if (isNaN(fileto[i][4])) {
            MessageBox.alert('第' + i.toString() + '行，第五列重量  要求是数字类型', '错误', {
              confirmButtonText: '确定',
              type: 'error'
            })
            return
          }
          if (isNaN(fileto[i][5])) {
            MessageBox.alert('第' + i.toString() + '行，第六列箱数  要求是数字类型', '错误', {
              confirmButtonText: '确定',
              type: 'error'
            })
            return
          }
          c = c + Number(fileto[i][1]) * Number(fileto[i][2]) * Number(fileto[i][3])
          w = w + Number(fileto[i][4])
          q = q + Number(fileto[i][5])
          r = r + 1
        }
        console.log('arr', fileto, c, w, q, r)
        this.newid = false
        this.filename = file.name
        this.packagefileForm = {}
        this.packagefileForm.cube = c / 1000000
        this.packagefileForm.weight = w / 1000
        this.packagefileForm.quanlity = q
        this.packagefileForm.rowcount = r
        this.packagefileForm.packingFile = this.result
        this.packagefileForm.packingFilename = this.filename
        this.packagefileForm.userId = 1
        this.packagefileForm.status = 1
        console.log('packagefileForm', this.packagefileForm)
        publishFile(this.packagefileForm)
        // this.getTeuList()
        window.location.reload()
      }
    },

    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          publishTeu(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
                confirmButtonText: '确定',
                type: 'error'
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
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          editTeu(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新箱体资料成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleDelete(row) {
      deleteTeu(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getTeuList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '集装箱id',
          '用户名称',
          'TEU类型',
          'TEU名称',
          '长(米)',
          '宽(米)',
          '高(米)',
          '体积(立方米)',
          '空箱自重(吨)',
          '毛重(吨)',
          '最大载重(吨)',
          '状态'
        ]
        const filterVal = [
          'id',
          'username',
          'teuTypeCn',
          'teuNameCn',
          'length',
          'width',
          'height',
          'cube',
          'tareWeight',
          'grossWeight',
          'maxPayload',
          'status'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '集装箱表信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
