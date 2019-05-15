const root='http://localhost:8087';

const Api={
  'getData':()=>`${root}/data`,
  'block':(block,start,end)=>`${root}/block?block=${block}&start=${start}&end=${end}`,
  'depth':(start,end)=>`${root}/searchAToB?start=${start}&end=${end}`
}
export default Api;
