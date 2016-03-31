package mathInterface;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;

import processing.core.*;

import org.jgap.IChromosome;
public class ProcessingApplication extends PApplet implements Observer {
	private static ProcessingApplication processingAppInstance;
	public static final int APPLICATION_WIDTH=800;
	public static final int APPLICATION_HEIGHT=600;
	public static final int NUM_GENERATIONS=3;
	public static final int POPULATION_SIZE=6;

	public static ChromosomeToFeedbackManifester feedbackManifester;
	JGAPAdapter JGAPAdapter;
	UI mathProblemUI;
	MathProblemHandler mathProblemHandler;

	Iterator<IChromosome> currentPopulation;


	public static final int RUNNING_INDIVIDUAL=0;
	public static final int UPDATING_POPULATION=1;
	private static final int FINISHED_ALL_POPULATIONS = 3;
	public static final int UPDATE_POPULATION_DELAY = 1000;
	public static final int[] UI_BACKGROUND_COLOR=new int[]{0,0,0};
	public static final int[] UI_FONT_COLOR=new int[]{255,255,255};
	public static final int UI_FONT_SIZE = 60;



	private int state;
	private VariableText populationUpdateTimer=new VariableText(0, UI_FONT_COLOR,0,0,UI_FONT_SIZE);
	private DisplayScreen populationUpdateScreen;
	private int generationNumber;


	private DisplayScreen allDoneScreen;






	public void settings(){
		size(APPLICATION_WIDTH,APPLICATION_HEIGHT);
	}



	public void setup(){
		processingAppInstance=this;
		feedbackManifester=ChromosomeToFeedbackManifester.getInstance();
		JGAPAdapter=JGAPAdapter.getInstance();
		mathProblemUI=UI.getInstance();
		mathProblemHandler=MathProblemHandler.getInstance();


		populationUpdateScreen=makeEmptyScreenSizedToApplication();
		populationUpdateScreen.setDurationOfDisplay(UPDATE_POPULATION_DELAY);
		populationUpdateScreen.addVariableText(new VariableText("Evolving next generation... please wait.",UI_FONT_COLOR,0,0,UI_FONT_SIZE));
		populationUpdateScreen.newLine();
		populationUpdateScreen.addVariableText(populationUpdateTimer);
		populationUpdateScreen.setName("populationScreen");
		populationUpdateScreen.addObserver(this);


		allDoneScreen=makeEmptyScreenSizedToApplication();
		allDoneScreen.addVariableText(new VariableText("All finished.",UI_FONT_COLOR,0,0,UI_FONT_SIZE));

		configureGenotype();		
		storeReferenceToCurrentPopulation();
		configureApplicationForNextIndividual(); //for first individual.

	}



	public static ProcessingApplication getInstance(){
		return processingAppInstance;
	}


	public void draw() {
		mathProblemUI.update();
		if(state==UPDATING_POPULATION){
			updateDisplayablePopulationTimer();
		}

	}






	public void mousePressed(){
		/* if we clicked in the submit answer button, then delegate control to the feedback message */
		mathProblemUI.mousePressed(mouseX-APPLICATION_WIDTH/2, mouseY-APPLICATION_HEIGHT/2);

	}





	public void keyPressed(){
		if (KeyInterpreter.isInt(key)) {
			mathProblemHandler.addDigitToAnswer(KeyInterpreter.toInt(key));
		}
		if (KeyInterpreter.isDelete(key)) {
			mathProblemHandler.removeDigitFromAnswer();
		}



		updateUserAnswerDigits();


	}


	private void updateUserAnswerDigits(){
		int[] userAnswerDigits=mathProblemHandler.currentAnswerDigits();

		for(int i=0;i<userAnswerDigits.length;i++)
			System.out.println("user answer digit: "+userAnswerDigits[i]);

		int i=0;
		for(;i<userAnswerDigits.length;i++){
			userAnswerDigitsTextObjects[i].update(userAnswerDigits[i]);
		}
		for(;i<userAnswerDigitsTextObjects.length;i++){
			userAnswerDigitsTextObjects[i].update("");
		}

	}



