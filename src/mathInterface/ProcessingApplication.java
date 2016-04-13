package mathInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;






import java.util.Stack;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;

import processing.core.*;

import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
public class ProcessingApplication extends PApplet implements Observer {

    public static final boolean PRINT_DEBUG_MESSAGES=false;

	private static ProcessingApplication processingAppInstance;
	public static final int APPLICATION_WIDTH=800;
	public static final int APPLICATION_HEIGHT=600;

	//GA params
	public static final int GA_SHAKEUP_PERIOD=2;
	public static final double THRESHOLD_PERFORMANCE_DECLINE=-.5;
	public static final int NUM_GENERATIONS=8;
	public static final int POPULATION_SIZE = 30;
	public static final int NUM_INDIVIDUALS_TO_SHOW_USER_PER_GENERATION=2;

	//math problem params
	public static final int MATH_PROBLEMS_PER_SET=4;
	public static final int MAX_ARGUMENT_VALUE = 9;
	public static final int MAX_DIGITS_IN_ANSWER =  2;


	public static ChromosomeToFeedbackManifester feedbackManifester;
	JGAPAdapter jgapAdaptor;
	UI mathProblemUI;
	MathProblemSetHandler mathProblemSetHandler;
	ChildPerformanceMonitor childPerformanceMonitor;



	Iterator<IChromosome> currentPopulation;
	
	
	private String displayText;
	private float displayFontSize;
	private float textDisplayWidth=APPLICATION_WIDTH-UI_FONT_SIZE*2;
	private float textDisplayHeight=APPLICATION_HEIGHT-UI_FONT_SIZE*2;
	private float textDisplayArea=textDisplayWidth*textDisplayHeight;
	
	private static final int DELAY_BEFORE_START_TIME = 5000;
	private static final int RECORDING_APTITUDE = 0;
	public static final int RUNNING_INDIVIDUAL=1;
	public static final int UPDATING_POPULATION=2;
	private static final int FINISHED_ALL_POPULATIONS = 3;
	private static final int DELAY_BEFORE_START = 4;
	private static final int SIGNALING_NEXT_INDIVIDUAL=5;

	public static final int UPDATE_POPULATION_DELAY = 1000;
	public static final int[] UI_BACKGROUND_COLOR=new int[]{0,0,0};
	public static final int[] UI_FONT_COLOR=new int[]{255,255,255};
	public static final int UI_FONT_SIZE = 60;

	private int state;

	private int generationNumber;


	private DisplayScreen allDoneScreen;

	private Button[] aptitude_buttons;
	private int delay_before_start_timer;
	public static final int HIGH=2;
	public static final int LOW=0;
	public static final int MED=1;
	public static final int NUM_APTITUDE_LEVELS=3;

	private static final int FAKE_CONFIGURATION_DELAY = 400;
	


	private List<GAShakeupStrategy> shakeupStrategies=new ArrayList<GAShakeupStrategy>();
    private List<GAShakeupStrategy> influenceOrder = new ArrayList<GAShakeupStrategy>();

	private static int curr_child_aptitude;
	public static int getCurrentChildAptitude(){
		return curr_child_aptitude;
	}


	public void settings(){
		size(APPLICATION_WIDTH,APPLICATION_HEIGHT);
	}



	public void setup(){
		processingAppInstance=this;
		feedbackManifester=ChromosomeToFeedbackManifester.getInstance();
		jgapAdaptor=JGAPAdapter.getInstance();
		mathProblemUI=UI.getInstance();
		mathProblemSetHandler=MathProblemSetHandler.getInstance();
		childPerformanceMonitor=ChildPerformanceMonitor.getInstance();

		allDoneScreen=makeEmptyScreenSizedToApplication();
		allDoneScreen.addVariableText(new VariableText("All finished.",UI_FONT_COLOR,0,0,20));

		//initialize shakeup strategies
		shakeupStrategies.add(new AdjustWeights(jgapAdaptor));
		shakeupStrategies.add(new SeedPopulationWithTailEnd(jgapAdaptor));
		shakeupStrategies.add(new RewardDeviationFromTemplates(jgapAdaptor));
		shakeupStrategies.add(new IncreaseMutationRate(jgapAdaptor));


		initializeRecordAptitudeButtons();



	}


