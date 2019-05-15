package com.java.map.other;

import com.java.map.model.MyStack;
import com.java.map.model.Paths;

import java.util.ArrayList;
import java.util.List;

public class DepthTraverse {
    public double[][] data;//二维矩阵
    public MyStack stack=new MyStack();//有邻接节点的node，被访问过的
    public MyStack path=new MyStack();//单条路径
    public List<Paths> paths=new ArrayList<>();//保存多条路径，并记录两点之间的距离
    public Paths onePath;//一条路径对象
    public int[][] visited;//标志
    public int row;//数组长度

    public DepthTraverse(double[][] G){
        row=G.length;
        data=new double[G.length][G.length];
        //载入数组
        for(int i=0;i<row;i++){
            for(int j=0;j<row;j++){
                data[i][j]=G[i][j];
            }
        }

        //初始化标志
        visited=new int[G.length][G.length];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                visited[i][j]=0;
            }
        }
    }
//不用递归的深度优先遍历
    public void dfs(int a,int b){
        for (int i=0; i<row; i++) {
            if (data[a][i]==0) {
                visited[a][i]=1;
                visited[i][a]=1;
                if (i==row-1) {
                    if (!stack.isEmpty()) {
                        for (int j=0; j<row; j++) {
                            visited[a][j]=0;
                        }
                        path.pop();
                        a=stack.pop();
                        i=-1;
                        continue;
                    }
                    else ;
                }
                else continue;
            }
            else{
                if (visited[a][i]!=1) {
                    if (stack.indexOf(i)>-1){
                        if (i==row-1) {
                            if (!stack.isEmpty()) {
                                for (int j=0; j<row; j++) {
                                    visited[a][j]=0;
                                }
                                path.pop();
                                a=stack.pop();
                                i=-1;
                                continue;
                            }
                            else ;
                        }
                        else continue;
                    }
                    else {
                        if(i==b){
                            visited[a][i]=1;
                            visited[i][a]=1;
                            path.push(a);
                            path.push(i);
                            //得到一条路径
//                            path.show();
//                            paths.addPath(path.elements);
                            onePath=new Paths();
                            onePath.setPath(path.elements);//用对象存储一条路径
                            paths.add(onePath);//保存一条路径在多路径集合

                            path.pop();
                            path.pop();
                            if (i==row-1) {
                                if (!stack.isEmpty()) {
                                    for (int j=0; j<row; j++) {
                                        visited[a][j]=0;
                                    }
                                    path.pop();
                                    a=stack.pop();
                                    i=-1;
                                    continue;
                                }
                                else ;
                            }
                            else continue;
                        }
                        else{
                            visited[a][i]=1;
                            visited[i][a]=1;
                            path.push(a);
                            stack.push(a);
                            a=i;
                            i=-1;
                            continue;
                        }
                    }
                }
                else {
                    if(i==row-1){
                        if (!stack.isEmpty()) {
                            for (int j=0; j<row; j++) {
                                visited[a][j]=0;
                            }
                            path.pop();
                            a=stack.pop();
                            i=-1;
                            continue;
                        }
                        else ;
                    }
                    else continue;
                }
            }
        }


//        System.out.println("所有路径的数组：");
//        paths.showPaths();
        /**
         * 得到每条路径的总距离
         * {
         *     path：[1，2，3，9，10]
         *     dist:1235
         * }
         */
        for (Paths fpath:paths){
            DistancePath distancePath=new DistancePath(data,fpath);
            fpath=distancePath.getPaths();
        }
    }

    //返回两点间的全部的路径
    public List<Paths> getPaths(){
        return paths;
    }
}
