import Vue from 'vue';
import Vuex from 'vuex'

Vue.use(Vuex);

const store=new Vuex.Store({
  state:{
    polylines:[],//多条路径
    points:[],//数据点
    dist:[]//存放距离
  },
  mutations:{
    //保存本地源文件的全部点
    setPoints(state,source){
      // console.log("执行setPoint():"+source.length);
      for (let i=0;i<source.length;i++){
        state.points[i]=source[i];
      }
    },
    //保存传输几条路径
    setPolylines(state,playload){
      console.log("得到多条路径，长度为："+playload.length);
      for (let i = 0; i < playload.length; i++) {
        state.polylines.push(playload[i]);//把一条路径push进数组
      }
    },
    setDist(state,playload){
      for (let i=0;i<playload.length;i++) {
        state.dist.push(playload[i]);
      }
    },
    clearDist(state){
      state.dist=[];
    }

    ,
    clearPoints(state){
      state.points=[];
    },
    clearPolylines(state){
      state.polylines=[];
    }
  }
});
export default store;
