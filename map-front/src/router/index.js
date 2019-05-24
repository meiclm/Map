import Vue from 'vue'
import Router from 'vue-router'

import MapContent from '../components/MapContent'
import SceneryAndFoolContent from '@/components/dialog/SceneryAndFoolContent'

Vue.use(Router);
// Vue.use(BMap1,{
//   ak:'0XcRMOCdpg2QXVZY2UEt2HH8x0lGrgfG'
// });

export default new Router({
  routes: [
    { path: '/', redirect: '/map' },
    {
      path: '/map',
      component: MapContent,
      /**
       * children:[
       {path:'/info',name:'info',component:UniversityInfo},
       {path: '/list',name: 'list',component:RoutesList}
       ]
       */
    },
    {path:'/travel',component: SceneryAndFoolContent,name:'travel'}
  ],
  mode:'history'
})
