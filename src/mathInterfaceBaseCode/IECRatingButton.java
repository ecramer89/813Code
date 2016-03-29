package mathInterfaceBaseCode;
import processing.core.*;


public class IECRatingButton {
	static int width, height;
	static float buttonColor;
	static int labelSize;
	static float labelColor;
	static IECMathInterfaceProcessingApplication processingUI;
	
	
	private int x, y, rating;
	
	public IECRatingButton(int x, int y, int rating){
		this.x=x;
		this.y=y;
		this.rating=rating;
	}
	
	public static void setProcessingUI(IECMathInterfaceProcessingApplication processingUI){
		IECRatingButton.processingUI=processingUI;
	}
	
	public static void setWidth(int width){
		IECRatingButton.width=width;
	}
	
   public static void setHeight(int height){
	   IECRatingButton.height=height;
	}
   
   public static void setButtonColor(float color){
	   IECRatingButton.buttonColor=color;
		
  	}
   
   public static void setLabelColor(float color){
	   IECRatingButton.labelColor=color;
 	}
   
   public static void setLabelSize(int ratingLabelSize){
	   IECRatingButton.labelSize=ratingLabelSize;
 	}
   
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getRating(){
		return rating;
	}
	
	public void render(){
		processingUI.pushMatrix();
		processingUI.translate(x,y);
		processingUI.fill(buttonColor);
		processingUI.rect(-width/2, -height/2, width, height);
		processingUI.textSize(labelSize);
		processingUI.fill(labelColor);
		processingUI.text(rating,0,height/2);
		processingUI.popMatrix();
		
	}
	
	public boolean wasClicked(int x, int y){
		return Math.abs(x-this.x)<width/2&&Math.abs(y-this.y)<height/2;
	}

}
