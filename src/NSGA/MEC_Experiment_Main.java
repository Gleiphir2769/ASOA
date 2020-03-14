package NSGA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import Bean.LBBean;
import Bean.UVBean;
import Computation.Select;
import Util.StaticParam.METHOD_TYPE;
import Util.XMLReader;

public class MEC_Experiment_Main extends AbstractAlgorithmRunner {

	public static void main(String[] args) {
		
		try {
			
			
			
		    DoubleProblem problem;
		    Algorithm<List<DoubleSolution>> algorithm;
		    MutationOperator<DoubleSolution> mutation;
		    DifferentialEvolutionCrossover crossover;
			
			String referenceParetoFront = "LZ09_F6.pf";  

//			problem = ProblemUtils.loadProblem("NSGA.MEC_Experiment");
			//problem = new MEC_Experiment();//定义优化问题（目标函数）
			

		    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem("NSGA.MEC_Experiment");

		    double cr = 1.0;
		    double f = 0.5;
		    
		    crossover= new DifferentialEvolutionCrossover();
		    crossover.setCr(cr);
		    crossover.setF(f);
		    double mutationProbability = 1.0 / problem.getNumberOfVariables();
		    double mutationDistributionIndex = 20.0;
		    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

		    algorithm =
		        new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
		            .setCrossover(crossover)
		            .setMutation(mutation)
		            .setMaxEvaluations(30000)
		            .setPopulationSize(100)
		            .setResultPopulationSize(100)
		            .setNeighborhoodSelectionProbability(0.9)
		            .setMaximumNumberOfReplacedSolutions(2)
		            .setNeighborSize(20)
		            .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
		            .setDataDirectory("MOEAD_Weights")
		            .build();

		    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		    List<DoubleSolution> population1 = algorithm.getResult();

		    long computingTime1 = algorithmRunner.getComputingTime();

//		    JMetalLogger.logger.info("Total execution time: " + computingTime1 + "ms");

		    printFinalSolutionSet(population1);
		    if (!referenceParetoFront.equals("")) {
		      printQualityIndicators(population1, referenceParetoFront);
		    }
//    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
//    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
//    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  
//			printFinalSolutionSet(population);  
//			if (!referenceParetoFront.equals("")) printQualityIndicators(population, referenceParetoFront);
				
			Copy_FUN();
			Copy_VAR();
//			ReConductExp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void Copy_FUN() {
		final String fromeFile = "FUN.tsv";
		final String toFile = "FUN_F.txt";
		try {
			BufferedReader read = new BufferedReader(new FileReader(new File(fromeFile)));
			FileWriter write = new FileWriter(new File(toFile), true);
			String temp;
			while((temp = read.readLine())!=null){
				write.write(temp);
				write.write('\n');
			}
			write.write('\n');
			write.close();
			read.close();
			//System.out.println("内容已从"+fromeFile+"复制追加到"+toFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}  

    public static void Copy_VAR() {
		final String fromeFile = "VAR.tsv";
		final String toFile = "VAR_F.txt";
		try {
			BufferedReader read = new BufferedReader(new FileReader(new File(fromeFile)));
			FileWriter write = new FileWriter(new File(toFile), true);
			String temp;
			while((temp = read.readLine())!=null){
				write.write(temp);
				write.write('\n');
			}
			write.write('\n');
			write.close();
			read.close();
			//System.out.println("内容已从"+fromeFile+"复制追加到"+toFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}  
    
    private static void ReConductExp() {
    	

    	ArrayList<Double> app_1 = new ArrayList<>();
		ArrayList<Double> app_2 = new ArrayList<>();
		ArrayList<Double> app_3 = new ArrayList<>();
		ArrayList<Integer> LocationList= new ArrayList<Integer>();
		 IntegerSolution solution =null;
		 
		try {
			BufferedReader read1 = new BufferedReader(new FileReader(new File("FUN.tsv")));
			String c;
			while((c = read1.readLine())!=null){
				String[] s = c.split(" ");
				app_1.add(Double.valueOf(s[0]));
				app_2.add(Double.valueOf(s[1]));
				app_3.add(Double.valueOf(s[2]));
			}
			read1.close();
		
			UVBean bean=Select.get_Value(app_1, app_2, app_3);
		
			int lineNumber =0;
			lineNumber=bean.getIndex(); 
			FileReader fileReader=new FileReader(new File("VAR.tsv"));
			LineNumberReader lineReader= new LineNumberReader(fileReader);

			
			if (lineNumber < 0 || lineNumber > getTotalLines(new File("VAR.tsv"))) {  
				System.out.println("不在文件的行数范围之内。");  
				
				
			}  
			//设置指定行数
			String locationString="";
			for (int i = 0; i < lineNumber+1; i++) {
				
				 locationString = lineReader.readLine();  
			}
			
			
	//		locationString="15 10 13 7 12 6 4 7 12 14 6 14 15 3 9 2 19 2 8 18 19 12 16 4 1 13 4 16 15 6 3 2 3 15 15 4 3 2 12 16 12 14 16 2 6 19 2 5 1 19 17 4 12 14 6 11 15 19 6 8 13 8 10 1 16 17 14 16 3 8 1 19 17 12 3 13 20 3 13 2 10 6 17 11 13 10 2 10 2 5 7 9 11 20 17 17 1 3 12 2 17 5 14 10 5 7 15 1 8 16 9 17 10 20 12 14 5 10 14 7 4 19 12 11 2 17 16 20 12 17 3 12 17 9 5 15 11 15 17 16 14 4 19 2 13 8 8 10 1 3 4 20 2 7 11 20 5 17 6 16 8 20 7 15 10 1 16 9 11 1 4 14 3 20 3 5 15 18 15 19 12 17 5 20 17 3 7 10 13 2 13 15 9 15 6 14 16 7 8 12 12 20 20 8 19 7 20 18 4 11 12 8 3 16 8 3 11 7 13 13 17 6 7 15 11 10 10 15 5 4 20 12 4 9 14 10 1 15 20 14 12 6 8 14 1 4 3 9 9 18 11 10 18 3 9 7 14 14 18 3 10 11 10 17 18 11 16 20 14 3 9 11 11 4 5 12 3 13 4 18 20 20 12 13 12 10 2 4 6 18 18 5 9 2 8 3 14 3 8 13 13 15 4 15 13 16 18 20 17 10 4 19 2 2 4 2 10 7 8 12 7 11 3 14 14 8 19 11 4 4 10 18 3 2 13 1 2 6 4 16 18 5 11 18 1 15 3 8 20 10 13 13 13 4 4 10 16 4 2 8 20 10 16 6 7 10 3 17 18 3 12 12 6 17 13 9 3 3 10 20 3 19 18 13 9 18 9 12 8 16 19 2 2 14 20 3 2 1 4 16 5 14 19 17 10 13 5 11 2 11 16 13 3 10 6 10 17 16 7 17 10 14 20 5 6 15 2 2 7 20 10 16 7 8 4 11 10 6 20 4 4 11 5 12 15 2 3 7 10 6 13 9 8 8 14 13 8 1 8 6 19 11 18 19 9 8 12 14 5 9 2 17 11 14 6 1 16 9 11 13 20 5 19 3 17 12 5 14 8 13 8 9 13 18 16 19 5 14 1 1 8 16 19 8 9 4 10 20 1 5 7 10 7 12 17 9 15 11 1 9 6 15 5 4 14 1 11 10 16 7 13 9 10 16 15 15 20 1 17 20 10 16 11 15 16 15 12 16 18 1 13 20 19 18 7 2 2 12 4 19 2 10 8 13 4 13 13 20 11 18 3 2 1 4 3 9 5 11 14 11 19 18 7 17 17 10 15 4 4 3 15 15 20 8 8 6 13 19 10 5";
			 System.out.println("local string : "+ locationString);
			 
			 
		     if (!locationString.isEmpty()) {
		    	 String[] sLocateion = locationString.split(" ");

		    	 for (int i = 0; i < sLocateion.length; i++) {
		    		// solution.setVariableValue(i, Integer.parseInt(sLocateion[i]));
					LocationList.add(Integer.parseInt(sLocateion[i]));
				}
			}
		     
				
				//重新创建单例, 如创建成功才执行
	    		if (XMLReader.RenewXmlReader()) {
					
	    			XMLReader.getXmlReader().InitlTask(null,LocationList,METHOD_TYPE.AFTER_MUMACO);//初始化队列参数;
	    			double TotalLatencyObject = 0;//总时延
	    			double TotalEnergyObject = 0;//总能耗
	    			double TotalUtilizationObject = 0;//负载均衡方差 
	    			double employedECNs=0;
	    			employedECNs=Select.CountEmployedECNs(LocationList,METHOD_TYPE.AFTER_MUMACO);
	    			
	    			Map<String, Double> totalMap=new HashMap<String, Double>();
	    			totalMap=XMLReader.TotalLatencyAndEnergy(METHOD_TYPE.MUMACO);
	    			TotalLatencyObject = totalMap.get("latency");
	    			TotalEnergyObject = totalMap.get("energy");
	    			
	    			TotalUtilizationObject=XMLReader.TotalAverageUtilization();
	    			
	    			
	    			System.out.println("MUMACO EmploedECNs : "+employedECNs);
	    			System.out.println("MUMACO Latency : "+TotalLatencyObject);
	    			System.out.println("MUMACO Energy : "+TotalEnergyObject);
	    			System.out.println("MUMACO LBVariance : "+TotalUtilizationObject);
	    			
	    			System.out.println("-------------------------------\r\n");
				}
					
			
		        
		        lineReader.close();  
		        fileReader.close();  
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    // 文件内容的总行数。   
    static int getTotalLines(File file) throws IOException {  
        FileReader in = new FileReader(file);  
        LineNumberReader reader = new LineNumberReader(in);  
        String s = reader.readLine();  
        int lines = 0;  
        while (s != null) {  
            lines++;  
            s = reader.readLine();  
        }  
        reader.close();  
        in.close();  
        return lines;  
    }  
}
