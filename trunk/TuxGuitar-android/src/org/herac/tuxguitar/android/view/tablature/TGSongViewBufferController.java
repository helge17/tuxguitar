package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGSongViewBufferController {
	
	private int selection;
	private TGSongViewController songView;
	private TGResourceBuffer resourceBuffer;
	
	public TGSongViewBufferController(TGSongViewController songView) {
		this.songView = songView;
	}
	
	public void updateSelection() {
		int selection = this.songView.getTrackSelection();
		if( selection != this.selection ) {
			this.selection = selection;
			if( this.selection != -1 ) {
				this.disposeBufferLater(this.getResourceBuffer());
				this.resourceBuffer = null;
			}
		}
	}
	
	public TGResourceBuffer getResourceBuffer() {
		if( this.resourceBuffer == null ) {
			this.resourceBuffer = new TGResourceBuffer();
		}
		return this.resourceBuffer;
	}
	
	public void disposeBufferLater(final TGResourceBuffer buffer) {
		TGSynchronizer.getInstance(this.songView.getContext()).executeLater(new Runnable() {
			public void run() {
				buffer.disposeAllResources();
			}
		});
	}
	
}
