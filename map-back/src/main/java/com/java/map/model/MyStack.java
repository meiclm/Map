package com.java.map.model;

/**
 * 自定义的栈
 */
public class MyStack {
    public int[] elements;

    public MyStack(){
        elements=new int[0];
    }

    public void push(int element){
        int[] newArr=new int[elements.length+1];

        for(int i=0;i<elements.length;i++){
            newArr[i]=elements[i];
        }
        newArr[elements.length]=element;
        elements=newArr;
    }

    public int peek() {
        return elements[elements.length-1];
    }

    public int pop(){
        int node=elements[elements.length-1];
        int[] newArr=new int[elements.length-1];
        for (int i = 0; i < newArr.length; i++) {
            newArr[i]=elements[i];
        }
        elements=newArr;
        return node;
    }

    public boolean isEmpty(){
        return elements.length==0?true:false;
    }

    public int indexOf(int a) {
        int k=-1;
        for (int i = 0; i < elements.length; i++) {
            if (a==elements[i]) {
                k=i;
            }
        }
        return k;
    }

    public int length() {
        return elements.length;
    }

    public void show() {
        for (int i = 0; i < elements.length; i++) {
            System.out.print(elements[i]+" ");
        }
        System.out.println("");
    }
    public void clear() {
        int[] newArr=new int [0];
        elements=newArr;
    }
}
