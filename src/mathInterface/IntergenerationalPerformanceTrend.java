package mathInterface;

public enum IntergenerationalPerformanceTrend {
	IMPROVED, WORSENED, STABLE, WORSENED_SEVERELY;

	static int min_improvement=0;
	static int max_decline=0;
	static final double THRESHOLD_DECLINE=ProcessingApplication.THRESHOLD_PERFORMANCE_DECLINE;


	public static void setMinImprovementAndMaxDecline(int min_improvement, int max_decline){
		IntergenerationalPerformanceTrend.min_improvement=min_improvement;
		IntergenerationalPerformanceTrend.max_decline=max_decline;
	}





	public static IntergenerationalPerformanceTrend calculateTrend(
			GenerationPerformanceData[] intergenerationalPerformanceData, int domain_start,
			int domain_end) {

		double average_slope=calculateAverageSlope(intergenerationalPerformanceData, domain_start,
				domain_end);


		return slopeToTrend(average_slope);

	}


	private static IntergenerationalPerformanceTrend slopeToTrend(
			double average_slope) {
	
		if(average_slope>min_improvement) return IMPROVED;
		if(average_slope<max_decline) return WORSENED;
		if(average_slope<THRESHOLD_DECLINE) return WORSENED_SEVERELY;
		return STABLE;
	}





	//store n-1 differences
	//calculate average difference
	//(this is the average slope)
	private static double calculateAverageSlope(
			GenerationPerformanceData[] intergenerationalPerformanceData,
			int domain_start, int domain_end) {
		double sumOfDelta=0;

		int domain_size=(domain_end-domain_start)+1; 


		for(int i=1; i<domain_size;i++){
			//get the previous score.
			GenerationPerformanceData prev_data=intergenerationalPerformanceData[counterToIndex(i-1, domain_start)];
			GenerationPerformanceData curr_data=intergenerationalPerformanceData[counterToIndex(i, domain_start)];
			if(prev_data==null || curr_data==null) break;
			double prev_score=prev_data.average();
			double curr_score=curr_data.average();
			double delta=curr_score-prev_score;
			sumOfDelta+=delta;

		}

		double slope=sumOfDelta/(double)(domain_size-1);

		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("---Message from Integenerational performance trend---");
			System.out.println("domain size: "+domain_size);
			System.out.println("sum of deltas: "+sumOfDelta);
			System.out.println("domain size (-1): "+(domain_size-1));
			System.out.println("Calculated slope: "+slope);
		}

		return slope;
	}

	private static int counterToIndex(int i, int domain_start) {
		return domain_start+i;
	}



}
