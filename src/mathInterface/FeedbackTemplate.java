package mathInterface;

import java.util.HashMap;
import java.util.Map;

public class FeedbackTemplate {
	static Feedback exploratory;
	static Feedback directive;
	
	
	static double[] exploratoryAptitudeWeightings=new double[ProcessingApplication.NUM_APTITUDE_LEVELS];
	static double[] directiveAptitudeWeightings=new double[ProcessingApplication.NUM_APTITUDE_LEVELS];
	

	static Map<Feedback, double[]> feedbackAptitudeLookup=new HashMap<Feedback, double[]>();
	
	
	private FeedbackTemplate(){
		initializeTemplates();
		initializeAptitudeWeightings();
		initializeMap();
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
		exploratory=new Feedback();
		exploratory.updateAllowingResubmission(1);
		exploratory.updateDirectiveFeedbackParameters(0,0);
		exploratory.updateProvideCorrectAnswerParameters(0,0);
		exploratory.updateErrorFlagParameters(0,0);
		exploratory.updateElaborationParameters(1,2000);
		exploratory.updateAttributeIsolationParameters(1,2000);
		exploratory.updateVerificationParameters(1,1000);
		exploratory.updateFeedbackDelay(1000);
		
		directive=new Feedback();
		directive.updateAllowingResubmission(1);
		directive.updateDirectiveFeedbackParameters(1,0);
		directive.updateProvideCorrectAnswerParameters(1,0);
		directive.updateErrorFlagParameters(1,0);
		directive.updateElaborationParameters(0,0);
		directive.updateAttributeIsolationParameters(0,0);
		directive.updateVerificationParameters(Feedback.EXPLICIT_VERIFICATION,Feedback.IMAGE_VERIFICATION);
		directive.updateFeedbackDelay(100);
		
	}


	
	
	public static double calculateFitnessForChildAptitude(
			Feedback createFeedback) {
		// TODO Auto-generated method stub
		
		
		/*
		 * 
		 * 
		 * 
		 */
		return 0;
	}
	
	

	



	
	

}
