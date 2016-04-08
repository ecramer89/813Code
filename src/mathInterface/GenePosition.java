package mathInterface;

public enum GenePosition {

	USER_SCORE(0,1),
	FEEDBACK_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY),
	TEXT_COLOR_R(0, 255),
	TEXT_COLOR_G(0, 255),
	TEXT_COLOR_B(0, 255), 
	VERIFICATION_MODALITY(0, FeedbackChromosomeFactory.NUM_VERIFCATION_MODALITIES-1),
	VERIFICATION_TYPE(0, FeedbackChromosomeFactory.NUM_VERIFICATION_TYPES-1), 
	P_ELABORATE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1), 
	DELAY_UNTIL_ELABORATE(0,FeedbackChromosomeFactory.MAX_DELAY), 
	P_ATTRIBUTE_ISOLATION(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1), 
	ATTRIBUTE_ISOLATION_DURATION(FeedbackChromosomeFactory.MIN_SCREEN_DURATION, FeedbackChromosomeFactory.MAX_SCREEN_DURATION), 
	P_DIRECTIVE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1), 
	DIRECTIVE_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY), 
	P_CORRECT_RESPONSE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1), 
	CORRECT_RESPONSE_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY), 
	P_ERROR_FLAG(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1), 
	ERROR_FLAG_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY), 
	P_ALLOW_RESUBMIT(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1);


	private double min_allelle_value, max_allelle_value;


	private GenePosition(double min_allelle_value, double max_allelle_value){
		this.max_allelle_value=max_allelle_value;
		this.min_allelle_value=min_allelle_value;
	}

	public double minAllelleValue(){
		return min_allelle_value;
	}

	public double maxAllelleValue(){
		return max_allelle_value;
	}
	
	public double allelleRange(){
		return max_allelle_value-min_allelle_value;
	}


}
