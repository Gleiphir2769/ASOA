package Util;

import java.lang.invoke.MethodType;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;

import Bean.CloudServerBean;
import Bean.LBBean;
import Bean.MECServerBean;
import Bean.TaskBean;
import Bean.TaskRequestBean;
import Bean.TaskRequestBean.Method;
import Model.Energy;
import Model.Latency;
import Bean.WaitLatencyBean;
import Util.StaticParam.METHOD_TYPE;

public class XMLReader {
	
	private static XMLReader xmlReader;
	private ArrayList<TaskBean> taskList=new ArrayList<TaskBean>();//ȫ��ΨһTask����
	private int TotalRequestAmount=0;//��������
	private Element root;
	public int SolutionCount=0;//���߱�������
	private int TotalDevicesCount=0;//���ƶ��豸��
	private int TotalCloudCount=1;//�ƶ˵����豸��








	




	private ArrayList<MECServerBean> MECList=new ArrayList<MECServerBean>();//ȫ��ΨһMECServer����
	public CloudServerBean cloudServer = new CloudServerBean();//ȫ��Ψһ�Ʒ�����
	private int employedMECCount=0;

	int index1=0;
	
	
	public  Element  XMLinit(String xmlPath) throws DocumentException {
		
		SAXReader saxReader=new SAXReader();
		
		Document document =saxReader.read(xmlPath);
		
		return document.getRootElement();//��ȡroot
		
		
	}
	
	
	
	
	//��ʼ��
	private XMLReader() {
		try {
			InitMECServer();
			
			Element root=XMLinit(StaticParam.APPLICATIONS_XMLPATH);
			
			Element App;
			
			Iterator<Element> iterator = root.elementIterator("APP");
			/*main��������ʱ���ȳ�ʼ��ʵ������������������Ա㴴�����߱���*/
			while (iterator.hasNext()) {
				App = (Element) iterator.next();
				TotalDevicesCount++ ;
				ArrayList<Element> itemList=(ArrayList<Element>) App.elements("item");
				if (!itemList.isEmpty()) {
					TotalRequestAmount += itemList.size();
				}
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//ָ���������,����ģʽ���´���ʱ�޷�ɾ��. �������¸�ֵ
	private void ClearList() {
		SolutionCount=0;
		TotalRequestAmount=0;
		TotalDevicesCount=0;
		taskList.clear();
		for (MECServerBean mecServerBean : MECList) {
			mecServerBean.initial();
		}
		cloudServer.getRequestList().clear();
	}
	
	//MOMCģʽ��ʼ��,����NSGA3�ľ��߱��� �������������λ��
	public void InitlTask(DoubleSolution solution, ArrayList<Integer> otherSolutionList,METHOD_TYPE methodType) {
		Element root;
		try {
			
			ClearList();
			
			root = XMLinit(StaticParam.APPLICATIONS_XMLPATH);
			Element App;
			
			Iterator<Element> iterator = root.elementIterator("APP");
//			/*����Item��ʱ�ô˸�ʽȡ�� XX-XX-XX-XX-XX ���ֱ���ٸ���������*/
			while (iterator.hasNext()) {
				App = (Element) iterator.next();
				
				ArrayList<Element> itemList=(ArrayList<Element>) App.elements("item");
				if (!itemList.isEmpty()) {
					
					ArrayList<TaskRequestBean> requestList = new ArrayList<TaskRequestBean>();
					TaskBean taskBean=new TaskBean();
					float localFrequecy= Float.parseFloat(App.elementText("frequency"));
					int Cw= Integer.parseInt(App.elementText("cw"));
					
					for (Element item : itemList) {
						String temp=item.getText();
						String priority = item.attributeValue("priority");
//						System.out.println(priority);
						//System.out.println(temp);
						if (!temp.isEmpty()) {
							
							//MUMACO�� BenchMark��FCFS 3��ʵ�鷽�����в���
							int tempSolutionIndex=0;
							switch (methodType) {
							case MUMACO:
								 tempSolutionIndex=(int) Math.rint(solution.getVariableValue(SolutionCount));
								break;
							case BENCHMARK:
							case FCFS:
								tempSolutionIndex = otherSolutionList.get(SolutionCount);
								break;
							case AFTER_MUMACO:
								tempSolutionIndex = otherSolutionList.get(SolutionCount);
								break;
							default:
								tempSolutionIndex = 0;
								break;
							}
							
								
								/* �˴��������У��ؼ����裩*/
								TaskRequestBean requestBean=new TaskRequestBean(Integer.parseInt(temp)
																				,tempSolutionIndex, Integer.parseInt(priority));
//								//��������䵽��ͬ�ķ�������
								if (tempSolutionIndex==0) {
									
									requestBean.setParentTaskIndex(taskList.size());
									requestBean.setIndex(requestList.size());
									requestList.add(requestBean);
								}else if (0<tempSolutionIndex && tempSolutionIndex<StaticParam.ServerAmount+1) {
									
									requestBean.setParentMECServerIndex(tempSolutionIndex-1);
									requestBean.setIndex(MECList.get(tempSolutionIndex-1).getRequestAmount());
									MECList.get(tempSolutionIndex-1).getRequestLists().add(requestBean);
								}else {
									
									requestBean.setParentCloudServer(cloudServer);
									//System.out.println("error : "+a);
									requestBean.setIndex(cloudServer.getRequestList().size());
									cloudServer.getRequestList().add(requestBean);
									
								}
								
								SolutionCount++;
								
								switch (methodType) {
								case MUMACO:
									if (SolutionCount==solution.getNumberOfVariables()) {
										SolutionCount = 0 ;
									}
									break;
								case BENCHMARK:
								case FCFS:
									if (SolutionCount == otherSolutionList.size()) {
										SolutionCount = 0 ;
									}
									
									break;
								case AFTER_MUMACO:
									if (SolutionCount == otherSolutionList.size()) {
										SolutionCount = 0 ;
									}
									break;
								default:
									SolutionCount = 0;
									break;
								}
								
							}
							
						}
					
						//�ֽ��ÿһ��RequestList����ÿ��Task��
						if (!requestList.isEmpty()) {
							taskBean.setCw(Cw);
							taskBean.setFrequency(localFrequecy);
							taskBean.setRequestList(requestList);
							taskList.add(taskBean);
						}
						
						
					}
					
				}

			
			//	��������������Ŷ�
			{
				for (TaskBean taskBean : taskList) {
					if (!taskBean.getRequestList().isEmpty()) {

						boolean isMin=CalcuPreTask(taskBean.getRequestList(), taskBean.getCw(),taskBean.getFrequency());
//						boolean isMin=CalcuPrePrioTask(taskBean.getRequestList(), taskBean.getCw(),taskBean.getFrequency());

					}
				}

			}
			//MEC���Ŷӷ���
			for (MECServerBean mecServerBean : MECList) {
				if (!mecServerBean.getRequestLists().isEmpty()) {
					
				boolean isMin =CalcuPreTask(mecServerBean.getRequestLists()
											, mecServerBean.getCw()
											, mecServerBean.getFrequency());
//					boolean isMin =CalcuPrePrioTask(mecServerBean.getRequestLists()
//							, mecServerBean.getCw()
//							, mecServerBean.getFrequency());
				}
			}
			
			//�ƶ��Ŷӷ���
			if (!cloudServer.getRequestList().isEmpty()) {
				
				boolean isMin = CalcuPreTask(cloudServer.getRequestList()
											, cloudServer.getCw()
											, cloudServer.getFrequency());
//				boolean isMin = CalcuPrePrioTask(cloudServer.getRequestList()
//						, cloudServer.getCw()
//						, cloudServer.getFrequency());
			}
			
			employedMECAmount();
//			System.out.println("index : "+index1);
			index1++;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		
	
	}
	
	//��ʼ���Ѳ����MEC����
	public void employedMECAmount() {
		
			employedMECCount=0;//��0
			
			for (MECServerBean mecServerBean : MECList) {
				if (!mecServerBean.getRequestLists().isEmpty()) {
					employedMECCount ++;
				}
			}
		
	}
	
	//��ʼ��MECServer����
	private void InitMECServer() throws DocumentException {

			int mecCount = 0 ;
			Element root = XMLinit(StaticParam.MECSERVERS_XMLPATH);
			
			Iterator<Element> iterator = root.elementIterator("MEC");
			while (iterator.hasNext()) {
				Element mecItem = (Element) iterator.next();
				
				MECServerBean mecServer= new MECServerBean(
											Integer.parseInt(mecItem.elementText("cw"))
											,Float.parseFloat(mecItem.elementText("frequency")));
				MECList.add(mecServer);
				mecCount++;
			}
			
			StaticParam.ServerAmount=mecCount;
	}
	
	//����ģʽ��ȫ��ΨһӦ�ö���
	public static XMLReader getXmlReader() {
		if (xmlReader==null) {
			
			xmlReader=new XMLReader();
		}
		return xmlReader;
	}

	//���¹���һ������,
	public static boolean RenewXmlReader() {
		
			 xmlReader=new XMLReader();
		
			 if (xmlReader!=null) {
				return true;
			}else {
				return false;
			}
			 
	}

	
	//ͨ��Cw������,����Ƶ�ʣ�������м���FCFSģʽ
	public boolean CalcuPreTask(ArrayList<TaskRequestBean> requestList,int Cw,float frequency) {
//		for (TaskRequestBean bean:requestList) {
//			System.out.println("bean:"+bean);
//		}

		if (requestList.size()<=Cw) {
			return false;
		}else {
			ArrayList<WaitLatencyBean> waitList = new ArrayList<WaitLatencyBean>();
			
			//��ʼ����������
			for (int i = 0; i < Cw; i++) {
				WaitLatencyBean waitBean = new WaitLatencyBean();
				waitBean.getRequestList().add(requestList.get(i));//��ʼ��������ֱ���ӵ�ÿһ��VM��
				waitBean.setIndex(i);
				waitList.add(waitBean);
			}

//			// todo: ������н������ȼ�����
//			for (int i=0; i<requestList.size()-1; i++) {
//				for (int j=0; j<requestList.size()-i-1; j++) {
//					if (requestList.get(j).getPriority()<requestList.get(j+1).getPriority()){
//						TaskRequestBean tempBean = requestList.get(j+1);
//						requestList.set(j+1, requestList.get(j));
//						requestList.set(j, tempBean);
//					}
//				}
//			}
//			Sort.quickSort(requestList, 0, requestList.size()-1);
//			Collections.sort(requestList, (o1, o2) -> o2.getPriority()-o1.getPriority());

			//FCFS�ؼ����裬��һ��WaitListȥģ��FCFS�Ŷ������Ȼ��ǰ�����������ע��TaskRequestʵ���У�ʵ���Ŷ�
			for (int i = Cw; i < requestList.size(); i++) {
				int minIndex = MinWaitListIndex(waitList, frequency);//���㵱ǰ�����������WaitList����
				
				ArrayList<TaskRequestBean> preRequestList = waitList.get(minIndex).getRequestList();
				if (!preRequestList.isEmpty()) {
					//todo: ������н������ȼ�����
//					int a = findMinPrioRequest(preRequestList).getIndex();
					int a =preRequestList.get(preRequestList.size()-1).getIndex();
					//System.out.println("XML PreTask : "+a);
					requestList.get(i).setPreTaskIndex(a);//���ǰ����������
					
					waitList.get(minIndex).getRequestList().add(requestList.get(i));//�ŶӶ������������
				}
			}
			
			
			return true;
		}
	}

	private static TaskRequestBean findMinPrioRequest(List<TaskRequestBean> taskRequestBeans) {
		int minPrio = 65535;
		TaskRequestBean minPrioBean = new TaskRequestBean();
		for (TaskRequestBean bean:taskRequestBeans) {
			if (bean.getPriority()<minPrio) {
				minPrio = bean.getPriority();
				minPrioBean = bean;
			}
		}
		return minPrioBean;
	}


	//��ȫ����������ڱ���ʱ��ʵ���������
	public ArrayList<TaskBean> LOCAL_Method_Init() throws DocumentException {
		Element root =XMLinit(StaticParam.APPLICATIONS_XMLPATH);
		
		Element App;
		
		Iterator<Element> iterator = root.elementIterator("APP");
		/*����Item��ʱ�ô˸�ʽȡ�� XX-XX-XX-XX-XX ���ֱ���ٸ���������*/
		while (iterator.hasNext()) {
			App = (Element) iterator.next();
			float localFrequecy= Float.parseFloat(App.elementText("frequency"));
			int Cw= Integer.parseInt(App.elementText("cw"));
			
			ArrayList<Element> itemList=(ArrayList<Element>) App.elements("item");
			if (!itemList.isEmpty()) {
				
				ArrayList<TaskRequestBean> requestList = new ArrayList<TaskRequestBean>();
				TaskBean taskBean=new TaskBean();
				
				for (Element item : itemList) {
					String temp=item.getText();
					
					if (!temp.isEmpty()) {
						TaskRequestBean requestBean=new TaskRequestBean(Integer.parseInt(temp), 0);
						requestBean.setMethodType(Method.LOCAL_METHOD);//����Ǩ�Ʋ���ģʽ

						requestBean.setParentTaskIndex(taskList.size());
						requestBean.setIndex(requestList.size());
						requestList.add(requestBean);
						
					}
				}
				taskBean.setCw(Cw);
				taskBean.setFrequency(localFrequecy);
				taskBean.setRequestList(requestList);
				boolean isMin=XMLReader.getXmlReader().CalcuPreTask(requestList, taskBean.getCw(),taskBean.getFrequency());

				taskList.add(taskBean);
			}
			
			
		}
		return taskList;
	}
	
	
	public CloudServerBean CLOUD_Method_Init() throws DocumentException {
		Element root =XMLinit(StaticParam.APPLICATIONS_XMLPATH);
		
		Element App;
		
		Iterator<Element> iterator = root.elementIterator("APP");
		/*����Item��ʱ�ô˸�ʽȡ�� XX-XX-XX-XX-XX ���ֱ���ٸ���������*/
		while (iterator.hasNext()) {
			App = (Element) iterator.next();
			
			ArrayList<Element> itemList=(ArrayList<Element>) App.elements("item");
			if (!itemList.isEmpty()) {
				
				ArrayList<TaskRequestBean> requestList = new ArrayList<TaskRequestBean>();
				TaskBean taskBean=new TaskBean();
				
				for (Element item : itemList) {
					String temp=item.getText();
					
					if (!temp.isEmpty()) {
						TaskRequestBean requestBean=new TaskRequestBean(Integer.parseInt(temp), StaticParam.ServerAmount+1);
						requestBean.setMethodType(Method.CLOUD_METHOD);//����Ǩ�Ʋ���ģʽ

						requestBean.setParentCloudServer(cloudServer);
						//System.out.println("error : "+a);
						requestBean.setIndex(cloudServer.getRequestList().size());
						cloudServer.getRequestList().add(requestBean);

						
					}
				}
			}
			boolean isMin=XMLReader.getXmlReader().CalcuPreTask(cloudServer.getRequestList(),cloudServer.getCw(),cloudServer.getFrequency());
			
			
		}
		return cloudServer;
		
	}
	
	
	//�����������������ɵ���������
	private int MinWaitListIndex(ArrayList<WaitLatencyBean> waitLIst ,float frequency) {
		
		int index=-1;
		float latency=Float.POSITIVE_INFINITY;
		for (int i=0; i<waitLIst.size();i++) {
			WaitLatencyBean waitLatencyBean = waitLIst.get(i);
			ArrayList<TaskRequestBean> waitRequestList= waitLatencyBean.getRequestList();
			if (!waitRequestList.isEmpty()) {
				
			int tempLatency=0;
			for (TaskRequestBean taskRequestBean : waitRequestList) {
				tempLatency += taskRequestBean.getWeight()/frequency;
			}
			
			if (tempLatency<latency) {
				index = i;
			 }
		}
	}
		
		return index;
	}
	
	//������Դ�����ʸ��ؾ���
			public static double TotalAverageUtilization() {
				
				
				
				ArrayList<MECServerBean> MECServerList = XMLReader.getXmlReader().getMECList();
				ArrayList<Double> utiList = new ArrayList<Double>();
				double TotalUtilization = 0.0f;
				for (MECServerBean mecServerBean : MECServerList) {
					 
					double utilization = (double)mecServerBean.getRequestAmount()/(double)mecServerBean.getCw();
						if (utilization>1.0f) {
							utilization=1.0f;
						}
						if (utilization<0.0f) {
							utilization=0.0f;
						}
					TotalUtilization += utilization;
					utiList.add(utilization);
				}
				
				
				double employed=(double)XMLReader.getXmlReader().getEmployedMECCount();
				
				double AverageUtilization=0.0;
				if (employed!=0) {
					 AverageUtilization = TotalUtilization/employed;
//					lbBean.setRU(AverageUtilization);
//					
//					
//					double TempVariance =0.0f;
//					
//					for (Double utilization : utiList) {
//					
//						TempVariance += Math.pow(utilization-AverageUtilization,2);
//					}
//					
//					lbBean.setLB( TempVariance/XMLReader.getXmlReader().getEmployedMECCount()); ;
				
					
				}else {
					 AverageUtilization=0.0;
					
				}
				
			return AverageUtilization;

			}

			
			public static Map<String, Double> TotalLatencyAndEnergy(METHOD_TYPE type) {
				
				double TotalLatencyObject = 0;//��ʱ��
				double TotalEnergyObject = 0;//���ܺ�
				double TotalTwait = 0;
				double highPrioLatency = 0;
				
				Map<String, Double> map=new HashMap<String, Double>();
				
			
				switch (type) {
				
//				{//����MEC�ϵ���ʱ�Ӻ����ܺ�
//					ArrayList<MECServerBean> taskList= XMLReader.getXmlReader().getMECList();
//					for (MECServerBean taskBean : taskList) {
//						ArrayList<TaskRequestBean> taskRequestList=taskBean.getRequestLists();
//						
//						for (TaskRequestBean taskRequestBean : taskRequestList) {
//							Latency latency = new Latency(taskRequestBean);
//							TotalLatencyObject +=latency.TimeTotal();
//							
//							Energy energy = new Energy(latency);
//							TotalEnergyObject +=energy.EnergyTotal();
//							
//							
//						}
//					}
//					
//				}
//					break;
				case FCFS:
				case BENCHMARK:
				case MUMACO:
					
				{//���㱾�ص���ʱ�Ӻ����ܺ�
					ArrayList<TaskBean> taskList= XMLReader.getXmlReader().getTaskList();
					for (TaskBean taskBean : taskList) {
						ArrayList<TaskRequestBean> taskRequestList=taskBean.getRequestList();
						
						for (TaskRequestBean taskRequestBean : taskRequestList) {
							Latency latency = new Latency(taskRequestBean);
							TotalLatencyObject +=latency.TimeTotal();
							TotalTwait += latency.getTwait();
							Energy energy = new Energy(latency);
							TotalEnergyObject +=energy.EnergyTotal();
							if (taskRequestBean.getPriority() >= 3) {
								highPrioLatency += latency.TimeTotal();
							}
						}
					}
					
					
					}
					
					{//����MEC�ϵ���ʱ�Ӻ����ܺ�
						ArrayList<MECServerBean> taskList= XMLReader.getXmlReader().getMECList();
						for (MECServerBean taskBean : taskList) {
							ArrayList<TaskRequestBean> taskRequestList=taskBean.getRequestLists();
							
							for (TaskRequestBean taskRequestBean : taskRequestList) {
								Latency latency = new Latency(taskRequestBean);
								TotalLatencyObject +=latency.TimeTotal();
								TotalTwait += latency.getTwait();
								Energy energy = new Energy(latency);
								TotalEnergyObject +=energy.EnergyTotal();

								if (taskRequestBean.getPriority() >= 3) {
									highPrioLatency += latency.TimeTotal();
								}
							}
						}
						
						
					}
					
					{//����Cloud�ϵ���ʱ�Ӻ����ܺ�
						ArrayList<TaskRequestBean> taskList= XMLReader.getXmlReader().getCloudServer().getRequestList();
							
							for (TaskRequestBean taskRequestBean : taskList) {
								Latency latency = new Latency(taskRequestBean);
								TotalLatencyObject +=latency.TimeTotal();
								
								Energy energy = new Energy(latency);
								TotalEnergyObject +=energy.EnergyTotal();

								if (taskRequestBean.getPriority() >= 3) {
									highPrioLatency += latency.TimeTotal();
								}
						}
					}
					break;
					default:
						break;
				}

//				System.out.println("������latency: "+highPrioLatency);
				map.put("latency", TotalLatencyObject);
				map.put("energy", TotalEnergyObject);
				return map;
				
			}


	public int getTotalRequestAmount() {
		return TotalRequestAmount;
	}




	public void setTotalRequestAmount(int totalRequestAmount) {
		TotalRequestAmount = totalRequestAmount;
	}
	
	
	
	public CloudServerBean getCloudServer() {
		return cloudServer;
	}




	public void setCloudServer(CloudServerBean cloudServer) {
		this.cloudServer = cloudServer;
	}
	
	public ArrayList<MECServerBean> getMECList() {
		return MECList;
	}




	public void setMECList(ArrayList<MECServerBean> mECList) {
		MECList = mECList;
	}


	public ArrayList<TaskBean> getTaskList() {
		return taskList;
	}




	public void setTaskList(ArrayList<TaskBean> taskList) {
		this.taskList = taskList;
	}




	public int getEmployedMECCount() {
		return employedMECCount;
	}




	public void setEmployedMECCount(int employedMECAmount) {
		this.employedMECCount = employedMECAmount;
	}



	

	public int getTotalDevicesCount() {
		return TotalDevicesCount;
	}




	public void setTotalDevicesCount(int totalDevicesCount) {
		TotalDevicesCount = totalDevicesCount;
	}
	
	public int getTotalCloudCount() {
		return TotalCloudCount;
	}




	public void setTotalCloudCount(int totalCloudCount) {
		TotalCloudCount = totalCloudCount;
	}



}
