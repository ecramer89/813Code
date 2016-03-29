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
		
		System.out.println(answer);
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
		int arg1=makeRandomArgument(min_arg,max_arg);
		int arg2=makeRandomArgument(min_arg, max_arg);
		MathOperator op = MathOperator.getRandomOperator();
		return new MathProblem(op,arg1,arg2);
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


}








