<template>
    <div id="box">
      <div id="top_box" >
        <!--v-on:polylinePaths="updatePolylinePaths"-->
        <HeaderInput  v-on:muchKeys="setKeysLocation"></HeaderInput>
      </div>
      <div id="baidu_map" class="bm-view"></div>
      <div v-on:routes="setShowRoutes">
        <RoutesDrawer  v-on:onePath="drawPolylinePath"></RoutesDrawer>
      </div>
    </div>
</template>

<script>
import HeaderInput from './top/Header'
import RoutesDrawer from './dialog/RoutesDrawer'
    export default {
      name: "MapContent.vue",
      components: {
        RoutesDrawer,
        HeaderInput
      },
      data() {
        return {
          myMap:{},
          center: {lng: 112.454155, lat: 38.021995},
          zoom: 17,
          show:false,
          search_route_icon: -1,
          showRoutes:false
        }
      },
      mounted(){
        this.setMap(this.center,this.zoom);
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
          var myIcon = new BMap.Icon("http://lbsyun.baidu.com/jsdemo/img/fox.gif", new BMap.Size(100,57));//图标
          var marker1=new BMap.Marker(points[0],{icon:myIcon});
          this.myMap.addOverlay(marker1);

          var polyline = new BMap.Polyline(points, {strokeColor:"red", strokeWeight:2, strokeOpacity:0.5});   //创建折线
          this.myMap.addOverlay(polyline);   //增加折线

          var marker2=new BMap.Marker(points[points.length-1]);
          this.myMap.addOverlay(marker2);

          //画好线后，中心点变一下，放大更美观
          this.center.lng = onePath[0].lng;
          this.center.lat = onePath[0].lat;
          this.zoom = 17
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
              `<h4 style='margin:0 0 5px 0;padding:0.2em 0'>${muchKeys[i].name}</h4>
           <img style='float:right;margin:4px' id='imgDemo' src='../assets/${muchKeys[i].url}'
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

        //重新渲染数据
        setShowRoutes(temp){
          console.log("设置showRoutes=true，显示")
          this.showRoutes=true;
        },
        //查看多条路径的，动画演示多条路径的
        animationRoutes(){
          
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
  .bm-view {width: 95%;height: 470px;overflow: hidden;margin:0;}
</style>
