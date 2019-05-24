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
    <Collapse v-else="showList" simple @on-change="getOnePathToMap">
      <Panel v-for="(onePath,index) in polylinePaths" :key="index">
        <span style="font-size: 12px">{{onePath[0].name}}...{{onePath[onePath.length-1].name}}</span>&nbsp;&nbsp;<span style="font-size: 10px">约{{distances[index].toFixed(2)}}米</span>
        <ul slot="content">
          <li style="list-style: none">
            <Button type="success" ghost class="my_button" @click="setAnimation(onePath)">动画效果</Button>
            <Button  icon="md-close" v-if="block==1" class="my_button" @click="setBlockId(onePath)">阻塞点</Button>
            <Button  icon="" v-if="block==0" class="my_button" @click="cancelBlock()">取消阻塞</Button>
          </li>
          <!--<li><span v-for="p in onePath">{{p.id}}=></span></li>-->
          <li v-for="(node,i) in onePath" :key="i" style="list-style: none">
            <div class="myIcon"></div><span v-if="node.name!=''">{{node.name}}</span>
            <span v-else>{{node.id}}</span>
            <div><span class="dotted"></span> <span v-if="i<onePathTwoPointLen[index].length">约{{onePathTwoPointLen[index][i]}}米</span></div>
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
    props:{
      fatherBlockId:Number,
      required:true
    },
    data(){
      return{
        block:1,
        polylinePaths:[],
        distances:[],
        showInfo:true,
        showList:false,
        // 输入框
        points_name:["一道门","二道门","三道门","四道门","二龙山-入口点","校医院","图书馆"],
        start_value:'',
        end_value:'',
        start:-1,
        end:-1,
        fatherBlock:-1,

        //数据点
        points:[],
        onePathTwoPointLen:[]
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
      /**
       * 查两点之间的路径
       * @param block
       * @param start
       * @param end
       */
      startAndEndSearch(block,start,end){
        let fpath=[];
        let that=this;
        if (block>=0){
          console.log("开始阻塞计算！");
        }
        Axios.get(Api.block(block,start,end))
          .then(function (response) {
            if (response.data!=null){
              fpath=response.data;
              for (let m=0;m<8;m++){
                console.log("深度计算后，获取路径"+m+"："+fpath[m].path);
              }

              if (fpath.length>0){
                let len=Store.state.points.length;
                if (len>0){
                  // console.log("转变为坐标点的操作");
                  //只取8条路径显示
                  that.onePathTwoPointLen=[];
                  that.distances=[];
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
                    that.distances.push(fpath[i].dist);//距离
                    // console.log("第"+i+"个distances："+that.distances[i]);
                    let twoPointLen=[];
                    for (let j = 0; j <fpath[i].path.length ; j++) {
                      if (j===fpath[i].path.length-1) {
                        that.onePathTwoPointLen.push(twoPointLen);
                        console.log("得到一条路径的两点之间的距离"+twoPointLen)
                      }else {
                        twoPointLen.push(Store.state.data[fpath[i].path[j]][fpath[i].path[j+1]]);
                      }
                      //获取一条路径的点的对象,
                      /**
                       * fpath[i].path[j]上的数字等于Point[j].id
                       */
                      that.polylinePaths[i].push(Store.state.points[fpath[i].path[j]]);
                      // for (let k = 0; k < len; k++) {
                      //   if (fpath[i].path[j]===Store.state.points[k].id) {
                      //     that.polylinePaths[i].push(Store.state.points[k]);
                      //   }
                      // }
                    }
                  }
                  console.log("结果的展示一条路[]的对象的点："+that.polylinePaths[0]);
                  console.log("每次第一条路径的距离："+this.distances[0])
                }
              }

            }
          });
      },

      searchTwoPoint(){
        console.log("searchTwoPoint："+this.start_value+","+this.end_value);
        this.showInfo=false;
        this.showList=true;
        // console.log("searchTwoPoint获取的points_name："+this.points_name);
        // console.log("获取到的数据："+this.points.length);

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
              // let fpath=[];
              //保存，等到阻塞的时候有用
              Store.commit("setStartAndEndPoint",{start:that.start,end:that.end});
              this.startAndEndSearch(-1,this.start,this.end);
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
      },
      setBlockId(onePath){
        this.block=0;
        console.log("点击了阻塞按钮："+onePath);
        this.$emit("blockOnePath",onePath);
      },
      cancelBlock(){
        this.block=1;
        console.log("取消阻塞：");
        Store.commit("clearBlock");
        this.$emit("blockOnePath",[]);
        this.startAndEndSearch(-1,Store.state.startAndEndPoint.start,Store.state.startAndEndPoint.end);
        this.$Message.info({
          content: '路线列表已经回复',
          duration: 10
        });
      },
      /**
       * 父组件传给子组件一个block信号，然后开始重新渲染组件
       */
      blockAndSearchTwoPoint(){
        this.fatherBlock=this.fatherBlockId;
        console.log("list阻塞点的block："+this.fatherBlock+"，start="+Store.state.startAndEndPoint.star+"，end="+Store.state.startAndEndPoint.end);
        if (Store.state.startAndEndPoint.start!=-1&&Store.state.startAndEndPoint.end!=-1&&this.fatherBlock!=-1){
          // let that=this;
          this.startAndEndSearch(this.fatherBlock,Store.state.startAndEndPoint.start,Store.state.startAndEndPoint.end);
        } else {
          this.$Message.info("没有起始点或者阻塞点");
        }
      }
    },
    mounted(){
      console.log("list挂载");
      this.getPointName();//获取自动补全的数据
    },
    updated(){
      console.log("list更新");
      // this.getPointName();//获取自动补全的数据
      // console.log(this.points_name);
      console.log('父传子：'+this.fatherBlockId);
      console.log("公共数据域中的blockId="+Store.state.block);
      // if (parseInt(this.fatherBlockId)>-1){
      //   this.blockAndSearchTwoPoint();
      // }else if (parseInt(this.fatherBlockId)==-2){
      //   this.startAndEndSearch(-1,Store.state.startAndEndPoint.start,Store.state.startAndEndPoint.end);
      // }

    },
    watch:{
      fatherBlockId(val){
        if (val>-1){
          this.blockAndSearchTwoPoint();
        } else if (val===-2){
          this.startAndEndSearch(-1,Store.state.startAndEndPoint.start,Store.state.startAndEndPoint.end)
        }
      }
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




<!--<template>-->
  <!--<Collapse simple>-->
      <!--<Panel v-for="(onePath,index) in polylinePaths" :key="index" name="$index">-->
        <!--<span>{{onePath[0].name}}...{{onePath[onePath.length-1].name}}&nbsp;&nbsp;</span>-->
        <!--<div slot="content">-->
          <!--<a href="javascript:void (0)" v-on:click="getOnePathToMap(onePath)">查看地图位置</a>&nbsp;&nbsp;<span>{{dist[index]}}米</span>-->
          <!--<ul>-->
            <!--<li v-for="(point,index) in onePath" v-if="point.name!=''" >{{point.name}}</li>-->
          <!--</ul>-->
        <!--</div>-->
      <!--</Panel>-->
  <!--</Collapse>-->
<!--</template>-->

<!--<script>-->
  <!--import Store from '../../vuex/store'-->
    <!--export default {-->
        <!--name: "RoutesList",-->
      <!--data(){-->
          <!--return{-->
            <!--polylinePaths:[],-->
            <!--dist:[],-->
            <!--newData:false-->
          <!--}-->
      <!--},-->
      <!--methods:{-->
        <!--//将一条路径传给baidu-map组件-->
        <!--getOnePathToMap(onePath){-->
          <!--console.log("给地图添加覆盖物");-->
          <!--console.log(onePath);-->
          <!--this.$emit("onePath",onePath);-->
        <!--},-->
        <!--//取得公共区域的数据-->
        <!--getPolylinePaths(){-->
          <!--console.log("重新渲染路线列");-->
          <!--this.newData=true;//重新渲染数据模块-->
          <!--console.log("list组件获取公共区域的数据长度："+Store.state.polylines.length);-->
          <!--for (let i = 0; i < Store.state.polylines.length; i++) {-->
            <!--this.polylinePaths.push(Store.state.polylines[i]);-->
            <!--this.dist.push(Store.state.dist[i]);-->
          <!--}-->

        <!--}-->
      <!--},-->
      <!--mounted() {-->
          <!--this.getPolylinePaths();-->
      <!--}-->
    <!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--*{-->
  <!--font-size: 14px;-->
<!--}-->
<!--</style>-->
