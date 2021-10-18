<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-width="300px">
      <el-form-item label="采购员提交询价单后超时" prop="quote_unReply_hours">
        <el-input v-model="dataForm.quote_unReply_hours" type="number" class="input-width" @keydown="handleInput">
          <template slot="append">小时</template>
        </el-input>
        <span class="info">供应商未提交报价单，则询价单自动失效,只能输入整数</span>
      </el-form-item>
      <!--      <el-form-item label="战略采购核价人员是"  prop="quote_approve_group">-->
      <!--        <el-input v-model="dataForm.quote_approve_group" class="input-width">-->
      <!--          <template slot="append">人</template>-->
      <!--        </el-input>-->
      <!--        <span class="info">这个我们还没想好用途 暂时放着吧</span>-->
      <!--      </el-form-item>-->
      <el-form-item label="操作员身份种类" prop="quote_end_approve">
        <el-input v-model="dataForm.quote_end_approve" class="input-width" />
        <span class="info">这里录入的内容 将作为管理员设置中的:身份,选择项目.用逗号分开 注意不要改默认的内容 因为程序中起作用,会导致运行不正常</span>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { updateOrder, listQuote } from '@/api/config'
import { optionsAdmin } from '@/api/admin'

export default {
  name: 'ConfigOrder',

  data() {
    return {
      listLoading: true,

      UserList: [],
      dataForm: {
        quote_unReply_hours: 0,
        // quote_approve_group:[],
        quote_end_approve: ''
      },
      rules: {
        quote_unReply_hours: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        quote_end_approve: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      downloadLoading: false
    }
  },

  created() {
    this.init()
  },
  methods: {
    init: function() {
      this.listLoading = true

      listQuote().then(response => {
        this.dataForm.quote_unReply_hours = response.data.data.quote_unReply_hours
        this.dataForm.quote_end_approve = response.data.data.quote_end_approve
        // this.dataForm.quote_approve_group = response.data.data.quote_approve_group
      }).catch(response => { this.$notify.error({ title: 'listQuote失败', message: response.data.errmsg }) })
      optionsAdmin().then(response => { this.UserList = response.data.data.list })
        .catch(response => { this.$notify.error({ title: 'optionsAdmin失败', message: response.data.errmsg }) })
      this.listLoading = false
    },
    formatRole(roleId) {
      for (let i = 0; i < this.UserList.length; i++) {
        if (roleId === this.UserList[i].value) {
          return this.UserList[i].label
        }
      }
      return ''
    },
    cancel() {
      this.init()
    },
    handleInput(e) {
      const a = e.key.replace(/[^\d]/g, '')
      if (!a) { e.preventDefault() }
    },
    update() {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.doUpdate()
      })
    },
    doUpdate() {
      updateOrder(this.dataForm).then(response => { this.$notify.success({ title: '成功', message: '询价单参数配置成功' }) })
        .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
      // this.dataForm.quote_approve_group = JSON.parse(this.dataForm.quote_approve_group)
    }
  }
}
</script>
<style scoped>
.input-width {
  width: 50%;
}
.info {
  margin-left: 15px;
}
</style>
