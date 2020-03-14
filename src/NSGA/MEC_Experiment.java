package NSGA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.spi.XmlReader;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;

import Bean.LBBean;
import Bean.MECServerBean;
import Bean.TaskBean;
import Bean.TaskRequestBean;
import Model.Energy;
import Model.Latency;
import Util.StaticParam;
import Util.StaticParam.METHOD_TYPE;
import Util.XMLReader;

public class MEC_Experiment extends AbstractDoubleProblem{

	
	public MEC_Experiment() {
		this(XMLReader.getXmlReader().getTotalRequestAmount());//创建实体类N个决策变量
	}
	
	
	//构造函数
	public MEC_Experiment(Integer IntegerSolution) {
		setNumberOfVariables(IntegerSolution);//N个决策变量  Xn
		setNumberOfObjectives(2);//3目标
		setName("MEC_Experiment");
		
		ArrayList<Double> lowerLimit= new ArrayList<Double>(getNumberOfVariables());
		ArrayList<Double> upperLimit= new ArrayList<Double>(getNumberOfVariables());
		
		//设置每一个决策变量的定义域，{0<=xn<=n+1}
		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(0.0);
			upperLimit.add(Double.valueOf(StaticParam.ServerAmount+1));  
			
//			lowerLimit.add(0.0);
//			upperLimit.add(1.0);
		}
		
		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	//目标函数入口,设置目标函数
	@Override
	public void evaluate(DoubleSolution solution) {
		ArrayList<Integer> emptyList =new ArrayList<Integer>();//空列表，用来分辨3种实验方法
		XMLReader.getXmlReader().InitlTask(solution,emptyList,METHOD_TYPE.MUMACO);//初始化队列参数;
		double TotalLatencyObject = 0;//总时延
		double TotalEnergyObject = 0;//总能耗
		double TotalUtilizationObject = 0;//负载均衡方差 
		
		Map<String, Double> totalMap=new HashMap<String, Double>();
		totalMap=XMLReader.TotalLatencyAndEnergy(METHOD_TYPE.MUMACO);
		TotalLatencyObject = totalMap.get("latency");
		TotalEnergyObject = totalMap.get("energy");
		
		
		 
		
//		LBBean lbBean= new LBBean();
		TotalUtilizationObject=XMLReader.TotalAverageUtilization();
		
//		TotalLoadBalanceObject=lbBean.getLB();
		
		
		System.out.println("TotalUtilization : "+TotalUtilizationObject);
		System.out.println("Latency : "+TotalLatencyObject);
		System.out.println("Energy : "+TotalEnergyObject);
		System.out.println("-------------------------------\r\n");
		
		//设置目标函数
		solution.setObjective(0, -TotalUtilizationObject);
		solution.setObjective(1, TotalLatencyObject);
//		solution.setObjective(2, TotalEnergyObject);
		
		
		  }


//	@Override
//	public void evaluate(DoubleSolution arg0) {
//		// TODO Auto-generated method stub
//		
//	}
	
	

	
	
	


}
