package com.example.edison.internshiptrackerapp;

import java.util.ArrayList;
import java.util.Collections;

public class DataHolder {

    private static long totalHours;
    private  static  int maxHours, minHours, hoursSize, goalHours;

    public void setGoalHours(int h){

        DataHolder.goalHours = h;
    }
    public void setMaxHours(int h){
        DataHolder.maxHours = h;

    }
    public void setMinHours(int h){
        DataHolder.minHours = h;

    }
    public void setTotalHours(long totalHours) {
        DataHolder.totalHours = totalHours;
    }
    public void setHoursSize(int size){
        DataHolder.hoursSize = size;
    }

    public static int getGoalHours(){
        return  goalHours;
    }
    public static int getHoursSize(){
        return hoursSize;
    }
  public static int getMaxHours(){
        return maxHours;
  }

    public static int getMinHours() {
        return minHours;
    }

    public static long getTotalHours(){
        return  totalHours;
    }


}
