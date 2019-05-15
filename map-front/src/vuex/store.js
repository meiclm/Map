import Vue from 'vue';
import Vuex from 'vuex'

Vue.use(Vuex);

const store=new Vuex.Store({
  state:{
    points:[],//数据点
    block:-1,//阻塞点,
    data:[]//二维矩阵，保存距离
  },
  mutations:{
    //保存本地源文件的全部点
    setPoints(state,source){
      console.log("执行vuex的保存公共的点在公共数据中setPoint():"+source.length);
      for (let i=0;i<source.length;i++){
        // state.points[i]=source[i];
        state.points.push(source[i]);
      }
      console.log(state.points.length)
    },
    clearPoints(state){
      state.points=[];
    },
    setBlock(state,playload){
      state.block=playload;
    },
    clearBlock(state){
      state.block=-1;
    },
    setData(state,playload){
      for (let i=0;i<playload.length;i++){
        state.data.push(playload[i]);//存入一行数据
      }
      console.log("state.data存入的数据："+state.data);
    },
    clearData(state){
      state.data=[];
    }
  }
});
export default store;
