package mathInterface;

public enum FeedbackGeneType {


	
	FEEDBACK_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY, "Feedback Delay"),
	VERIFICATION_MODALITY(0, FeedbackChromosomeFactory.NUM_VERIFCATION_MODALITIES-1, "Verification Modality"),
	VERIFICATION_TYPE(0, FeedbackChromosomeFactory.NUM_VERIFICATION_TYPES-1, "Verification Type"), 
	P_ELABORATE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1, "Likelihood of Elaborative Feedback"), 

	P_ATTRIBUTE_ISOLATION(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1, "Likelihood of Attribute Isolation"), 
	ATTRIBUTE_ISOLATION_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY, "Delay before Attribute Isolation"), 
	P_DIRECTIVE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1, "Likelihood of Directive Feedback"), 
	P_CORRECT_RESPONSE(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1,"Likelihood of Revealing Solution"), 
	
	CORRECT_RESPONSE_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY, "Likelihood of Revealing Solution"), 
	P_ERROR_FLAG(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1, "Delay before Revealing Solution"), 
	ERROR_FLAG_DELAY(0,FeedbackChromosomeFactory.MAX_DELAY, "Delay before Flagging Errors"), 
	P_ALLOW_RESUBMIT(FeedbackChromosomeFactory.MIN_EVENT_PROBABILITY,1, "Likelihood of Flagging Errors");


	private double min_allelle_value, max_allelle_value;
    private String name;

	private FeedbackGeneType(double min_allelle_value, double max_allelle_value, String name){
		this.max_allelle_value=max_allelle_value;
		this.min_allelle_value=min_allelle_value;
		this.name=name;
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

	
	public String nameOfGene(){
		return name;
	}

}
