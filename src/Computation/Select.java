package Computation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import Bean.UVBean;
import Util.XMLReader;
import Util.StaticParam.METHOD_TYPE;

public class Select {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		double x=
				4.75E-04

;
		System.out.println((0.06-x)/0.06);
		

	}
	
	

	public static UVBean get_Value(ArrayList<Double> app_1, ArrayList<Double> app_2, ArrayList<Double> app_3) {
		
		//w1+w2+w3 = 1
		float weight1 =  (float) (33.3/100);
		float weight2 =  (float) (33.3/100);
		float weight3 =  (float) (33.3/100);
		
		UVBean uv=new UVBean();
		
		
		ArrayList<Double> V = new ArrayList<>();
		double app_1_Max = Collections.max(app_1);
		double app_1_Min = Collections.min(app_1);
		double Mm_app_1 = app_1_Max - app_1_Min;
		double app_2_Max = Collections.max(app_2);
		double app_2_Min = Collections.min(app_2);
		double Mm_app_2 = app_2_Max - app_2_Min;
		System.out.println("lantency max : "+app_2_Max + "min : "+app_2_Min );
		double app_3_Max = Collections.max(app_3);
		double app_3_Min = Collections.min(app_3);
		double Mm_app_3 = app_3_Max - app_3_Min;
//		System.out.println("energy max : "+app_3_Max + "min : "+app_3_Min );
		
		for (int i = 0; i < app_1.size(); i++) {
			double v1 = 0.0;
			double v2 = 0.0;
			double v3 = 0.0;
			if(Mm_app_1 != 0) {
				v1 = weight1*(app_1_Max - app_1.get(i))/Mm_app_1;
			}else {
				v1 = weight1*1;
			}
//			System.out.println(" variance value : "+v1);

			if(Mm_app_2 != 0) {
				v2 = weight2*(app_2_Max - app_2.get(i))/Mm_app_2;
			}else {
				v2 = weight2*1;
			}
//			System.out.println(" latency value : "+v2);
			if(Mm_app_3 != 0) {
				v3 = weight3*(app_3_Max - app_3.get(i))/Mm_app_3;
			}else {
				v3 = weight3*1;
			}
//			System.out.println(" energy value : "+v3);

			double v = v1 + v2 + v3;
			System.out.println("index: "+i+" uv: "+v);
			V.add(v);
		}
		
		
		int i = 0,a=-1;
		for (Double d : V) {
			if(d == Collections.max(V)) {
				uv.setIndex(i);
				uv.setUv(V.get(i));
				System.out.println("MAXindex"+i);
//				System.out.println("uv"+V.get(i));
			}else {
				i++;
			}
		}	
		return uv;
	}
	
	//计算已部署的ECNs
	public static int CountEmployedECNs(ArrayList<Integer> intList,METHOD_TYPE type)
	{
		Set<Integer> set=new TreeSet<Integer>();
		
		for (int i = 0; i < intList.size(); i++) {
			
			switch (type) {
			case AFTER_MUMACO:
			case MUMACO:
				if (intList.get(i)!=0 && intList.get(i)!=XMLReader.getXmlReader().getMECList().size()+1) {
					set.add(intList.get(i));
				}
				break;

			case BENCHMARK:
			case FCFS:
				set.add(intList.get(i));
				break;
			default:
				break;
			}
			
		}
		
		return set.size();
		
		
	}

}
