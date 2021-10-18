<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.Id" clearable class="filter-item" style="width: 160px;" placeholder="请输入知识库ID" />
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 160px;" placeholder="请输入知识库名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="table-expand">
            <el-form-item label="知识库ID">
              <span>{{ props.row.id }}</span>
            </el-form-item>
            <el-form-item label="知识库名称">
              <span>{{ props.row.title }}</span>
            </el-form-item>
            <el-form-item label="知识阅读量">
              <span>{{ props.row.read_count }}</span>
            </el-form-item>

            <el-form-item label="知识所属类目ID">
              <span>{{ props.row.knowledge_catalog_id }}</span>
            </el-form-item>
            <el-form-item label="知识内容">
              <span>{{ props.row.content }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column align="center" label="知识库ID" prop="id" />

      <el-table-column align="center" min-width="100" label="知识库名称" prop="title" />

      <el-table-column align="center" label="知识阅读量" prop="read_count" />
      <el-table-column align="center" label="知识所属类目ID" prop="knowledge_catalog_id" />

      <el-table-column align="center" label="是否发布" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.status ? '发布' : '非发布' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否中文" prop="lang">
        <template slot-scope="scope">
          <el-tag :type="scope.row.lang ? 'success' : 'error' ">{{ scope.row.lang ? '中文' : '英语' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否常用" prop="is_popular">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isPopular ? 'success' : 'error' ">{{ scope.row.isPopular ? '常用' : '非常用' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<style>
.table-expand {
  font-size: 0;
}
.table-expand label {
  width: 100px;
  color: #99a9bf;
}
.table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
}
.gallery {
  width: 80px;
  margin-right: 10px;
}
.goods-detail-box img {
  width: 100%;
}
</style>

<script>
import { listKnowledge, deleteKnowledge } from '@/api/knowledge'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'KnowledgeList',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        title: undefined,
        sort: 'sort_order',
        order: 'desc'
      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listKnowledge(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.$router.push({ path: '/knowledge/create' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/knowledge/edit', query: { id: row.id }})
    },
    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      deleteKnowledge(row).then(response => {
        this.$notify.success({
          title: '成功',
          message: '删除成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['商品ID', '商品编号', '名称', '专柜价格', '当前价格', '是否新品', '是否热品', '是否在售', '首页主图', '宣传图片列表', '商品介绍', '详细介绍', '商品图片', '商品单位', '关键字', '类目ID', '品牌商ID']
        const filterVal = ['id', 'goodsSn', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'brief', 'detail', 'picUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