	private void updateDisplayablePopulationTimer() {
		populationUpdateTimer.update(populationUpdateTimer.toInt()+1);

	}

	private void configureGenotype(){
		try {
			JGAPAdapter.createInitialGenotype();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}



	private void storeReferenceToCurrentPopulation(){

		currentPopulation=JGAPAdapter.iterator();
	}


	//you should call this when you are done with the current indiviaul
	private void configureApplicationForNextIndividual(){
		//get a fresh new set of problems
		mathProblemHandler.initializeNewProblemSet();
		mathProblemHandler.createRandomProblems();
		//setup first problem
		setupNextProblemAndUpdateUI();
		//update feedback variables
		feedbackManifester.updateFeedbackChromosome(currentPopulation.next());
		feedbackManifester.initializeFeedbackScreens();
		mathProblemUI.setFeedbackScreens(feedbackManifester.getFeedbackScreens());
	}



	public DisplayScreen makeEmptyScreenSizedToApplication() {
		DisplayScreen screen=new DisplayScreen(0,0, APPLICATION_WIDTH,APPLICATION_HEIGHT);
		screen.setBackgroundColor(UI_BACKGROUND_COLOR);
		return screen;
	}



	public void handleSubmittedAnswer() {
		//prevent user re submitting after feedback has started.
		if(feedbackManifester.acceptingResponse()){
			mathProblemHandler.updateUserScore();
			feedbackManifester.provideFeedback(mathProblemHandler.currentProblem());
		}
	}


	private VariableText[] userAnswerDigitsTextObjects;
	private void setupNextProblemAndUpdateUI(){
		VariableText usersAnswerFirstDigit=new VariableText("x",UI_FONT_COLOR,-UI_FONT_SIZE/2-UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		VariableText usersAnswerSecondDigit=new VariableText("x",UI_FONT_COLOR,0,0,UI_FONT_SIZE);
		VariableText usersAnswerThirdDigit=new VariableText("x",UI_FONT_COLOR,UI_FONT_SIZE/2+UI_FONT_SIZE/4,0,UI_FONT_SIZE);
		userAnswerDigitsTextObjects=new  VariableText[]{usersAnswerFirstDigit,usersAnswerSecondDigit,usersAnswerThirdDigit};
		mathProblemHandler.prepareNextProblem();
		mathProblemUI.clearAnswerScreen();


		for(int i=0;i<userAnswerDigitsTextObjects.length;i++){
			mathProblemUI.getAnswerScreen().addVariableText(userAnswerDigitsTextObjects[i]);
		}

		mathProblemUI.updateProblemScreen(mathProblemHandler.currentProblem());
	}

	private void resetFeedbackScreensAndUpdateUI(){
		feedbackManifester.initializeFeedbackScreens();
		mathProblemUI.setFeedbackScreens(feedbackManifester.getFeedbackScreens());
	}


	public void feedbackDone(){
		if(!mathProblemHandler.currentProblemSetFinished()){
			setupNextProblemAndUpdateUI();
			resetFeedbackScreensAndUpdateUI();
		}
		else {
			if(currentPopulation.hasNext()){
				//store user score as the individual's fitness
				configureApplicationForNextIndividual();
			}
			else {
				generationNumber++;
				if(generationNumber<NUM_GENERATIONS){
					JGAPAdapter.updatePopulation();
					storeReferenceToCurrentPopulation();
					state=UPDATING_POPULATION;
					populationUpdateTimer.update(0);
					mathProblemUI.activateOverlayScreen(populationUpdateScreen);
					configureApplicationForNextIndividual();
				}
				else {
					mathProblemUI.activateOverlayScreen(allDoneScreen);
					state=FINISHED_ALL_POPULATIONS;
				}
			}
		}
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

}







