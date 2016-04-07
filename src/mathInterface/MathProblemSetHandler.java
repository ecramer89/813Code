package mathInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MathProblemSetHandler {


	static MathProblemSetHandler instance;
	public static final int MATH_PROBLEMS_PER_SET=ProcessingApplication.MATH_PROBLEMS_PER_SET;
	public static final int MAX_ARGUMENT_VALUE=ProcessingApplication.MAX_ARGUMENT_VALUE;
	public static final int MAX_DIGITS_IN_ANSWER=ProcessingApplication.MAX_DIGITS_IN_ANSWER;

	static MathProblemSet currentProblemSet;

	//the current problem
	MathProblem currentProblem;



	private MathProblemSetHandler(){
		initializeNewProblemSet();
	}

	public static MathProblemSetHandler getInstance(){
		if(instance==null) instance=new MathProblemSetHandler();
		return instance;
	}


	/*create a new set of 10 random math problems */
	public void createRandomProblems(){

		for(int i=0;i<MATH_PROBLEMS_PER_SET;i++){
			currentProblemSet.add(MathProblem.makeRandomProblem(0,MAX_ARGUMENT_VALUE));
		}

	}


	public void initializeNewProblemSet() {
		currentProblemSet=new MathProblemSet(MATH_PROBLEMS_PER_SET);
		
	
	}


	public boolean currentProblemSetFinished(){
		return !currentProblemSet.hasNext();
	}


	public void prepareNextProblem() {
		if(currentProblemSet.hasNext()){
			currentProblemSet.next();
			currentProblem=currentProblemSet.current();
		}

	}



	public void addDigitToAnswer(int int1) {

		if(currentProblem.numDigitsInAnswer()<MAX_DIGITS_IN_ANSWER){
			currentProblemSet.current().addDigitToAnswer(int1);
		}

	}

	public void removeDigitFromAnswer() {
		if(currentProblem.numDigitsInAnswer()>0){
			currentProblemSet.current().removeDigitFromAnswer();
		}
	}

	public MathProblem currentProblem() {
		return currentProblem;
	}

	public int currentAnswer() {
		return currentProblem.currentAnswer();
	}

	public void updateUserScore() {
		boolean correct=currentProblem().getData().correct;
		currentProblemSet.recordResultForCurrentProblem(correct);
	}

	public int[] currentAnswerDigits() {

		return currentProblem.getAnswerDigits();
	}

	public int[] getResultsForProblemSet() {

		return currentProblemSet.getResults();
	}


	
	
	
	
	













}
