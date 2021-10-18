<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <el-button type="danger" class="filter-item" size="mini" icon="el-icon-refresh" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/bargain/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>
<!--    &lt;!&ndash;表单组件&ndash;&gt;-->
<!--    <eForm ref="form" :is-add="isAdd" />-->
    <!--表格渲染-->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column prop="id" label="砍价产品ID" />
      <el-table-column prop="goodsId" label="商品ID" />
      <el-table-column prop="title" label="商品名称" />
      <el-table-column prop="image" label="商品图片">
        <template slot-scope="scope">
          <a :href="scope.row.image" style="color: #42b983" target="_blank"><img :src="scope.row.image" alt="点击打开" class="el-avatar"></a>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="砍价价格" />
      <el-table-column prop="price" label="砍价区间">
        <template slot-scope="scope">
          <span>{{ scope.row.bargainMinPrice }}~{{ scope.row.bargainMaxPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="minPrice" label="最低价" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="statusStr" label="砍价状态" />


      <el-table-column prop="startTime" label="开始时间">
        <template slot-scope="scope">
          <span>{{ formatTimeTwo(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stopTime" label="结束时间">
        <template slot-scope="scope">
          <span>{{ formatTimeTwo(scope.row.stopTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sales" label="销量" />
      <el-table-column prop="look" label="浏览量" />
      <el-table-column prop="share" label="分享量" />

      <el-table-column align="center" label="操作" width="300" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/bargain/read']" type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-permission="['POST /admin/bargain/update']" type="info" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/bargain/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
  </div>
</template>

<script>
import { del, listBargain } from '@/api/yxStoreBargain'
import initData from '@/api/crud'
import eForm from './form'
import { formatTimeTwo, parseTime } from '@/utils/index'
export default {
  components: { eForm },
  mixins: [initData],
  data() {
    return {
      delLoading: false,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    formatTimeTwo,
    getList() {
      this.listLoading = true
      listBargain(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
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
      this.getList()
    },
    beforeInit() {
      this.url = 'api/yxStoreBargain'
      const sort = 'id,desc'
      this.params = { page: this.page, size: this.size, sort: sort }
      return true
    },
    subDelete(id) {
      this.delLoading = true
      del(id).then(res => {
        this.delLoading = false
        this.$refs[id].doClose()
        this.dleChangePage()
        this.init()
        this.$notify({
          title: '删除成功',
          type: 'success',
          duration: 2500
        })
      }).catch(err => {
        this.delLoading = false
        this.$refs[id].doClose()
        console.log(err.response.data.message)
      })
    },
    add() {
      this.isAdd = true
      this.$refs.form.dialog = true
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '优惠券ID',
          '名称',
          '内容',
          '标签',
          '最低消费',
          '减免金额',
          '每人限领',
          '优惠券数量'
        ]
        const filterVal = [
          'id',
          'name',
          'desc',
          'tag',
          'min',
          'discount',
          'limit',
          'total'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '砍价产品')
        this.downloadLoading = false
      })
    },
    handleCreate() {
      this.isAdd = true
      // this.$refs.form.dialog = true
      this.$router.push({ path: '/promotion/bargain/bargain-form' })
    },
    edit(data) {
      this.isAdd = false
      const _this = this.$refs.form
      _this.form = {
        id: data.id,
        productId: data.productId,
        title: data.title,
        image: data.image,
        unitName: data.unitName,
        stock: data.stock,
        sales: data.sales,
        images: data.images,
        imageArr: data.image.split(','),
        sliderImageArr: data.images.split(','),
        startTime: data.startTime,
        stopTime: data.stopTime,
        storeName: data.storeName,
        price: data.price,
        minPrice: data.minPrice,
        num: data.num,
        bargainMaxPrice: data.bargainMaxPrice,
        bargainMinPrice: data.bargainMinPrice,
        bargainNum: data.bargainNum,
        status: data.status,
        description: data.description,
        giveIntegral: data.giveIntegral,
        info: data.info,
        cost: data.cost,
        sort: data.sort,
        isHot: data.isHot,
        isDel: data.isDel,
        addTime: data.addTime,
        isPostage: data.isPostage,
        postage: data.postage,
        rule: data.rule,
        look: data.look,
        share: data.share,
        startTimeDate: new Date(data.startTimeDate),
        endTimeDate: new Date(data.endTimeDate)
      }
      _this.dialog = true
    }
  }
}
</script>

<style scoped>

</style>
