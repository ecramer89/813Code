package mathInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MathProblemSet {
	       
		private int indexOfNext=-1;
		private List<MathProblem> problems;
		private int numProblemsAnsweredCorrectly;

		
		
		public MathProblemSet(){
			problems=new LinkedList<MathProblem>();
		}
	
	
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return indexOfNext<problems.size()-1;
		}

		
		public MathProblem next() {
			// TODO Auto-generated method stub
			indexOfNext++;
			return current();
		}
		
		
		public MathProblem current() {
			// TODO Auto-generated method stub
			return problems.get(indexOfNext);
		}

		
	

		public void add(MathProblem problem) {
			// TODO Auto-generated method stub
			problems.add(problem);
			
		}


		public void incrementNumProblemsAnsweredCorrectly() {
			numProblemsAnsweredCorrectly++;
			
		}
		
		
		
		
		
	
}
