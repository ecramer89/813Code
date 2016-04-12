package mathInterface;
import java.util.Stack;

import processing.core.*;


public class MathProblem {


	static final String EQUALS="=";
	static final String X="X";
	static final String NEGATION="-";


	MathOperator op;
	int arg1, arg2, solution;

	Integer answer=new Integer(0);
	int numDigitsInAnswer;

	String problemAsString;



	public MathProblem(MathOperator op, int arg1, int arg2){
		this.op=op;
		this.arg1=arg1;
		this.arg2=arg2;
		//cache answer and string representation
		solution=(int)(MathOperator.resolve(op, arg1, arg2));

		createRepresentationOfProblemAsString();


	}


	void createRepresentationOfProblemAsString(){
		StringBuilder builder=new StringBuilder();
		builder.append(arg1);
		builder.append(" ");
		builder.append(op.toString());
		builder.append(" ");
		builder.append(arg2);
		builder.append(" ");
		builder.append(EQUALS);
		problemAsString=builder.toString();
	}

	public int numDigitsInAnswer(){
		return numDigitsInAnswer;
	}

	public static MathProblem makeRandomProblem(int min_arg, int max_arg){
		int arg1, arg2;
		MathOperator op;

		arg1=makeRandomArgument(min_arg,max_arg);
		arg2=makeRandomArgument(min_arg, max_arg);
		op = MathOperator.getRandomOperator();
        
		if(divisionByZero(arg1, arg2, op)){
			op=MathOperator.MULTIPLICATION;
		}
		
		//for the time being, prevent answers that have negatives.
		if (answerWouldBeNegative(arg1, arg2,op)){
			int tmp=arg1;
			arg1=arg2;
			arg2=tmp;
		}
	

		return new MathProblem(op,arg1,arg2);
	}


	private static boolean divisionByZero(int arg1, int arg2, MathOperator op) {

		return arg2==0&&op==MathOperator.DIVISION;
	}


	private static boolean answerWouldBeNegative(int arg1, int arg2,
			MathOperator op) {
		return arg1<arg2 && op==MathOperator.SUBTRACTION;

	}

	private static int makeRandomArgument(int min_arg, int max_arg){
		return (int) (min_arg+Math.random()*(max_arg-min_arg));
	}


	public void addDigitToAnswer(int digit) {

		answer*=10;
		answer+=digit;
		numDigitsInAnswer++;
	}

	public void removeDigitFromAnswer() {
		answer/=10;
		numDigitsInAnswer--;
	}

	public ProblemData getData() {
		ProblemData data=new ProblemData();
		data.answer=answer;
		data.solution=solution;
		data.correct=answer==solution;
		return data;
	}


	public String toString(){
		return problemAsString;
	}


	public Integer currentAnswer() {
		return answer;
	}


	/* param significance: power of 10 that corresponds to desired signifance of digit.
	 *0 = least 
	 *1= second least...
	 *2 = third least...
	 */
	public int[] getAnswerDigits(){

		int[] digits=new int[numDigitsInAnswer];
		int answer_quo=answer;
		for(int i=0;i<numDigitsInAnswer;i++){
			int digit=answer_quo%10;
			answer_quo/=10;
			digits[numDigitsInAnswer-1-i]=digit;
		}
		return digits;
	}


	public int[] getSolutionDigits(int numDigits) {

		int[] temp=new int[numDigits];
		int solution_quo=solution;
		int i=temp.length-1;
		int num_digits_in_solution=0;
		for(;i>-1;i--){
			
			int digit=solution_quo%10;
			temp[i]=digit;
			num_digits_in_solution++;
			
			solution_quo/=10;
			if(solution_quo==0) break;
			
		}

		int[] result=new int[num_digits_in_solution];
		for(int j=0;j<num_digits_in_solution;j++){
			result[j]=temp[i];
			i++;	
		}
		
		
		return result;
	}


	public boolean currentAnswerIsCorrect(){
		return answer==solution;
	}


}