	private void initializeRecordAptitudeButtons() {
		aptitude_buttons=new Button[3];
		for(int i=0;i<aptitude_buttons.length;i++){
			Button b=new Button(-100+(100)*i, 0, 80, 40, i);
			b.setMessage(intToAptitude(i), UI_FONT_SIZE);
			b.setColor(new float[]{120,120,120});
			aptitude_buttons[i]=b;
		}
	}



	private String intToAptitude(int i) {
	
		switch(i){
		case LOW: 
			return "LOW";
		case MED:
			return "MED";
		case HIGH:
			return "HIGH";
		}

		return "MEDIUM";
	}


	public static ProcessingApplication getInstance(){
		return processingAppInstance;
	}




	private void updateRecordAptitudeUI(){
		pushMatrix();
		background(UI_BACKGROUND_COLOR);
		translate(width/2, height/2);
		textAlign(CENTER);
		textSize(30);
		fill(UI_FONT_COLOR);
		text("Please select this student's aptitude level.",0,-100);
		for(int i=0;i<aptitude_buttons.length;i++){
			Button b=aptitude_buttons[i];
			b.update(this);
		}
		popMatrix();
	}

	private void fill(int[] color) {
		fill(color[0], color[1], color[2]);

	}


	public void background(int[] color) {
		background(color[0], color[1], color[2]);

	}

	private void handleTransitionScreen(){
		delay_before_start_timer--;
		if(delay_before_start_timer==0){
			state=RUNNING_INDIVIDUAL;
			delay_before_start_timer=DELAY_BEFORE_START_TIME;
		}
		textSize(20);
		background(UI_BACKGROUND_COLOR);
		fill(UI_FONT_COLOR);
		textAlign(CENTER);
		text("(Click to skip ahead)",UI_FONT_SIZE,height-UI_FONT_SIZE, textDisplayWidth,UI_FONT_SIZE*2);
	}

	
	
	public void draw() {
		switch(state){
		case RECORDING_APTITUDE:
			updateRecordAptitudeUI();
			return;
		case DELAY_BEFORE_START:
			handleTransitionScreen();
			textSize(20);
		
			text(displayText,UI_FONT_SIZE,UI_FONT_SIZE, textDisplayWidth,(int)(textDisplayHeight/1.5));
			int config_timer=delay_before_start_timer-(DELAY_BEFORE_START_TIME-FAKE_CONFIGURATION_DELAY);
            
			
			if(config_timer==0){
            	setDisplayTextToSummarizeCurrentPopulation();
            	
            }
            if(config_timer>0) {
            	text("Configuring initial population... please wait.",UI_FONT_SIZE,textDisplayHeight/2+UI_FONT_SIZE*2, textDisplayWidth,textDisplayHeight);
            	text(config_timer+"",UI_FONT_SIZE,textDisplayHeight/2+UI_FONT_SIZE*3, textDisplayWidth,textDisplayHeight);
            }

			if(wait_one_frame_to_configure==0){
				configureApplicationForNextGeneration();
				configureApplicationForNextIndividual(); //for first individual.
				wait_one_frame_to_configure=-1;
				//can print the population info now.
			}

			if(wait_one_frame_to_configure>0) {
				wait_one_frame_to_configure--;
			}
			return;
			
			
			
			
		case SIGNALING_NEXT_INDIVIDUAL:
			handleTransitionScreen();
			textSize(20);
			text(displayText,UI_FONT_SIZE,UI_FONT_SIZE, textDisplayWidth,textDisplayHeight);
			config_timer=delay_before_start_timer-(DELAY_BEFORE_START_TIME-FAKE_CONFIGURATION_DELAY);
            
			
			if(config_timer==0){
            	appendIndividualDataToDisplayText();
            }
            if(config_timer>0) {
            	
            	text(config_timer+"",UI_FONT_SIZE,textDisplayHeight/2+UI_FONT_SIZE*3, textDisplayWidth,textDisplayHeight);
            }
		
		
			return;
		case UPDATING_POPULATION:
			handleTransitionScreen();
			textSize(20);
			text(displayText,UI_FONT_SIZE,UI_FONT_SIZE, textDisplayWidth,textDisplayHeight);
			config_timer=delay_before_start_timer-(DELAY_BEFORE_START_TIME-FAKE_CONFIGURATION_DELAY*2);
            
			if(config_timer==FAKE_CONFIGURATION_DELAY){
				appendIndividualDataToDisplayText();
			}
			
			
			if(config_timer==FAKE_CONFIGURATION_DELAY/2){
				
				displayText="Thank you. We're now going to use your data- along with the expected fitnesses of the other feedbacks you didn't see- to create a brand new set of feedbacks! \n As before, we'll choose "+NUM_INDIVIDUALS_TO_SHOW_USER_PER_GENERATION+" of these to present to you... \n Generating the new population of feedbacks... please wait.";
			
				
            }
			if(config_timer==0){
				setDisplayTextToSummarizeCurrentPopulation();
				System.out.println("new display text ");
				System.out.println(displayText);
			}
            if(config_timer>0) {
            	
            	text(config_timer+"",UI_FONT_SIZE,textDisplayHeight/2+UI_FONT_SIZE*3, textDisplayWidth,textDisplayHeight);
            }
			
			
			return;
			
			
			
			///add another screen, this one will display:
			//information about the intergenerational trend
			
			//add another screen, this one will display:
			//information about the shakeup strategy applied (if we apply a shakeup strategy)
		}


		mathProblemUI.update();

	}


	

