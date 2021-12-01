<template>
  <div class="app-container">
    <el-dialog :visible.sync="quoteDialogVisible"  width="1200">

  <!-- 订单详情对话框 -->
      <el-card class="box-card">
        <h3>询价信息</h3>
    <el-form ref="quote"  :model="quote" status-icon label-position="left"   label-width="200px">
      <!-- 发货对话框 -->
      <el-form-item label="询价发放日期">
        <span>{{ quote.submitDate }}</span>
      </el-form-item>
      <el-form-item label="报价截止时间">
        <span>{{ quote.submitDate }}</span>
      </el-form-item>
      <el-form-item label="询价主题">
        <span>{{ quote.purchaserNote }}</span>
      </el-form-item>
      <el-form-item label="单据类型">
        <span>{{ quote.modelName }}</span>
        <el-button size="mini" type="primary" icon="el-icon-download"  @click="openExcel(quote.quoteModelExcel)">下载询价单</el-button>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="requoteDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="voting">确定提交</el-button>
    </div>
      </el-card>
      <el-card class="box-card">
        <h3>报价信息</h3>
    <el-form ref="reQuote"  :model="reQuote"  status-icon label-position="left" label-width="200px">
      <el-form-item label="状态">
        <span>{{ reQuote.status | orderStatusFilter }}<br/></span>
      </el-form-item>
      <el-form-item label="描述">
        <span>{{ reQuote.purchaserNote }}</span>
      </el-form-item>
      <el-form-item label="报价单">
        <el-button size="mini" type="primary" icon="el-icon-download"  @click="openExcel(reQuote.quoteModelExcel)">上传报价单</el-button>
      </el-form-item>
      <div class="op-container">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="voting">更新商品</el-button>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="requoteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="voting">确定提交</el-button>
      </div>
    </el-form>
      </el-card>

    <el-form ref="Approve"  :model="Approve"   status-icon label-position="left" label-width="200px">
      <el-card class="box-card">
        <h3>审批信息</h3>
        <el-table :data="Approve" border fit highlight-current-row>
          <el-table-column align="center" label="发起人" prop="billName" />
          <el-table-column align="center" label="接收人" prop="receiver" />
          <el-table-column align="center" label="时间" prop="addTime" />
          <el-table-column align="center" label="留言" prop="note" />
          <el-table-column align="center" label="动作" prop="action" />
        </el-table>
      </el-card>
    </el-form>
    </el-dialog>

  </div>

</template>

<script>
import { read } from '@/api/requote'
import { detail as quoteDetail } from '@/api/quote'
import {detailApprove, createApproveinfo} from "@/api/approveinfo";
import {editGoods} from "@/api/goods";
const statusMap = {
  0: '询价',
  1: '签收',
  2: '提交报价',
  3: '中标',
  4: '未中标',
  5: '结案',
  6: '重新提交',
  9: '撤销报价'
}
export default {
  name: "approve",
  filters: {
    orderStatusFilter(status) {
      return statusMap[status]
    }
  },
  data() {
    return {
      quoteDialogVisible:true,
      requoteDialogVisible: false,
      approveDialogVisible: false,
      reQuote: {},
      quote: [],
      Approve: [] ,
      total: 0,
      listLoading: true,
      statusMap,
      dialogStatus: '',
      textMap: {
        update: '编辑'
      },
    }
  },
  created() {
    this.getList()
    // this.fixApprove()
    // var val = this.reQuote.quote.modelName
    // this.selectSupply(val)
  },
  methods: {
    getList() {
      if (this.$route.query.rbgz == null) {
        return
      }
      this.quoteDialogVisible=true
      this.requoteDialogVisible= true
      this.approveDialogVisible= true
      const rbgz = this.$route.query.rbgz.id
      const quoteId = this.$route.query.rbgz.quoteId
      const adminId = this.$route.query.rbgz.adminId
      const receiver = this.$route.query.rbgz.receiver
      const billId = this.$route.query.rbgz.billId
      const billClass = this.$route.query.rbgz.billClass

      detailApprove(quoteId,receiver,adminId,billId,billClass).then(response => { this.Approve = response.data.data
        console.log("detailApprove" + JSON.stringify(this.Approve))

        console.log("quote data:"+quoteId)

      quoteDetail(quoteId).then(response => { this.quote = response.data.data
          console.log("quote:" + JSON.stringify(response.data.data))}).catch(() => {
        console.log("quote error:")})

      })
    },
    fixApprove(){
      for(let i = 0; i< this.reQuote.Approve.length; i++){
        this.$set(this.reQuote,'ApproveInfo',this.formatAdmin(this.reQuote.Approve[i].AdminId) + '\n' + this.reQuote.Approve[i].addTime
          + '\n' + this.reQuote.Approve[i].action  + '\n' + this.reQuote.Approve[i].note  + '\n' + this.formatAdmin(this.reQuote.Approve[i].receiver))
      }
      console.log(JSON.stringify(this.reQuote.reQuote.ApproveInfo))

    },
    handleCancel: function() {
      this.$router.push({ path: '/requote/list' })
    },
    selectSupply(val){ //根据设备组id获取相应的商品
      for(let i = 0; i< this.modelNameList.length; i++){
        if(this.modelNameList[i]['value'] === val){ this.$set(this.reQuote,'modelName',this.modelNameList[i].supply)}
      }
      console.log(JSON.stringify(this.reQuote.reQuote.modelName))

    },
    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    voting: function() {
      const finalGoods = {
        goods: this.goods,
        specifications: this.specifications,
        products: this.products,
        attributes: this.attributes
      }
      createApproveinfo(finalGoods)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '编辑成功'
          })
          this.$store.dispatch('tagsView/delView', this.$route)
          this.$router.push({ path: '/goods/list' })
        })
        .catch(response => {
          MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
            confirmButtonText: '确定',
            type: 'error'
          })
        })
    },
  }
}
</script>

<style scoped>

</style>
