<template>
  <div class="app-container">

<!--    <el-form ref="topic" :rules="rules" :model="topic" status-icon label-position="left" label-width="100px" style="width: 800px; margin-left:50px;">-->
<!--  <el-dialog :append-to-body="true"   :close-on-click-modal="false" :before-close="cancel" :visible.sync="dialog" :title="isAdd ? '新增' : '开启砍价'" width="900px">-->
    <el-form ref="form" :model="form" :rules="rules" :inline="true" size="small" label-width="140px">
      <el-button style="float:right;" size="mini" type="primary" @click="handleCreate()">创建商品</el-button>
      <el-form-item label="商品id">
        <el-input v-model="form.goodsId" style="width: 500px;" />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="form.title" style="width: 500px;" />
      </el-form-item>
      <el-form-item label="砍价简介">
        <el-input v-model="form.info" style="width: 500px;" rows="5" type="textarea" />
      </el-form-item>
      <el-form-item label="单位">
        <el-input v-model="form.unitName" style="width: 500px;" />
      </el-form-item>

      <el-form-item label="活动开始时间">
        <template>
          <el-date-picker
            v-model="form.startTimeDate"
            type="datetime"
            placeholder="选择日期时间"
          />
        </template>
      </el-form-item>
      <el-form-item label="活动结束时间">
        <template>
          <el-date-picker  v-model="form.endTimeDate" type="datetime" placeholder="选择日期时间"/>
        </template>
      </el-form-item>
<!--      <el-form-item label="砍价产品主图片">-->
<!--        <el-upload :headers="headers"-->
<!--          :action="uploadPath"-->
<!--          :show-file-list="false"-->
<!--          :on-success="uploadPicUrl"-->
<!--          class="avatar-uploader"-->
<!--          accept=".jpg,.jpeg,.png,.gif">-->
<!--          <img v-if="form.imageArr" :src="form.imageArr" class="avatar">-->
<!--          <i v-else class="el-icon-plus avatar-uploader-icon"/>-->
<!--        </el-upload>-->
<!--      </el-form-item>-->
      <el-form-item label="砍价产品轮播图">
        <MaterialList v-model="form.sliderImageArr" style="width: 500px" type="image" :num="4" :width="150" :height="150" />
      </el-form-item>
      <el-form-item label="库存">
        <el-input-number v-model="form.stock" />
      </el-form-item>
      <el-form-item label="销量">
        <el-input-number v-model="form.sales" />
      </el-form-item>
      <el-form-item label="砍价金额">
        <el-input-number v-model="form.price" />
      </el-form-item>
      <el-form-item label="允许砍到最低价">
        <el-input-number v-model="form.minPrice" />
      </el-form-item>
      <el-form-item label="限购">
        <el-input-number v-model="form.num" />
      </el-form-item>
      <el-form-item label="单次砍最高价">
        <el-input-number v-model="form.bargainMaxPrice" />
      </el-form-item>
      <el-form-item label="单次砍最低价">
        <el-input-number v-model="form.bargainMinPrice" />
      </el-form-item>
      <el-form-item label="用户每次砍价的次数">
        <el-input-number v-model="form.bargainNum" />
      </el-form-item>
      <el-form-item label="砍价状态">
        <el-radio v-model="form.status" :label="1">开启</el-radio>
        <el-radio v-model="form.status" :label="0" style="width: 110px;">关闭</el-radio>
      </el-form-item>
      <el-form-item label="成本价">
        <el-input-number v-model="form.cost" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" />
      </el-form-item>
      <el-form-item label="是否包邮">
        <el-radio v-model="form.isPostage" :label="1">是</el-radio>
        <el-radio v-model="form.isPostage" :label="0" style="width: 110px;">否</el-radio>
      </el-form-item>
      <el-form-item v-if="form.isPostage ===0" label="邮费">
        <el-input-number v-model="form.postage" />
      </el-form-item>
      <el-form-item label="砍价规则">
        <editor v-model="form.rule" />
      </el-form-item>
      <el-form-item label="砍价详情">
        <editor v-model="form.description" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="cancel">取消</el-button>
      <el-button :loading="loading" type="primary" @click="doSubmit">确认</el-button>
    </div>
