<template>
  <div>
    <div class="Center_L" style="z-index: 99;">
      <img src="@/assets/images/Center_L.png" alt="">
    </div>
    <common>
    </common>

    <div class="bgc1">
      <div class="bac1_div1">
        <img src="@/assets/images/tj1_img.png" alt="" class="tj1_img">
        <p class="tj1_img_wz">人口年龄分布</p>
      </div>
      <div id="tjt1" style="width:100%;height: 330px; "></div>
    </div>
    <div class=" bottom">
      <div class="bottom_div">
        <img src="@/assets/images/tj1_img.png" alt="" class="tj1_img">
        <p class="tj1_img_wz">人口性别分布</p>
        <div ref="myCharts" style="width: 400px;height: 370px;" id="bzt1"></div>
      </div>
      <div class="bottom_div1">
        <img src="@/assets/images/tj1_img.png" alt="" class="tj1_img">
        <p class="tj1_img_wz">社区数据</p>
        <div id="bzt2" style="width: 400px;height: 450px;"></div>
      </div>
      <div class="bottom_div2">
        <img src="@/assets/images/tj1_img.png" alt="" class="tj1_img">
        <p class="tj1_img_wz">社区数据</p>
        <div id="bzt3" style="width: 400px;height: 450px;"></div>
      </div>
    </div>
    <div class="Center_R">
      <img src="@/assets/images/Center_R.png" alt="">
    </div>
  </div>

</template>

