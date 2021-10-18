<template>
  <div v-loading="listLoading">
    <div class="headboard">
      <div class="wrap">
        <el-row :gutter="40" class="panel-group">
          <el-col :xs="24" :sm="14" :lg="14" class="card-panel-col">
            <div class="hello">
              <div>
                <el-avatar :icon="avatar" :src="avatar">{{ name }}</el-avatar>
              </div>
              <div class="hello-text">
                管理者 {{ helloTime }}{{ name }} 祝你开心每一天<br>
                <div class="day-text hiden-xs">
                  『 {{ yiyan.hitokoto }}』 —— 《{{ yiyan.from }}》
                  <a @click="getYiYan">
                    <el-button type="text" icon="el-icon-refresh" />
                  </a>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="dashboard-editor-container">
      <el-row :gutter="40" class="panel-group">
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
          <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
            <div class="card-panel-icon-wrapper icon-people">
              <svg-icon icon-class="peoples" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">报价单总数</div>
              <count-to :start-val="0" :end-val="totalCount" :duration="2600" class="card-panel-num" />
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
          <div class="card-panel" @click="handleSetLineChartData('messages')">
            <div class="card-panel-icon-wrapper icon-message">
              <svg-icon icon-class="message" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">中标总数</div>
              <count-to :start-val="0" :end-val="overCount" :duration="3000" class="card-panel-num" />
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
          <div class="card-panel" @click="handleSetLineChartData('purchases')">
            <div class="card-panel-icon-wrapper icon-money">
              <svg-icon icon-class="message" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">未投标总数</div>
              <count-to :start-val="0" :end-val="waitpurTotalCount" :duration="3200" class="card-panel-num" />
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
          <div class="card-panel" @click="handleSetLineChartData('shoppings')">
            <div class="card-panel-icon-wrapper icon-shoppingCard">
              <svg-icon icon-class="money" class-name="card-panel-icon" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">超时作废总数</div>
              <count-to :start-val="0" :end-val="waitTotalCount" :duration="3600" class="card-panel-num" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>

</template>

<script>
import { quote } from '@/api/dashboard'
import CountTo from 'vue-count-to'
import { showHelloTime } from '@/utils/index'
import { getYiYan } from '@/utils/senddingtalk'

export default {
  components: {
    CountTo
  },
  data() {
    return {
      listLoading: true,

      rproductTotal: 0,
      purTotalCount: 0,
      waitpurTotalCount: 0,
      purOverCount: 0,
      messages: [],
      lastAcs: [],
      unCheckCnt: 0,
      current: [],
      aclist: [],
      name: '',
      avatar: '',
      role: '',
      count: 0,
      yiyan: []
    }
  },
  computed: {
    helloTime() {
      return showHelloTime()
    }
  },
  created() {
    this.getYiYan()
    this.init()
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    },
    init() {
      this.listLoading = true
      quote().then(response => {
        this.overCount = response.data.data.value.overCount
        this.totalCount = response.data.data.value.totalCount
        this.waitpurTotalCount = response.data.data.value.waitpurTotalCount
        this.waitTotalCount = response.data.data.value.waitTotalCount

        this.current = Object.assign({}, response.data.data.currentUser)
        this.name = this.current.dept + ':' + this.current.nickname + ':' + this.current.id + '(' + this.current.capacity + ')'
        this.avatar = this.current.avatar
        sessionStorage.removeItem('currentRole')
      })
      this.listLoading = false
    },
    getYiYan() {
      getYiYan().then(res => {
        this.yiyan = res.data
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
  .hello {
    display: flex;
    align-items: center;
    height: 90px;
    padding-left: 20px;
    .hello-text {
      padding-top: 16px;
      margin-left: 16px;
      font-size: 16px;
      color: #333;
      .day-text {
        padding-top: 8px;
        font-size: 10px;
        color: #8c8c8c;
      }
    }
  }
}

.panel-group {
  margin-top: 18px;

  .card-panel-col{
    margin-bottom: 32px;
  }
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-people {
         background: #40c9c6;
      }
      .icon-message {
        background: #36a3f7;
      }
      .icon-money {
        background: #f4516c;
      }
      .icon-shoppingCard {
        background: #34bfa3
      }
    }
    .icon-people {
      color: #40c9c6;
    }
    .icon-message {
      color: #36a3f7;
    }
    .icon-money {
      color: #f4516c;
    }
    .icon-shoppingCard {
      color: #34bfa3
    }
    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}
</style>
