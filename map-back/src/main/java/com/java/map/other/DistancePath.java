package com.java.map.other;

import com.java.map.model.Paths;

public class DistancePath {
    public Paths paths;

    public DistancePath(double[][] data,Paths myPaths){
        int len=myPaths.path.length;
//        paths=myPaths;

        //计算一条路径的距离
        for (int i = 0; i <len-1 ; i++) {
            int k=i+1;
            myPaths.dist+=data[myPaths.path[i]][myPaths.path[k]];
        }

    }

    //返回对象
    public Paths getPaths() {
        return paths;
    }
}
