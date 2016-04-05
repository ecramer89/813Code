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
	private static FeedbackTemplate instance;
	
	
	private FeedbackTemplate(){
		initializeTemplates();
		initializeAptitudeWeightings();
		initializeMap();
	}


	private void initializeAptitudeWeightings() {
		exploratoryAptitudeWeightings[ProcessingApplication.HIGH]=.99;
		exploratoryAptitudeWeightings[ProcessingApplication.MED]=.5;
		exploratoryAptitudeWeightings[ProcessingApplication.LOW]=-.8;
		
		
		directiveAptitudeWeightings[ProcessingApplication.HIGH]=-.8;
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
		
		

		
	}


	public static FeedbackTemplate getInstance(){
		if(instance==null)
			instance=new FeedbackTemplate();
		return instance;
	}
	
	public double calculateFitnessForChildAptitude(
			Feedback feedback) {
		// TODO Auto-generated method stub
		double result=0;
		
		for(int i=0;i<templates.length;i++){
			Feedback template=templates[i];
			double distance=template.distanceFrom(feedback);
			System.out.println(template);
			double weighting=lookupWeighting(template,ProcessingApplication.getCurrentChildAptitude());
		    result+=transferFunction(distance,weighting);
		}
	
		return result;
	}


	private static double transferFunction(double distance, double weighting) {
		double result=1/(1+Math.pow(Math.E,-distance));
		result*=weighting;
		return result;
	}


	private static double lookupWeighting(Feedback template,
			int currentChildAptitude) {
		double[] weightings=feedbackAptitudeLookup.get(template);
		System.out.println(weightings==null);
		return weightings[currentChildAptitude];
		
	}
	
	

	



	
	

}
