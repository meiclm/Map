import Vue from 'vue'
import Router from 'vue-router'

import MapContent from '../components/MapContent'


Vue.use(Router);
// Vue.use(BMap1,{
//   ak:'0XcRMOCdpg2QXVZY2UEt2HH8x0lGrgfG'
// });

export default new Router({
  routes: [
    { path: '/', redirect: '/map' },
    {
      path: '/map',
      component: MapContent
    },
  ],
  mode:'history'
})