    String fitness_of_last_individual="";
	private void appendIndividualDataToDisplayText() {
		displayText=displayText+"\n "+fitness_of_last_individual;
	}


	private void setDisplayTextToSummarizeCurrentPopulation() {
		displayText=jgapAdaptor.summarizeCurrentPopulationAsString()+"\n"+jgapAdaptor.summarizeSelectedSubsetAsString();
	
	}


	public void mousePressed(){

		if(state==RECORDING_APTITUDE){
			Button pressed=identifyPressedAptitudeButton(mouseX-APPLICATION_WIDTH/2, mouseY-APPLICATION_HEIGHT/2);
			if(pressed!=null){
				handlePressedAptitudeButton(pressed);
			}
		}
		else if (state==RUNNING_INDIVIDUAL) {
			/* if we clicked in the submit answer button, then delegate control to the feedback message */
			mathProblemUI.mousePressed(mouseX-APPLICATION_WIDTH/2, mouseY-APPLICATION_HEIGHT/2);
		}
		else if (delayTimerActive()){
			skipTransitionScreen();
		}
	}


	//set to 20, provide a tiny delay following the press before we switch state
	private void skipTransitionScreen() {
		delay_before_start_timer=30;
	}


	private boolean delayTimerActive() {
		
		return delay_before_start_timer>0&&delay_before_start_timer<DELAY_BEFORE_START_TIME;
	}


	Button identifyPressedAptitudeButton(int mouseX, int mouseY) {
		for(Button button : aptitude_buttons){
			if (!button.isPressed() && button.wasPressed(mouseX, mouseY)) return button; 
		}
		return null;
	}

	int wait_one_frame_to_configure=1;

	

	
	void handlePressedAptitudeButton(Button button){
		button.flashToIndicatePressed(this);
		recordButtonIDAsAptitude(button.id);

		state=DELAY_BEFORE_START;
		delay_before_start_timer=(int)(DELAY_BEFORE_START_TIME);
		
		
		displayText=getDelayBeforeStartMessage();



	}




	private String getDelayBeforeStartMessage() {
		
		
		StringBuilder s=new StringBuilder();
		s.append("Thank you!");
		s.append("\n");
		s.append("We will use this information to generate the first set of Feedbacks.");
		s.append("\n");
		s.append("Since you entered: "+intToAptitude(curr_child_aptitude));
		s.append("\n");
		s.append("We will reward PROXIMITY to: ");
		s.append("\n");
		s.append(FeedbackTemplate.getInstance().GetAppropriateForAsString(curr_child_aptitude));
		s.append("We will reward DISTANCE from: ");
		s.append("\n");
		s.append(FeedbackTemplate.getInstance().getInappropriateForAsString(curr_child_aptitude));
		
		
		return s.toString();
	}


	private void recordButtonIDAsAptitude(int id) {
		
		curr_child_aptitude=id;	
	}


	public void keyPressed(){
		if (KeyInterpreter.isInt(key)) {
			mathProblemSetHandler.addDigitToAnswer(KeyInterpreter.toInt(key));
		}
		if (KeyInterpreter.isDelete(key)) {
			mathProblemSetHandler.removeDigitFromAnswer();
		}
		updateUserAnswerDigits();


	}


