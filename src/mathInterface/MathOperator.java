package mathInterface;

public enum MathOperator {
	
	MULTIPLICATION("x"),
	DIVISION("/"),
	SUBTRACTION("-"),
	ADDITION("+");

	
	String operator;
	
	public static float resolve(MathOperator op, float arg1, float arg2){
		switch(op){
		case MULTIPLICATION:
			return arg1*arg2;
		case DIVISION:
			return arg1/arg2;
		case SUBTRACTION:
			return arg1-arg2;
		case ADDITION:
			return arg1+arg2;
		}
	
		return 0;
	}
	
	
	
	
	MathOperator(String operator){
		this.operator=operator;
	}
	
	public String toString(){
		return operator;
	}




	public static MathOperator getRandomOperator() {
		MathOperator[] values=values();
       int rand_index = (int)(Math.random()*values.length);
       return values[rand_index];
	}

	
	
	
	

}
