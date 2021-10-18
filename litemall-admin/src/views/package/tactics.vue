<template>
  <div class="app-container">
    <el-table v-loading="listLoading" :data="dataV" element-loading-text="正在查询中。。。" row-key="code"  style="width: 100%;margin-bottom: 20px;" border="">
      <el-table-column label="装箱计算" prop="name" />
      <el-table-column label="策略名称" prop="type">
        <template slot-scope="scope">
          {{ scope.row.type | typeFilter }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="查看" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-if="scope.row.type > 1" type="primary" size="mini" @click="handleDo(scope.row)">装箱图</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-card>
      <div ref="echarts" id="echarts" />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts/lib/echarts'

import 'echarts-gl'

export default {
  name: 'Echarts',

  filters: {
    typeFilter(status) {
      const typeMap = {
        '1': '装箱策略',
        '2': '装载容器',
        '3': '工作面'
      }
      return typeMap[status]
    }
  },
  data() {
    return {
      list: [],
      listLoading: true,
      downloadLoading: false,
      schema_num: 0,
      dataV: [],
      getData: [],
      tueMax: [],
      position: [],
      positionWorkFace: [],
      series: [],
      namedata: [],
      barSize: [],
      Stack: '',
      option: [],
      myEcharts: ''
    }
  },
  created() {
    this.init()
  },
  // mounted() {
  //   this.bar3D()
  // },
  methods: {
    init: function() {
      this.listLoading = true
      if (this.$route.query.tmpMethod == null) {
        return
      }
      this.dataV = this.$route.query.tmpMethod
      this.listLoading = false
    },
    getPosition() {
      this.namedata = []
      var tmp = []
      for (var i = 0; i < this.dataV.length; i++) { // 一个个集装箱安装
        for (var j = 0; j < this.dataV[i].children.length; j++) { // 一个个策略选择
          tmp = this.dataV[i].children[j]
          this.positionWorkFace = tmp.children
          this.namedata.push(tmp.name)
          if (this.position.code === this.dataV[i].children[j].code) { this.tueMax = this.dataV[i].tueMax }
          for (var k = 0; k < this.dataV[i].children[j].children.length; k++) { // 工作面
            tmp = this.dataV[i].children[j].children[k]
            if (this.position.code === this.dataV[i].children[j].children[k].code) { this.tueMax = this.dataV[i].tueMax }
          }
        }
      }
      console.log('position:', this.position, this.tueMax)
    },
    handleDo(row) {
      this.position = Object.assign({}, row)
      console.log('dataV:', this.dataV)

      if (this.position.type === 3) {
        this.getPosition()
        this.type3One()
      }
      if (this.position.type === 2) {
        this.getPosition()
        this.type2One()
      }
    },
    type3One() {
      var breakId = 0
      var s = 0
      this.series = []
      for (var i1 = 0; i1 < this.position.W_Z_N; i1++) {
        this.data = []
        if (breakId === 1) break
        for (var i3 = 0; i3 < this.position.W_Y_N; i3++) {
          if (s <= this.position.InContain) {
            console.log('BoxRotate data:', this.position.BoxRotate, this.position.x, this.position.x / 2, this.position.SStart_X)

            if (this.position.BoxRotate === 3) {
              this.data.push([i3 * this.position.y + this.position.y / 2 + this.position.SStart_Z, this.position.x + this.position.x / 2 + this.position.SStart_X, this.position.z])
            } else {
              this.data.push([this.position.x / 2 + this.position.SStart_X, i3 * this.position.y + this.position.y / 2 + this.position.SStart_Z, this.position.z])
            }
          } else {
            breakId = 1
            break
          }
          s = s + 1
        }
        if (this.position.BoxRotate === 2) {
          this.barSize = [200 / this.tueMax.Max_Y * (this.position.y - 0.01), 200 / this.tueMax.Max_Y * (this.position.x - 0.01)]
        } else {
          this.barSize = [200 / this.tueMax.Max_X * (this.position.x - 0.01), 200 / this.tueMax.Max_X * (this.position.y - 0.01)]
        }
        if (i1 === 0) {
          this.Stack = 'stack'
        } else {
          this.Stack = 'stack'
        }
        this.pushSeries()
        console.log('data:', this.data, this.position.x, this.position.y, this.Stack, this.position.BoxRotate, 'series', this.series)
      }
      this.bar3D()
    },
    pushSeries() {
      this.series.push({
        type: 'bar3D',
        name: this.position.name,
        barSize: this.barSize,
        bevelSize: 0.15,
        bevelSmoothness: 16,
        data: this.data,
        stack: this.Stack,
        shading: 'lambert',
        label: {
          fontSize: 16,
          borderWidth: 1
        },
        emphasis: {
          label: {
            fontSize: 20,
            color: '#900'
          },
          itemStyle: {
            color: '#900'
          }
        }
      })
    },
    type2One() {
      var s = 0
      var InContain = 0
      this.series = []
      this.data1 = []

      for (var k = 0; k < this.positionWorkFace.length; k++) {
        this.position = this.positionWorkFace[k]
        InContain = InContain + this.position.InContain
        for (var i2 = 0; i2 < this.position.W_Z_N; i2++) {
          for (var i3 = 0; i3 < this.position.W_Y_N; i3++) {
            if (this.position.BoxRotate === 3) {
              this.barSize = [200 / this.tueMax.Max_Y * (this.position.y - 0.01), 200 / this.tueMax.Max_Y * (this.position.x - 0.01)]
            } else {
              this.barSize = [200 / this.tueMax.Max_X * (this.position.x - 0.01), 200 / this.tueMax.Max_X * (this.position.y - 0.01)]
            }
            s = s + 1
            if (s <= InContain) {
              if (this.position.BoxRotate === 3) {
                this.data1.push([i3 * this.position.y + this.position.y / 2 + this.position.SStart_Y, this.position.x / 2 + this.position.SStart_X, this.position.z, this.barSize, i2])
              } else {
                this.data1.push([this.position.x / 2 + this.position.SStart_X, i3 * this.position.y + this.position.y / 2 + this.position.SStart_Z, this.position.z, this.barSize, i2])
              }
            } else {
              if (this.position.BoxRotate === 3) {
                this.data1.push([i3 * this.position.y + this.position.y / 2 + this.position.SStart_Y, this.position.x / 2 + this.position.SStart_X, 0, this.barSize, i2])
              } else {
                this.data1.push([this.position.x / 2 + this.position.SStart_X, i3 * this.position.y + this.position.y / 2 + this.position.SStart_Z, 0, this.barSize, i2])
              }
            }
          }
        }
      }
      console.log('data1:', this.data1, InContain)
      for (var i1 = 0; i1 < this.positionWorkFace[0].W_Z_N; i1++) { // 工作面
        this.data = []
        for (k = 0; k < this.data1.length; k++) {
          if (this.data1[k][4] === i1) {
            this.data.push([this.data1[k][0], this.data1[k][1], this.data1[k][2]])
            this.barSize = this.data1[k][3]
          }
        }
        this.Stack = 'stack'
        this.pushSeries()
      }
      console.log('type2One data:', this.data, this.position.x, this.position.y, this.Stack, this.position.BoxRotate, 'series', this.series)
      this.bar3D()
    },
    bar3D() {
      console.log('myChart:', this.myEcharts)
      if (this.myEcharts === '') { this.myEcharts = echarts.init(document.getElementById('echarts')) }
      this.getOption()
      this.option && this.myEcharts.setOption(this.option)
    },
    getOption() {
      console.log('this.tueMax data:', this.tueMax, this.series)

      this.option = {
        title: {
          text: '某站点用户访问来源',
          subtext: '纯属虚构',
          left: 'center'
        },
        tooltip: {},
        visualMap: {
          max: 20,
          inRange: {
            color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
          }
        },
        legend: {
          selectedMode: 'single',
          left: 'left',
          data: this.series.name,
          orient: 'vertical',
          textStyle: {
            color: '#fff'
          }
        },
        xAxis3D: {
          max: this.tueMax.Max_X
        },
        yAxis3D: {
          max: this.tueMax.Max_Y
        },
        zAxis3D: {
          max: this.tueMax.Max_Z
        },
        grid3D: {
          boxWidth: 200,
          boxDepth: 200 / this.tueMax.Max_X * this.tueMax.Max_Y,
          viewControl: {
            // projection: 'orthographic'
            projection: 'perspective',
            distance: 400
          },
          light: {
            main: {
              intensity: 1.2,
              quality: 'ultra',
              shadow: true
            },
            ambient: {
              intensity: 0.3
            }
          }
        },
        series: this.series
      }
    }
  }
}

</script>
<style lang="scss">
#echarts {
  width: 1500px;
  height: 1000px;
  //position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
}
</style>

