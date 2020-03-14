package Bean;

import java.util.ArrayList;

import Util.StaticParam;

public class MECServerBean {

	private int Cw=StaticParam.MECServerCw;//�ɳ�ʼ����VMs����
	private int requestAmount;//Server���������������
	private ArrayList<TaskRequestBean> requestLists=new ArrayList<TaskRequestBean>(); //�����������
	private boolean isMax=false;
	private float frequency=StaticParam.fm;//����Ƶ��

	
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
