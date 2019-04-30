const root='http://localhost:8087';

const Api={
  //floyd最短路径
  'floyd':(block,start,end)=>`${root}/search/min_routes?block=${block}&start=${start}&end=${end}`,
  'depth':(start,end)=>`${root}/searchAToB?start=${start}&end=${end}`
}
export default Api;
