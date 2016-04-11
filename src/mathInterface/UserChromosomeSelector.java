package mathInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.Population;


public class UserChromosomeSelector  {

	double prop_from_head;
	double prop_from_tail;
	
	
	public UserChromosomeSelector(double prop_from_head){
		adjustProportionsOfFittestToLeastFit(prop_from_head);
	
	}
	
	public void adjustProportionsOfFittestToLeastFit(double adjust_most_fit){
		
		/*System.out.println("message from selector: ");
		System.out.println("adjusting proportions");
		System.out.println("adjustment: "+adjust_most_fit);*/
		prop_from_head+=adjust_most_fit;
		prop_from_head=ProcessingApplication.constrain(prop_from_head, 0, 1);
		prop_from_tail=1-prop_from_head;
		/*System.out.println("from head: "+prop_from_head);
		System.out.println("from tail: "+prop_from_tail);*/
		
	}
	
	
	public void selectChromosomesToDisplayToUser(int a_howManyToSelect,
			List<IChromosome> from_pop, Map<IChromosome,Double> to_set){
		
		int from_tail=(int)(a_howManyToSelect*prop_from_tail);
		
		if(from_tail==0){
			from_tail++;
			
		}
		
		int from_head=a_howManyToSelect-from_tail;
		
		
		System.out.println("message from selector: ");
		System.out.println("should select: "+a_howManyToSelect);
		System.out.println("from head: "+from_head);
		System.out.println("from tail: "+from_tail);
		
		
		//confused- it was in order of fitness before? will i need to sort?
		int idx=0;
		for(int i=0;i<from_head;i++){
			IChromosome to_Add=from_pop.get(idx);
			System.out.println("idx: "+idx);
			System.out.println("adding: "+to_Add);
			to_set.put(to_Add,(double) 0);
			idx++;
			
		}
		
		idx=from_pop.size()-1;
		for(int j=0; j<from_tail;j++){
			IChromosome to_Add=from_pop.get(idx);
			System.out.println("idx: "+idx);
			System.out.println("adding: "+to_Add);
			to_set.put(to_Add,(double) 0);
			idx--;
		}
		
	}
		
	
	

	

}
