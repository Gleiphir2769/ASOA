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
		 CloudServerBean cloudServer  = XMLReader.getXmlReader().CLOUD_Method_Init();//全局唯一云服务器
		;
		
		//////////////////计算总时延和能耗////////////////
		double TotalLatencyObject = 0;//总时延
		double TotalEnergyObject = 0;//总能耗
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
