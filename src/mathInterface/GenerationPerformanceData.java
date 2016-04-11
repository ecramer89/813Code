package mathInterface;


//contains summary data about how the child did, using the feedbacks from a specific population.
public class GenerationPerformanceData {

	/* assumes that scores are between 0 and 1 */
	private double averageScore;
	private double highScore;
	private double lowScore;
	private double variance;
	private double standard_deviation;

	//the raw scores of the individuals of the generation that this object summarizes
	private double[] rawScores;


	//set the raw scores for each individual of the population that this object summarizes
	public GenerationPerformanceData(double[] rawScores){
		this.rawScores=rawScores;
		summarize();
	}



	//computes all of the summary statistics on the basis of the data contained in rawScores.
	private void summarize(){
		//to calculate the average, i just sum the scores and divide by the result.
		//to calculate the standard deviation, i need the average... i calculate the distance of each score from the average and i 
		//then square the result, sum the squared deviations and i take the sqrt of that.
		//the min and max scores can be determined while i compute the average (no point in not)
		calculateAverageMinAndMax();
		calculateStandardDeviation();

	}



	private void calculateStandardDeviation() {
		for(int i=0;i<rawScores.length;i++){
			double x=rawScores[i];
			double deviation=averageScore-x;
			double sqr_deviation=Math.pow(deviation,2);
			variance+=sqr_deviation;
		}
		standard_deviation=Math.sqrt(variance);

		System.out.println("variance: "+variance);
		System.out.println("standard_deviation: "+standard_deviation);
	}



	private void calculateAverageMinAndMax() {
		highScore=0;
		lowScore=1;
		for(int i=0;i<rawScores.length;i++){
			double x=rawScores[i];
			if(x>highScore) highScore=x;
			if(x<lowScore) lowScore=x;
			averageScore+=x;	
		}
		averageScore=averageScore/(double)rawScores.length;

		System.out.println("---message from GenerationPerformanceData---");
		System.out.println("summary data for the current generation: ");
		System.out.println("n: "+rawScores.length);
		System.out.println("averageScore: "+averageScore);
		System.out.println("highScore: "+highScore);
		System.out.println("lowScore: "+lowScore);
	}



	public double average(){
		return averageScore;
	}

	public double high(){
		return highScore;
	}


	public double low(){
		return lowScore;
	}

	public double variance(){
		return variance;
	}


	public double standardDeviation(){
		return standard_deviation;
	}



}
