package mathInterface;



public class ChildPerformanceMonitor {


	private static final double BASELINE = .7;

	private static ChildPerformanceMonitor instance;

	GenerationPerformanceData[] intergenerationalPerformanceData=new GenerationPerformanceData[ProcessingApplication.NUM_GENERATIONS];
	int index_of_current_generation=-1;
	private int index_of_baseline_generation=0;

	//array that is used to record the summary scores for each individual of the current population.
	//needs to be refreshed each time we changed the population.
	double[] summaryScoresForCurrentPopulation=new double[ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER_PER_GENERATION];
	int index_of_current_individual=-1;


	private ChildPerformanceMonitor(){}

	public static ChildPerformanceMonitor getInstance(){
		if(instance==null) instance=new ChildPerformanceMonitor();
		return instance;
	}

	public void prepareForNextIndividual(){
		index_of_current_individual++;
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from child performance monitor: ");
			System.out.println("preparing for next individual: ");
			System.out.println("index of current individual is: "+index_of_current_individual);
		}
	}




	public void prepareForNextGeneration(){
		index_of_current_generation++;
		summaryScoresForCurrentPopulation=new double[ProcessingApplication.NUM_INDIVIDUALS_TO_SHOW_USER_PER_GENERATION];
		index_of_current_individual=-1;
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from child performance monitor: ");
			System.out.println("preparing for next generation: ");
			System.out.println("index of current generation is: "+index_of_current_generation);}
	}

	public void recordSummaryScoreForCurrentIndividual(int[] resultsForCurrentIndividual){

		double score=summarize(resultsForCurrentIndividual);
		summaryScoresForCurrentPopulation[index_of_current_individual]=score;
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from child performance monitor: ");

			System.out.println("recording summary score for current individual (index): "+index_of_current_individual);
			System.out.println("summary score for current individual is: "+score);
		}
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
	

			IntergenerationalPerformanceTrend result= IntergenerationalPerformanceTrend.calculateTrend(intergenerationalPerformanceData,index_of_baseline_generation,index_of_current_generation);
			calculateAverageScoreAboveBaseline(intergenerationalPerformanceData,index_of_baseline_generation,index_of_current_generation);
			
			index_of_baseline_generation=index_of_current_generation;
			
			return result;
		}



	static double avgscore_for_gen;
		private void calculateAverageScoreAboveBaseline(
			GenerationPerformanceData[] intergenerationalPerformanceData,
			int index_of_baseline_generation, int index_of_current_generation) {
		
		avgscore_for_gen=(intergenerationalPerformanceData[index_of_baseline_generation].average()+intergenerationalPerformanceData[index_of_current_generation].average())/2;
        
		
		}
		
		public static boolean averageIntergenerationalScoreAboveBaseline(){
			return avgscore_for_gen>BASELINE;
		}

		private double summarize(int[] resultsForIndividual) {
			double score=0;
			if(ProcessingApplication.PRINT_DEBUG_MESSAGES){System.out.println("message from child performance monitor: ");
			System.out.println("the values for each of the individual records: ");}
			for(int i=0;i<resultsForIndividual.length;i++){
				double result_for_problem=resultsForIndividual[i];
				score+=result_for_problem;
				if(ProcessingApplication.PRINT_DEBUG_MESSAGES) System.out.println("problem# "+i+": "+result_for_problem);
			}
			double result=score/(double)resultsForIndividual.length;

			return result;
		}
	}
