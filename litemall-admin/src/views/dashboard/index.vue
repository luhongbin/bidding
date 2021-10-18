<template>
  <div class="dashboard-editor-container">
    <component :is="currentRole" />
  </div>
</template>

<script>
import purchaseDashboard from './purchase'
import lutecDashboard from './lutec'
import supplyDashboard from './supply'
import { currentuser } from '@/api/profile'

export default {
  name: 'Dashboard',
  components: { purchaseDashboard, lutecDashboard, supplyDashboard },
  data() {
    return {
      currentRole: 'lutecDashboard',
      current: []
    }
  },

  created() {
    currentuser().then(response => {
      this.current = Object.assign({}, response.data.data)
      if (this.current.capacity.indexOf('供应商') >= 0) {
        this.currentRole = 'supplyDashboard'
      } else if (this.current.capacity.indexOf('采购员') >= 0) {
        this.currentRole = 'purchaseDashboard'
      } else { this.currentRole = 'lutecDashboard' }
    })
    // console.log(JSON.stringify(this.listAdmin))
    // sessionStorage.removeItem('Username')
    // sessionStorage.setItem('Username', this.currentuser)
    // sessionStorage.removeItem('nickname')
    // sessionStorage.removeItem('userid')
    // sessionStorage.removeItem('currentRole')
    // sessionStorage.removeItem('deptname')
    // const username = sessionStorage.getItem('Username')
    // for (let i = 0; i < this.listAdmin.length; i++) {
    //   if (username === this.listAdmin[i].dd) {
    //     sessionStorage.setItem('"nickname', this.listAdmin[i].label)
    //     sessionStorage.setItem('userid', this.listAdmin[i].value)
    //     sessionStorage.setItem('deptname', this.listAdmin[i].deptname)
    //     if (this.listAdmin[i].capacity.indexOf('供应商')) {
    //       sessionStorage.setItem('currentRole', '供应商')
    //     } else if (this.listAdmin[i].capacity.indexOf('采购员')) {
    //       sessionStorage.setItem('currentRole', '采购员')
    //     } else {
    //       sessionStorage.setItem('currentRole', '管理员')
    //     }
    //     sessionStorage.setItem('role', this.listAdmin[i].capacity)
    //     break
    //   }
    // }
    // if (sessionStorage.getItem('Username') === '供应商') {
    //   this.currentRole = 'supplyDashboard'
    // } else if (sessionStorage.getItem('Username') === '采购员') {
    //   this.currentRole = 'purchaseDashboard'
    // } else { this.currentRole = 'lutecDashboard' }
  },
  mounted() {
  }
}
</script>
