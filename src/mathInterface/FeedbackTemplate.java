package mathInterface;

import java.util.HashMap;
import java.util.Map;

public class FeedbackTemplate {

	
	static Feedback exploratory=new Feedback();
	static Feedback directive=new Feedback();
	static Feedback[] templates=new Feedback[]{exploratory,directive};

	static double[] exploratoryAptitudeWeightings=new double[ProcessingApplication.NUM_APTITUDE_LEVELS];
	static double[] directiveAptitudeWeightings=new double[ProcessingApplication.NUM_APTITUDE_LEVELS];


	static Map<Feedback, double[]> feedbackAptitudeLookup=new HashMap<Feedback, double[]>();


	//"bad" feedbacks
	static Feedback preSearch=new Feedback();
	static Feedback[] badFeedbacks=new Feedback[]{preSearch};


	private static double maxDistanceBetweenFeedbacks;
	private static FeedbackTemplate instance;



	private FeedbackTemplate(){
		initializeTemplates();
		initializeAptitudeWeightings();
		initializeMap();
		cacheMaxDistanceBetweenFeedbackAndOther();
		
	}




	private void initializeAptitudeWeightings() {
		exploratoryAptitudeWeightings[ProcessingApplication.HIGH]=.99;
		exploratoryAptitudeWeightings[ProcessingApplication.MED]=.5;
		exploratoryAptitudeWeightings[ProcessingApplication.LOW]=.01;


		directiveAptitudeWeightings[ProcessingApplication.HIGH]=.01;
		directiveAptitudeWeightings[ProcessingApplication.MED]=.5;
		directiveAptitudeWeightings[ProcessingApplication.LOW]=.99;	

	}


	private void initializeMap() {

		feedbackAptitudeLookup.put(exploratory, exploratoryAptitudeWeightings);
		feedbackAptitudeLookup.put(directive, directiveAptitudeWeightings);
	}


	private void initializeTemplates() {

		exploratory.updateAllowingResubmission(1);
		exploratory.updateDirectiveFeedbackParameters(0);
		exploratory.updateProvideCorrectAnswerParameters(0,0);
		exploratory.updateErrorFlagParameters(0,0);
		exploratory.updateElaborationParameters(1);
		exploratory.updateAttributeIsolationParameters(1,2000);
		exploratory.updateVerificationParameters(1,1000);
		exploratory.updateFeedbackDelay(1000);
		


		directive.updateAllowingResubmission(1);
		directive.updateDirectiveFeedbackParameters(1);
		directive.updateProvideCorrectAnswerParameters(1,0);
		directive.updateErrorFlagParameters(1,0);
		directive.updateElaborationParameters(0);
		directive.updateAttributeIsolationParameters(0,0);
		directive.updateVerificationParameters(Feedback.EXPLICIT_VERIFICATION,Feedback.IMAGE_VERIFICATION);
		directive.updateFeedbackDelay(100);
		


		preSearch.updateAllowingResubmission(1);
		preSearch.updateDirectiveFeedbackParameters(1);
		preSearch.updateProvideCorrectAnswerParameters(1,0);
		preSearch.updateErrorFlagParameters(Feedback.DISCOUNT,Feedback.DISCOUNT);
		preSearch.updateElaborationParameters(Feedback.DISCOUNT);
		preSearch.updateAttributeIsolationParameters(Feedback.DISCOUNT,Feedback.DISCOUNT);
		preSearch.updateVerificationParameters(Feedback.DISCOUNT,Feedback.DISCOUNT);
		preSearch.updateFeedbackDelay(Feedback.DISCOUNT);
		



	}

	private static void cacheMaxDistanceBetweenFeedbackAndOther() {
		maxDistanceBetweenFeedbacks=0;
		GenePosition[] genes=GenePosition.values();
		for(int i=0;i<genes.length;i++){
			GenePosition gp=genes[i];
			maxDistanceBetweenFeedbacks+=Math.pow(gp.allelleRange(),2);
		}
		maxDistanceBetweenFeedbacks=Math.sqrt(maxDistanceBetweenFeedbacks);
		System.out.println("Message from FeedbackTemplate");
		System.out.println("Maximum distance between feedbacks: "+maxDistanceBetweenFeedbacks);
	}




	public static FeedbackTemplate getInstance(){
		if(instance==null)
			instance=new FeedbackTemplate();
		return instance;
	}

	 //param sign: do you punish distance from good and proximity to bad templates or vice versa?
	private double calculateFitnessForChildAptitude(
			Feedback feedback, int sign) {
		double result=0;

		for(int i=0;i<templates.length;i++){
			Feedback template=templates[i];
			double distance=template.distanceFrom(feedback);

			double weighting=lookupWeighting(template,ProcessingApplication.getCurrentChildAptitude());
			result+=transferFunction(distance,weighting, sign);
		}
		
		System.out.println("Message from FeedbackTemplate");
		System.out.println("fitness for proximity to aptitude-appropriate templates: "+result);
	
		return result;
	}


	private double calculateFitnessForProximityToBadFeedbacks(Feedback feedback, int sign){
		double result=(sign>0? 0 : maxDistanceBetweenFeedbacks);
		for(int i=0;i<badFeedbacks.length;i++){
			Feedback badFeedback=badFeedbacks[i];
			double distance=badFeedback.distanceFrom(feedback);
			result+=sign*distance;
		}
		System.out.println("Message from FeedbackTemplate");
		System.out.println("fitness for proximity to bad feedbacks: "+result);
		
		
		return result;
		
	}


	//sign=1 (i.e., "high" values = large distances from "good" templates. want to punish these
	//sign =-1 (rewards distance from "good" templates)
	private static double transferFunction(double distance, double weighting, int sign) {
		double result=(10/(1+100*Math.pow(Math.E,sign*distance)));
		result*=weighting;
		return result;
	}


	private static double lookupWeighting(Feedback template,
			int currentChildAptitude) {
		double[] weightings=feedbackAptitudeLookup.get(template);

		return weightings[currentChildAptitude];

	}


	//note that the opposite of prox to bad feedbacks- prox to "good" feedbacks- is captured by
	//given child aptitude. idea is there isnt a single "good" feedback; it
	//depends on child aptitude. child aptitude calc. takes distance from each template
	//into account.
	public double calculateExpectedFitness(Feedback feedback, int sign) {
		double given_child_aptitude=calculateFitnessForChildAptitude(feedback, sign);
		double given_proximity_to_bad_feedbacks=calculateFitnessForProximityToBadFeedbacks(feedback, sign);

		return given_child_aptitude+given_proximity_to_bad_feedbacks;
	}










}
