package mathInterface;

import java.util.List;
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
			List<IChromosome> from_pop, Set<IChromosome> to_set){
		
		int from_tail=(int)(a_howManyToSelect*prop_from_tail);
		int from_head=(int)(a_howManyToSelect*prop_from_head);
		
		if(from_tail==0){
			from_tail++;
			from_head--;
		}
		
		/*System.out.println("message from selector: ");
		System.out.println("from head: "+from_head);
		System.out.println("from tail: "+from_tail);*/
		
		int idx=0;
		for(int i=0;i<from_head;i++){
			to_set.add(from_pop.get(idx));
			idx++;
		}
		
		idx=from_pop.size()-1;
		for(int j=0; j<from_tail;j++){
			to_set.add(from_pop.get(idx));
			idx--;
		}
		
	}
		
	
	

	

}
