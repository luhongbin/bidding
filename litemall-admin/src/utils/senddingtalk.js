import * as dd from 'dingtalk-jsapi'
import axios from '_axios@0.18.1@axios'

// 从一言 API 获取句子
export function getYiYan() {
  return axios({
    url: '/yiyan' + '/?c=i&encode=json',
    method: 'get'
  })
}

export function getdd(callback) {
  sessionStorage.removeItem('access_token')
  dd.error(function(error) { this.$notify.error({ title: '失败', message: JSON.stringify(error) }) })
  dd.ready(function() {
    dd.runtime.permission.requestAuthCode({
      corpId: 'ding8c0384f4285df03035c2f4657eb6378f',
      onSuccess: function(info) {
        axios.get('/tokn' + '/gettoken?appkey=&appsecret=-').then(res => {
          if (res.data.access_token === undefined) { this.$notify.success({ title: '钉钉', message: '钉钉登录失败' }); return }
          axios.get('/tokn' + '/user/getuserinfo?access_token=' + res.data.access_token + '&code=' + info.code).then(res1 => { callback(res1.data.userid) })
        })
      },
      onFail: function(err) { this.$notify.error({ title: '失败', message: JSON.stringify(err) }) }
    })
  })
}

export function token(callback) {
  // dd.error(function(error){ alert(JSON.stringify(error)); });
  // dd.ready(function() {
  axios.get('/tokn' + '/gettoken?appkey=&appsecret=-').then(res => {
    if (res.data.access_token === 'undefined' || res.data.access_token === undefined) { this.$notify.success({ title: '钉钉', message: '钉钉登录失败' }); return }
    setTimeout(callback(res.data.access_token), 3300)
  })
    .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
}
export function robotSend1(row, title, markdown, singleTitle, singleUrl) {
  var infoSend = { userid_list: row, agent_id: '1231569276', msg: { 'msgtype': 'action_card', 'action_card': { 'title': title, 'single_title': singleTitle, 'single_url': singleUrl, 'markdown': markdown }}}

  axios.post('/tokn' + '/robot/send?access_token=' + this.approve.access_token, JSON.stringify(infoSend), { headers: { 'Content-Type': 'application/json' }})
    .then(res1 => { this.$notify.error({ title: '失败', message: JSON.stringify(res1.data) }) })
    .catch(res1 => { this.$notify.error({ title: '失败', message: JSON.stringify(res1.data) }) })
}

export function dingtalkSend(infoSend) {
  console.log('infoSend 3:' + JSON.stringify(infoSend))
  // console.log('access_token:'+this.approve.access_token)
  axios.get('/tokn' + '/gettoken?appkey=&appsecret=-').then(res => {
    if (res.data.access_token === undefined) { this.$notify.success({ title: '钉钉', message: '钉钉登录失败' }); return }
    axios.post('/tokn' + '/topapi/message/corpconversation/asyncsend_v2?access_token=' + res.data.access_token, JSON.stringify(infoSend), { headers: { 'Content-Type': 'application/json' }})
      .then(res1 => { console.log('data' + JSON.stringify(res1.data)) }).catch(res1 => { console.log('失败' + JSON.stringify(res1.data.errmsg)) })
  })
}
