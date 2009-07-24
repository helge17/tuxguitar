package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

public abstract class TGSpacing {
	
	public static final int SCORE = 0x01;
	public static final int TABLATURE = 0x02;
	
	private int flags;
	private int[] spacing;
	private int[][] positions;
	
	public TGSpacing(ViewLayout layout, int[][] positions, int len){
		this.flags = 0;
		this.flags |= ((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ? SCORE : 0 );
		this.flags |= ((layout.getStyle() & ViewLayout.DISPLAY_TABLATURE) != 0 ? TABLATURE : 0 );
		this.positions = positions;
		this.spacing = new int[len];
	}
	
	public void clear(){
		for(int i = 0; i < this.spacing.length; i ++){
			this.spacing[i] = 0;
		}
	}
	
	public void setSize(int index,int size){
		if( size > this.spacing[ this.positions [this.flags -1] [index] ] ){
			this.spacing[ this.positions [this.flags -1] [index] ] = size;
		}
	}
	
	public int getSize(int index){
		return this.spacing[ this.positions [this.flags -1] [index] ];
	}
	
	public int getSize(){
		int spacing = 0;
		for(int i = 0;i < this.spacing.length; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
	
	public int getPosition(int index){
		int spacing = 0;
		for(int i = 0;i < this.positions[this.flags -1][index]; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
}
