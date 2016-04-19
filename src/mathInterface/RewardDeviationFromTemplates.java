package mathInterface;

public class RewardDeviationFromTemplates extends GAShakeupStrategy {

	
	public RewardDeviationFromTemplates(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor);
	}



	@Override
	public void applyChange() {
		/* System.out.println("message from reward deviation from templates:");
	      System.out.println("called apply change");*/
		jgapAdaptor.getFitnessFunction().invertSign();
		
	}



	@Override
	protected void revertChange() {
		 /*System.out.println("message from reward deviation from templates:");
	      System.out.println("called undo change");*/
		jgapAdaptor.getFitnessFunction().invertSign();
		
	}



	@Override
	public String getAuditInformation() {
		// TODO Auto-generated method stub
		return " We've inverted the sign of the fitness function. Now it will reward feedbacks that are FAR from best practice templates and that are CLOSE TO 'worst' templates.";
	}

	
}
