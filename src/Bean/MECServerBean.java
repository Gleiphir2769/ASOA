package Bean;

import java.util.ArrayList;

import Util.StaticParam;

public class MECServerBean {

	private int Cw=StaticParam.MECServerCw;//可初始化的VMs容量
	private int requestAmount;//Server上任务请求的数量
	private ArrayList<TaskRequestBean> requestLists=new ArrayList<TaskRequestBean>(); //任务请求队列
	private boolean isMax=false;
	private float frequency=StaticParam.fm;//工作频率

	
	public void initial() {
		
		isMax=false;
		requestAmount=0;
		requestLists.clear();
	}
	
	
	public MECServerBean(int Cw,float frequency) {
		this.Cw=Cw;
		this.frequency = frequency;
	}
	public boolean isFilled() {
		if (requestLists.size()==Cw) {
			return true;
		}else if (requestLists.size()>Cw) {
			return true;
		}
		
		return false;
	}
	
	public int getCw() {
		return Cw;
	}

	public void setCw(int cw) {
		Cw = cw;
	}

	public ArrayList<TaskRequestBean> getRequestLists() {
		return requestLists;
	}

	public void setRequestLists(ArrayList<TaskRequestBean> requestLists) {
		this.requestLists = requestLists;
	}

	public int getRequestAmount() {
		return requestLists.size();
	}

	public void setRequestAmount(int requestAmount) {
		this.requestAmount = requestAmount;
	}


	public float getFrequency() {
		return frequency;
	}


	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}
	
	
}