	private void updateUserAnswerDigits(){
		int[] userAnswerDigits=mathProblemSetHandler.currentAnswerDigits();

		int i=0;
		for(;i<userAnswerDigits.length;i++){
			userAnswerDigitsTextObjects[i].update(userAnswerDigits[i]);
		}
		for(;i<userAnswerDigitsTextObjects.length;i++){
			userAnswerDigitsTextObjects[i].update("");
		}

	}




	private void storeReferenceToCurrentIndividuals(){

		currentPopulation=jgapAdaptor.iterator();
		
	}


	//you should call this when you are done with the current indiviaul
	private void configureApplicationForNextIndividual(){

		//get a fresh new set of problems
		mathProblemSetHandler.initializeNewProblemSet();
		mathProblemSetHandler.createRandomProblems();
		//update feedback variables
		feedbackManifester.setCurrentFeedbackChromosome(currentPopulation.next());
		feedbackManifester.initializeFeedbackScreens();
		//notify the child performance monitor that we are running a new individual
		//of the current generation.
		childPerformanceMonitor.prepareForNextIndividual();
		//setup first problem
		setupNextProblemAndUpdateUI();	
	}



	public DisplayScreen makeEmptyScreenSizedToApplication() {
		DisplayScreen screen=new DisplayScreen(0,0, APPLICATION_WIDTH,APPLICATION_HEIGHT);
		screen.setBackgroundColor(UI_BACKGROUND_COLOR);
		return screen;
	}


	int numAttempts;
	boolean isFirstAttemptAtThisProblem(){
		return numAttempts==1;
	}
	public void handleSubmittedAnswer() {
		//prevent user re submitting after feedback has started.
		numAttempts++;
		if(feedbackManifester.acceptingResponse()){
			if(isFirstAttemptAtThisProblem()){
				mathProblemSetHandler.updateUserScore();
			}
			feedbackManifester.provideFeedback(mathProblemSetHandler.currentProblem());
		}
	}


	private VariableText[] userAnswerDigitsTextObjects=new VariableText[3];


