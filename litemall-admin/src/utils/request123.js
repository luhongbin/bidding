import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

var isIneer = isInnerIPFn()
console.log('isIneer' + isIneer)

// create an axios instance
var base_url = ''
if (isIneer === true) { base_url = 'http://192.168.0.2:8080/admin'} else { base_url = 'http://61.175.134.14:8190/admin' }

const service = axios.create({
  baseURL: base_url, // api 的 base_url
  timeout: 5000 // request timeout
})
// request interceptor
service.interceptors.request.use(
  config => {
    // Do something before request is sent
    if (store.getters.token) {
      // 让每个请求携带token-- ['X-Litemall-Admin-Token']为自定义key 请根据实际情况自行修改
      config.headers['X-Litemall-Admin-Token'] = getToken()
    }
    return config
  },
  error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data

    if (res.errno === 501) {
      MessageBox.alert('系统未登录，请重新登录', '错误', {
        confirmButtonText: '确定',
        type: 'error'
      }).then(() => {
        store.dispatch('FedLogOut').then(() => {
          location.reload()
        })
      })
      return Promise.reject('error')
    } else if (res.errno === 502) {
      MessageBox.alert('系统内部错误，请联系管理员维护', '错误', {
        confirmButtonText: '确定',
        type: 'error'
      })
      return Promise.reject('error')
    } else if (res.errno === 503) {
      MessageBox.alert('请求业务目前未支持', '警告', {
        confirmButtonText: '确定',
        type: 'error'
      })
      return Promise.reject('error')
    } else if (res.errno === 504) {
      MessageBox.alert('更新数据已经失效，请刷新页面重新操作', '警告', {
        confirmButtonText: '确定',
        type: 'error'
      })
      return Promise.reject('error')
    } else if (res.errno === 505) {
      MessageBox.alert('更新失败，请再尝试一次', '警告', {
        confirmButtonText: '确定',
        type: 'error'
      })
      return Promise.reject('error')
    } else if (res.errno === 506) {
      MessageBox.alert('没有操作权限，请联系管理员授权', '错误', {
        confirmButtonText: '确定',
        type: 'error'
      })
      return Promise.reject('error')
    } else if (res.errno !== 0) {
      // 非5xx的错误属于业务错误，留给具体页面处理
      return Promise.reject(response)
    } else {
      return response
    }
  }, error => {
    console.log('err' + error)// for debug
    Message({
      message: '登录连接超时（后台不能连接，请联系系统管理员）'+base_url,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  })
export default service

function isInnerIPFn(){
  // 获取当前页面url
  var relUrl = document.domain
  var curPageUrl =relUrl.replace(':3211/','');

  if (curPageUrl == 'localhost') {
    curPageUrl ='192.168.0.2'
  }
  console.log('curPageUrl-1  '+curPageUrl);

  var reg2 = /\:+/g;//替换冒号为一点
  curPageUrl =curPageUrl.replace(reg2,'.');
  // console.log('curPageUrl-2  '+curPageUrl);

  curPageUrl = curPageUrl.split('.');//通过一点来划分数组
  console.log(curPageUrl);
  var ipAddress = curPageUrl[0] + '.' + curPageUrl[1] + '.'+curPageUrl[2] + '.' + curPageUrl[3];

  var isInnerIp = false;//默认给定IP不是内网IP
  console.log('ipNum  ' + ipAddress);

  var ipNum = getIpNum(ipAddress);
  console.log('getIpNum  ' + ipNum);

  /**
   * 私有IP：
   *         A类  10.0.0.0    -10.255.255.255
   *       B类  172.16.0.0  -172.31.255.255
   *       C类  192.168.0.0 -192.168.255.255
   *       D类  127.0.0.0   -127.255.255.255(环回地址)
   **/
  var aBegin = getIpNum("10.0.0.0");
  var aEnd = getIpNum("10.255.255.255");

  var bBegin = getIpNum("172.16.0.0");
  var bEnd = getIpNum("172.31.255.255");

  var cBegin = getIpNum("192.168.0.0");
  var cEnd = getIpNum("192.168.255.255");

  var dBegin = getIpNum("127.0.0.0");
  var dEnd = getIpNum("127.255.255.255");

  isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || isInner(ipNum, dBegin, dEnd);
  console.log('是否是内网:'+isInnerIp);
  return isInnerIp;
}
function getIpNum(ipAddress) {/*获取IP数*/
  var ip = ipAddress.split(".");
  var a = parseInt(ip[0]);
  var b = parseInt(ip[1]);
  var c = parseInt(ip[2]);
  var d = parseInt(ip[3]);
  var ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
  return ipNum;
}

function isInner(userIp,begin,end){
  return (userIp>=begin) && (userIp<=end);
}
