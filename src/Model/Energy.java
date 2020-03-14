package Model;

public class Energy {

	private Latency LatencyModel;//延迟模型
	private double Pa=1.2,Pi=0.1,Pt=1.2,TempP=0.0f;//功率
	private double Eexec=0,Etrans=0,Etotal=0;//能耗参数
	
	public Energy(Latency LatencyModel) {
		
		this.LatencyModel=LatencyModel;
	
	}
	
	//执行能耗
	private double EnergyExecution() {
		if (LatencyModel.isCalcu) {
			switch (LatencyModel.Request.getNetType()) {
			case LOCAL:		
				TempP=Pa;
				break;
			case MEC:
				TempP=Pi;
				break;		
				
			case CLOUD:
				TempP=Pi;
				break;
			default:
				TempP=0;
				break;
			}
			Eexec=LatencyModel.Texec*TempP;
		}
	
		return Eexec;
	}
	//传输能耗
	private double EnergyTransimission() {
		if (LatencyModel.isCalcu) {
			
			Etrans=LatencyModel.Ttrans*Pt;
		}
		
		return Etrans;
	}
	//总能耗
	public double EnergyTotal() 
	{
		return Etotal=EnergyExecution()+EnergyTransimission();		
	}
	
}
