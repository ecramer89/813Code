package mathInterface;

public class VariableText {

	private Object o=new Object();
	private int[] color;
	int x, y, size;

	public VariableText(Object o, int[] color, int x, int y, int size) {
		if(o!=null) this.o=o;
		this.color=color;
		this.x=x;
		this.y=y;
		this.size=size;
		
	}
	
	
	public String toText() {
		return o.toString();
	}

	public void update(Object newO){
		o=newO;
	}
	
	public int[] getColor(){
		return color;
	}

	
	public int toInt() {
		if(o instanceof Integer){
			Integer i = (Integer)o;
			return i.intValue();
		}
		return o.hashCode();
	}

	public void display(ProcessingApplication processing) {
		processing.textSize(size);
		processing.textAlign(processing.CENTER);
		processing.fill(color[0],color[1], color[2],255);
		processing.text(toText(),x,y);
		
	}

}
