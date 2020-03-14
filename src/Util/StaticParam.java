package Util;

//全局静态参数
public class StaticParam {

	public static int ServerAmount=3;//MECserver数量
	public static int LocalServerCw=2;//本地server的Cw容量
	public static int MECServerCw=8;//MECserver的Cw容量
	public static int CloudServerCw=50;//Cloudserver的Cw容量
	
	public static double Allocate_Local_Rate = 0.0;
	public static double Allocate_Cloudl_Rate = 0.15;

	
	
//	public static String APPLICATIONS_XMLPATH="src/Applications_5_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_15_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_20_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_25_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_30_10.xml";
	public static String APPLICATIONS_XMLPATH="src/Applications_35_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_40_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_45_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_50_10.xml";

//	public static String APPLICATIONS_XMLPATH="src/Applications_5_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_15_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_20_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_25_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_30_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_35_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_40_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_45_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_50_20.xml";

//	public static String APPLICATIONS_XMLPATH="src/Applications_10_5.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_15.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_25.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_30.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_35.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_40.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_45.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_10_50.xml";

//	public static String APPLICATIONS_XMLPATH="src/Applications_2_10.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_20.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_30.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_40.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_50.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_100.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_150.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_200.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_250.xml";
//	public static String APPLICATIONS_XMLPATH="src/Applications_2_300.xml";

//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_4.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_6.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_8.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_10.xml";
	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_12.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_14.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_16.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_18.xml";
//	public static String MECSERVERS_XMLPATH = "src/MECServers_diff_20.xml";
//    public static String MECSERVERS_XMLPATH = "src/MECServers_diff_15.xml";




	//工作频率(暂定在这里，方便以后用XML读取不同mec的频率)
	public static float fl=100.0f;
	public static float fm=1000.0f;
	public static float fc=6000.0f;
	
	public enum METHOD_TYPE{MUMACO,BENCHMARK,FCFS,AFTER_MUMACO};


}
