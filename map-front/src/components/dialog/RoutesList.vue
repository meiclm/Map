<template>
  <div>
    <span class="start"></span><AutoComplete
    v-model="start_value"
    :data="points_name"
    :filter-method="filterMethod"
    placeholder="输入起名"></AutoComplete>
    <br>
    <br>
    <span class="end"></span><AutoComplete
    v-model="end_value"
    :data="points_name"
    :filter-method="filterMethod"
    @keyup.enter.native="searchTwoPoint"
    placeholder="输入终名"></AutoComplete>
    <!--<Button type="success" ghost class="my_button" @click="searchTwoPoint">查看</Button>-->
    <br>
    <br>
    <UniversityInfo v-if="showInfo"></UniversityInfo>
    <Collapse v-if="showList" simple @on-change="getOnePathToMap">
      <Panel v-for="(onePath,index) in polylinePaths" :key="index">
        <span style="font-size: 12px">{{onePath[0].name}}...{{onePath[onePath.length-1].name}}</span>&nbsp;&nbsp;<span style="font-size: 10px">约{{distances[index].toFixed(2)}}米</span>
        <ul slot="content">
          <li style="list-style: none">
            <Button type="success" ghost class="my_button" @click="setAnimation(onePath)">动画效果</Button>
          </li>
          <li v-for="(node,i) in onePath" :key="i" style="list-style: none">
            <div class="myIcon"></div><span v-if="node.name!=''">{{node.name}}</span>
            <div><span class="dotted"></span> <span>约{{}}米</span></div>
          </li>
        </ul>
      </Panel>
    </Collapse>
  </div>

</template>

<script>
  import Store from '../../vuex/store'
  import Axios from 'axios'
  import Api from '../../server/api'

  import UniversityInfo from './UniversityInfo'
    export default {
        name: "RoutesList",
      components:{
        UniversityInfo
      },
      data(){
          return{
            start:-1,
            end:-1,
            polylinePaths:[],
            distances:[],
            showInfo:true,
            showList:false,
            // 输入框
            points_name:["一道门","二道门","三道门","四道门","二龙山-入口点","校医院","图书馆"],
            start_value:'',
            end_value:'',

            //数据点
            points:[]
          }
      },
      methods:{
        //自动补全提示输入框,过滤
        filterMethod(value,option){
          return option.indexOf(value)!= -1;
        },
        getPointName(){
          this.points_name=[];
          console.log("list的数据点");
          // for (let i=0;i<Store.state.points.length;i++) {
          //   this.points.push(Store.state.points[i]);
          //   if (Store.state.points[i].name!="") {
          //     this.points_name.push(Store.state.points[i].name);
          //     console.log(Store.state.points[i].name)
          //   }
          // }

          let that=this;
          Axios.get("static/points.json").then((response)=> {
            if (response.data.Points.length>0){
              that.points=response.data.Points;
              // Store.commit("setPoints",response.data.Points);//保存本地文件到公共数据中
              // console.log(that.points.length);//40
              // that.$set(that.points[0],0,response.data.Points[0]);
              // console.log("取到静态资源文件的数据点");
              that.points_name=[];
              for (var i=0;i<response.data.Points.length;i++) {
                if (response.data.Points[i].name!="") {
                  that.points_name.push(response.data.Points[i].name);
                  // that.points_name[index]=response.data.Points[i].name;
                  // index++;
                }
              }
            }
          });

          console.log("this.points_name："+this.points_name)
        },
        searchTwoPoint(){
          console.log("searchTwoPoint："+this.start_value+","+this.end_value);
          this.showInfo=false;
          this.showList=true;
          console.log("searchTwoPoint获取的points_name："+this.points_name);
          console.log("获取到的数据："+this.points.length);

          if (this.start_value!=''&&this.end_value!=''){
            if (this.points.length>0){
              for (let i=0;i<this.points.length;i++){
                if (this.points[i].name.indexOf(this.start_value)>-1) {
                  this.start=this.points[i].id;
                  console.log("this.start："+this.start)
                }
              }
              for (let i=0;i<this.points.length;i++){
                if (this.points[i].name.indexOf(this.end_value)>-1){
                  this.end=this.points[i].id;
                  console.log("this.end："+this.end)
                }
              }

              if (this.start!=-1&&this.end!=-1&& this.start!=this.end){
                console.log("进入深度遍历");
                let that=this;
                let fpath=[];
                Axios.get(Api.depth(that.start,that.end))
                  .then(function (response) {
                    if (response.data!=null){
                      fpath=response.data;
                      console.log("深度计算后，获取路径长度："+fpath.length);

                      if (fpath.length>0){
                        let len=Store.state.points.length;
                        if (len>0){
                          // console.log("转变为坐标点的操作");
                          //只取8条路径显示
                          for (let i=0;i<8;i++){
                            /**
                             * polylinePaths={
                             *     {{point[1]},...{point[12]}},
                             *     {{point[1]},...{point[12]}}
                             *     {{point[1]},...{point[12]}}
                             * }
                             * distances[i]对应polylinePaths[i][]该条路径的距离
                             * distances={12,34,45}
                             */
                            that.polylinePaths[i]=[];
                            that.distances.push(fpath[i].dist);

                            console.log("第"+i+"个distances："+that.distances[i])
                            for (let j = 0; j <fpath[i].path.length ; j++) {
                              for (let k = 0; k < len; k++) {
                                if (fpath[i].path[j]===Store.state.points[k].id) {
                                  that.polylinePaths[i].push(Store.state.points[k]);
                                }
                              }
                            }
                          }
                          console.log("结果的展示data："+that.polylinePaths[0]);
                        }
                      }

                    }
                  });
              }
            }
          }
        },
        //将一条路径传给baidu-map组件
        getOnePathToMap(key){
          console.log("给地图添加覆盖物");
          console.log(key);
          let onePath=this.polylinePaths[key];
          console.log(onePath);
          this.$emit("onePath",onePath);
        },
        //传一条路径给动画效果
        setAnimation(onePath){
          this.$emit("animation",onePath);
        }
      },
      mounted(){
        console.log("list挂载");
        this.getPointName();//获取自动补全的数据
      },
      updated(){
        console.log("list更新");
        // this.getPointName();//获取自动补全的数据
        console.log(this.points_name);
      }
    }
</script>

<style scoped>
*{
  font-size: 14px;
}
.end,.start{
  position: absolute;
}
  .start{
    top: 25px;
    left: 2px;
    display: inline-block;
    /*border: 1px rebeccapurple solid;*/
    width: 11px;
    height: 10px;
    background: url('../../assets/start_f51000d.png') no-repeat 0 0;
  }
  .end{
    top: 80px;
    left: 2px;
    display: inline-block;
    /*border: 1px deepskyblue solid;*/
    width: 11px;
    height: 10px;
    background: url('../../assets/end_f68595d.png') no-repeat 0 0;
  }
.my_button{
  margin: 5px auto;
}
  .myIcon{
    display: inline-block;
    width: 29px;
    height: 31px;
    background:  url('../../assets/walkwithline_2ea0d6c.png') no-repeat 14px 0;
    /*float: left;*/
  }
</style>
