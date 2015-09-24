package org.herac.tuxguitar.android.view.tablature;

public class TGScroll {
	
	private TGScrollAxis x;
	private TGScrollAxis y;
	
	public TGScroll() {
		this.x = new TGScrollAxis();
		this.y = new TGScrollAxis();
	}

	public TGScrollAxis getX() {
		return x;
	}

	public TGScrollAxis getY() {
		return y;
	}
}
