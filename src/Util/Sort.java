package Util;

import Bean.TaskBean;
import Bean.TaskRequestBean;

import java.util.List;

/**
 * @author: daqige
 * @date: 2020/2/23 23:44
 */
public class Sort {
    public static void quickSort(List<TaskRequestBean> taskRequestBeans, int low, int high){
        int i,j;
        TaskRequestBean t, temp;
        if(low>high){
            return;
        }
        i=low;
        j=high;
        //temp就是基准位
        temp = taskRequestBeans.get(low);

        while (i<j) {
            //先看右边，依次往左递减
            while (temp.getPriority()<=taskRequestBeans.get(j).getPriority()&&i<j) {
                j--;
            }
            //再看左边，依次往右递增
            while (temp.getPriority()>=taskRequestBeans.get(i).getPriority()&&i<j) {
                i++;
            }
            //如果满足条件则交换
            if (i<j) {
                t = taskRequestBeans.get(j);
                taskRequestBeans.set(j, taskRequestBeans.get(i));
                taskRequestBeans.set(i, t);
            }

        }
        //最后将基准为与i和j相等位置的数字交换
        taskRequestBeans.set(low, taskRequestBeans.get(i));
        taskRequestBeans.set(i, temp);

        //递归调用左半数组
        quickSort(taskRequestBeans, low, j-1);
        //递归调用右半数组
        quickSort(taskRequestBeans, j+1, high);
    }

}
