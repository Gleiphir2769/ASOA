package Model;

import Bean.TaskRequestBean;
import Util.XMLReader;

public class Latency {

	public TaskRequestBean Request;
	private double Infinite=Double.POSITIVE_INFINITY;
	private  double Twait=0;//����ȴ�ʱ��(Ŀǰ�趨ĳֵ)	
	private  double Bm = 400.0f,Bc = 300.0f;//kb/s����
	private  double Llan = 0.5/1000 ,Lwan = 2/1000;//wan��lan���ӳ�
	public double Texec,Ttrans,Ttotal;//ʱ�Ӳ���
	public boolean isCalcu=false;//�Ƿ��Ѽ����
	
	//���촫��
	public Latency(TaskRequestBean Request) {
		this.Request=Request;
		
		//��������Ŷ�ʱ��
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
					 Twait += preLatency.TimeTotal();//����ǰ������,��ע�ⲻ�ǵݹ�

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
				Twait += preLatency.TimeTotal();//����ǰ������,��ע�ⲻ�ǵݹ�
				
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
				Twait += preLatency.TimeTotal();//����ǰ������,��ע�ⲻ�ǵݹ�
				
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
	
	//ִ��ʱ��
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
	//����ʱ��
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
	
	//��ʱ��
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
