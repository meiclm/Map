<template>
  <Collapse simple>
      <Panel v-for="(onePath,index) in polylinePaths" :key="index" name="$index">
        <span>{{onePath[0].name}}...{{onePath[onePath.length-1].name}}&nbsp;&nbsp;</span>
        <div slot="content">
          <a href="javascript:void (0)" v-on:click="getOnePathToMap(onePath)">查看地图位置</a>&nbsp;&nbsp;<span>{{dist[index]}}米</span>
          <ul>
            <li v-for="point in onePath" v-if="point.name!=''" :key="index" >{{point.name}}</li>
          </ul>
        </div>
      </Panel>
  </Collapse>
</template>

<script>
  import Store from '../../vuex/store'
    export default {
        name: "RoutesList",
      data(){
          return{
            polylinePaths:[],
            dist:[],
            newData:false
          }
      },
      methods:{
        //将一条路径传给baidu-map组件
        getOnePathToMap(onePath){
          console.log("给地图添加覆盖物");
          console.log(onePath);
          this.$emit("onePath",onePath);
        },
        //取得公共区域的数据
        getPolylinePaths(){
          console.log("重新渲染路线列");
          this.newData=true;//重新渲染数据模块
          console.log("list组件获取公共区域的数据长度："+Store.state.polylines.length);
          for (let i = 0; i < Store.state.polylines.length; i++) {
            this.polylinePaths.push(Store.state.polylines[i]);
            this.dist.push(Store.state.dist[i]);
          }

        }
      },
      mounted() {
          this.getPolylinePaths();
      }
    }
</script>

<style scoped>
*{
  font-size: 14px;
}
</style>
