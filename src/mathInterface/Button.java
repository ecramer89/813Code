package mathInterface;

import processing.core.*;

public class Button {


	int x, y, width, height;
	String message="";
	int id;
	float[] defaultColor=new float[]{120,120,120};
	float[] pressedColor;
	float[] displayColor;
	float[] messageColor=new float[]{255,255,255};
	int messageSize;
	
	boolean pressed;
	long millisUponPressed;
	int pressedColorTime=30;
	
	public Button(int x, int y, int width, int height, int id){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
		setPressedAndDisplayColors();
	}
	
	public void setColor(float[] color){
		this.defaultColor=color;
		setPressedAndDisplayColors();
	}
	
	private void setPressedAndDisplayColors(){
		pressedColor=new float[]{defaultColor[0]/2, defaultColor[1]/2, defaultColor[2]/2};
		displayColor=defaultColor;
	}
	
	
	public void setMessage(String message,int messageSize){
		this.message=message;
		this.messageSize=Math.min(messageSize, width/message.length());
	}
	
	
	public int getId(){
		return id;
	}


	public boolean wasPressed(int mouseX, int mouseY) {
		return Math.abs(mouseX-x)<width/2&&Math.abs(mouseY-y)<height/2;
	}
	
	
	public void update(PApplet processing){
		if(pressed){flash(processing);}
		display(processing);
	}
	
	
	private void flash(PApplet processing){
			long currentMillis=processing.millis();
			if(Math.abs(currentMillis-millisUponPressed)>pressedColorTime){
				pressed=false;
				displayColor=defaultColor;
			}	
	}
	
	
	private void display(PApplet processing){
		processing.pushMatrix();
		processing.translate(x,y);
		   processing.fill(displayColor[0],displayColor[1], displayColor[2]);
		   processing.rect(-width/2, -height/2, width, height,processing.ROUND);	
		   processing.textSize(messageSize);
		   processing.fill(messageColor[0],messageColor[1],messageColor[2]);
		   processing.text(message, 0,0);
		   processing.popMatrix();
	}
	
	
	public void flashToIndicatePressed(PApplet processing){
		millisUponPressed=processing.millis();
	    pressed=true;
	    displayColor=pressedColor;
	}

	public boolean isPressed() {
		// TODO Auto-generated method stub
		return pressed;
	}
	
	
	
	
	
}
