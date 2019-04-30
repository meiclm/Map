<template>
    <div >
      <Drawer title="路线展示":mask="false" width="350px" :closable="true" v-model="drawerValue">
        <Collapse simple v-if="newData">
          <Panel v-for="(onePath,index) in polylinePaths_data" :key="index" name="$index">
            <span>{{onePath[0].name}}...{{onePath[onePath.length-1].name}}&nbsp;&nbsp;</span>
            <div slot="content">
              <a href="javascript:void (0)" v-on:click="getOnePathToMap(onePath)">查看地图位置</a>&nbsp;&nbsp;<span>{{dist[index]}}米</span>
              <ul>
                <li v-for="point in onePath" v-if="point.name!=''" :key="index" >{{point.name}}</li>
              </ul>
            </div>
          </Panel>
        </Collapse>
      </Drawer>
    </div>
</template>

<script>
  // import RoutesList from './RoutesList'

  import Bus from '../../server/bus'
  import Store from '../../vuex/store'
    export default {
        name: "RoutesDrawer",
      // components:{
      //     RoutesList
      // },
        data(){
          return{
            drawerValue:false,
            polylinePaths:[],
            distances:[],

            polylinePaths_data:[],
            dist:[],
            newData:false
          }
        },
      methods:{
          //polylinePaths[][],多条路径的点
        //fpath传过来的数组，是一些数字，不是坐标点
          receiveBrotherpolylines(fpath){
            // console.log("非父子间的传值，长度为"+fpath.length);
            console.log("得到新的数据fpath"+fpath[0].path);
            let len=Store.state.points.length;
            if (len>0){
              // console.log("转变为坐标点的操作");
              for (let i=0;i<8;i++){
                this.polylinePaths[i]=[];
                this.distances[i]=fpath[i].dist;
                // let polyline={};
                for (let j = 0; j <fpath[i].path.length ; j++) {

                  for (let k = 0; k < len; k++) {
                    if (fpath[i].path[j]===Store.state.points[k].id) {
                      // this.polylinePaths[i][j]=Store.state.points[k];//一个对象，一个点的对象
                      // console.log("得到一个坐标点"+this.polylinePaths[i][j].id);
                      this.polylinePaths[i].push(Store.state.points[k])
                    }
                  }
                }
              }
            }
            //保存到公共域中
            Store.commit("clearPolylines",Store.state);
            Store.commit("setPolylines",this.polylinePaths);
            Store.commit("clearDist",Store.state);
            Store.commit("setDist",this.distances);
          },

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
        let that=this;
        Bus.$on("fpath",(data)=>{
          console.log("接收同级组件的数据："+data.length);
          that.drawerValue=true;
          if (data.length>0){
            that.receiveBrotherpolylines(data);
          }
        });
        this.getPolylinePaths();
      }
    }
</script>

<style scoped>

</style>
