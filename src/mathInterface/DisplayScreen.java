package mathInterface;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.io.*;

import processing.core.PImage;

public class DisplayScreen extends Observable {


	
	private static ProcessingApplication processing;
	private static int HAS_NOT_BEEN_STARTED=-1; 

	private int offset_x;
	private int offset_y;
	private int delay_time;
	private int display_time=Integer.MAX_VALUE;
	private int activation_time=HAS_NOT_BEEN_STARTED;
	private int width, height;
	private int[] background_color=new int[]{255,255,255};
	private int background_alpha=255;

	private List<VariableText> text=new LinkedList<VariableText>();

	private int[] text_color=new int[]{0,0,0};
	
	private int text_size=20;
	private int text_offset_x, text_offset_y;
	private VariableText NEW_LINE = new VariableText("\n",text_color,text_offset_x,text_offset_y,text_size);

	private PImage img;
	private int img_offset_x, img_offset_y;
	
	
	private int time_elapsed_since_activated;

	private String screenName;

	public DisplayScreen(int x, int y, int width, int height){
		this.offset_x=x;
		this.offset_y=y;
		this.width=width;
		this.height=height;
		processing=ProcessingApplication.getInstance();
		
	
	}


	public void setImage(PImage img){
		this.img=img;
		if(this.img!=null){
			
			while(img.width>width||img.height>height/2-text_offset_y-text_size){
				img.resize(img.width/2, img.height/2);
			}
			
			img_offset_x=text_offset_x;
			img_offset_y=text_offset_y+text_size+img.width/2;

		}	
	}

	public void setName(String name){
		this.screenName=name;
	}

	public String getName(){
		return screenName;
	}

	public void setBackgroundColor(int[] bgColor){
		this.background_color=bgColor;
	}

	public void addVariableText(VariableText text){
		this.text.add(text);
	}


	public void setDelayBeforeDisplay(int delay) {
		this.delay_time=delay;
	}

	public void setDurationOfDisplay(int duration) {
		display_time=duration;
	}


	public boolean isActive(){
		return activation_time!=HAS_NOT_BEEN_STARTED;

	}

	public boolean hasTimedDuration(){
		return display_time<Integer.MAX_VALUE;
	}


	public void activate(){
		time_elapsed_since_activated=0;
		activation_time=processing.millis();
	}


	public boolean isDone() {
		return hasTimedDuration()&&time_elapsed_since_activated>delay_time+display_time;
	}


	void updateElapsedTime(){
		int current_time=processing.millis();
		time_elapsed_since_activated=Math.abs(current_time-activation_time);

	}


	public void update() {

		if(isActive()&&!isDone()){
			updateElapsedTime();
			if(time_elapsed_since_activated>delay_time){
				display();
			}
		}


		if(isDone()){
			setChanged();
			notifyObservers();			
		}

	}

	private void display(){

		processing.pushMatrix();
		renderBackground();
		renderImage();
		renderText();
		processing.popMatrix();
	}


	private void renderBackground(){	
		processing.translate(offset_x, offset_y);
		processing.fill(background_color[0],background_color[1],background_color[2],background_alpha);
		processing.rect(-width/2, -height/2, width, height);
	}

	void renderImage(){
		if(img!=null)
			processing.image(img, img_offset_x-img.width/2, img_offset_y-img.height/2);
	}

	void renderText(){
		//processing.textSize(text_size); //to do, refactor text size into vt class too
		for(VariableText vt : text){
			vt.display(processing);
		}
	
	}

	private String textToString() {
		StringBuilder s=new StringBuilder("");
		for(VariableText sVar : text)
			s.append(sVar.toText());
		return s.toString();
	}

	public void clearText() {
		text=new LinkedList<VariableText>();

	}

	public void setTransparent(boolean isTransparent) {
		background_alpha=(isTransparent? 0: 255);

	}

	public String getText() {
		return textToString();
	}

	//appends a new line character to rthe current text
	public void newLine() {
		text.add(NEW_LINE);

	}


	
	//returns an empty display screen with the same dimension, position and 
	//dimension and position of text and imagery as this display screen
	//(idea is to expedite makig overlays)
	public DisplayScreen getTransform() {
		DisplayScreen result=new DisplayScreen(offset_x, offset_y, width, height);
		result.img_offset_x=img_offset_x;
		result.img_offset_x=img_offset_y;
		result.text_offset_x=text_offset_x;
		result.text_offset_y=text_offset_y;
		result.text_size=text_size;
		return result;
	
	}


	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	
	public int getWidth() {
		// TODO Auto-generated method stub
		return width;
	}














}
