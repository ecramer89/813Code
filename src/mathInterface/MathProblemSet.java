package mathInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MathProblemSet {


	private static final int VALUE_FOR_CORRECT = 1;
	private static final int VALUE_FOR_INCORRECT = 0;
	private int indexOfNext=-1;
	private List<MathProblem> problems;
	private int[] user_results;



	public MathProblemSet(int size){
		problems=new LinkedList<MathProblem>();
		user_results=new int[size];
	}


	public boolean hasNext() {
		return indexOfNext<problems.size()-1;
	}


	public MathProblem next() {
		indexOfNext++;
		return current();
	}

	public void recordResultForCurrentProblem(boolean correct){
		if(indexOfNext<user_results.length)
			user_results[indexOfNext]=(correct? VALUE_FOR_CORRECT:VALUE_FOR_INCORRECT);
		else throw new ArrayIndexOutOfBoundsException("index of next exceeds array indices in problem set");
	}


	public MathProblem current() {

		return problems.get(indexOfNext);
	}




	public void add(MathProblem problem) {

		problems.add(problem);

	}


	public int[] getResults() {
		// TODO Auto-generated method stub
		return 	user_results;
	}








}
