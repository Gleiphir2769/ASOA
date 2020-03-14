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
        //temp���ǻ�׼λ
        temp = taskRequestBeans.get(low);

        while (i<j) {
            //�ȿ��ұߣ���������ݼ�
            while (temp.getPriority()<=taskRequestBeans.get(j).getPriority()&&i<j) {
                j--;
            }
            //�ٿ���ߣ��������ҵ���
            while (temp.getPriority()>=taskRequestBeans.get(i).getPriority()&&i<j) {
                i++;
            }
            //������������򽻻�
            if (i<j) {
                t = taskRequestBeans.get(j);
                taskRequestBeans.set(j, taskRequestBeans.get(i));
                taskRequestBeans.set(i, t);
            }

        }
        //��󽫻�׼Ϊ��i��j���λ�õ����ֽ���
        taskRequestBeans.set(low, taskRequestBeans.get(i));
        taskRequestBeans.set(i, temp);

        //�ݹ�����������
        quickSort(taskRequestBeans, low, j-1);
        //�ݹ�����Ұ�����
        quickSort(taskRequestBeans, j+1, high);
    }

}
