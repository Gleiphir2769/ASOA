package NSGA;

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import Bean.TaskBean;
import Bean.TaskRequestBean;
import Bean.TaskRequestBean.Method;
import Model.Energy;
import Model.Latency;
import Util.XMLReader;

public class Local_Main {

	public static void main(String[] args) throws DocumentException {
		
		
		 ArrayList<TaskBean> taskList= XMLReader.getXmlReader().LOCAL_Method_Init();//初始化;

		
		///////////////////计算时延和能耗///////////////////////
		
		double TotalLatencyObject = 0;//总时延
		double TotalEnergyObject = 0;//总能耗
		
		for (TaskBean taskBean : taskList) {
			
			ArrayList<TaskRequestBean> requestList =new ArrayList<TaskRequestBean>();
			requestList = taskBean.getRequestList();
			
			int i =0 ;
			for (TaskRequestBean requestTaskBean : requestList) {
				
				Latency latency = new Latency(requestTaskBean);
				TotalLatencyObject +=latency.TimeTotal();
				
				Energy energy = new Energy(latency);
				TotalEnergyObject +=energy.EnergyTotal();
				
//				System.out.println(" total " +i);i++;
//				System.out.println("-----------------------------------");
			}
		}
		
		System.out.println("Local Latency : "+TotalLatencyObject);
		System.out.println("Local Energy : "+TotalEnergyObject);

		
		
	}
}
