package NSGA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;

import Bean.LBBean;
import Bean.MECServerBean;
import Bean.TaskRequestBean;
import Computation.Select;
import Model.Energy;
import Model.Latency;
import Util.StaticParam;
import Util.XMLReader;
import Util.StaticParam.METHOD_TYPE;

public class BenchMark_Main {

	public static void main(String[] args) throws DocumentException {

		ArrayList<Double> min = new ArrayList<Double>();
		for (int i = 0; i < 1; i++) {
			
			DoubleSolution solution = null;
			ArrayList<Integer> emptyList =Allocate();//���б������ֱ�3��ʵ�鷽��
			
			XMLReader.getXmlReader().InitlTask(solution,emptyList,METHOD_TYPE.BENCHMARK);//��ʼ�����в���;
			double TotalLatencyObject = 0;//��ʱ��
			double TotalEnergyObject = 0;//���ܺ�
			double TotalUtilizationObject = 0;//���ؾ��ⷽ�� 
			double employedECNs=0;
			employedECNs=Select.CountEmployedECNs(emptyList,METHOD_TYPE.BENCHMARK);
				Map<String, Double> totalMap=new HashMap<String, Double>();
				totalMap=XMLReader.TotalLatencyAndEnergy(METHOD_TYPE.BENCHMARK);
				TotalLatencyObject = totalMap.get("latency");
				TotalEnergyObject = totalMap.get("energy");
				
				TotalUtilizationObject=XMLReader.TotalAverageUtilization();
				
				
//				System.out.println("BenchMark EmploedECNs : "+employedECNs);
//				System.out.println("BenchMark Latency : "+TotalLatencyObject);
//				System.out.println("BenchMark Energy : "+TotalEnergyObject);
//				System.out.println("BenchMark Resource Utilization : "+TotalUtilizationObject);
System.out.println(TotalUtilizationObject+" "+TotalLatencyObject+" "+TotalEnergyObject);
//				System.out.println("-------------------------------\r\n");
			
			
			//min.add(TotalEnergyObject);
		}
		
		//System.out.println("min energy"+Collections.min(min));
	}

	//�������յ���˳���cloudlet1��2������˳����С�
		private static ArrayList<Integer> Allocate() throws DocumentException {
			Random random= new Random();
			int bound = XMLReader.getXmlReader().getMECList().size();
			ArrayList<Integer> solutionList= new ArrayList<Integer>();

			ArrayList<ArrayList<Integer>> tempList= new ArrayList<ArrayList<Integer>>();

			int  TotalRequestAmount = XMLReader.getXmlReader().getTotalRequestAmount();
			int  TotalDevicesCount = XMLReader.getXmlReader().getTotalDevicesCount();
			int  TotalCloudCount = XMLReader.getXmlReader().getTotalCloudCount();
			
//			int FCFS_Local_app = (int) (TotalRequestAmount * StaticParam.Allocate_Local_Rate); 
//			int FCFS_Cloud_app = (int) (TotalRequestAmount * StaticParam.Allocate_Cloudl_Rate);
			int FCFS_Local_app = (int) (TotalRequestAmount * 0.2); 
			int FCFS_Cloud_app = (int) (TotalRequestAmount * 0);
	 		int FCFS_Cloudlet_app= TotalRequestAmount-FCFS_Local_app-FCFS_Cloud_app;
			
			int Average_MD_Local_app = FCFS_Local_app / TotalDevicesCount;
			int Average_MD_Cloud_app = FCFS_Cloud_app / TotalDevicesCount;
			int Average_MD_Cloudlet_app = FCFS_Cloudlet_app / TotalDevicesCount;
			
			int Average_MD_Local_app_mod = FCFS_Local_app % TotalDevicesCount;
			int local_index = Average_MD_Local_app;
			int Average_MD_Cloudlet_app_mod = FCFS_Cloudlet_app % TotalDevicesCount;
			int cloudlet_index = Average_MD_Cloudlet_app;
			int Average_MD_Cloud_app_mod = FCFS_Cloud_app % TotalDevicesCount;
			int cloud_index = Average_MD_Cloud_app;
			

			//��һ��ѭ�����ܱ�������������ȫ����ʱ����
			for (int i = 0; i < XMLReader.getXmlReader().getTotalDevicesCount(); i++) {
						
				ArrayList<Integer> tempItemList= new ArrayList<Integer>();

				{//���ض˸��ݱ������в�Ǩ��, ÿ���豸��ǰAverage_MD_Local_app��Ӧ��ȫ����ΪǨ�Ʋ���0 (��������Ǩ��)
					for (int j = 0; j < Average_MD_Local_app; j++) {
						tempItemList.add(0);
					}
				}
				
				
				
				{//�ƶ��豸���ݱ������б�Ե������Ǩ��, ÿ���豸��(TotalRequestAmount-FCFS_Local_app-FCFS_Cloud_app)��Ӧ��ȫ����Ϊ˳��Ǩ�Ʋ���
					
				
					
					for (int j = 0; j < Average_MD_Cloudlet_app; j++) {
						
					tempItemList.add(random.nextInt(bound)+1);
					
					}
					
				
				}
				
				{//�ƶ��豸���ݱ��������ƶ˱�Ե������Ǩ��, ÿ���豸��(Average_MD_Cloud_app)��Ӧ��ȫ����ΪCloud ��ֵ
					for (int j = 0; j < Average_MD_Cloud_app; j++) {
						
						tempItemList.add(XMLReader.getXmlReader().getMECList().size()+1);
					}
					
					
				}	
				
				
				tempList.add(tempItemList);
				
				
				}
			
			int index_current=0;
			
			//����ʣ�����
			for (int i = 0; i < Average_MD_Local_app_mod; i++) {
				
				tempList.get(index_current).add(0);
				index_current++;
			}
			//��Եʣ�����
			for (int i = 0; i < Average_MD_Cloudlet_app_mod; i++) {
				tempList.get(index_current).add(random.nextInt(bound)+1);
				
				
				index_current++;
				if (index_current == TotalDevicesCount) {
					index_current =0 ;
				}
			}
			
			for (int i = 0; i < Average_MD_Cloud_app_mod; i++) {
				tempList.get(index_current).add(XMLReader.getXmlReader().getMECList().size()+1);
				index_current++;
				if (index_current == TotalDevicesCount) {
					index_current = 0 ;
				}
			}
						
			
			for (ArrayList<Integer> integerList : tempList) {
				
				for (Integer integer : integerList) {
					solutionList.add(integer);
				}
			}
			
			
						solutionList.add(random.nextInt(bound)+1);
			
			
			StringBuilder builder= new StringBuilder();
			for (Integer integer : solutionList) {
				builder.append(" ");
				builder.append(integer);
			}
			System.out.println(builder);
			return solutionList;
		}
	

	
	
	
	
}
