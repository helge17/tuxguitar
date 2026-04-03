package app.tuxguitar.song.models;

import app.tuxguitar.song.factory.TGFactory;

public abstract class TGPickStroke {

	public static final int PICK_STROKE_NONE = 0;
	public static final int PICK_STROKE_UP = 1;
	public static final int PICK_STROKE_DOWN = -1;

	private int direction;

	public TGPickStroke(){
		this.direction = PICK_STROKE_NONE;
	}

	public int getDirection() {
		return this.direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public TGPickStroke clone(TGFactory factory){
		TGPickStroke tgPickStroke = factory.newPickStroke();
		tgPickStroke.copyFrom(this);
		return tgPickStroke;
	}

	public void copyFrom(TGPickStroke pickStroke){
		this.setDirection(pickStroke.getDirection());
	}
}
