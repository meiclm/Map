<template>
    <div style="margin-top:12px ">
      <div id="logo" class="left_box">
        <div id="logo_box" >
        </div>
        <!--<span style="">中北</span>-->
      </div>
      <div class="lef_box" style=" height: 95px;width: 75%;overflow: hidden">
        <div id="box_input">
          <AutoComplete
            v-model="value1"
            :data="points_name"
            :filter-method="filterMethod"
            @keyup.enter.native="searchPoint"
            placeholder="输入地名"
            class="my_input"></AutoComplete>
          <Button type="primary" icon="ios-search" class="my_button" @click="searchPoint()">Search</Button>
          <Button  icon="ios-git-branch" class="my_button" @click="setDrawer">Route</Button>
          <Button  icon="md-close" v-if="block==1" class="my_button" @click="setBlockId">阻塞</Button>
          <Button  icon="" v-if="block==0" class="my_button" @click="cancelBlock">取消阻塞</Button>
          <!--&nbsp; &nbsp;-->
          <!--<a href="http://www.nuc.edu.cn/index.htm">中北大学官网</a>-->
        </div>

      </div>

    </div>
</template>

<script>
  import axios from 'axios'
  import Store from '../../vuex/store'
  import api from '../../server/api'

    export default {
      name: "Header",
      props:["source_points_length"],
      data(){
          return{
            value1:'',
            value2:false,
            loading:true,
            block:1,
            start_value:"",
            end_value:'',
            start:-1,
            end:-1,
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
          axios.get("static/points.json").then((response)=> {
            if (response.data.Points.length>0){
              that.points=response.data.Points;
              Store.commit("setPoints",response.data.Points);//保存本地文件到公共数据中
              // console.log(that.points.length);//40
              // that.$set(that.points[0],0,response.data.Points[0]);
              console.log("取到静态资源文件的数据点");
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

          //获取距离数据，矩阵
          axios.get(api.getData()).then((response)=>{
            if (response.data.length>0){
              Store.commit("setData",response.data);
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
              if (this.points[i].name===this.value1){
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
        //给父组件MapContent传递drawer=1,显示drawer框
        setDrawer(){
          this.$emit("drawer",1);
        },
        setBlockId(){
          this.block=0;
          this.$emit("blockId",1);
        },
        cancelBlock(){
          this.block=1;
          this.$emit("blockId",0);
        }
      },
      mounted() {
        this.getPointsName();//获取json数据，并显示在自动匹配区
        console.log("Header挂载："+this.points_name);
      },
      updated(){
        console.log("Header更新："+this.points_name);
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

</style>
