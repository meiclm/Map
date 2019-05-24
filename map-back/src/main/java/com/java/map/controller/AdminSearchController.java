package com.java.map.controller;

import com.java.map.model.Paths;
import com.java.map.other.DepthTraverse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//返回数据
@RestController
//支持跨域
@CrossOrigin
public class AdminSearchController {
//    public double[][] data=getSourceData();
//    int row=0;
    public double[][] getData(){
        System.out.print("执行了取数据");
        return getSourceData();
    }

    @GetMapping("/data")
    public double[][] getArrayData(){
        return getData();
    }
    //找两点间的所有的路径
    @GetMapping("/searchAToB")
    public List<Paths> searchAToB(@RequestParam("start") int start,@RequestParam("end") int end){
        List<Paths> fpath=null;
        List<Paths> results=null;
        double[][] data=getData();
        int row=data.length;
        int min=-1;

        if (start!=-1&&end!=-1){
            DepthTraverse depthTraverse=new DepthTraverse(data);
            System.out.println("开始查两点间的路线");
            depthTraverse.dfs(start,end);
            fpath=depthTraverse.getPaths();

            //排序,按照最短路径排序
            Collections.sort(fpath);
            System.out.println("排序好了");
        }
        System.out.println("查到两点间的路线");
        return fpath;
    }

    @GetMapping("/block")
    public List<Paths> blockResult(@RequestParam("block") int block,@RequestParam("start") int start,
                                   @RequestParam("end") int end){
        List<Paths> newFpath=null;
        double[][] data;
        if(block>-1){
            data=blockPoint(block);//得到被阻塞后的新的数组
            System.out.println("阻塞的矩阵");
        }else{
            data=getData();
            System.out.println("正常矩阵");
        }

        int row=data.length;
        int min=-1;

        if (start!=-1&&end!=-1){
            DepthTraverse depthTraverse=new DepthTraverse(data);
            System.out.println("开始查新的路线");
            depthTraverse.dfs(start,end);
            newFpath=depthTraverse.getPaths();

            //排序,按照最短路径排序
            Collections.sort(newFpath);
        }
//        System.out.println("查到两点间新的路线");
        return newFpath;
    }
    //读取静态资源json文件
    public double[][] getSourceData(){
        double[][] nums;
        File file = new File("F:/毕设/graduation-MapSystem/myData.txt");
        List list = new ArrayList();
        try {
            BufferedReader bw = new BufferedReader(new FileReader(file));
            String line = null;
            //因为不知道有几行数据，所以先存入list集合中
            while((line = bw.readLine()) != null){
                list.add(line);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //确定数组长度
        nums =new double[list.size()][94];
        for(int i=0;i<list.size();i++){
            String s = (String) list.get(i);
            String[] sArray=s.split(",");//读取一行的数据，分割
            for (int j = 0; j < sArray.length; j++) {
                if (sArray[j]!=null&&sArray[j]!="") {
                    nums[i][j]=Double.parseDouble(sArray[j]);
                }
            }
//		   nums[i] = Double.parseDouble(s.trim());
        }
        System.out.println("取得数据");
        return nums;
    }

    //模拟堵塞,得到新的矩阵
    public double[][] blockPoint( int block){
        double[][] data=getData();
        int row=data.length;
        double[][] newData=new double[row][row];
        for (int i=0;i<row;i++){
            if (i==block){
                for (int k=0;k<row;k++){//该行置为0
                    newData[i][k]=0;
                }
            }else {
                for (int j=0;j<row;j++){
                    newData[i][j]=data[i][j];
                    if (j==block){//该列置为0
                        newData[i][j]=0;
                    }
                }
            }
        }
        return newData;
    }

}
