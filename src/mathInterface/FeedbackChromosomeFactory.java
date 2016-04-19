package mathInterface;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

public class FeedbackChromosomeFactory {



	//in milliseconds
	public static final int MAX_DELAY = 2000;
	public static final int MIN_SCREEN_DURATION = 1000;
	public static final int MAX_SCREEN_DURATION = 4000;

	//integer type codes
	public static final int NUM_VERIFICATION_TYPES=3;
	public static final int NUM_VERIFCATION_MODALITIES=5;

	public static final double MIN_EVENT_PROBABILITY = .8;



	//for calculating summary user score
	private static double maximum_weighed_user_score=-1;




	/*Prepares a set of sample genes for use by the configuration code
	 * just needs to specify the variable types and limits on each gene in the chromosome */
	public static Gene[] getSampleGenes(Configuration conf)
			throws InvalidConfigurationException {

	

		Gene delayGene = new IntegerGene(conf, 0, MAX_DELAY);

		Gene verificationTypeGene = new IntegerGene(conf,0,NUM_VERIFICATION_TYPES);
		Gene verificationModalityGene=new IntegerGene(conf,0,NUM_VERIFCATION_MODALITIES);


		Gene pElaborateGene=new DoubleGene(conf, MIN_EVENT_PROBABILITY,1);
		
		//given elaboration occurred.
		Gene pAttributeIsolationGene=new DoubleGene(conf, MIN_EVENT_PROBABILITY,1);
		Gene attributeIsolationDurationGene=new IntegerGene(conf, MIN_SCREEN_DURATION,MAX_SCREEN_DURATION);


		Gene pDirectiveGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		
		Gene pCorrectAnswerGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		Gene correctAnswerDelay = new IntegerGene(conf, 0, MAX_DELAY);
		Gene pErrorFlagGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		Gene errorFlagDelayGene= new IntegerGene(conf, 0, MAX_DELAY);

		Gene pAllowResubmitGene = new DoubleGene(conf,MIN_EVENT_PROBABILITY,1);




		Gene[] genes = new Gene[FeedbackGeneType.values().length];

		genes[FeedbackGeneType.FEEDBACK_DELAY.ordinal()] = delayGene;
	

		genes[FeedbackGeneType.VERIFICATION_MODALITY.ordinal()]=verificationModalityGene;
		genes[FeedbackGeneType.VERIFICATION_TYPE.ordinal()]=verificationTypeGene;		


		genes[FeedbackGeneType.P_ELABORATE.ordinal()]=pElaborateGene;
		
		genes[FeedbackGeneType.P_ATTRIBUTE_ISOLATION.ordinal()]=pAttributeIsolationGene;
		genes[FeedbackGeneType.ATTRIBUTE_ISOLATION_DELAY.ordinal()]=attributeIsolationDurationGene;

		genes[FeedbackGeneType.P_DIRECTIVE.ordinal()]=pDirectiveGene;
	
		genes[FeedbackGeneType.P_CORRECT_RESPONSE.ordinal()]=pCorrectAnswerGene;
		genes[FeedbackGeneType.CORRECT_RESPONSE_DELAY.ordinal()]=correctAnswerDelay;
		genes[FeedbackGeneType.P_ERROR_FLAG.ordinal()]=pErrorFlagGene;
		genes[FeedbackGeneType.ERROR_FLAG_DELAY.ordinal()]=errorFlagDelayGene;
		genes[FeedbackGeneType.P_ALLOW_RESUBMIT.ordinal()]=pAllowResubmitGene;


		return genes;
	}

	public static void recordUserScoreAsFitnessTermForCurrentFeedback(
			IChromosome currentGenotype, int[] resultsForProblemSet) {
		recordMaximumUserScoreIfNecessary(resultsForProblemSet);
		
		double scoreAsFitness=resultsToFitness(resultsForProblemSet);
		scoreAsFitness/=maximum_weighed_user_score;
		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from FeedbackChromosome Factory: ");
			System.out.println("recording user score as fitness: "+scoreAsFitness);
		}
		JGAPAdapter.getInstance().setUserScoreFor(currentGenotype, scoreAsFitness);

	}
	
	private static void recordMaximumUserScoreIfNecessary(int[] resultsForProblemSet){
		if(maximum_weighed_user_score==-1){
			int[] topScore=new int[resultsForProblemSet.length];
			for(int i=0;i<topScore.length;i++){
				topScore[i]=1;
			}
			maximum_weighed_user_score=resultsToFitness(topScore);
				
		}
	}
	
	
	

	private static double resultsToFitness(int[] results) {
		double result=0;
		for(int i=0;i<results.length;i++){
			double weight=transferFunction(i);
			result+=(results[i]*weight);
		}
		return result;
	}

	private static double transferFunction(double problemNumber) {
		double result=(10/(1+100*Math.pow(Math.E,-problemNumber)));

		if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
			System.out.println("message from feedback chromosome factory: ");
			System.out.println("weight for this problem: "+result);
		}
		return result;
	}







}
