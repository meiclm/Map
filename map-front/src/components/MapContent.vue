<template>
    <div id="box">
      <div id="top_box" >
        <HeaderInput  v-on:muchKeys="setKeysLocation" v-on:drawer="setDrawerShow" v-on:show="setShow" v-on:blockId="setBlock"></HeaderInput>
      </div>
      <div id="baidu_map" class="bm-view"></div>
      <Drawer title="查找路线区":mask="false" :closable="true" width="380px"
              v-model="drawerValue"  >
        <RoutesList v-on:onePath="drawPolylinePath" v-on:animation="animationRoutes"></RoutesList>
      </Drawer>
    </div>
</template>

<script>
import HeaderInput from './top/Header'
import RoutesList from './dialog/RoutesList'

import Store from '../vuex/store'

    export default {
      name: "MapContent.vue",
      components: {
        HeaderInput,
        RoutesList,
      },
      data() {
        return {
          myMap:{},
          center: {lng: 112.454155, lat: 38.021995},
          zoom: 17,
          show:false,
          search_route_icon: -1,

          drawerValue:false,
          showInfo:true,
          showList:false,
        }
      },
      mounted(){
        this.setMap(this.center,this.zoom);
        console.log("Map：")
      },
      methods: {
        setMap(center,zoom){
          /** 只需要在index.html中引入<script>和webpack.base.conf.js中引入 externals，
           * 会自动加载百度地图的api*/
          this.myMap=new BMap.Map('baidu_map');
          var point=new BMap.Point(center.lng,center.lat);
          this.myMap.centerAndZoom(point,zoom);
          this.myMap.enableScrollWheelZoom(true);
        },
        //画一条线 , points[]一条路径上的点集合
        drawPolylinePath(onePath){
          console.log("画线");
          //清除覆盖物
          this.myMap.clearOverlays();

          let points=[];//坐标点
          for (let i = 0; i < onePath.length; i++) {
            points[i]=new BMap.Point(onePath[i].lng,onePath[i].lat);
          }
          //获取points.length
          let len=points.length/2;
          var myIcon1 = new BMap.Icon("http://localhost:8087/imgResource/start.gif", new BMap.Size(50,50));//图标
          var marker1=new BMap.Marker(points[0],{icon:myIcon1});
          this.myMap.addOverlay(marker1);

          var polyline = new BMap.Polyline(points, {strokeColor:"red", strokeWeight:2, strokeOpacity:0.5});   //创建折线
          this.myMap.addOverlay(polyline);   //增加折线

          var myIcon2 = new BMap.Icon("http://localhost:8087/imgResource/end.gif", new BMap.Size(50,50));//图标
          var marker2=new BMap.Marker(points[points.length-1],{icon:myIcon2});
          this.myMap.addOverlay(marker2);

          this.myMap.centerAndZoom(new BMap.Point(onePath[len].lng,onePath[len].lat),16);
          //画好线后，中心点变一下，放大更美观

        },

        //定位，根据经纬度，多关键字检索
        setKeysLocation(muchKeys){
          //清除地图上的覆盖物Marker
          this.myMap.clearOverlays();
          let allOverlay = this.myMap.getOverlays();
          for(let i=0;i<allOverlay.length;i++){
            this.myMap.removeOverlay(allOverlay[i]);
          }
          let points=[];
          let infoWindow=[];

          for(let i=0;i<muchKeys.length;i++){
            console.log(i);
            if (muchKeys[i].url=="") {
              muchKeys[i].url=="logo.PNG";
              muchKeys[i].explain==""
            }
            let sContent =
              `<div><h4 style='margin:0 0 5px 0;padding:0.2em 0'>${muchKeys[i].name}</h4>
              <img style='float:right;margin:4px' id='imgDemo' src='http://localhost:8087/imgResource/${muchKeys[i].url}'
                  width='139' height='104' title='${muchKeys[i].name}'/>
              <p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>
                  ${muchKeys[i].explain}
               </p>
           </div>`;

            points[i]=new BMap.Point(muchKeys[i].lng,muchKeys[i].lat);//获取坐标点
            var marker = new BMap.Marker(points[i]);  // 创建标注
            infoWindow.push(new BMap.InfoWindow(sContent));//创建信息窗口
            this.myMap.addOverlay(marker);              // 将标注添加到地图中

            marker.addEventListener("click", function(){
              this.openInfoWindow(infoWindow[i]);
              //图片加载完毕重绘infowindow
              document.getElementById('imgDemo').onload = function (){
                //防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
                infoWindow[i].redraw();
              }
            });
          }
          this.myMap.openInfoWindow(infoWindow[0],points[0]);//打开窗口，显示该点信息
          this.myMap.panTo(points[0]);//移动到该点
        },

        //查找路线弹出来
        setDrawerShow(drawer){
          if (drawer==1){
            this.drawerValue=true;
          }
        },

        //显示静态数据还是路线列表查找框
        setShow(show){
          if (show==0){
            this.showInfo=true;
            this.showList=false;
          } else {
            this.showInfo=false;
            this.showList=true;
          }
        },
        //获取所有的数据点，点击该点形成阻塞矩阵
        setBlock(blockId){
          if (blockId===1) {
            Store.commit("setBlock",blockId);
            this.showAllPointAtMap();
          }else {
            Store.commit("clearBlock",blockId)
          }
        },
        //在地图上显示所有的数据点
        showAllPointAtMap(){
          this.myMap
          let len=Store.state.points;
          function addMarker(point,i){
            var marker = new BMap.Marker(point);
            this.myMap.addOverlay(marker);
            marker.addEventListener("click",function () {
              //阻塞该点
              Store.state.points[i].id;
            });
          }
          for (let i=0;i<len;i++){
            let point = new BMap.Point(Store.state.points[i].lng,Store.state.points[i].lat);
            addMarker(point,i);
          }
        },

        //查看该条路径的模拟人的行走，动画演示多条路径的
        animationRoutes(onePath){
          console.log("在地图上移动的marker");
          var points=[];
          for (let i = 0; i < onePath.length; i++) {
            points[i]=new BMap.Point(onePath[i].lng,onePath[i].lat);
          }

          var running=new BMap.Icon("http://localhost:8087/imgResource/running.png", new BMap.Size(60,60));//图标

          let that=this;
          let k=0;
          let my = setInterval(function () {
            if (k===points.length-1){
              clearInterval(my);
            }
            var marker = new BMap.Marker(points[k],{icon:running});  // 创建标注
            that.myMap.addOverlay(marker);
            console.log("添加marker动画:");
            var allOverlays=that.myMap.getOverlays();
            console.log("marker长度："+allOverlays.length);
            console.log("删除最后一个marker后的长度："+allOverlays.length)
            k++;
          },1000*2);
          that.myMap.removeOverlay(allOverlays[allOverlays.length-1]);//删除最后一个marker
        }
        }

      }
</script>

<style scoped>
  #box{
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
  #top_box{
    width: 100%;
    height: 120px;
    margin: 12px auto;
  }
  .bm-view {width: 100%;height: 470px;overflow: hidden;margin:0;}
  .drawer{
    width: 10%;
    border: 1px red solid;
    float: right;
  }
</style>
