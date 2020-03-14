package Bean;

import java.util.ArrayList;

import Util.StaticParam;

public class CloudServerBean {

	private ArrayList<TaskRequestBean> requestList = new ArrayList<TaskRequestBean>();
	private int Cw=StaticParam.CloudServerCw;
	private float frequency=StaticParam.fc;//¹¤×÷ÆµÂÊ

	
	
	public CloudServerBean() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean isFilled() {
		if (requestList.size()==Cw) {
			return true;
		}else if (requestList.size()>Cw) {
			return true;
		}
		
		return false;
	}
	
	public ArrayList<TaskRequestBean> getRequestList() {
		return requestList;
	}

	public void setRequestList(ArrayList<TaskRequestBean> requestList) {
		this.requestList = requestList;
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
