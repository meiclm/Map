<template>
    <div style="margin-top:12px ">
      <div id="logo" class="left_box">
        <div id="logo_box"></div>
        <!--<span style="">中北</span>-->
      </div>
      <div class="lef_box" style=" height: 95px;width: 75%;overflow: hidden">
        <div id="box_input">
          <AutoComplete
            v-model="value1"
            :data="points_name"
            :filter-method="filterMethod"
            @keyup.enter="searchPoint"
            placeholder="输入地名"
            class="my_input"></AutoComplete>
          <Button type="primary" icon="ios-search" class="my_button" @click="searchPoint()">Search</Button>
          <Button  icon="ios-git-branch" class="my_button" @click="value2 = true">Route</Button>
            <Modal
              v-model="value2"
              title="输入路线"
              :loading="loading"
              :styles="{top:'20px'}"
              @on-ok="asyncOK">
              <br>
              起点：<AutoComplete
              v-model="start_value"
              :data="points_name"
              :filter-method="filterMethod"
              placeholder="输入起名"
              class="route_value"></AutoComplete>
              <!--<Input v-model="" placeholder="起始点" autocomplete=""/>-->
              <br>
              <br>
              终点：<AutoComplete
              v-model="end_value"
              :data="points_name"
              :filter-method="filterMethod"
              placeholder="输入终名"
              class="route_value"></AutoComplete>
              <!--<Input v-model="end_value" placeholder="终点"/>-->
            </Modal>
            &nbsp; &nbsp;
          <a href="http://www.nuc.edu.cn/index.htm">中北大学官网</a>
        </div>

      </div>

    </div>
</template>

<script>
  import Axois from 'axios'
  import Store from '../../vuex/store'
  import Bus from '../../server/bus'

  import Api from '../../server/api'
    export default {
      name: "Header",
      props:["source_points_length"],
      data(){
          return{
            value1:'',
            value2:false,
            loading:true,
            block:-1,
            start_value:"",
            end_value:'',
            points_name:["一道门","二道门","三道门","四道门","二龙山","校医院","图书馆"],
            //json对象
            points:[],
            muchKeys:[]
          }
      },
      methods:{
        //获取json文件里的所有不为空的name，整合给points_name[],并且获取json对象
        getPointsName(){
          let that=this;
          let index=0;
          Axois.get("static/points.json").then((response)=> {
            if (response.data.Points.length>0){
              that.points=response.data.Points;
              Store.commit("setPoints",response.data.Points);//保存本地文件到公共数据中
              // console.log(that.points.length);//40
              // that.$set(that.points[0],0,response.data.Points[0]);
              for (var i=0;i<response.data.Points.length;i++) {
                if (response.data.Points[i].name!="") {
                  that.points_name[index]=response.data.Points[i].name;
                  index++;
                }
              }
            }
          });
        },
        //查到该点的坐标
        searchPoint(){
          //清空坐标集合
          // let keyLength=this.muchKeys.length
          if (this.muchKeys.length>0){
            for (let i = 0; i < this.muchKeys.length; i++) {
              this.muchKeys.pop();
              if (this.muchKeys.length===0){
                break;
              }
            }
          }
          var len=this.points.length;
          console.log("本地文件的点集合的长度："+len);
          //匹配json文件的数据
          if (this.value1!=""&&this.points.length>0){
            // that.value1=trim(that.value1);
            for (let i=0;i<len;i++){
              if (this.points[i].name.indexOf(this.value1)>-1){
                this.muchKeys.push(this.points[i]);
              }
            }
            // console.log("匹配到的点"+this.muchKeys.length);
            //将查到的点对象传给父组件，muchKeys是关键字
            this.$emit("muchKeys",this.muchKeys);
          }
        },
        //自动补全提示输入框,过滤
        filterMethod(value,option){
            return option.indexOf(value)!=-1;
        },
        //查路线
        asyncOK(){
          let that=this;
          let start=-1;
          let end=-1;
          let fpath=[];
          // let allPathsNum=0;
          //清空search输入框
          this.value1="";
          //加载到数据
          if (this.points!=null){
            if (that.start_value!=""&&that.end_value!=""&&this.points.length>0){
              //模糊查询匹配数据
              let points_length=that.points.length;
              for (let i=0;i<points_length;i++){
                if (this.points[i].name.indexOf(that.start_value)>-1) {//匹配起点
                  start=i;
                  break;
                }
              }
              for (let i = 0; i <points_length ; i++) {
                if (this.points[i].name.indexOf(that.end_value)>-1) {
                  end=i;
                  break;
                }
              }

              if (start>-1&&end>-1) {
                Axois.get(Api.depth(start, end))
                  .then(function (response) {
                    if (response.data != null) {
                      fpath = response.data;
                      // allPathsNum = response.data.length;
                      // console.log("更新一次："+fpath[0].dist)

                      Bus.$emit('fpath', fpath);//传给RoutesDrawer同级组件

                    }
                  });

              }else {
                  that.$Message("不能为空！");
                }
              }
            }
          setTimeout(()=>{
            that.value2=false;
          },1500);
          console.log("传输routes=1给父组件")
          this.$emit("routes","1");
        }
      },
      created() {
        this.getPointsName();//获取json数据，并显示在自动匹配区
        // console.log(this.points_name);
      }
    }
</script>

<style scoped>
  *{
    font-size: 18px;
    font-weight: bold;
  }
  #logo{
    width: 25%;
  }
  #logo_box{
    height: 95px;
    width: 270px;
    background-image: url('../../assets/logo.png');
  }
#box_input{
  padding: 50px 0 0 0px;
  overflow: hidden;
}
div.left_box{
    float: left;
    box-sizing: border-box;
  }
  .my_input{
    width:290px;
  }
  .my_button{
    margin: 0 10px;
  }
  .route_value{

  }
</style>
