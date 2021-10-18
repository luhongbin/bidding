<template>
  <div class="app-container">
    <el-card class="box-card">
      <h3>箱体信息<span>[用户名:{{ nickname }}  用户ID:{{ userId }}]</span></h3>
      <el-table :data="teuData" border @selection-change="handleSelectionChangeTeu">
        <el-table-column type="selection" :reserve-selection="true" label="全选" width="55" />
        <el-table-column align="center" label="TEU ID" prop="id" />
        <el-table-column align="center" label="TEU类型" prop="teuTypeCn" />
        <el-table-column align="center" label="TEU名称" prop="teuNameCn" />
        <el-table-column align="center" label="长(米)" prop="length" />
        <el-table-column align="center" label="宽(米)" prop="width" />
        <el-table-column align="center" label="高(米)" prop="height" />
        <el-table-column align="center" label="体积(立方米)" prop="cube" />
        <el-table-column align="center" label="空箱自重(吨)" prop="tareWeight" />
        <el-table-column align="center" label="毛重(吨)" prop="grossWeight" />
        <el-table-column align="center" label="最大载重(吨)" prop="maxPayload" />
      </el-table>
    </el-card>
    <el-card class="box-card">
      <h3>装箱文件名称:{{ filename }}</h3>
      <el-table :data="filetoForm" border @selection-change="handleSelectionChangeBill">
        <el-table-column type="selection" :reserve-selection="true" width="55" />
        <el-table-column align="center" label="货号" prop="name" />
        <el-table-column align="center" label="长(米)" prop="length" />
        <el-table-column align="center" label="宽(米)" prop="width" />
        <el-table-column align="center" label="高(米)" prop="height" />
        <el-table-column align="center" label="体积(立方米)" prop="cube" />
        <el-table-column align="center" label="重量(kg)" prop="weight" />
        <el-table-column align="center" label="件数" prop="quantity" />
      </el-table>
    </el-card>
    <el-card class="box-card">
      <h3>计算设置选项</h3>
      <el-form label-width="150px">
        <el-form-item label="装箱优先策略">
          <el-checkbox-group v-model="priorStatic">
            <el-checkbox id="width" label="宽大优先">宽大优先
              <el-radio-group v-model="widthOrder">
                <el-radio :label="true">顺序</el-radio>
                <el-radio :label="false">逆序</el-radio>
              </el-radio-group></el-checkbox>
            <el-checkbox id="length" label="长大优先">长大优先
              <el-radio-group v-model="lengthOrder">
                <el-radio :label="true">顺序</el-radio>
                <el-radio :label="false">逆序</el-radio>
              </el-radio-group></el-checkbox>
            <el-checkbox id="quantity" label="数量优先">数量优先
              <el-radio-group v-model="quanlityOrder">
                <el-radio :label="true">顺序</el-radio>
                <el-radio :label="false">逆序</el-radio>
              </el-radio-group></el-checkbox>
            <el-checkbox id="cube" label="体积优先">体积优先
              <el-radio-group v-model="cubeOrder">
                <el-radio :label="true">顺序</el-radio>
                <el-radio :label="false">逆序</el-radio>
              </el-radio-group></el-checkbox>
            <el-checkbox id="weight" label="重量优先">重量优先
              <el-radio-group v-model="weightOrder">
                <el-radio :label="true">顺序</el-radio>
                <el-radio :label="false">逆序</el-radio>
              </el-radio-group></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="工作面拆分策略">
          <el-checkbox-group v-model="priorSplit">
            <el-checkbox id="deep" label="归集">归集</el-checkbox>
            <el-checkbox id="shallow" label="单面">单面</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="计算方式">
          <el-radio-group v-model="isDeep">
            <el-radio :label="true">全面</el-radio>
            <el-radio :label="false">简洁</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button @click="handlePackagefile2result()">开始并计算</el-button>
      </el-form>
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
import { getToken } from '@/utils/auth'

