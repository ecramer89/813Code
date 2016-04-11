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

	public static final double MIN_EVENT_PROBABILITY = .5;
	
	
	
	//for calculating summary user score
	private static double maximum_weighed_user_score=-1;


	

	/*Prepares a set of sample genes for use by the configuration code
	 * just needs to specify the variable types and limits on each gene in the chromosome */
	public static Gene[] getSampleGenes(Configuration conf)
			throws InvalidConfigurationException {

		Gene textRGene= new IntegerGene(conf, 0, 255);
		Gene textGGene= new IntegerGene(conf, 0, 255);
		Gene textBGene= new IntegerGene(conf, 0, 255);

		Gene delayGene = new IntegerGene(conf, 0, MAX_DELAY);

		Gene verificationTypeGene = new IntegerGene(conf,0,NUM_VERIFICATION_TYPES);
		Gene verificationModalityGene=new IntegerGene(conf,0,NUM_VERIFCATION_MODALITIES);


		Gene pElaborateGene=new DoubleGene(conf, MIN_EVENT_PROBABILITY,1);
		Gene delayUntilElaborateGene=new IntegerGene(conf, 0,MAX_DELAY);
		//given elaboration occurred.
		Gene pAttributeIsolationGene=new DoubleGene(conf, MIN_EVENT_PROBABILITY,1);
		Gene attributeIsolationDurationGene=new IntegerGene(conf, MIN_SCREEN_DURATION,MAX_SCREEN_DURATION);


		Gene pDirectiveGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		Gene delayUntilDirectiveGene = new IntegerGene(conf, 0, MAX_DELAY);
		Gene pCorrectAnswerGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		Gene correctAnswerDelay = new IntegerGene(conf, 0, MAX_DELAY);
		Gene pErrorFlagGene = new DoubleGene(conf, MIN_EVENT_PROBABILITY, 1);
		Gene errorFlagDelayGene= new IntegerGene(conf, 0, MAX_DELAY);

		Gene pAllowResubmitGene = new DoubleGene(conf,MIN_EVENT_PROBABILITY,1);




		Gene[] genes = new Gene[GenePosition.values().length];

		genes[GenePosition.FEEDBACK_DELAY.ordinal()] = delayGene;
		genes[GenePosition.TEXT_COLOR_R.ordinal()]=textRGene;
		genes[GenePosition.TEXT_COLOR_G.ordinal()]=textGGene;
		genes[GenePosition.TEXT_COLOR_B.ordinal()]=textBGene;


		genes[GenePosition.VERIFICATION_MODALITY.ordinal()]=verificationModalityGene;
		genes[GenePosition.VERIFICATION_TYPE.ordinal()]=verificationTypeGene;		


		genes[GenePosition.P_ELABORATE.ordinal()]=pElaborateGene;
		genes[GenePosition.DELAY_UNTIL_ELABORATE.ordinal()]=delayUntilElaborateGene;
		genes[GenePosition.P_ATTRIBUTE_ISOLATION.ordinal()]=pAttributeIsolationGene;
		genes[GenePosition.ATTRIBUTE_ISOLATION_DURATION.ordinal()]=attributeIsolationDurationGene;

		genes[GenePosition.P_DIRECTIVE.ordinal()]=pDirectiveGene;
		genes[GenePosition.DIRECTIVE_DELAY.ordinal()]=delayUntilDirectiveGene;
		genes[GenePosition.P_CORRECT_RESPONSE.ordinal()]=pCorrectAnswerGene;
		genes[GenePosition.CORRECT_RESPONSE_DELAY.ordinal()]=correctAnswerDelay;
		genes[GenePosition.P_ERROR_FLAG.ordinal()]=pErrorFlagGene;
		genes[GenePosition.ERROR_FLAG_DELAY.ordinal()]=errorFlagDelayGene;
		genes[GenePosition.P_ALLOW_RESUBMIT.ordinal()]=pAllowResubmitGene;


		return genes;
	}

	public static void recordUserScoreAsFitnessTermForCurrentFeedback(
			IChromosome currentGenotype, int[] resultsForProblemSet) {
		
		if(maximum_weighed_user_score==-1){
			int[] topScore=new int[resultsForProblemSet.length];
			for(int i=0;i<topScore.length;i++){
				topScore[i]=1;
			}
			maximum_weighed_user_score=resultsToFitness(topScore);
			System.out.println("message from FeedbackChromosome Factory: ");
			System.out.println("max weighted score: "+maximum_weighed_user_score);
		}
		double scoreAsFitness=resultsToFitness(resultsForProblemSet);
	    scoreAsFitness/=maximum_weighed_user_score;
		
		//System.out.println("message from FeedbackChromosome Factory: ");
		//System.out.println("recording user score as fitness: "+scoreAsFitness);
		
		JGAPAdapter.getInstance().setUserScoreFor(currentGenotype, scoreAsFitness);
	
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
		double result=(10/(1+100*Math.pow(Math.E,-problemNumber)))/10;
		//System.out.println("message from feedback chromosome factory: ");
		//System.out.println("weight for this problem: "+result);
		
		return result;
	}
	
	
	
	



}