<!--    <el-dialog :visible.sync="addVisiable"  title="添加商品">-->
<!--      <div class="search">-->
<!--        <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号"/>-->
<!--        <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称"/>-->
<!--        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>-->
<!--        <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">-->
<!--          <el-table-column type="radio" width="55"/>-->
<!--          <el-table-column align="center" label="商品ID" prop="id"/>-->
<!--          <el-table-column align="center" property="picUrl" label="图片">-->
<!--            <template slot-scope="scope">-->
<!--              <img :src="scope.row.picUrl" width="40">-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--          <el-table-column align="center" label="商品名称" prop="name"/>-->
<!--        </el-table>-->
<!--        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />-->
<!--      </div>-->
<!--      <div slot="footer" class="dialog-footer">-->
<!--        <el-button @click="addVisiable = false">取消</el-button>-->
<!--        <el-button type="primary" @click="confirmAdd">确定</el-button>-->
<!--      </div>-->
<!--    </el-dialog>-->
  </div>
</template>
<style>
  .el-dialog {
    width: 800px;
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
</style>

<script>
import { add, edit } from '@/api/yxStoreBargain'
import editor from '@/components/Editor'
import MaterialList from '@/components/material'
import {  uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'

export default {
  components: { editor, MaterialList },
  props: {
    isAdd: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      uploadPath,
      addVisiable: false,
      loading: false, dialog: false,
      form: {
        id: '',
        productId: '',
        title: '',
        image: '',
        unitName: '',
        stock: '',
        sales: '',
        images: '',
        imageArr: [],
        sliderImageArr: [],
        startTime: '',
        stopTime: '',
        storeName: '',
        price: '',
        minPrice: '',
        num: '',
        bargainMaxPrice: '',
        bargainMinPrice: '',
        bargainNum: '',
        status: '',
        description: '',
        giveIntegral: '',
        info: '',
        cost: '',
        sort: 0,
        isHot: 0,
        isDel: 0,
        addTime: '',
        isPostage: 1,
        postage: '',
        rule: '',
        look: '',
        share: '',
        startTimeDate: '',
        endTimeDate: ''
      },
      rules: {
      }
    }
  },
  watch: {
    'form.imageArr': function(val) {
      if (val) {
        this.form.image = val.join(',')
      }
    },
    'form.sliderImageArr': function(val) {
      if (val) {
        this.form.images = val.join(',')
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
  },
  methods: {
    cancel() {
      this.resetForm()
    },
    doSubmit() {
      this.loading = true
      if (this.isAdd) {
        this.doAdd()
      } else this.doEdit()
    },
    doAdd() {
      add(this.form).then(res => {
        this.resetForm()
        this.$notify({
          title: '添加成功',
          type: 'success',
          duration: 2500
        })
        this.loading = false
        this.$parent.init()
      }).catch(err => {
        this.loading = false
        console.log(err.response.data.message)
      })
    },
    uploadPicUrl: function(response) {
      this.form.imageArr = response.data.url
    },
    handleCreate() {
      this.listQuery = {
        page: 1,
        limit: 5,
        id: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      }
      this.list = []
      this.total = 0
      this.selectedlist = []
      this.addVisiable = true
    },
    doEdit() {
      edit(this.form).then(res => {
        this.resetForm()
        this.$notify({
          title: '修改成功',
          type: 'success',
          duration: 2500
        })
        this.loading = false
        this.$parent.init()
      }).catch(err => {
        this.loading = false
        console.log(err.response.data.message)
      })
    },

    resetForm() {
      this.dialog = false
      this.$refs['form'].resetFields()
      this.form = {
        id: '',
        productId: '',
        title: '',
        image: '',
        unitName: '',
        stock: '',
        sales: '',
        images: '',
        imageArr: [],
        sliderImageArr: [],
        startTime: '',
        stopTime: '',
        storeName: '',
        price: '',
        minPrice: '',
        num: '',
        bargainMaxPrice: '',
        bargainMinPrice: '',
        bargainNum: '',
        status: '',
        description: '',
        giveIntegral: '',
        info: '',
        cost: '',
        sort: '',
        isHot: '',
        isDel: '',
        addTime: '',
        isPostage: '',
        postage: '',
        rule: '',
        look: '',
        share: ''
      }
    }
  }
}
</script>

<style scoped>

</style>
