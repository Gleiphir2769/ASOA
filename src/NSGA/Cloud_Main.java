package NSGA;

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import Bean.CloudServerBean;
import Bean.TaskBean;
import Bean.TaskRequestBean;
import Bean.TaskRequestBean.Method;
import Model.Energy;
import Model.Latency;
import Util.StaticParam;
import Util.XMLReader;

public class Cloud_Main {

	public static void main(String[] args) throws DocumentException {
		// TODO Auto-generated method stub
		 CloudServerBean cloudServer  = XMLReader.getXmlReader().CLOUD_Method_Init();//ȫ��Ψһ�Ʒ�����
		;
		
		//////////////////������ʱ�Ӻ��ܺ�////////////////
		double TotalLatencyObject = 0;//��ʱ��
		double TotalEnergyObject = 0;//���ܺ�
		int i =0;
		ArrayList<TaskRequestBean> requestList =new ArrayList<TaskRequestBean>();
		 requestList = cloudServer.getRequestList();
		 for (TaskRequestBean taskRequestBean : requestList) {
			
			 Latency latency = new Latency(taskRequestBean);
				TotalLatencyObject +=latency.TimeTotal();
				
				Energy energy = new Energy(latency);
				TotalEnergyObject +=energy.EnergyTotal();
			 
//				System.out.println(" total " +i);i++;
//				System.out.println("-----------------------------------");
			
		}
		 
			System.out.println("Cloud Latency : "+TotalLatencyObject);
			System.out.println("Cloud Energy : "+TotalEnergyObject);
		
		
	}

}