<!--// console.log('positionWorkFace:', this.positionWorkFace)-->
<!--// for (var i1 = 0; i1 < this.positionWorkFace[0].W_Z_N; i1++) { // 工作面-->
<!--//   this.data = []-->
<!--//   for (var k = 0; k < this.positionWorkFace.length; k++) {-->
<!--//     this.position = this.positionWorkFace[k]-->
<!--//     if (i1 === 0) { InContain = InContain + this.position.InContain }-->
<!--//     console.log('InContain:', InContain)-->
<!--//-->
<!--//     for (var i3 = 0; i3 < this.position.W_Y_N; i3++) {-->
<!--//       s = s + 1-->
<!--//       if (s <= InContain) {-->
<!--//         if (this.position.BoxRotate === 0) {-->
<!--//           this.data.push([i3 * this.position.y + this.position.y / 2 + this.position.SStart_Y, this.position.x / 2 + this.position.SStart_X, this.position.z])-->
<!--//         } else {-->
<!--//           this.data.push([this.position.x / 2 + this.position.SStart_X, i3 * this.position.y + this.position.y / 2 + this.position.SStart_Y, this.position.z])-->
<!--//         }-->
<!--//       } else {-->
<!--//         breakId = 1-->
<!--//         break-->
<!--//       }-->
<!--//       if (this.position.BoxRotate === 0) {-->
<!--//         this.barSize = [200 / this.tueMax.Max_Y * (this.position.y - 0.01), 200 / this.tueMax.Max_Y * (this.position.x - 0.01)]-->
<!--//       } else {-->
<!--//         this.barSize = [200 / this.tueMax.Max_X * (this.position.x - 0.01), 200 / this.tueMax.Max_X * (this.position.y - 0.01)]-->
<!--//       }-->
<!--//     }-->
<!--//     if (k === 0) {-->
<!--//       this.Stack = 'stack'-->
<!--//     } else {-->
<!--//       this.Stack = 'stack'-->
<!--//     }-->
<!--//     this.pushSeries()-->
<!--//   }-->
<!--//   console.log('type2One pushSeries:', this.series)-->
<!--// }-->
<!--//   console.log('type2One data:', this.data, this.position.x, this.position.y, this.Stack, this.position.BoxRotate, 'series', this.series)-->
<!--//   this.bar3D()-->
