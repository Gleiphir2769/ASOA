package Model;

import Bean.MECServerBean;

/* ���ؾ���ģ��*/

public class LoadBlance {

	private MECServerBean MECServer;
	private double ResourceUtility;
	//
	public LoadBlance(MECServerBean MECServer) {
		this.MECServer=MECServer;
	}
 
	//������Դ������
	private double CalcuRU() {
		
		return ResourceUtility = MECServer.getRequestAmount() / MECServer.getCw();
		
	}


}
