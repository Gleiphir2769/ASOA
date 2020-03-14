package Model;

import Bean.TaskRequestBean;
import Util.XMLReader;

public class Latency {

	public TaskRequestBean Request;
	private double Infinite=Double.POSITIVE_INFINITY;
	private  double Twait=0;//任务等待时间(目前设定某值)	
	private  double Bm = 400.0f,Bc = 300.0f;//kb/s带宽
	private  double Llan = 0.5/1000 ,Lwan = 2/1000;//wan和lan的延迟
	public double Texec,Ttrans,Ttotal;//时延参数
	public boolean isCalcu=false;//是否已计算过
	
	//构造传入
	public Latency(TaskRequestBean Request) {
		this.Request=Request;
		
		//计算各自排队时间
		MOMC_InitTwait(Request);

	}
	
	
	private void MOMC_InitTwait(TaskRequestBean Request) {
		Latency preLatency;
		switch (Request.getNetType()) {
		case LOCAL:
				if (Request.getPreTaskIndex()!= -1) {
					 preLatency = new Latency(XMLReader.getXmlReader()
																.getTaskList()
																.get(Request.getParentTaskIndex())
																.getRequestList()
																.get(Request.getPreTaskIndex()));
					 Twait += preLatency.TimeTotal();//迭代前驱过程,请注意不是递归

				}else {
					 Twait = 0;
				}
			break;
		case MEC:
			if (Request.getPreTaskIndex()!= -1) {
				 preLatency = new Latency(XMLReader.getXmlReader()
															.getMECList()
															.get(Request.getParentMECServerIndex())
															.getRequestLists()
															.get(Request.getPreTaskIndex()));
				Twait += preLatency.TimeTotal();//迭代前驱过程,请注意不是递归
				
			}else {
				Twait = 0;
			}
			
			break;
		case CLOUD:
			
			if (Request.getPreTaskIndex()!= -1) {
				 preLatency = new Latency(XMLReader.getXmlReader()
															.getCloudServer()	
															.getRequestList()
															.get(Request.getPreTaskIndex()));
				Twait += preLatency.TimeTotal();//迭代前驱过程,请注意不是递归
				
			}else {
				Twait = 0;
			}
			
			break;
		default:
			Twait=0;
			break;
			
		}
		//System.out.println("Request Index : "+Request.getIndex()+"Twait : "+Twait);

	}
	
	//执行时延
	private double TimeExecution() {
		switch (Request.getNetType()) {
		case LOCAL:
			Texec=Twait+(Request.getWeight()/XMLReader.getXmlReader()
														.getTaskList()
														.get(Request.getParentTaskIndex())
														.getFrequency());
			break;

		case MEC:
			Texec=Twait+(Request.getWeight()/XMLReader.getXmlReader()
														.getMECList()
														.get(Request.getParentMECServerIndex())
														.getFrequency())+Llan;
							
			break;
		case CLOUD:
			Texec=Twait+(Request.getWeight()/XMLReader.getXmlReader()
														.getCloudServer()
														.getFrequency())+Lwan;
			break;
		default:
			Texec=0;
			break;
		}
	  return Texec;
	}
	//传输时延
	private double TimeTransimission() {
		switch (Request.getNetType()) {
		case LOCAL:
			Ttrans=Request.getWeight()/Infinite;
			break;

		case MEC:
			Ttrans=Request.getWeight()/Bm;
			break;
		case CLOUD:
			Ttrans=Request.getWeight()/Bc;
			break;
		default:
			Ttrans=0;
			break;
		}
		return Ttrans;
	 
	}
	
	//总时延
	public double TimeTotal() {
	
		isCalcu=true;
		return Ttotal=TimeExecution()+TimeTransimission();
	}

	public double getTwait() {
		return Twait;
	}

	public void setTwait(double twait) {
		Twait = twait;
	}
}