export default {
  name: 'Teuedit',

  data() {
    return {
      tmpPack: [],
      checkAll: false,
      allElection: [], // 全选
      tueMax: [],
      billMax: [],
      multipleTeuSelection: [], // 选中要传给后台的数据 对象
      multipleBillSelection: [], // 选中要传给后台的数据 对象
      priorStatic: ['宽大优先'],
      priorSplit: ['归集'],
      tmpMethod: 0,
      isDeep: true,
      lengthOrder: true,
      cubeOrder: true,
      widthOrder: true,
      quanlityOrder: true,
      weightOrder: true,
      isIndeterminate: true,
      WorkSplit: '',
      userId: 0,
      CodeId: 0,
      sonTmp: 0,
      nickname: '',
      fileData: '',
      filename: '',
      teuData: {},
      filetoForm: [],
      packagefileForm: { id: 0, userId: 0, addTime: '', updateTime: '', cube: 0.00, weight: 0.00, quanlity: 0, rowcount: 0, status: true },
      workSum: 0, // 工作面编号
      teuSn: 0, // 装箱的容器排號
      freeBoxIndexs: 0,
      BalesQuanlity: 0,
      boxRotate: 0,
      boxsName: '',
      tmpStart_X: 0,
      tmpStart_Y: 0,
      tmpStart_Z: 0,
      Start_X: 0, // 工作面起点坐标
      Start_Y: 0,
      Start_Z: 0,
      SMax_X: 0,
      W_X_N: 0,
      W_Z_CON: 0,
      teuFreeSpace: 0, // 容器总体积
      exitInbox: false,
      Bales_X: 0,
      Bales_Y: 0,
      Bales_Z: 0,
      WFace_X: 0,
      WFace_Y: 0,
      WFace_Z: 0,
      inBoxOver: 0, // 装载完毕标志 0 未完成 1完成
      exitWorkface: false,
      BoxIndexs: 0,
      W_Z_N: 0,
      W_Y_N: 0,
      Max_X: 0,
      Max_Y: 0,
      Max_Z: 0,
      Re_X1: 0, // 当前工作面剩余空间的尺寸
      Re_X2: 0,
      Re_X3: 0,
      Re_X4: 0,
      Re_Y1: 0,
      Re_Y2: 0,
      Re_Y3: 0,
      Re_Y4: 0,
      Re_Z1: 0,
      Re_Z2: 0,
      Re_Z3: 0,
      Re_Z4: 0,
      S_Start_X1: 0, // 剩余空间起点坐标
      S_Start_X2: 0,
      S_Start_X3: 0,
      S_Start_X4: 0,
      S_Start_Y1: 0,
      S_Start_Y2: 0,
      S_Start_Y3: 0,
      S_Start_Y4: 0,
      S_Start_Z1: 0,
      S_Start_Z2: 0,
      S_Start_Z3: 0,
      S_Start_Z4: 0,
      selectCase: 0,
      boxTotalVol: 0,
      TeuSel: 0,
      W_X_NeedCount: 0, // X方向需要的空间數量
      InContain: 0 // 装箱數量
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    },
    indeterminate() {
      return this.multipleTeuSelection.length && this.multipleTeuSelection.length < this.teuData.length
    },
    checked: {
      get() {
        return this.multipleTeuSelection.length === this.teuData.length
      },
      set(val) {
        this.$refs.multipleTable.toggleAllSelection(val)
      }
    }
  },
  created() {
    this.init()
    this.handleFile()
  },
  methods: {
    init: function() {
      if (this.$route.query.userId == null) {
        return
      }
      this.userId = this.$route.query.userId
      this.nickname = this.$route.query.nickname
      this.fileData = this.$route.query.fileData
      this.filename = this.$route.query.filename
      this.teuData = this.$route.query.tuedata
      console.log('fileData', this.fileData)
    },
    getRowKeys(row) {
      return row.number
    },
    handleSelectionChangeTeu(val) {
      var vlength = val.length
      this.multipleTeuSelection = val
      this.checkAll = vlength === this.pagesize
      this.isIndeterminate = vlength > 0 && vlength < this.pagesize
      console.log('multipleTeuSelection:', this.multipleTeuSelection)
    },
    handleSelectionChangeBill(val) {
      var vlength = val.length
      this.multipleBillSelection = []
      val.forEach(item => {
        if (item.length > 0) { this.multipleBillSelection.push({ name: item.name, length: item.length, width: item.width, height: item.height, quanlity: item.quantity, weight: item.weight, cube: item.cube, quan: item.quantity }) }
      })
      this.checkAll = vlength === this.pagesize
      this.isIndeterminate = vlength > 0 && vlength < this.pagesize
    },
    handleCheckAllChange() {
      this.$refs.multipleTable.toggleAllSelection()
      this.isIndeterminate = false
    },
    handlePackagefile2result() {
      if (this.multipleTeuSelection.length <= 0) {
        alert('必须选择至少一款 集装箱容器')
        return
      }
      if (this.multipleBillSelection.length <= 0) {
        alert('必须选择至少一款 待装箱产品')
        return
      }
      if (this.priorStatic.length <= 0) {
        alert('必须选择至少一种 装箱优先策略')
        return
      }
      if (this.priorSplit.length <= 0) {
        alert('必须选择至少一款 搜索策略')
        return
      }
      this.beginCalcPack()
      this.$router.push({ path: '/package/tactics', query: { tmpMethod: this.tmpPack }})
    },

    OrderBales(order_flag) { // '优先级排序
      var key, order
      switch (order_flag) {
        case '宽大优先':
          key = 'width'
          if (this.widthOrder) order = 'desc'
          else order = 'asc'
          break
        case '长度优先':
          key = 'length'
          if (this.lengthOrder) order = 'desc'
          else order = 'asc'
          break
        case '体积优先':
          key = 'cube'
          if (this.cubeOrder) order = 'desc'
          else order = 'asc'
          break
        case '数量优先':
          key = 'quanlity'
          if (this.quantityOrder) order = 'desc'
          else order = 'asc'
          break
        case '重量优先':
          if (this.weightOrder) order = 'desc'
          else order = 'asc'
          key = 'weight'
          break
      }
      this.sortByKey(this.multipleBillSelection, key, order)
      for (var b = 0; b < this.multipleBillSelection.length; b++) { // 重新分配初始數量
        this.multipleBillSelection[b]['quanlity'] = this.multipleBillSelection[b]['quan']
      }

      console.log('multipleBill:', this.multipleBillSelection, key, order)
    },
    sortByKey(arr, key, order) { // 传入数组重新排序,排序功能arr多维数组，key多维数组键名，order升序或降序）
      for (var i = 0; i < arr.length; i++) {
        for (var j = i + 1; j < arr.length; j++) {
          if (order === 'desc') {
            if (parseFloat(arr[i][key]) <= parseFloat(arr[j][key])) {
              var min = arr[i]
              arr[i] = arr[j]
              arr[j] = min
            }
          } else {
            if (parseFloat(arr[i][key]) >= parseFloat(arr[j][key])) {
              var max = arr[i]
              arr[i] = arr[j]
              arr[j] = max
            }
          }
        }
      }
      return arr
    },
    handleFile() {
      this.result = this.fileData
      var array = this.result.toString().split('\n')
      var fileto = []
      var w, c, q, r
      r = w = c = q = 0
      for (var i in array) {
        fileto[i] = array[i].split('|')
        this.filetoForm.push({ name: fileto[i][0], length: Number(fileto[i][1]) / 1000, width: Number(fileto[i][2]) / 1000, height: Number(fileto[i][3]) / 1000, cube: Number(fileto[i][1]) * Number(fileto[i][2]) * Number(fileto[i][3]) * Number(fileto[i][5]) / 1000000000, weight: Number(fileto[i][4]) * Number(fileto[i][5]) / 1000, quantity: Number(fileto[i][5]) })
        c = c + Number(fileto[i][1]) * Number(fileto[i][2]) * Number(fileto[i][3]) * Number(fileto[i][5])
        w = w + Number(fileto[i][4]) * Number(fileto[i][5])
        q = q + Number(fileto[i][5])
        r += 1
      }
      this.filetoForm.push({ name: '合计:' + r + '项', cube: c / 1000000000, weight: w / 1000, quantity: q })
      console.log('arr', fileto, c, w, q, r)
      this.newid = false
      this.userId = 1
      this.status = 1
    },
    checkBoxin() {
      var tmp
      tmp = this.multipleTeuSelection[this.TeuSel]
      this.BoxIndexs = -1 // 货物在货物数组中的索引
      this.freeBoxIndexs = 0
      if (this.WorkSplit === '归集') {
        this.WFace_X = this.SMax_X; this.WFace_Y = this.Max_Y; this.WFace_Z = this.Max_Z
      } else {
        this.WFace_X = this.SMax_X; this.WFace_Y = tmp['width']; this.WFace_Z = tmp['height'] // 设置当前工作面可用空间尺寸
      }
      for (var i = 0; i < this.multipleBillSelection.length; i++) { // 遍历货物数组搜寻可以放入工作面空间的货物
        tmp = this.multipleBillSelection[i]
        if (tmp['quanlity'] > 0 && tmp['width'] < this.WFace_Y && tmp['height'] < this.WFace_Z) { // '是否可装载，并且剩余数量大于0
          this.Bales_X = tmp['length']; this.Bales_Y = tmp['width']; this.Bales_Z = tmp['height']; this.BalesQuanlity = tmp['quanlity']; this.boxsName = tmp['name']
          this.boxRotate = 0 // 设置货物是否水平旋转标志 0 未旋转 1
          if (this.Bales_X < this.WFace_X) { // 货物长度小于工作面可用长度
            this.BoxIndexs = i
            break
          }
        }
      }
      if (this.BoxIndexs === -1) { // 如果没有合适装载尺寸的货物，货物水平旋转
        for (i = 0; i < this.multipleBillSelection.length; i++) { // 遍历货物数组搜寻可以放入工作面空间的货物
          tmp = this.multipleBillSelection[i]
          if (tmp['quanlity'] > 0 && tmp['length'] < this.WFace_Y && tmp['height'] < this.WFace_Z) { // 是否未装载，并且剩余数量大于0
            this.Bales_X = tmp['width']; this.Bales_Y = tmp['length']; this.Bales_Z = tmp['height']; this.BalesQuanlity = tmp['quanlity']; this.boxsName = tmp['name']
            this.boxRotate = 1
            if (this.Bales_X < this.WFace_X) {
              this.BoxIndexs = i
              break
            }
          }
        }
      }
      if (this.BoxIndexs === -1) { // 如果是0 會和系統衝突 定義之前已經設置默認值-1，如果还没有可装载的货物 判断是否装箱完成
        this.freeBoxIndexs = -1
        for (i = 0; i < this.multipleBillSelection.length; i++) {
          if (this.multipleBillSelection[i]['quanlity'] > 0) { // 是否未装载，并且剩余数量大于0
            this.freeBoxIndexs = i
            break
          }
        }
        if (this.freeBoxIndexs === -1) {
          this.inBoxOver = 1 // 装箱成功
          this.exitInbox = true
          this.exitWorkface = true // 当前容器剩余空间无法放下合适的箱子,使用下一个容器
        }
      }
      console.log('遍历', this.teuSn, this.inBoxOver, this.freeBoxIndexs, this.boxsName, this.BoxIndexs, this.InContain, this.BalesQuanlity, tmp, this.WFace_Z, this.WFace_Y, this.WFace_X)
      this.W_X_N = Math.floor(this.WFace_X / this.Bales_X); this.W_Y_N = Math.floor(this.WFace_Y / this.Bales_Y); this.W_Z_N = Math.floor(this.WFace_Z / this.Bales_Z)
      if (this.WorkSplit !== '归集') this.W_X_N = 1
      if (this.W_Y_N * this.W_Z_N * this.W_X_N > this.BalesQuanlity) { this.InContain = this.BalesQuanlity } else { this.InContain = this.W_Y_N * this.W_Z_N * this.W_X_N } // 工作面可装入箱子的数量是否小于箱子总数
      if (this.InContain % this.W_Y_N > 0) { if (this.InContain < this.W_Y_N) { this.W_Z_CON = 1 } else { this.W_Z_CON = Math.floor(this.InContain / this.W_Y_N) + 1 } } else { this.W_Z_CON = Math.floor(this.InContain / this.W_Y_N) }
    },
    initBox() {
      this.exitInbox = false
      var tmp = this.multipleTeuSelection[this.TeuSel]
      this.Max_X = tmp['length']; this.Max_Y = tmp['width']; this.Max_Z = tmp['height'] // 设置工作空间最大尺寸等于容器尺寸
    },
    initSpace() {
      this.teuSn = this.inBoxOver = this.teuFreeSpace = this.workSum = this.Start_X = this.Start_Y = this.Start_Z = 0
      this.exitWorkface = false
      this.CodeId += 1
      this.tmpPack[this.selectCase].children.push({
        children: [],
        name: this.multipleTeuSelection[this.TeuSel].teuNameCn + '.' + (this.teuSn + 1).toString(),
        type: 2,
        code: this.CodeId
      })
      console.log('initSpace', this.inBoxOver, this.multipleTeuSelection[this.TeuSel].teuNameCn + '.' + (this.teuSn + 1).toString(), this.tmpPack[this.selectCase])
    },
    installTeu() { // 小工作面
      this.initBox()
      while (!this.exitInbox) {
        this.initSpace()
        while (!this.exitWorkface) { // 工作面循环开始
          this.WFace_X = this.Max_X; this.WFace_Y = this.Max_Y; this.WFace_Z = this.Max_Z
          this.SMax_X = this.Max_X // X方向上可用的最大尺寸
          this.checkBoxin() // 货物初始化
          console.log('InContain开始', this.InContain, this.WFace_X, this.WFace_Y, this.WFace_Z, this.SMax_X)
          this.SStart_X = this.Start_X; this.SStart_Y = this.Start_Y; this.SStart_Z = this.Start_Z
          if (this.WorkSplit === '归集') {
            this.W_X_N = Math.floor(this.WFace_X / this.Bales_X)
            this.reSpaceMutilCalc()
            this.SMax_X = this.SMax_X - this.Bales_X * this.W_X_NeedCount // 剩余长度方向可用尺寸
          } else {
            this.W_X_N = 1
            this.reSpaceOneCalc()
            this.SMax_X = this.SMax_X - this.Bales_X // 剩余长度方向可用尺寸
          }
          this.tmpStart_X = this.Start_X; this.tmpStart_Y = this.Start_Y; this.tmpStart_Z = this.Start_Z
          if (this.InContain <= 0) break
          this.workSum += 1 // 设置工作面编号
          this.saveChild()
          var lo
          if (this.isDeep) { lo = 4 } else { lo = 3 } // 采用深度搜索
          for (this.sonTmp = 1; this.sonTmp <= lo; this.sonTmp++) {
            // eslint-disable-next-line no-eval
            this.SStart_X = eval('this.S_Start_X' + this.sonTmp.toString()); this.SStart_Y = eval('this.S_Start_Y' + this.sonTmp.toString()); this.SStart_Z = eval('this.S_Start_Z' + this.sonTmp.toString())
            // eslint-disable-next-line no-eval
            this.WFace_Y = eval('this.Re_Y' + this.sonTmp.toString()); this.WFace_Z = eval('this.Re_Z' + this.sonTmp.toString()); this.WFace_X = eval('this.Re_X' + this.sonTmp.toString())
            this.checkBoxin()
            if (this.freeBoxIndexs === -1) return
            console.log('工作面' + this.workSum.toString() + ',' + this.boxsName, this.InContain, this.isDeep, this.sonTmp, this.BoxIndexs, this.multipleBillSelection)
            this.tmpStart_X = this.SStart_X; this.tmpStart_Y = this.SStart_Y; this.tmpStart_Z = this.SStart_Z
            this.saveChild()
            if (this.isDeep) { this.reSpaceMutilCalc() } else { this.reSpaceOneCalc() }
          }
          this.reSpace()
          if (this.WorkSplit === '归集') {
            this.Start_X = this.Start_X + this.Bales_X * this.W_X_NeedCount; this.Start_Y = this.Start_Z = 0 // 下一工作面起点坐标
          } else { this.Start_X = this.Start_X + this.Bales_X; this.Start_Y = this.Start_Z = 0 }
        }
        var tmp = this.multipleTeuSelection[this.TeuSel]
        if (this.WorkSplit === '归集') {
          this.teuFreeSpace = this.teuFreeSpace + (tmp['length'] - this.SMax_X) * tmp['width'] * tmp['height']
        } else {
          this.teuFreeSpace = this.teuFreeSpace + tmp['length'] * tmp['width'] * tmp['height']
        }
        this.teuSn += 1
      }
      this.tmpPack[this.selectCase]['tue_total'] = this.teuSn // 箱数
      this.tmpPack[this.selectCase]['effective'] = this.teuFreeSpace / this.boxTotalVol * 100 // 计算效率
    },
    reSpace() {
      var lo
      if (this.isDeep) { lo = 4 } else { lo = 3 } // 采用深度搜索
      for (this.sonTmp = 1; this.sonTmp <= lo; this.sonTmp++) {
        // eslint-disable-next-line no-eval
        this.WFace_Y = eval('this.Re_Y' + this.sonTmp.toString()); this.WFace_Z = eval('this.Re_Z' + this.sonTmp.toString()); this.WFace_X = eval('this.Re_X' + this.sonTmp.toString())
        // eslint-disable-next-line no-eval
        this.tmpStart_X = eval('this.S_Start_X' + this.sonTmp.toString()); this.tmpStart_Y = eval('this.S_Start_Y' + this.sonTmp.toString()); this.tmpStart_Z = eval('this.S_Start_Z' + this.sonTmp.toString())
        console.log('reSpace', this.sonTmp)
        this.checkBoxin()
        if (this.freeBoxIndexs === -1) return
        console.log('工作面' + this.workSum.toString() + ',' + this.boxsName, this.InContain, this.isDeep, this.sonTmp, this.BoxIndexs, this.param)
        this.saveChild()
        // 工作面可装入箱子的数量是否小于箱子总数
        if (this.W_Y_N * this.W_Z_N * this.W_X_N > this.BalesQuanlity) { this.InContain = this.BalesQuanlity } else { this.InContain = this.W_Y_N * this.W_Z_N * this.W_X_N }
        if (this.isDeep) { this.reSpaceMutilCalc() } else { this.reSpaceOneCalc() }
      }
      this.reSpace()
    },
    saveChild() {
      console.log('saveChild', this.selectCase, this.teuSn, '工作面' + this.workSum.toString() + ',' + this.boxsName, this.BoxIndexs, this.selectCase, this.CodeId, this.tmpPack)
      this.multipleBillSelection[this.BoxIndexs]['quanlity'] = this.multipleBillSelection[this.BoxIndexs]['quanlity'] - this.InContain
      this.CodeId += 1
      this.tmpPack[this.selectCase].children[this.teuSn].children.push({
        x: this.Bales_X,
        y: this.Bales_Y,
        z: this.Bales_Z,
        workSum: this.workSum,
        SonNum: this.sonTmp,
        name: '工作面' + this.workSum.toString() + ',' + this.boxsName + '(' + this.InContain + '/' + this.multipleBillSelection[this.BoxIndexs]['quan'] + ')',
        InContain: this.InContain,
        W_X_N: this.W_X_NeedCount,
        type: 3,
        code: this.CodeId,
        W_Y_N: this.W_Y_N,
        W_Z_N: this.W_Z_N,
        SStart_X: this.tmpStart_X,
        SStart_Y: this.tmpStart_Y,
        SStart_Z: this.tmpStart_Z,
        BoxRotate: this.boxRotate
      })
    },
    beginCalcPack() {
      var IsDeep, j, i, k, tmpName, tmp
      this.selectCase = 0
      this.tmpMethod = this.priorStatic.length * this.priorSplit.length * this.multipleTeuSelection.length
      for (i = 0; i < this.multipleBillSelection.length; i++) {
        this.boxTotalVol = this.boxTotalVol + this.multipleBillSelection[i]['cube'] * this.multipleBillSelection[i]['quanlity']
      }
      for (i = 0; i < this.multipleTeuSelection.length; i++) { // 遍历货物数组搜寻可以放入工作面空间的货物
        tmp = this.multipleTeuSelection[i]
        this.tueMax = { 'Max_X': tmp.length, 'Max_Y': tmp.width, 'Max_Z': tmp.height, 'Max_W': tmp.weight }
        for (j in this.priorStatic) { // '循环优先策略
          for (k in this.priorSplit) { // '循环工作面策略
            if (this.isDeep === true) { IsDeep = '深度搜索' } else IsDeep = '浅度搜索' // 浅度搜索
            tmpName = '方案' + (this.selectCase + 1).toString() + ':' + this.multipleTeuSelection[i]['teuNameCn'] + '.' + this.priorStatic[j] + '.' + this.priorSplit[k] + '.' + IsDeep
            this.CodeId += 1
            this.tmpPack[this.selectCase] = { children: [], tueMax: this.tueMax, code: this.CodeId, name: tmpName, type: 1, priorStatic: this.priorStatic[j], priorSplit: this.priorSplit[k], isDeep: this.isDeep, tue_total: 0, effective: 0 }
            this.OrderBales(this.priorStatic[j]) // 重排货物列表
            this.TeuSel = i
            this.WorkSplit = this.priorSplit[k]
            this.installTeu()
            this.selectCase += 1
          }
        }
      }
    },
    reSpaceOneCalc() {
      if (this.InContain % this.W_Y_N === 0) { // '无剩余空间2
        this.Re_X1 = this.Bales_X; this.Re_X2 = 0; this.Re_X3 = this.Bales_X
        this.Re_Y1 = this.W_Y_N * this.Bales_Y; this.Re_Y2 = 0; this.Re_Y3 = this.WFace_Y - this.Bales_Y * this.W_Y_N
        this.Re_Z1 = this.WFace_Z - this.Bales_Z * this.W_Z_CON; this.Re_Z2 = 0; this.Re_Z3 = this.WFace_Z
        this.S_Start_Y1 = this.tmpStart_Y; this.S_Start_Y2 = this.tmpStart_Y + this.W_Y_N * this.Bales_Y; this.S_Start_Y3 = this.tmpStart_Y + this.W_Y_N * this.Bales_Y
        this.S_Start_Z1 = this.tmpStart_Z + this.Bales_Z * this.W_Z_CON; this.S_Start_Z2 = this.tmpStart_Z; this.S_Start_Z3 = this.tmpStart_Z
      } else {
        if (this.W_Z_CON > 1) {
          this.Re_X1 = this.Bales_X; this.Re_Y1 = (this.InContain % this.W_Y_N) * this.Bales_Y; this.Re_Z1 = this.WFace_Z - this.Bales_Z * this.W_Z_CON
          this.Re_X2 = this.Bales_X; this.Re_Y2 = (this.W_Y_N - (this.InContain % this.W_Y_N)) * this.Bales_Y; this.Re_Z2 = this.WFace_Z - this.Bales_Z * (this.W_Z_CON - 1)
          this.Re_X3 = this.Bales_X; this.Re_Y3 = this.WFace_Y - this.Bales_Y * this.W_Y_N; this.Re_Z3 = this.WFace_Z
          this.S_Start_Y1 = this.tmpStart_Y; this.S_Start_Z1 = this.tmpStart_Z + this.Bales_Z * this.W_Z_CON
          this.S_Start_Y2 = this.tmpStart_Y + (this.InContain % this.W_Y_N) * this.Bales_Y; this.S_Start_Z2 = this.tmpStart_Z + this.Bales_Z * (this.W_Z_CON - 1)
          this.S_Start_Y3 = this.tmpStart_Y + this.Bales_Y * this.W_Y_N; this.S_Start_Z3 = this.tmpStart_Z
        } else { // '无剩余空间3
          this.Re_X1 = this.Bales_X; this.Re_Y1 = (this.InContain % this.W_Y_N) * this.Bales_Y; this.Re_Z1 = this.WFace_Z - this.Bales_Z * this.W_Z_CON
          this.Re_X2 = this.Bales_X; this.Re_Y2 = this.WFace_Y - (this.InContain % this.W_Y_N) * this.Bales_Y; this.Re_Z2 = this.WFace_Z
          this.Re_X3 = this.Re_Y3 = this.Re_Z3 = 0
          this.S_Start_Y1 = this.tmpStart_Y
          this.S_Start_Y2 = this.tmpStart_Y + (this.InContain % this.W_Y_N) * this.Bales_Y
          this.S_Start_Y3 = this.tmpStart_Y + (this.InContain % this.W_Y_N) * this.Bales_Y
          this.S_Start_Z1 = this.tmpStart_Z + this.Bales_Z * this.W_Z_CON
          this.S_Start_Z2 = this.tmpStart_Z
          this.S_Start_Z3 = this.tmpStart_Z
        }
      }
    },
    reSpaceMutilCalc() {
      if (this.InContain % (this.W_Y_N * this.W_Z_N) === 0) {
        this.W_X_NeedCount = this.InContain / (this.W_Y_N * this.W_Z_N)
        this.Re_X1 = this.Bales_X * this.W_X_NeedCount; this.Re_Y1 = this.Bales_Y * this.W_Y_N; this.Re_Z1 = this.WFace_Z - this.Bales_Z * this.W_Z_N
        this.Re_X2 = this.Bales_X * this.W_X_NeedCount; this.Re_Y2 = this.WFace_Y - this.Bales_Y * this.W_Y_N; this.Re_Z2 = this.WFace_Z
        this.Re_X3 = this.Re_Y3 = this.Re_Z3 = 0
        this.Re_X4 = this.Re_Y4 = this.Re_Z4 = 0
        this.S_Start_X1 = this.tmpStart_X; this.S_Start_Y1 = this.tmpStart_Y; this.S_Start_Z1 = this.tmpStart_Z + this.Bales_Z * this.W_Z_N
        this.S_Start_X2 = this.tmpStart_X; this.S_Start_Y2 = this.tmpStart_Y + this.Bales_Y * this.W_Y_N; this.S_Start_Z2 = this.tmpStart_Z
        this.S_Start_X3 = this.S_Start_Y3 = this.S_Start_Z3 = 0
        this.S_Start_X4 = this.S_Start_Y4 = this.S_Start_Z4 = 0
      } else {
        this.W_X_NeedCount = Math.floor(this.InContain / (this.W_Y_N * this.W_Z_N)) + 1
        this.Re_X1 = this.Bales_X * (this.W_X_NeedCount - 1); this.Re_Y1 = this.Bales_Y * this.W_Y_N; this.Re_Z1 = this.WFace_Z - this.Bales_Z * this.W_Z_N
        this.Re_X2 = this.Bales_X * this.W_X_NeedCount; this.Re_Y2 = this.WFace_Y - this.Bales_Y * this.W_Y_N; this.Re_Z2 = this.WFace_Z
        this.S_Start_X1 = this.tmpStart_X; this.S_Start_Y1 = this.tmpStart_Y; this.S_Start_Z1 = this.tmpStart_Z + this.Bales_Z * this.W_Z_N
        this.S_Start_X2 = this.tmpStart_X; this.S_Start_Y2 = this.tmpStart_Y + this.Bales_Y * this.W_Y_N; this.S_Start_Z2 = this.tmpStart_Z
        if (((this.InContain % (this.W_Y_N * this.W_Z_N)) % this.W_Y_N) > 0) {
          this.Re_X3 = this.Bales_X; this.Re_Y3 = this.Bales_Y * ((this.InContain % (this.W_Y_N * this.W_Z_N)) % this.W_Y_N); this.Re_Z3 = this.WFace_Z - this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N)) / this.W_Y_N + 1)
          this.Re_X4 = this.Bales_X; this.Re_Y4 = this.Bales_Y * (this.W_Y_N - ((this.InContain % (this.W_Y_N * this.W_Z_N)) % this.W_Y_N)); this.Re_Z4 = this.WFace_Z - this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N)) / this.W_Y_N)
          this.S_Start_X3 = this.tmpStart_X + this.Bales_X * (this.W_X_NeedCount - 1); this.S_Start_Y3 = this.tmpStart_Y; this.S_Start_Z3 = this.tmpStart_Z + this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N) / this.W_Y_N) + 1)
          this.S_Start_X4 = this.tmpStart_X + this.Bales_X * (this.W_X_NeedCount - 1); this.S_Start_Y4 = this.tmpStart_Y + this.Bales_Y * ((this.InContain % (this.W_Y_N * this.W_Z_N)) % this.W_Y_N); this.S_Start_Z4 = this.tmpStart_Z + this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N)) / this.W_Y_N)
        } else {
          this.Re_X3 = this.Bales_X; this.Re_Y3 = this.Bales_Y * this.W_Y_N; this.Re_Z3 = this.WFace_Z - this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N)) / this.W_Y_N)
          this.Re_X4 = this.Re_Y4 = this.Re_Z4 = 0
          this.S_Start_X3 = this.tmpStart_X + this.Bales_X * (this.W_X_NeedCount - 1); this.S_Start_Y3 = this.tmpStart_Y; this.S_Start_Z3 = this.tmpStart_Z + this.Bales_Z * ((this.InContain % (this.W_Y_N * this.W_Z_N)) / this.W_Y_N)
          this.S_Start_X4 = this.S_Start_Y4 = this.S_Start_Z4 = 0
        }
      }
      console.log('reSpaceMutilCalc')
      console.log('S_Start_X', this.S_Start_X1, this.S_Start_X2, this.S_Start_X3, this.S_Start_X4)
      console.log('S_Start_Y', this.S_Start_Y1, this.S_Start_Y2, this.S_Start_Y3, this.S_Start_Y4)
      console.log('S_Start_Z', this.S_Start_Z1, this.S_Start_Z2, this.S_Start_Z3, this.S_Start_Z4)
      console.log('WFace', this.WFace_X, this.WFace_Y, this.WFace_Z)
      console.log('W_Y_N', this.W_X_NeedCount, this.InContain, this.W_X_N, this.W_Y_N, this.W_Z_N)
    }
  }
}
</script>
