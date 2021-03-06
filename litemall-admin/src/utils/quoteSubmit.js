import axios from '_axios@0.18.1@axios'
import { saveAs } from 'file-saver'

export function getCurrentTime() {
  var date = new Date()

  var year = date.getFullYear();       //年 ,从 Date 对象以四位数字返回年份
  var month = date.getMonth() + 1      //月 ,从 Date 对象返回月份 (0 ~ 11) ,date.getMonth()比实际月份少 1 个月
  var day = date.getDate();            //日 ,从 Date 对象返回一个月中的某一天 (1 ~ 31)

  var hours = date.getHours();         //小时 ,返回 Date 对象的小时 (0 ~ 23)
  var minutes = date.getMinutes()      //分钟 ,返回 Date 对象的分钟 (0 ~ 59)
  var seconds = date.getSeconds()      //秒 ,返回 Date 对象的秒数 (0 ~ 59)

  //获取当前系统时间
  var currentDate = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds

  //修改月份格式
  if (month >= 1 && month <= 9) {
    month = "0" + month
  }

  //修改日期格式
  if (day >= 0 && day <= 9) {
    day = "0" + day
  }

  //修改小时格式
  if (hours >= 0 && hours <= 9) {
    hours = "0" + hours
  }

  //修改分钟格式
  if (minutes >= 0 && minutes <= 9) {
    minutes = "0" + minutes
  }

  //修改秒格式
  if (seconds >= 0 && seconds <= 9) {
    seconds = "0" + seconds
  }

  //获取当前系统时间  格式(yyyy-mm-dd hh:mm:ss)
  var currentFormatDate = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds
  return currentFormatDate
}
export const openExcel1 = (data, name) => {
  const fileName = name
  const elink = document.createElement('a')
  elink.setAttribute('download', fileName)
  elink.style.display = 'none'
  const blob = new Blob([data], { type: 'application/x-xls' })
  elink.href = window.URL.createObjectURL(blob)
  document.body.appendChild(elink)
  elink.click()
}

export function openExcel(url,id) {
  // const name = url.replace("61.175.134.14","")
  // const fileName = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate() + id +'.'+name.substr(name.indexOf('.')+1)
  saveAs(url, id)
}
export function openExcel0(url, name) {
  const fileName = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate() + name + '.xlsx'
  const a = document.createElement('a')
  a.setAttribute('href', url)
  a.setAttribute('target', '_blank')
  a.setAttribute('download', fileName)
  a.click()
  // window.open(url, '_blank')
}

export function openExcel111(url, name) {
  // const fileName = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate() + name + '.xlsx'
  const a = document.createElement('a')
  // a.setAttribute('target', '_blank')
  a.setAttribute('href', url)
  a.setAttribute('download', 'ggggg.xlsx')
  a.click()
  // window.open(url, '_blank')
}
export function openExcel33(url, name) {
  const bloc = dataURLtoBlob(url)
  const blob = new Blob([bloc], { type: 'application/vnd.ms-excel' })
  // const fileName = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate() + name + '.xlsx'
  const downLoadEle = document.createElement('a')
  // 通过返回的二进制数据来创建一个对象URL.
  const href = URL.createObjectURL(blob)
  downLoadEle.style.display = 'none'
  downLoadEle.href = href
  // 信息表为自定义文件名
  downLoadEle.download = 'baojd'
  document.body.appendChild(downLoadEle)
  downLoadEle.click()
  document.body.removeChild(downLoadEle)
  // 当加载完成后释放对象URL.
  window.URL.revokeObjectURL(href)
  // window.open(fileurl, '_blank')
}

function dataURLtoBlob(dataurl) {
  const arr = dataurl.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new Blob([u8arr], { type: mime })
}

export function openExcel3(url, name) {
  const requestConfig = { headers: { 'Content-Type': 'application/vnd.ms-excel' }}
  axios.post(url, requestConfig, { responseType: 'blob'
  }).then(res => {
    // 处理返回的文件流
    const content = res.data
    const blob = new Blob([content])
    var date = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate()
    const fileName = date + name + '.xlsx'
    if ('download' in document.createElement('a')) {
      // 非IE下载
      const elink = document.createElement('a')
      elink.target = '_blank'
      elink.download = fileName
      // elink.style.display = 'none'
      elink.href = URL.createObjectURL(blob)
      document.body.appendChild(elink)
      elink.click()
      URL.revokeObjectURL(elink.href) // 释放URL 对象
      document.body.removeChild(elink)
    } else {
      // IE10+下载
      navigator.msSaveBlob(blob, fileName)
    }
  })
}
