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

	
}
