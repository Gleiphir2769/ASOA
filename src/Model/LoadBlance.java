package Model;

import Bean.MECServerBean;

/* 负载均衡模型*/

public class LoadBlance {

	private MECServerBean MECServer;
	private double ResourceUtility;
	//
	public LoadBlance(MECServerBean MECServer) {
		this.MECServer=MECServer;
	}
 
	//计算资源利用率
	private double CalcuRU() {
		
		return ResourceUtility = MECServer.getRequestAmount() / MECServer.getCw();
		
	}


}
