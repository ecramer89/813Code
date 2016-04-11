package mathInterface;

public class AdjustWeights extends GAShakeupStrategy {

	public AdjustWeights(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor); 

		}

		double last_adjust;



		@Override
		public void applyChange() {
			/*System.out.println("Message from adjust weights");
			System.out.println("Apply change called");*/
			last_adjust=ProcessingApplication.constrain(Math.random(),.1, .5);
			//System.out.println("Adjustment: "+last_adjust);
			if(Math.random()<.5) last_adjust*=-1;
			jgapAdaptor.getFitnessFunction().adjustWeights(last_adjust);
			
		}



		@Override
		protected void revertChange() {
			/*System.out.println("Message from adjust weights");
			System.out.println("revert change called");*/
			last_adjust*=-1;
			jgapAdaptor.getFitnessFunction().adjustWeights(last_adjust);
		
		}

	

	}
