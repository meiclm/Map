package com.java.map.model;

public class Paths implements Comparable<Paths> {
    public int[] path;
    public double dist;

    public int[] getPaths() {
        return path;
    }

    public void setPaths(int[] path) {
        this.path = path;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    @Override
    public int compareTo(Paths o) {
        int k=0;
        double i = this.getDist() - o.getDist();//先按照年龄排序
//        if(i == 0){
//            return this.score - o.getScore();//如果年龄相等了再用分数进行排序
//        }
        if (i>0){
            k=1;
        }else if (i<0){
            k=-1;
        }else {
            k=0;
        }
        return k;
    }

}
