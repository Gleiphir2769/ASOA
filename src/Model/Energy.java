package Model;

public class Energy {

	private Latency LatencyModel;//�ӳ�ģ��
	private double Pa=1.2,Pi=0.1,Pt=1.2,TempP=0.0f;//����
	private double Eexec=0,Etrans=0,Etotal=0;//�ܺĲ���
	
	public Energy(Latency LatencyModel) {
		
		this.LatencyModel=LatencyModel;
	
	}
	
	//ִ���ܺ�
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
	//�����ܺ�
	private double EnergyTransimission() {
		if (LatencyModel.isCalcu) {
			
			Etrans=LatencyModel.Ttrans*Pt;
		}
		
		return Etrans;
	}
	//���ܺ�
	public double EnergyTotal() 
	{
		return Etotal=EnergyExecution()+EnergyTransimission();		
	}
	
}
