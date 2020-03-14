package Bean;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import Util.StaticParam;

//任务请求实例
public class TaskRequestBean {

	
	public enum NetType{NONE,LOCAL,MEC,CLOUD}//枚举
	public enum Method{LOCAL_METHOD,MOMC_METHOD,CLOUD_METHOD}//判断此次之行实验数据的类型
	
	private double Weight;//任务请求大小
	private int Location;//任务请求分配位置
	private NetType netType=NetType.NONE;//
	private Method methodType=Method.MOMC_METHOD;
	private int Index=-1;//在Server队列中的索引
	private int parentMECServerIndex;//任务请求所绑定的MECServer
	private CloudServerBean parentCloudServer;//
	private int parentTaskIndex;//
	private int  preTaskIndex = -1;
	// 任务优先级
	private int priority = -1;
	
	public TaskRequestBean() {
		// TODO Auto-generated constructor stub
	}
	//构造函数
	public TaskRequestBean(int Weight,int Location) {
		this.Weight = Weight;
		this.Location = Location;
		JudgeType();
	}

	public TaskRequestBean(int Weight,int Location, int priority) {
		this.Weight = Weight;
		this.Location = Location;
		this.priority = priority;
		JudgeType();
	}

	
	//判断迁移策略类型，
	public void JudgeType() {
		if (Location==0){
			setNetType(NetType.LOCAL);
			
		}else if (0<Location && Location<(StaticParam.ServerAmount+1)) {
			setNetType(NetType.MEC);

		}else if (Location==(StaticParam.ServerAmount+1)) {		
			setNetType(NetType.CLOUD);

		}else {
			setNetType(NetType.NONE);

		}

	}
	
	
	public double getWeight() {
		return Weight;
	}

	public void setWeight(double Weight) {
		this.Weight = Weight;
	}

	public int getLocation() {
		return Location;
	}

	public void setLocation(int location) {
		Location = location;
	}


	public NetType getNetType() {
		return netType;
	}


	public void setNetType(NetType netType) {
		this.netType = netType;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int inMECindex) {
		this.Index = inMECindex;
	}


	public CloudServerBean getParentCloudServer() {
		return parentCloudServer;
	}

	public void setParentCloudServer(CloudServerBean parentCloudServer) {
		this.parentCloudServer = parentCloudServer;
	}

	public int getParentMECServerIndex() {
		return parentMECServerIndex;
	}

	public void setParentMECServerIndex(int parentMECServerIndex) {
		this.parentMECServerIndex = parentMECServerIndex;
	}

	public int getParentTaskIndex() {
		return parentTaskIndex;
	}

	public void setParentTaskIndex(int parentTaskIndex) {
		this.parentTaskIndex = parentTaskIndex;
	}
	public int getPreTaskIndex() {
		return preTaskIndex;
	}
	public void setPreTaskIndex(int preTaskIndex) {
		this.preTaskIndex = preTaskIndex;
	}
	public Method getMethodType() {
		return methodType;
	}
	public void setMethodType(Method methodType) {
		this.methodType = methodType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "TaskRequestBean{" +
				"Weight=" + Weight +
				", priority=" + priority +
				'}';
	}
}
