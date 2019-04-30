package com.java.map.model;

/**
 * 自定义的二维数组
 */
public class My2DArray {
    public int[][] paths;
    public My2DArray(){
        paths=new int[0][];
    }
    public void addPath(int[] onePath) {
        int[][] newArr=new int[paths.length+1][];
        for(int i=0;i<paths.length;i++){
            newArr[i]=paths[i];
        }
        newArr[paths.length]=new int[onePath.length];
        for(int j=0;j<onePath.length;j++){
            newArr[paths.length][j]=onePath[j];
        }
        paths=newArr;
    }

    public void showPaths(){
        for(int i=0;i<paths.length;i++){
            for(int j=0;j<paths[i].length;j++){
                System.out.print(paths[i][j]+" ");
            }
            System.out.println("");
        }
    }
}
