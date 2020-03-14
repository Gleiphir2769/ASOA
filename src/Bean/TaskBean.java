package Bean;

import java.util.ArrayList;

import Util.StaticParam;

//应用数实例
public class TaskBean {

	private int Amount;//应用请求数量
	private ArrayList<TaskRequestBean> RequestList;//应用各自的请求队列
	private int Cw=StaticParam.LocalServerCw;
	private float frequency=StaticParam.fl;//工作频率
	
	//初始化
	public void initial() {
		RequestList.clear();
		Amount=0;
	}
	
	public boolean isFilled() {
		if (RequestList.size()==Cw) {
			return true;
		}else if (RequestList.size()>Cw) {
			return true;
		}
		
		return false;
	}
	
	public TaskBean() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<TaskRequestBean> getRequestList() {
		return RequestList;
	}

	public void setRequestList(ArrayList<TaskRequestBean> requestList) {
		RequestList = requestList;
	}

	public int getAmount() {
		return RequestList.size();
	}

	public void setAmount(int amount) {
		Amount = amount;
	}
	
	public int getCw() {
		return Cw;
	}

	public void setCw(int cw) {
		Cw = cw;
	}

	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}
}
