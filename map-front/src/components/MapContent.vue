<template>
    <div id="box">
      <div id="top_box" >
        <HeaderInput  v-on:muchKeys="setKeysLocation" v-on:drawer="setDrawerShow" v-on:show="setShow"></HeaderInput>
      </div>
      <div id="baidu_map" class="bm-view"></div>
      <Drawer title="查找路线区":mask="false" :closable="true" width="380px"
              v-model="drawerValue"  >
        <RoutesList :fatherBlockId="blockId" v-on:onePath="drawPolylinePath" v-on:animation="animationRoutes" v-on:blockOnePath="setBlock"></RoutesList>
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
          myMap: {},
          center: {lng: 112.454155, lat: 38.021995},
          zoom: 17,
          show: false,
          search_route_icon: -1,

          drawerValue: false,
          showInfo: true,
          showList: false,
          allOverlaysLen: -1,
          blockId: -1,
          blockMarker:{},//阻塞点的Marker
          manMK:{},//小人
          timer:null,//定时器
        }
      },
      mounted() {
        this.setMap(this.center, this.zoom);
      },
      methods: {
        setMap(center, zoom) {
          /** 只需要在index.html中引入<script>和webpack.base.conf.js中引入 externals，
           * 会自动加载百度地图的api*/
          this.myMap = new BMap.Map('baidu_map');
          var point = new BMap.Point(center.lng, center.lat);
          this.myMap.centerAndZoom(point, zoom);
          this.myMap.enableScrollWheelZoom(true);
          this.myMap.clearOverlays();
        },
        //画一条线 , points[]一条路径上的点集合
        drawPolylinePath(onePath) {
          console.log("画线");
          //清除覆盖物
          this.myMap.clearOverlays();
          if (this.blockMarker){
            console.log("阻塞点的Marker："+this.blockMarker);
            this.myMap.addOverlay(this.blockMarker);
          }

          let points = [];//坐标点
          for (let i = 0; i < onePath.length; i++) {
            points[i] = new BMap.Point(onePath[i].lng, onePath[i].lat);
          }
          //获取points.length
          let len = points.length / 2;
          var myIcon1 = new BMap.Icon("http://localhost:8087/imgResource/start.gif", new BMap.Size(50, 50));//图标
          var marker1 = new BMap.Marker(points[0], {icon: myIcon1});
          this.myMap.addOverlay(marker1);

          var polyline = new BMap.Polyline(points, {strokeColor: "red", strokeWeight: 2, strokeOpacity: 0.5});   //创建折线
          this.myMap.addOverlay(polyline);   //增加折线

          var myIcon2 = new BMap.Icon("http://localhost:8087/imgResource/end.gif", new BMap.Size(50, 50));//图标
          var marker2 = new BMap.Marker(points[points.length - 1], {icon: myIcon2});
          this.myMap.addOverlay(marker2);

          this.myMap.centerAndZoom(new BMap.Point(onePath[len].lng, onePath[len].lat), 16);
          //画好线后，中心点变一下，放大更美观

        },

        //定位，根据经纬度，多关键字检索
        setKeysLocation(muchKeys) {
          //清除地图上的覆盖物Marker
          this.myMap.clearOverlays();
          console.log("该关键字："+muchKeys);
          let allOverlay = this.myMap.getOverlays();
          for (let i = 0; i < allOverlay.length; i++) {
            this.myMap.removeOverlay(allOverlay[i]);
          }
          let points = [];
          let infoWindow = [];

          for (let i = 0; i < muchKeys.length; i++) {
            console.log(i);
            if (muchKeys[i].url == "") {
              muchKeys[i].url == "logo.PNG";
              muchKeys[i].explain == ""
            }
            let sContent =
              `<div><h4 style='margin:0 0 5px 0;padding:0.2em 0'>${muchKeys[i].name}</h4>
              <img style='float:right;margin:4px' id='imgDemo' src='http://localhost:8087/imgResource/${muchKeys[i].url}'
                  width='139' height='104' title='${muchKeys[i].name}'/>
              <p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>
                  ${muchKeys[i].explain}
               </p>
           </div>`;

            points[i] = new BMap.Point(muchKeys[i].lng, muchKeys[i].lat);//获取坐标点
            var marker = new BMap.Marker(points[i]);  // 创建标注
            infoWindow.push(new BMap.InfoWindow(sContent));//创建信息窗口
            this.myMap.addOverlay(marker);              // 将标注添加到地图中

            marker.addEventListener("click", function () {
              this.openInfoWindow(infoWindow[i]);
              //图片加载完毕重绘infowindow
              document.getElementById('imgDemo').onload = function () {
                //防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
                infoWindow[i].redraw();
              }
            });
          }
          this.myMap.openInfoWindow(infoWindow[0], points[0]);//打开窗口，显示该点信息
          this.myMap.panTo(points[0]);//移动到该点
        },

        //查找路线框弹出来
        setDrawerShow(drawer) {
          if (drawer == 1) {
            this.drawerValue = true;
          }
        },

        //显示静态数据还是路线列表查找框
        setShow(show) {
          if (show == 0) {
            this.showInfo = true;
            this.showList = false;
          } else {
            this.showInfo = false;
            this.showList = true;
          }
        },
        //获取所有的数据点，点击该点形成阻塞矩阵
        setBlock(blockOnePath) {
          if (blockOnePath.length > 0) {
            this.showOnePathAllPoint(blockOnePath)
          } else {
            Store.commit("clearBlock");
            //清除覆盖物
            console.log("取消阻塞的Marker，blockMarker："+typeof this.blockMarker);
            this.myMap.removeOverlay(this.blockMarker);
          }
        },
        showOnePathAllPoint(onePath) {
          let that = this;

          //给路上的显示可点击的阻塞的点
          function addMarker(point, pointId) {

            var myblockMarker = new BMap.Marker(point);
            that.myMap.addOverlay(myblockMarker);

            //给该覆盖物添加点击事件，点击，阻塞
            myblockMarker.addEventListener("click", function () {
              that.blockId=pointId;
              console.log("传给子的数据："+that.blockId);
              Store.commit("setBlock",pointId);//将阻塞点保存到公共数据域
              //将marker记录下来
              that.blockMarker=myblockMarker;
              console.log("记录下来的阻塞Marker："+typeof that.blockMarker);
              that.myMap.clearOverlays();
              that.$Message.info({
                content: '路线列表已经更新完毕！',
                duration: 10
              });
            });
          }

          if (onePath.length > 0) {
            console.log("阻塞路径的所有点：" + onePath);
            //原本地图上有的Marker
            var allOverlays = this.myMap.getOverlays();
            this.allOverlaysLen = allOverlays.length;
            console.log("阻塞前marker长度：" + this.allOverlaysLen);

            //开始与结束两个点原本就画好了
            for (let i = 1; i < onePath.length - 1; i++) {
              var point = new BMap.Point(onePath[i].lng, onePath[i].lat);
              addMarker(point, onePath[i].id);
            }

          } else {
            console.log("取消阻塞");
            if (this.allOverlaysLen > 0) {
              // let len=this.myMap.getOverlays().get
              this.blockId = -2;
            }

          }
        },

        //查看该条路径的模拟人的行走，动画演示多条路径的,onePath是一个point的对象数组
        animationRoutes(onePath) {
          this.timer=null;
          console.log("在地图上移动的marker");
          var points = [];
          console.log("传过来的点的长度；"+onePath.length);
          for (let i = 0; i < onePath.length; i++) {
            points[i] = new BMap.Point(onePath[i].lng, onePath[i].lat);
          }

          // this.myMap.centerAndZoom(new BMap.Point(points[0]),14);  // 初始化地图,设置中心点坐标和地图级别
          var myIcon = new BMap.Icon("http://localhost:8087/imgResource/running.png", new BMap.Size(60, 60));//图标

          var k=1;
          // this.manMK=new BMap.Marker(this.getPoint(k,points),{icon:myIcon});//获取图标;
          var manMK=new BMap.Marker(getPoint(k,points),{icon:myIcon});//获取图标;
          let that=this;

          this.timer=setInterval(function(){
            // that.manMK=new BMap.Marker(that.getPoint(k,points),{icon:myIcon});//获取图标;
            // k=that.run(k,points,that.manMK);
            k=run(k,points);
            console.log("执行了run后：i="+k);
            if(k==points.length+1||k==-1){//长度刚好，或者执行错误
              clearInterval(that.timer);
              this.timer=null;
            }
          },600);

          function run(i,points){
            console.log("manMK的数据类型："+typeof manMK);
            if(manMK){//该图标存在
              console.log("清除该Marker对象");
              that.myMap.removeOverlay(manMK);
            }
            if (i<points.length+1){
              manMK = new BMap.Marker(getPoint(i,points),{icon:myIcon});//获取图标
              console.log("获取图标！");
              that.myMap.addOverlay(manMK);//添加覆盖物
              var poi=[];
              for(var j=0;j<i;j++){
                poi.push(points[j]);
              }
              if(poi.length>=1&&poi.length<=points.length){
                drawRunnningLine(poi);
                i++;
                console.log("i在曾："+i);
              }
              return i;
            }
            return -1;
          }

          function getPoint(index,points){
            if (index>=1&&index<=points.length){
              return points[index-1];
            }
          }
          //画线，points是坐标点的数组
          function drawRunnningLine(pois){
            // console.log("pois.length="+pois.length+"pois：="+pois);
            if(pois.length>=2){
              var sy = new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
                scale: 0.6,//图标缩放大小
                strokeColor:'#fff',//设置矢量图标的线填充颜色
                strokeWeight: '1',//设置线宽
              });
              var icons = new BMap.IconSequence(sy, '10', '30');
              var polyline =new BMap.Polyline(pois, {
                enableEditing: false,//是否启用线编辑，默认为false
                enableClicking: true,//是否响应点击事件，默认为true
                // icons:[icons],
                strokeWeight:'5',//折线的宽度，以像素为单位
                strokeOpacity: 0.8,//折线的透明度，取值范围0 - 1
                strokeColor:"#18a45b" //折线颜色
              });
              that.myMap.removeOverlay(polyline);//清除指定的覆盖物
              // console.log("添加线条！");
              that.myMap.addOverlay(polyline);
              // console.log("画动画线");
            }else {
              console.log("点的数量少于2");
            }
          }
        },

      },

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
