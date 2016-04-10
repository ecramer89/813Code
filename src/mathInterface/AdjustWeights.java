package mathInterface;

public class AdjustWeights extends GAShakeupStrategy {

	public AdjustWeights(JGAPAdapter jgapAdaptor) {
		super(jgapAdaptor); 

		}

		double last_adjust;



		@Override
		public void applyChange() {
			last_adjust=Math.random();
			if(Math.random()<.5) last_adjust*=-1;
			jgapAdaptor.getFitnessFunction().adjustWeights(last_adjust);
			
		}



		@Override
		protected void revertChange() {
			last_adjust*=-1;
			jgapAdaptor.getFitnessFunction().adjustWeights(last_adjust);
		
		}

	

	}