<script>
import common from "./dap1.vue";
import echarts from 'echarts'
import 'echarts-gl'
export default {
  data() {
    return {

    }
  },
  components: {
    common
  },
  methods: {
    bzt1() {
      // let myMidd = this.$echarts.init(document.getElementById('bzt1'))
      const myCharts = this.$echarts.init(this.$refs.myCharts);

      function getParametricEquation(startRatio, endRatio, isSelected, isHovered, k, height) {

        // 计算
        // console.log("getParametricEquation")
        let midRatio = (startRatio + endRatio) / 2;

        let startRadian = startRatio * Math.PI * 2;
        let endRadian = endRatio * Math.PI * 2;
        let midRadian = midRatio * Math.PI * 2;

        // 如果只有一个扇形，则不实现选中效果。
        if (startRatio === 0 && endRatio === 1) {
          isSelected = false;
        }

        // 通过扇形内径/外径的值，换算出辅助参数 k（默认值 1/3）
        k = typeof k !== 'undefined' ? k : 1 / 3;

        // 计算选中效果分别在 x 轴、y 轴方向上的位移（未选中，则位移均为 0）
        let offsetX = isSelected ? Math.cos(midRadian) * 0.1 : 0;
        let offsetY = isSelected ? Math.sin(midRadian) * 0.1 : 0;

        // 计算高亮效果的放大比例（未高亮，则比例为 1）
        let hoverRate = isHovered ? 1.05 : 1;

        // 返回曲面参数方程
        return {

          u: {
            min: -Math.PI,
            max: Math.PI * 3,
            step: Math.PI / 32
          },

          v: {
            min: 0,
            max: Math.PI * 2,
            step: Math.PI / 20
          },

          x: function(u, v) {
            if (u < startRadian) {
              return offsetX + Math.cos(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
            }
            if (u > endRadian) {
              return offsetX + Math.cos(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
            }
            return offsetX + Math.cos(u) * (1 + Math.cos(v) * k) * hoverRate;
          },

          y: function(u, v) {
            if (u < startRadian) {
              return offsetY + Math.sin(startRadian) * (1 + Math.cos(v) * k) * hoverRate;
            }
            if (u > endRadian) {
              return offsetY + Math.sin(endRadian) * (1 + Math.cos(v) * k) * hoverRate;
            }
            return offsetY + Math.sin(u) * (1 + Math.cos(v) * k) * hoverRate;
          },

          z: function(u, v) {
            if (u < -Math.PI * 0.5) {
              return Math.sin(u);
            }
            if (u > Math.PI * 2.5) {
              return Math.sin(u);
            }
            return Math.sin(v) > 0 ? 1 * height : -1;
          }
        };
      }

      // 生成模拟 3D 饼图的配置项
      function getPie3D(pieData, internalDiameterRatio) {

        let series = [];
        let sumValue = 0;
        let startValue = 0;
        let endValue = 0;
        let legendData = [];
        let k = typeof internalDiameterRatio !== 'undefined' ? (1 - internalDiameterRatio) / (1 + internalDiameterRatio) :
          1 / 3;

        // 为每一个饼图数据，生成一个 series-surface 配置
        for (let i = 0; i < pieData.length; i++) {

          sumValue += pieData[i].value;

          let seriesItem = {
            name: typeof pieData[i].name === 'undefined' ? `series${i}` : pieData[i].name,
            type: 'surface',
            parametric: true,
            wireframe: {
              show: false
            },
            pieData: pieData[i],
            pieStatus: {
              selected: false,
              hovered: false,
              k: k
            }
          };

          if (typeof pieData[i].itemStyle != 'undefined') {

            let itemStyle = {};

            typeof pieData[i].itemStyle.color != 'undefined' ? itemStyle.color = pieData[i].itemStyle.color : null;
            typeof pieData[i].itemStyle.opacity != 'undefined' ? itemStyle.opacity = pieData[i].itemStyle.opacity : null;

            seriesItem.itemStyle = itemStyle;
          }
          series.push(seriesItem);
        }

        // 使用上一次遍历时，计算出的数据和 sumValue，调用 getParametricEquation 函数，
        // 向每个 series-surface 传入不同的参数方程 series-surface.parametricEquation，也就是实现每一个扇形。
        for (let i = 0; i < series.length; i++) {
          endValue = startValue + series[i].pieData.value;
          console.log(series[i]);
          series[i].pieData.startRatio = startValue / sumValue;
          series[i].pieData.endRatio = endValue / sumValue;
          series[i].parametricEquation = getParametricEquation(series[i].pieData.startRatio, series[i].pieData.endRatio,
            false, false, k, series[i].pieData.value);

          startValue = endValue;

          legendData.push(series[i].name);
        }



        // 准备待返回的配置项，把准备好的 legendData、series 传入。
        let option = {
          tooltip: {
            formatter: params => {
              if (params.seriesName !== 'mouseoutSeries') {
                return `${params.seriesName}<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${params.color};"></span>${option.series[params.seriesIndex].pieData.value}`;
              }
            }
          },
          legend: {
            data: legendData,
            textStyle: {
              color: '#fff',
              fontSize: 26
            }
          },
          xAxis3D: {
            min: -1,
            max: 1
          },
          yAxis3D: {
            min: -1,
            max: 1
          },
          zAxis3D: {
            min: -1,
            max: 1
          },

          grid3D: {
            show: false,
            boxHeight: 20,
            //top: '30%',
            bottom: '50%',
            environment: "auto", //背景色,auto为自适应颜色
            viewControl: {
              distance: 300,
              alpha: 25,
              beta: 130,
            },

          },
          series: series
        };
        // console.log(option,"option") ;
        return option
      }

      let option = getPie3D([{
        name: '男',
        value: 3,
        itemStyle: {
          opacity: 0.5,
          color: 'rgba(0,127,244,.8)',
        }
      }

        , {
          name: '女',
          value: 1,
          itemStyle: {
            opacity: 0.5,
            color: 'rgba(209,126,23,.8)',
          }
        }

      ], 2);
      myCharts.setOption(option)
    }
  },
  mounted() {
    this.bzt1()
  },
  name: "tjt1",

}
</script>

<style scoped="scoped">
.bgc1 {
  width: 1550px;
  height: 322px;
  background: url(../../assets/images/txbgc.png);
  position: absolute;
  top: 16%;
  left: 8%;
  /* color: transparent; */
  /* opacity: 0.5; */
}

.tj1_img {
  width: 42px;
  height: 42px;
  position: absolute;
  top: 32px;
  left: 88px;

}

.tj1_img_wz {
  position: absolute;
  top: 38px;
  left: 143px;
  width: 132px;
  height: 18px;
  font-size: 20px;
  font-family: Microsoft YaHei;
  font-weight: 400;
  color: #AADDFF;
}

.bottom_div {
  width: 628px;
  height: 417px;
  background-repeat: no-repeat;
  background: url("../../assets/images/tj_bottom.png");
  position: absolute;
  top: 50%;
  left: 8%;
}

.bottom_div1 {
  width: 628px;
  height: 417px;
  background-repeat: no-repeat;
  background: url("../../assets/images/tj_bottom.png");
  position: absolute;
  top: 50%;
  left: 34.5%;
}

.bottom_div2 {
  width: 628px;
  height: 417px;
  background-repeat: no-repeat;
  background: url("../../assets/images/tj_bottom.png");
  position: absolute;
  top: 50%;
  left: 61%;
}

#bzt1 {
  position: absolute;
  left: 94px;
}

#bzt2 {
  position: absolute;
  left: 94px;
}

#bzt3 {
  position: absolute;
  left: 94px;
}

.Center_L {
  position: absolute;
  left: 2%;
  top: 30%;
}

.Center_R {
  position: absolute;
  right: 2%;
  top: 30%;
}
#bzt1{
  position: absolute;
  margin-top: 80px;
  /* top: 80px; */
}
</style>