	private void setupNextProblemAndUpdateUI(){
	
		VariableText usersAnswerFirstDigit=new VariableText("x",UI_FONT_COLOR,-UI_FONT_SIZE/2-UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		VariableText usersAnswerSecondDigit=new VariableText("x",UI_FONT_COLOR,0,0,UI_FONT_SIZE);
		VariableText usersAnswerThirdDigit=new VariableText("x",UI_FONT_COLOR,UI_FONT_SIZE/2+UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		userAnswerDigitsTextObjects[0]=usersAnswerFirstDigit;
		userAnswerDigitsTextObjects[1]=usersAnswerSecondDigit;
		userAnswerDigitsTextObjects[2]=usersAnswerThirdDigit;
		mathProblemSetHandler.prepareNextProblem();
		mathProblemUI.clearAnswerScreen();


		for(int i=0;i<userAnswerDigitsTextObjects.length;i++){
			mathProblemUI.getAnswerScreen().addVariableText(userAnswerDigitsTextObjects[i]);
		}

		mathProblemUI.updateProblemScreen(mathProblemSetHandler.currentProblem());




	}

	private void resetFeedbackScreens(){
		feedbackManifester.resetFeedbackScreens();
	}




	public void feedbackDone(){

		if(!mathProblemSetHandler.currentProblemSetFinished()){
			setupNextProblemAndUpdateUI();
			resetFeedbackScreens(); 
		}
		else {//current problem set finished.

			FeedbackChromosomeFactory.recordUserScoreAsFitnessTermForCurrentFeedback(feedbackManifester.getCurrentGenotype(), mathProblemSetHandler.getResultsForProblemSet());
			//tell the child performance monitor to record the summary score for the current individual.
			childPerformanceMonitor.recordSummaryScoreForCurrentIndividual(mathProblemSetHandler.getResultsForProblemSet());
			setDisplayTextToSummarizeLastIndividual();
			//more individuals in this generation to examine
			if(currentPopulation.hasNext()){
				configureApplicationForNextIndividual();
				state=SIGNALING_NEXT_INDIVIDUAL;
				
			}
			else { //next generation
				
				childPerformanceMonitor.recordSummaryDataForCurrentPopulation();
				generationNumber++;
				
				
				if(generationNumber<NUM_GENERATIONS){
					if(generationNumber%GA_SHAKEUP_PERIOD==0){
						shakeUpGeneticAlgorithmOnTheBasisOfIntergenerationalChildPerformance();
					}
					configureApplicationForNextGeneration();
			
					state=UPDATING_POPULATION;
					configureApplicationForNextIndividual();
				}
				else {
					mathProblemUI.activateOverlayScreen(allDoneScreen);
					state=FINISHED_ALL_POPULATIONS;
				}
			}
		}
	}

	


	private void setDisplayTextToSummarizeLastIndividual() {
		StringBuilder s = new StringBuilder("");
		s.append("Congratulations. You have finished evaluating this feedback.");
		s.append("\n");
		s.append("We're converting your score into a rating for this feedback... just a moment.");
		displayText=s.toString();
		
		//cache the individual data as a string
		fitness_of_last_individual=jgapAdaptor.getFitnessFunction().fitnessFunctionToStringFor(feedbackManifester.getCurrentGenotype());
		
	}


	private boolean shouldShakeUpGeneticAlgorithm(IntergenerationalPerformanceTrend currentTrend){
		return currentTrend==IntergenerationalPerformanceTrend.WORSENED || currentTrend==IntergenerationalPerformanceTrend.STABLE;
	}

	private void shakeUpGeneticAlgorithmOnTheBasisOfIntergenerationalChildPerformance() {
		IntergenerationalPerformanceTrend currentTrend=childPerformanceMonitor.getCurrentIntergenerationalPerformanceTrend();
		if(shouldShakeUpGeneticAlgorithm(currentTrend)){
			 applyRandomStrategyToAlgorithm();
		}
		if(shouldRollBackMostRecentStrategy(currentTrend)){
			rollbackMostRecentStrategy();
		}

	}
	
	
	private void rollbackMostRecentStrategy() {
		
		GAShakeupStrategy mostRecent=popMostRecentInfluence();
		Population populationBeforeAction=mostRecent.getPopulationBeforeAction();
	    jgapAdaptor.replaceActivePopulation(populationBeforeAction);
	
	
	}


	private boolean shouldRollBackMostRecentStrategy(
			IntergenerationalPerformanceTrend currentTrend) {
		return currentTrend==IntergenerationalPerformanceTrend.WORSENED_SEVERELY&&!influenceOrder.isEmpty();
	}


	private void applyRandomStrategyToAlgorithm(){
		
		GAShakeupStrategy strategy=pickRandomStrategy(); //only policy I have so far

		if(strategy.hasUndoneChanges()){
			strategy.undo();
			eraseInfluenceRecord(strategy);
		}
		else {
			strategy.shakeUp();
		}
		recordInfluenceRecord(strategy);
		
	
	}
	
	private GAShakeupStrategy popMostRecentInfluence(){
		int top=influenceOrder.size()-1;
		GAShakeupStrategy result= influenceOrder.get(top);
		eraseInfluenceRecord(top);
		return result;
	}


	private void recordInfluenceRecord(GAShakeupStrategy strategy) {
		influenceOrder.add(strategy);
		
	}


	private void eraseInfluenceRecord(GAShakeupStrategy strategy) {
		influenceOrder.remove(strategy);	
	}
	
	private void eraseInfluenceRecord(int idx_of_strategy) {
		influenceOrder.remove(idx_of_strategy);	
	}


	private GAShakeupStrategy pickRandomStrategy() {
		int rand_idx=(int)random(0, shakeupStrategies.size());
		return shakeupStrategies.get(rand_idx);

	}


	private void configureApplicationForNextGeneration(){
		jgapAdaptor.evolveNewIndividuals(); 
		storeReferenceToCurrentIndividuals();
		childPerformanceMonitor.prepareForNextGeneration();
	}





	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof DisplayScreen){
			DisplayScreen s = (DisplayScreen)arg0;
			if(s.getName().equals("populationScreen")){
				mathProblemUI.removeOverlayScreen();
				state=RUNNING_INDIVIDUAL;
			}
		}

	}


	public static double constrain(double d, double lower, double upper) {

		return Math.min(Math.max(lower,d), upper);
	}

}







