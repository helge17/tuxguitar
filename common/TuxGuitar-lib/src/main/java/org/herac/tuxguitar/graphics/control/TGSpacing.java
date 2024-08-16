package org.herac.tuxguitar.graphics.control;


public abstract class TGSpacing {
	
	public static final int SCORE = 0x01;
	public static final int TABLATURE = 0x02;
	
	private int flags;
	private float[] spacing;
	private int[][] positions;
	
	public TGSpacing(TGLayout layout, int[][] positions, int len){
		this.flags = 0;
		this.flags |= ((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ? SCORE : 0 );
		this.flags |= ((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ? TABLATURE : 0 );
		this.positions = positions;
		this.spacing = new float[len];
	}
	
	public void clear(){
		for(int i = 0; i < this.spacing.length; i ++){
			this.spacing[i] = 0;
		}
	}
	
	public void setSize(int index, float size){
		if( size > this.spacing[ this.positions [this.flags -1] [index] ] ){
			this.spacing[ this.positions [this.flags -1] [index] ] = size;
		}
	}
	
	public float getSize(int index){
		return this.spacing[ this.positions [this.flags -1] [index] ];
	}
	
	public float getSize(){
		float spacing = 0;
		for(int i = 0;i < this.spacing.length; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
	
	public float getPosition(int index){
		float spacing = 0;
		for(int i = 0;i < this.positions[this.flags -1][index]; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
}
