<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户名" />
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable />
      <el-table-column align="center" label="用户名" prop="username" />
      <el-table-column align="center" label="手机号码" prop="mobile" />
      <el-table-column align="center" label="性别" prop="gender">
        <template slot-scope="scope">
          <el-tag>{{ genderDic[scope.row.gender] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="生日" prop="birthday" />
      <el-table-column align="center" label="用户等级" prop="userLevel">
        <template slot-scope="scope">
          <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="箱体信息" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!--    &lt;!&ndash; 订单详情对话框 &ndash;&gt;-->
    <!--    <el-dialog :visible.sync="teuDialogVisible" title="箱体详情" width="800">-->
    <!--      <section ref="print">-->
    <!--        <el-form :data="userDetail" label-position="left">-->
    <!--          <el-form-item label="用户ID">-->
    <!--            <span>{{ userDetail.user.id }}</span>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item label="用户名">-->
    <!--            <span>{{ userDetail.user.name }}</span>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item label="用户等级">-->
    <!--            <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item label="状态">-->
    <!--            <el-tag>{{ statusDic[scope.row.status] }}</el-tag>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item label="箱体信息">-->
    <!--            <el-table :data="userDetail.teu" border fit highlight-current-row>-->
    <!--              <el-table-column align="center" label="TEU类型" prop="teuTypeCn" />-->
    <!--              <el-table-column align="center" label="TEU名称" prop="teuNameCn"/>-->
    <!--              <el-table-column align="center" label="长(米)" prop="length" />-->
    <!--              <el-table-column align="center" label="宽(米)" prop="width" />-->
    <!--              <el-table-column align="center" label="高(米)" prop="height"/>-->
    <!--              <el-table-column align="center" label="体积(立方米)" prop="cube" />-->
    <!--              <el-table-column align="center" label="空箱自重(吨)" prop="tareWeight" />-->
    <!--              <el-table-column align="center" label="毛重(吨)" prop="grossWeight"/>-->
    <!--              <el-table-column align="center" label="最大载重(吨)" prop="maxPayload"/>-->
    <!--              <el-table-column align="center" label="是否启用" prop="status">-->
    <!--                <template slot-scope="scope">-->
    <!--                  <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>-->
    <!--                </template>-->
    <!--              </el-table-column>-->
    <!--              <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">-->
    <!--                <template slot-scope="scope">-->
    <!--                  <el-button v-permission="['POST /admin/teu/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>-->
    <!--                  <el-button v-permission="['POST /admin/teu/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>-->
    <!--                </template>-->
    <!--              </el-table-column>-->
    <!--            </el-table>-->
    <!--          </el-form-item>-->
    <!--        </el-form>-->
    <!--      </section>-->
    <!--      <span slot="footer" class="dialog-footer">-->
    <!--        <el-button @click="userDialogVisible = false">取 消</el-button>-->
    <!--        <el-button type="primary" @click="printOrder">打 印</el-button>-->
    <!--      </span>-->
    <!--    </el-dialog>-->
  </div>
</template>

<script>
import { fetchList, detailTeu } from '@/api/user'
import Pagination from '@/components/Pagination'
import { deleteTeu } from '@/api/teu'

export default {
  name: 'User',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      userVisible: false,

      userDetail: {
        user: {},
        teu: []
      },
      teuDialogVisible: false,
      downloadLoading: false,
      genderDic: ['未知', '男', '女'],
      levelDic: ['普通用户', 'VIP用户', '高级VIP用户'],
      statusDic: ['可用', '禁用', '注销']
    }
  },
  created() {
    this.getList()
  },

  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
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
    handleDetail(row) {
      detailTeu(row.id).then(response => {
        this.userDetail = response.data.data
      })
      this.orderDialogVisible = true
    },
    handleUpdate(row) {
      this.$router.push({ path: '/package/teuedit', query: { id: row.id }})
    },
    handleDelete(row) {
      deleteTeu({ userId: row.id }).then(response => {
        this.$notify.success({
          title: '成功',
          message: '箱体删除成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    printOrder() {
      this.$print(this.$refs.print)
      this.teuDialogVisible = false
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['用户名', '手机号码', '性别', '生日', '状态']
        const filterVal = ['username', 'mobile', 'gender', 'birthday', 'status']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
