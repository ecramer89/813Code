package mathInterface;



public class ChildPerformanceMonitor {


	private static ChildPerformanceMonitor instance;

	GenerationPerformanceData[] intergenerationalPerformanceData=new GenerationPerformanceData[ProcessingApplication.NUM_GENERATIONS];
	int index_of_current_generation=-1;

	//array that is used to record the summary scores for each individual of the current population.
	//needs to be refreshed each time we changed the population.
	double[] summaryScoresForCurrentPopulation=new double[ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER];
	int index_of_current_individual=-1;

	private ChildPerformanceMonitor(){}

	public static ChildPerformanceMonitor getInstance(){
		if(instance==null) instance=new ChildPerformanceMonitor();
		return instance;
	}

	public void prepareForNextIndividual(){
		index_of_current_individual++;
		/*System.out.println("message from child performance monitor: ");
		System.out.println("preparing for next individual: ");
		System.out.println("index of current individual is: "+index_of_current_individual);
	*/
	}


	public void prepareForNextGeneration(){
		index_of_current_generation++;
		summaryScoresForCurrentPopulation=new double[ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER];
		index_of_current_individual=-1;
		/*System.out.println("message from child performance monitor: ");
		System.out.println("preparing for next generation: ");
		System.out.println("index of current generation is: "+index_of_current_generation);*/
	}

	public void recordSummaryScoreForCurrentIndividual(int[] resultsForCurrentIndividual){

		double score=summarize(resultsForCurrentIndividual);
		summaryScoresForCurrentPopulation[index_of_current_individual]=score;
		System.out.println("message from child performance monitor: ");
		System.out.println("recording summary score for current individual: ");
		System.out.println("summary score for current individual is: "+score);
	}


	//invoked when it is time to switch to a new generation.
	//calculates and stores the summary data across each individual of the current population.
	public void recordSummaryDataForCurrentPopulation(){
		if(hasStarted()){
			GenerationPerformanceData data=new GenerationPerformanceData(summaryScoresForCurrentPopulation);
			intergenerationalPerformanceData[index_of_current_generation]=data;
		}
	}
	
	private boolean hasStarted(){
		return index_of_current_generation>-1;
	}


	public IntergenerationalPerformanceTrend getCurrentIntergenerationalPerformanceTrend(){
		System.out.println("message from child performance monitor: ");
		System.out.println("you just asked me to calculate the current trend...: ");
		System.out.println("what I have recorded for each individual so far: ");
		for(int i=0;i<intergenerationalPerformanceData.length;i++){
			GenerationPerformanceData d=intergenerationalPerformanceData[i];
			System.out.println("at index "+ i+" I have recorded: "+d);
		}
		return IntergenerationalPerformanceTrend.calculateTrend(intergenerationalPerformanceData,0,index_of_current_generation);
	}




	private double summarize(int[] resultsForIndividual) {
		double score=0;
		System.out.println("message from child performance monitor: ");
		System.out.println("the values for each of the individual records: ");
		for(int i=0;i<resultsForIndividual.length;i++){
			double result_for_problem=resultsForIndividual[i];
			score+=result_for_problem;
			System.out.println("problem# "+i+": "+result_for_problem);
		}
		double result=score/(double)resultsForIndividual.length;

		return result;
	}
}
