<template>
    <div class="">
      <div class="topHeader">
        <div><Input style="width: 400px"/></div>
        <div>
          <div>中北特色美食栏：</div>
          <div>中北风景区：</div>
        </div>
        <div class="showContent"></div>
      </div>
      <!--地图-->
      <div class="b_map" id="my_bmap"></div>
    </div>
</template>

<script>
    export default {
        name: "SceneryAndFoolContent",
        data(){
          return{
            mapObj:{},//地图
            center: {lng: 112.454155, lat: 38.021995},
          }
        },
       mounted(){
          this.getMap(this.center,16);
       },
        methods:{
          getMap(center,zoom){
            this.mapObj=new BMap.Map('my_bmap');
            var point=new BMap.Point(center.lng, center.lat);
            this.mapObj.centerAndZoom(point, zoom);
            this.mapObj.enableScrollWheelZoom(true);
            this.mapObj.clearOverlays();
          },
        }
    }
</script>

<style scoped>
  .topHeader{
    border: 1px orange solid;
    width: 100%;
    height: 27vh;
    overflow: hidden;
    z-index: 66;
  }
  .showContent{
    border: 1px blue solid;
    width: 30%;
  }
  .b_map{
    border: 1px red solid;
    width: 100%;
    height: 100vh;
  }
</style>
