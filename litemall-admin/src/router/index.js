import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/views/layout/Layout'

/** note: Submenu only appear when children.length>=1
 *  detail see  https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 **/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    perms: ['GET /aaa','POST /bbb']     will control the page perms (you can set multiple perms)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
**/
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: false,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/authredirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/errorPage/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/errorPage/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '??????', icon: 'dashboard', affix: true }
      }
    ]
  }

]

export const asyncRoutes = [
  {
    path: '/quoteManage',

    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'quoteManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'quotebill',
        component: () => import('@/views/quote/quotebill'),
        name: 'quotebill',
        meta: {
          perms: ['GET /admin/quoteBill/list', 'GET /admin/quoteBill/listCeo', 'POST /admin/quoteBill/delete', 'POST /admin/quoteBill/create', 'POST /admin/quoteBill/update'],
          title: '???????????????',
          noCache: true
        }
      },
      {
        path: 'quotelist',
        component: () => import('@/views/quote/quotebilllist'),
        name: 'QuoteList',
        meta: {
          perms: ['GET /admin/quoteBill/search'],
          title: '????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'quotebill-create',
        component: () => import('@/views/quote/quotebillCreate'),
        name: 'quotebillCreate',
        meta: {
          perms: ['POST /admin/quoteBill/create'],
          title: '??????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'quotebill-edit',
        component: () => import('@/views/quote/quotebillEdit'),
        name: 'quotebillEdit',
        meta: {
          perms: ['POST /admin/quoteBill/update'],
          title: '??????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'quotebill-approve',
        component: () => import('@/views/quote/quotebillApprove'),
        name: 'quotebillApprove',
        meta: {
          perms: ['GET /admin/quoteBill/list'],
          title: '??????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'purchase',
        component: () => import('@/views/quote/purchase'),
        name: 'purchase',
        meta: {
          perms: ['GET /admin/purchase/list'],
          title: '???????????????',
          noCache: true
        }
      },
      {
        path: 'inspection',
        component: () => import('@/views/quote/inspection'),
        name: 'inspection',
        meta: {
          perms: ['GET /admin/inspection/list'],
          title: '??????????????????',
          noCache: true
        }
      },
      {
        path: 'invoice',
        component: () => import('@/views/quote/invoice'),
        name: 'invoice',
        meta: {
          perms: ['GET /admin/invoice/list'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/supplyManage',

    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'supplyManage',
    meta: {
      title: '???????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'requote',
        component: () => import('@/views/quote/requote'),
        name: 'requote',
        meta: {
          perms: ['GET /admin/requote/list', 'GET /admin/requote/listCeo', 'POST /admin/requote/delete', 'POST /admin/requote/create', 'POST /admin/requote/update', 'POST /admin/requote/submit', 'GET /admin/requote/submit'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'requotelist',
        component: () => import('@/views/quote/requotebilllist'),
        name: 'requotelist',
        meta: {
          perms: ['GET /admin/requote/search'],
          title: '????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'requote-edit',
        component: () => import('@/views/quote/requotebillEdit'),
        name: 'requoteEdit',
        meta: {
          perms: ['POST /admin/requote/update'],
          title: '???????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'requote-edit_modify',
        component: () => import('@/views/quote/requotebillEditMobile'),
        name: 'requoteEdit',
        meta: {
          perms: ['POST /admin/requote/update'],
          title: '?????????????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'requote-approve',
        component: () => import('@/views/quote/requoteApprove'),
        name: 'requoteApprove',
        meta: {
          perms: ['POST /admin/requote/update'],
          title: '??????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'approCve',
        component: () => import('@/views/quote/approve'),
        name: 'approve',
        meta: {
          perms: ['GET /admin/purchase/list'],
          title: '???????????????????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'Tpurchase',
        // component: () => import('@/views/quote/purchase'),
        name: 'purcEhase',
        meta: {
          perms: ['GET /admin/purchase/list'],
          title: '???????????????????????????',
          noCache: true
        }
      },
      {
        path: 'pWurchase',
        // component: () => import('@/views/quote/purchase'),
        name: 'purcWhase',
        meta: {
          perms: ['GET /admin/purchase/list'],
          title: '???????????????????????????',
          noCache: true
        }
      },
      {
        path: 'inspecGtion',
        // component: () => import('@/views/quote/inspection'),
        name: 'inspecTtion',
        meta: {
          perms: ['GET /admin/inspection/list'],
          title: '?????????????????????',
          noCache: true
        }
      },
      {
        path: 'inXvoice',
        // component: () => import('@/views/quote/invoice'),
        name: 'invoiTce',
        meta: {
          perms: ['GET /admin/purchase/list'],
          title: '?????????????????????',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/workrecord',
    component: Layout,
    redirect: '/workrecord/index/',
    alwaysShow: true,
    name: 'workRecord',
    meta: {
      title: '??????????????????',
      icon: 'thesis'
    },
    children: [
      {
        path: 'user',
        component: () => import('@/views/user/user'),
        name: 'user',
        meta: {
          perms: ['GET /admin/user/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'address',
        component: () => import('@/views/user/address'),
        name: 'address',
        meta: {
          perms: ['GET /admin/address/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'collect',
        component: () => import('@/views/user/collect'),
        name: 'collect',
        meta: {
          perms: ['GET /admin/collect/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'footprint',
        component: () => import('@/views/user/footprint'),
        name: 'footprint',
        meta: {
          perms: ['GET /admin/footprint/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'history',
        component: () => import('@/views/user/history'),
        name: 'history',
        meta: {
          perms: ['GET /admin/history/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'feedback',
        component: () => import('@/views/user/feedback'),
        name: 'feedback',
        meta: {
          perms: ['GET /admin/feedback/list'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'userManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'user',
        component: () => import('@/views/user/user'),
        name: 'user',
        meta: {
          perms: ['GET /admin/user/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'address',
        component: () => import('@/views/user/address'),
        name: 'address',
        meta: {
          perms: ['GET /admin/address/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'collect',
        component: () => import('@/views/user/collect'),
        name: 'collect',
        meta: {
          perms: ['GET /admin/collect/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'footprint',
        component: () => import('@/views/user/footprint'),
        name: 'footprint',
        meta: {
          perms: ['GET /admin/footprint/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'history',
        component: () => import('@/views/user/history'),
        name: 'history',
        meta: {
          perms: ['GET /admin/history/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'feedback',
        component: () => import('@/views/user/feedback'),
        name: 'feedback',
        meta: {
          perms: ['GET /admin/feedback/list'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },

  {
    path: '/mall',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'mallManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'region',
        component: () => import('@/views/mall/region'),
        name: 'region',
        meta: {
          perms: ['GET /admin/brand/list', 'POST /admin/brand/create', 'GET /admin/brand/read', 'POST /admin/brand/update', 'POST /admin/brand/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'brand',
        component: () => import('@/views/mall/brand'),
        name: 'brand',
        meta: {
          perms: ['GET /admin/brand/list', 'POST /admin/brand/create', 'GET /admin/brand/read', 'POST /admin/brand/update', 'POST /admin/brand/delete'],
          title: '???????????????',
          noCache: true
        }
      },
      {
        path: 'category',
        component: () => import('@/views/mall/category'),
        name: 'category',
        meta: {
          perms: ['GET /admin/category/list', 'POST /admin/category/create', 'GET /admin/category/read', 'POST /admin/category/update', 'POST /admin/category/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/mall/order'),
        name: 'order',
        meta: {
          perms: ['GET /admin/order/list', 'GET /admin/order/detail', 'POST /admin/order/ship', 'POST /admin/order/refund', 'POST /admin/order/delete', 'POST /admin/order/reply'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'aftersale',
        component: () => import('@/views/mall/aftersale'),
        name: 'aftersale',
        meta: {
          perms: ['GET /admin/aftersale/list', 'GET /admin/aftersale/detail', 'POST /admin/order/receive', 'POST /admin/aftersale/complete', 'POST /admin/aftersale/reject'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'issue',
        component: () => import('@/views/mall/issue'),
        name: 'issue',
        meta: {
          perms: ['GET /admin/issue/list', 'POST /admin/issue/create', 'GET /admin/issue/read', 'POST /admin/issue/update', 'POST /admin/issue/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'keyword',
        component: () => import('@/views/mall/keyword'),
        name: 'keyword',
        meta: {
          perms: ['GET /admin/keyword/list', 'POST /admin/keyword/create', 'GET /admin/keyword/read', 'POST /admin/keyword/update', 'POST /admin/keyword/delete'],
          title: '?????????',
          noCache: true
        }
      }
    ]
  },

  {
    path: '/goods',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'goodsManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'list',
        component: () => import('@/views/goods/list'),
        name: 'goodsList',
        meta: {
          perms: ['GET /admin/goods/list', 'POST /admin/goods/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'create',
        component: () => import('@/views/goods/create'),
        name: 'goodsCreate',
        meta: {
          perms: ['POST /admin/goods/create'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'edit',
        component: () => import('@/views/goods/edit'),
        name: 'goodsEdit',
        meta: {
          perms: ['GET /admin/goods/detail', 'POST /admin/goods/update', 'POST /admin/goods/catAndBrand'],
          title: '????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'comment',
        component: () => import('@/views/goods/comment'),
        name: 'goodsComment',
        meta: {
          perms: ['GET /admin/comment/list', 'POST /admin/comment/delete'],
          title: '????????????',
          noCache: true
        }
      }

    ]
  },
  {
    path: '/promotion',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'promotionManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'ad',
        component: () => import('@/views/promotion/ad'),
        name: 'ad',
        meta: {
          perms: ['GET /admin/ad/list', 'POST /admin/ad/create', 'GET /admin/ad/read', 'POST /admin/ad/update', 'POST /admin/ad/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'coupon',
        component: () => import('@/views/promotion/coupon'),
        name: 'coupon',
        meta: {
          perms: ['GET /admin/coupon/list', 'POST /admin/coupon/create', 'POST /admin/coupon/update', 'POST /admin/coupon/delete'],
          title: '???????????????',
          noCache: true
        }
      },
      {
        path: 'couponDetail',
        component: () => import('@/views/promotion/couponDetail'),
        name: 'couponDetail',
        meta: {
          perms: ['GET /admin/coupon/list', 'GET /admin/coupon/listuser'],
          title: '???????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'topic',
        component: () => import('@/views/promotion/topic'),
        name: 'topic',
        meta: {
          perms: ['GET /admin/topic/list', 'POST /admin/topic/create', 'GET /admin/topic/read', 'POST /admin/topic/update', 'POST /admin/topic/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'topic-create',
        component: () => import('@/views/promotion/topicCreate'),
        name: 'topicCreate',
        meta: {
          perms: ['POST /admin/topic/create'],
          title: '????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'topic-edit',
        component: () => import('@/views/promotion/topicEdit'),
        name: 'topicEdit',
        meta: {
          perms: ['GET /admin/topic/read', 'POST /admin/topic/update'],
          title: '????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'groupon-rule',
        component: () => import('@/views/promotion/grouponRule'),
        name: 'grouponRule',
        meta: {
          perms: ['GET /admin/groupon/list', 'POST /admin/groupon/create', 'POST /admin/groupon/update', 'POST /admin/groupon/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'groupon-activity',
        component: () => import('@/views/promotion/grouponActivity'),
        name: 'grouponActivity',
        meta: {
          perms: ['GET /admin/groupon/listRecord'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },

  {
    path: '/sys',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'sysManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'admin',
        component: () => import('@/views/sys/admin'),
        name: 'admin',
        meta: {
          perms: ['GET /admin/admin/list', 'POST /admin/admin/create', 'POST /admin/admin/update', 'POST /admin/admin/delete'],
          title: '?????????',
          noCache: true
        }
      },
      {
        path: 'notice',
        component: () => import('@/views/sys/notice'),
        name: 'sysNotice',
        meta: {
          perms: ['GET /admin/notice/list', 'POST /admin/notice/create', 'POST /admin/notice/update', 'POST /admin/notice/delete'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'log',
        component: () => import('@/views/sys/log'),
        name: 'log',
        meta: {
          perms: ['GET /admin/log/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'approveinfo',
        component: () => import('@/views/sys/approve'),
        name: 'approveinfo',
        meta: {
          perms: ['GET /admin/approveinfo/detail', 'POST /admin/approveinfo/update', 'POST /admin/approveinfo/catAndBrand'],
          title: '??????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'role',
        component: () => import('@/views/sys/role'),
        name: 'role',
        meta: {
          perms: ['GET /admin/role/list', 'POST /admin/role/create', 'POST /admin/role/update', 'POST /admin/role/delete', 'GET /admin/role/permissions', 'POST /admin/role/permissions'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'os',
        component: () => import('@/views/sys/os'),
        name: 'os',
        meta: {
          perms: ['GET /admin/storage/list'],
          title: '????????????',
          noCache: true
        },
      }
    ]
  },
  {
    path: '/knowledge',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'knowledgeManage',
    meta: {
      title: '???????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'xmcatalog',
        component: () => import('@/views/knowledge/xmcatalog'),
        name: 'xmcatalog',
        meta: {
          perms: ['GET /admin/xmcatalog/list', 'POST /admin/xmcatalog/create', 'POST /admin/xmcatalog/update', 'POST /admin/xmcatalog/delete', 'GET /admin/xmcatalog/permissions', 'POST /admin/xmcatalog/permissions'],
          title: '??????????????????',
          noCache: true
        }
      },
      {
        path: 'knowledge',
        component: () => import('@/views/knowledge/knowledge'),
        name: 'knowledge',
        meta: {
          perms: ['GET /admin/knowledge/list', 'POST /admin/knowledge/create', 'POST /admin/knowledge/update', 'POST /admin/knowledge/delete', 'GET /admin/knowledge/permissions', 'POST /admin/knowledge/permissions'],
          title: '?????????',
          noCache: true
        }
      },
      {
        path: 'create',
        component: () => import('@/views/knowledge/create'),
        name: 'create',
        meta: {
          perms: ['GET /admin/knowledge/list', 'POST /admin/knowledge/create', 'POST /admin/knowledge/update', 'POST /admin/knowledge/delete', 'GET /admin/knowledge/permissions', 'POST /admin/knowledge/permissions'],
          title: '???????????????',
          noCache: true
        }
      },
      {
        path: 'edit',
        component: () => import('@/views/knowledge/edit'),
        name: 'edit',
        meta: {
          perms: ['GET /admin/knowledge/detail', 'POST /admin/knowledge/update', 'POST /admin/knowledge/catAndBrand'],
          title: '???????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'like',
        component: () => import('@/views/knowledge/like'),
        name: 'like',
        meta: {
          perms: ['GET /admin/knowledge/like', 'POST /admin/knowledge/like'],
          title: '??????',
          noCache: true
        }
      },
      {
        path: 'xmfeedback',
        component: () => import('@/views/knowledge/xmfeedback'),
        name: 'xmfeedback',
        meta: {
          perms: ['GET /admin/knowledge/xmfeedback', 'POST /admin/knowledge/xmfeedback'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/config',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'configManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'quote',
        component: () => import('@/views/config/quote'),
        name: 'configQuote',
        meta: {
          perms: ['GET /admin/config/quote', 'POST /admin/config/quote'],
          title: '?????????????????????',
          noCache: true
        }
      },
      {
        path: 'quotemodel',
        component: () => import('@/views/config/quotemodel'),
        name: 'configQuoteMode;',
        meta: {
          perms: ['GET /admin/quoteModel/list', 'POST /admin/quoteModel/create'],
          title: '????????????????????????',
          noCache: true
        }
      },
      {
        path: 'quotemodel-create',
        component: () => import('@/views/config/quotemodelCreate'),
        name: 'quotemodelCreate',
        meta: {
          perms: ['POST /admin/quoteModel/create'],
          title: '??????????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'quotemodel-edit',
        component: () => import('@/views/config/quotemodelEdit'),
        name: 'quotemodelEdit',
        meta: {
          perms: ['POST /admin/quoteModel/update'],
          title: '??????????????????',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'mall',
        component: () => import('@/views/config/mall'),
        name: 'configMall',
        meta: {
          perms: ['GET /admin/config/mall', 'POST /admin/config/mall'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'express',
        component: () => import('@/views/config/express'),
        name: 'configExpress',
        meta: {
          perms: ['GET /admin/config/express', 'POST /admin/config/express'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/config/order'),
        name: 'configOrder',
        meta: {
          perms: ['GET /admin/config/order', 'POST /admin/config/order'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'wx',
        component: () => import('@/views/config/wx'),
        name: 'configWx',
        meta: {
          perms: ['GET /admin/config/wx', 'POST /admin/config/wx'],
          title: '???????????????',
          noCache: true
        }
      }
    ]
  },

  {
    path: '/stat',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'statManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'user',
        component: () => import('@/views/stat/user'),
        name: 'statUser',
        meta: {
          perms: ['GET /admin/stat/user'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/stat/order'),
        name: 'statOrder',
        meta: {
          perms: ['GET /admin/stat/order'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'goods',
        component: () => import('@/views/stat/goods'),
        name: 'statGoods',
        meta: {
          perms: ['GET /admin/stat/goods'],
          title: '????????????',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/package',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'packageManage',
    meta: {
      title: '????????????',
      icon: 'chart'
    },
    children: [
      {
        path: 'teu',
        component: () => import('@/views/package/teu'),
        name: 'teu',
        meta: {
          perms: ['GET /admin/teuedit/detail', 'GET /admin/teu/list'],
          title: '?????????????????????',
          noCache: true
        }
      },
      {
        path: 'teuedit',
        component: () => import('@/views/package/teuedit'),
        name: 'teuedit',
        meta: {
          perms: ['GET /admin/teuedit/detail', 'GET /admin/user/list', 'GET /admin/teuedit/list', 'POST /admin/teuedit/create', 'GET /admin/teuedit/read', 'POST /admin/teuedit/update', 'POST /admin/teuedit/delete'],
          title: '?????????????????????',
          noCache: true
        }
      },
      {
        path: 'file',
        component: () => import('@/views/package/file'),
        name: 'file',
        meta: {
          perms: ['GET /admin/file/list'],
          title: '?????????????????????',
          noCache: true
        }
      },
      {
        path: 'select',
        component: () => import('@/views/package/select'),
        name: 'select',
        meta: {
          perms: ['GET /admin/select/list'],
          title: '????????????',
          noCache: true
        }
      },
      {
        path: 'tactics',
        component: () => import('@/views/package/tactics'),
        name: 'tactics',
        meta: {
          perms: ['GET /admin/tactics/list'],
          title: '??????????????????',
          noCache: true
        }
      }
    ]

  },

  {
    path: 'external-link',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'externalLink',
    meta: {
      title: '??????',
      icon: 'link'
    },
    children: [
      {
        path: 'https://cloud.tencent.com/product/cos',
        meta: { title: '???????????????', icon: 'link' }
      },
      {
        path: 'https://cloud.tencent.com/product/sms',
        meta: { title: '???????????????', icon: 'link' }
      },
      {
        path: 'https://pay.weixin.qq.com/index.php/core/home/login',
        meta: { title: '????????????', icon: 'link' }
      },
      {
        path: 'https://mpkf.weixin.qq.com/',
        meta: { title: '???????????????', icon: 'link' }
      },
      {
        path: 'https://www.alibabacloud.com/zh/product/oss',
        meta: { title: '???????????????', icon: 'link' }
      },
      {
        path: 'https://www.qiniu.com/products/kodo',
        meta: { title: '???????????????', icon: 'link' }
      },
      {
        path: 'http://www.kdniao.com/api-track',
        meta: { title: '?????????', icon: 'link' }
      }
    ],
    hidden: true
  },
  {
    path: '/profile',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    children: [
      {
        path: 'password',
        component: () => import('@/views/profile/password'),
        name: 'password',
        meta: { title: '????????????', noCache: true }
      },
      {
        path: 'notice',
        component: () => import('@/views/profile/notice'),
        name: 'notice',
        meta: { title: '????????????', noCache: true }
      }
    ],
    hidden: true
  },

  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
