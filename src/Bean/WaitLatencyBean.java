package Bean;

import java.util.ArrayList;

public class WaitLatencyBean {

	private ArrayList<TaskRequestBean> requestList= new ArrayList<TaskRequestBean>();
	private int index=-1;
	
	public WaitLatencyBean() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<TaskRequestBean> getRequestList() {
		return requestList;
	}
	public void setRequestList(ArrayList<TaskRequestBean> requestList) {
		this.requestList = requestList;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
