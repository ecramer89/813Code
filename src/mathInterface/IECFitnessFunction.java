package mathInterface;

import java.text.DecimalFormat;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

/*
 * Maps the fitness of this individual to the rating that the user assigned to it.
 * (*assumes that we will set the fitness value of each chromosome to be the userès rating)
 */
public class IECFitnessFunction extends FitnessFunction {
	private double SCALE_FACTOR=50;


	private double default_user_score_weight=.8;
	private double default_expected_fitness_weight=1-default_user_score_weight;

	private int sign=1;
	//boolean done_testing=false;




	@Override
	protected double evaluate(IChromosome a_subject) {
		double expec_weight=default_expected_fitness_weight;
		double user_score_weight=default_user_score_weight;


		double expectedFitness=FeedbackTemplate.getInstance().calculateExpectedFitness(ChromosomeToFeedbackManifester.createFeedback(a_subject), sign);
		double userScore=0;//= (Double)a_subject.getGene(GenePosition.USER_SCORE.ordinal()).getAllele();


		//just use the automated part of the fitness function if the individual lacks a user rating.
		if(JGAPAdapter.getInstance().wasSelectedForPresentationToUser(a_subject)){
			userScore=JGAPAdapter.getInstance().getUserScoreFor(a_subject);

			if(ProcessingApplication.PRINT_DEBUG_MESSAGES){
				System.out.println("message from fitness function: ");
				System.out.println("got user score from the jgap adaptor: "+userScore);
			}
		}
		else {
			expec_weight=1.0;
			user_score_weight=0;
		}


		//put user score (raw, between 0 and 1) on same scale as 
		//expected fitness; 
		//weight the two constituents of the overall fitness value
		//(i elected to allow for abolute large values of the fitness function
		//over normalizing; greater ranges seem to yield better performance of the GA
		double weighted_exp_fitness=expec_weight*expectedFitness;
		double scaled_user_score=userScore*FeedbackTemplate.getMaximumExpectedFitness();
		double weighted_user_score_fitness=user_score_weight*scaled_user_score;
	
		return weighted_exp_fitness+weighted_user_score_fitness;

	}
	
	
	private class FitnessData{
		double weight_expected_fitness, weight_obs_fitness, exp_fitness, obs_fitness, total_fitness;
		IChromosome a_subject;
		public FitnessData(IChromosome a_subject){
			this.a_subject=a_subject;
			evaluate();
		}
		
		private void evaluate(){
			weight_expected_fitness=default_expected_fitness_weight;
			weight_obs_fitness=default_user_score_weight;


			exp_fitness=FeedbackTemplate.getInstance().calculateExpectedFitness(ChromosomeToFeedbackManifester.createFeedback(a_subject), sign);
			obs_fitness=0;

			if(JGAPAdapter.getInstance().wasSelectedForPresentationToUser(a_subject)){
				obs_fitness=JGAPAdapter.getInstance().getUserScoreFor(a_subject);
				obs_fitness*=FeedbackTemplate.getMaximumExpectedFitness();
			
			}
			else {
				weight_expected_fitness=1.0;
				weight_obs_fitness=0;
			}
			
		   
			total_fitness= (weight_expected_fitness*exp_fitness)+(weight_obs_fitness*obs_fitness);
			
		}
		
		
		
		
		
	}
	



	public void adjustWeights(double adjust_actual_fitness_weight){

		default_user_score_weight=ProcessingApplication.getInstance().constrain(default_user_score_weight+adjust_actual_fitness_weight,0,1);
		default_expected_fitness_weight=1-default_user_score_weight;
	
	}

	public void invertSign(){
	
		sign=(sign<1? 1:-1);
		
	}




	DecimalFormat df = new DecimalFormat("#.##");
	public String fitnessFunctionToStringFor(IChromosome currentGenotype) {
		FitnessData data=new FitnessData(currentGenotype);
		StringBuilder s=new StringBuilder();
		String obs_fitness=df.format(data.obs_fitness);
		String exp_fitness=df.format(data.exp_fitness);
		String weight_expected_fitness=df.format(data.weight_expected_fitness);
		String weight_obs_fitness=df.format(data.weight_obs_fitness);
		String total_fitness=df.format(data.total_fitness);
		
		s.append("Observed fitness (your score, converted into a fitness value): ");
		s.append(obs_fitness);
		s.append("\n");
		s.append("Expected fitness (how we thought this feedback would do, before we saw your data): ");
		s.append(exp_fitness);
		s.append("\n");
		s.append("Total fitness: ");
		s.append(weight_expected_fitness);
		s.append("x");
		s.append(exp_fitness);
		s.append(" + ");
		s.append(weight_obs_fitness);
		s.append("x");
		s.append(obs_fitness);
		s.append(" = ");
		s.append(total_fitness);
		
	
		return s.toString();
	}






}


