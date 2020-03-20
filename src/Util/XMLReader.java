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
	private ArrayList<TaskBean> taskList=new ArrayList<TaskBean>();//全局唯一Task队列
	private int TotalRequestAmount=0;//请求总数
	private Element root;
	public int SolutionCount=0;//决策变量计数
	private int TotalDevicesCount=0;//总移动设备数
	private int TotalCloudCount=1;//云端的总设备数








	




	private ArrayList<MECServerBean> MECList=new ArrayList<MECServerBean>();//全局唯一MECServer队列
	public CloudServerBean cloudServer = new CloudServerBean();//全局唯一云服务器
	private int employedMECCount=0;

	int index1=0;
	
	
	public  Element  XMLinit(String xmlPath) throws DocumentException {
		
		SAXReader saxReader=new SAXReader();
		
		Document document =saxReader.read(xmlPath);
		
		return document.getRootElement();//获取root
		
		
	}
	
	
	
	
	//初始化
	private XMLReader() {
		try {
			InitMECServer();
			
			Element root=XMLinit(StaticParam.APPLICATIONS_XMLPATH);
			
			Element App;
			
			Iterator<Element> iterator = root.elementIterator("APP");
			/*main方法运行时，先初始化实例，获得请求总数，以便创建决策变量*/
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
	
	
	//指针数据清除,因单例模式重新创建时无法删除. 所以重新赋值
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
	
	//MOMC模式初始化,根据NSGA3的决策变量 给任务请求分配位置
	public void InitlTask(DoubleSolution solution, ArrayList<Integer> otherSolutionList,METHOD_TYPE methodType) {
		Element root;
		try {
			
			ClearList();
			
			root = XMLinit(StaticParam.APPLICATIONS_XMLPATH);
			Element App;
			
			Iterator<Element> iterator = root.elementIterator("APP");
//			/*任务Item暂时用此格式取： XX-XX-XX-XX-XX 来分辨多少个任务请求*/
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
							
							//MUMACO， BenchMark，FCFS 3种实验方法进行测试
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
							
								
								/* 此处创建队列（关键步骤）*/
								TaskRequestBean requestBean=new TaskRequestBean(Integer.parseInt(temp)
																				,tempSolutionIndex, Integer.parseInt(priority));
//								//将请求分配到不同的服务器中
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
					
						//分解的每一个RequestList加入每个Task类
						if (!requestList.isEmpty()) {
							taskBean.setCw(Cw);
							taskBean.setFrequency(localFrequecy);
							taskBean.setRequestList(requestList);
							taskList.add(taskBean);
						}
						
						
					}
					
				}

			
			//	添加完任务后进行排队
			{
				for (TaskBean taskBean : taskList) {
					if (!taskBean.getRequestList().isEmpty()) {

						boolean isMin=CalcuPreTask(taskBean.getRequestList(), taskBean.getCw(),taskBean.getFrequency());
//						boolean isMin=CalcuPrePrioTask(taskBean.getRequestList(), taskBean.getCw(),taskBean.getFrequency());

					}
				}

			}
			//MEC端排队分配
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
			
			//云端排队分配
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
	
	//初始化已部署的MEC数量
	public void employedMECAmount() {
		
			employedMECCount=0;//清0
			
			for (MECServerBean mecServerBean : MECList) {
				if (!mecServerBean.getRequestLists().isEmpty()) {
					employedMECCount ++;
				}
			}
		
	}
	
	//初始化MECServer队列
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
	
	//单例模式，全局唯一应用队列
	public static XMLReader getXmlReader() {
		if (xmlReader==null) {
			
			xmlReader=new XMLReader();
		}
		return xmlReader;
	}

	//重新构造一个单例,
	public static boolean RenewXmlReader() {
		
			 xmlReader=new XMLReader();
		
			 if (xmlReader!=null) {
				return true;
			}else {
				return false;
			}
			 
	}

	
	//通过Cw的容量,工作频率，任务队列计算FCFS模式
	public boolean CalcuPreTask(ArrayList<TaskRequestBean> requestList,int Cw,float frequency) {
//		for (TaskRequestBean bean:requestList) {
//			System.out.println("bean:"+bean);
//		}

		if (requestList.size()<=Cw) {
			return false;
		}else {
			ArrayList<WaitLatencyBean> waitList = new ArrayList<WaitLatencyBean>();
			
			//初始化容量队列
			for (int i = 0; i < Cw; i++) {
				WaitLatencyBean waitBean = new WaitLatencyBean();
				waitBean.getRequestList().add(requestList.get(i));//初始化将任务分别添加到每一个VM里
				waitBean.setIndex(i);
				waitList.add(waitBean);
			}

//			// todo: 请求队列进行优先级排序
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

			//FCFS关键步骤，用一个WaitList去模拟FCFS排队情况，然后将前驱任务的索引注入TaskRequest实例中，实现排队
			for (int i = Cw; i < requestList.size(); i++) {
				int minIndex = MinWaitListIndex(waitList, frequency);//计算当前最快完成任务的WaitList索引
				
				ArrayList<TaskRequestBean> preRequestList = waitList.get(minIndex).getRequestList();
				if (!preRequestList.isEmpty()) {
					//todo: 请求队列进行优先级排序
//					int a = findMinPrioRequest(preRequestList).getIndex();
					int a =preRequestList.get(preRequestList.size()-1).getIndex();
					//System.out.println("XML PreTask : "+a);
					requestList.get(i).setPreTaskIndex(a);//添加前驱任务索引
					
					waitList.get(minIndex).getRequestList().add(requestList.get(i));//排队队列添加新任务
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


	//当全部任务分配在本地时的实验调试数据
	public ArrayList<TaskBean> LOCAL_Method_Init() throws DocumentException {
		Element root =XMLinit(StaticParam.APPLICATIONS_XMLPATH);
		
		Element App;
		
		Iterator<Element> iterator = root.elementIterator("APP");
		/*任务Item暂时用此格式取： XX-XX-XX-XX-XX 来分辨多少个任务请求*/
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
						requestBean.setMethodType(Method.LOCAL_METHOD);//设置迁移策略模式

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
		/*任务Item暂时用此格式取： XX-XX-XX-XX-XX 来分辨多少个任务请求*/
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
						requestBean.setMethodType(Method.CLOUD_METHOD);//设置迁移策略模式

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
	
	
	//计算虚拟机中最早完成的任务索引
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
	
	//计算资源利用率负载均衡
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
				
				double TotalLatencyObject = 0;//总时延
				double TotalEnergyObject = 0;//总能耗
				double TotalTwait = 0;
				double highPrioLatency = 0;
				
				Map<String, Double> map=new HashMap<String, Double>();
				
			
				switch (type) {
				
//				{//计算MEC上的总时延和总能耗
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
					
				{//计算本地的总时延和总能耗
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
					
					{//计算MEC上的总时延和总能耗
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
					
					{//计算Cloud上的总时延和总能耗
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

//				System.out.println("高敏感latency: "+highPrioLatency);
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
