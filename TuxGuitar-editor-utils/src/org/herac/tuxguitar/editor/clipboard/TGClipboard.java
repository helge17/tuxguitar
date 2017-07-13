package org.herac.tuxguitar.editor.clipboard;

import org.herac.tuxguitar.song.helpers.TGSongSegment;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGClipboard {
	
	private TGSongSegment data;
	
	private TGClipboard() {
		super();
	}
	
	public TGSongSegment getData() {
		return data;
	}
	
	public void setData(TGSongSegment data) {
		this.data = data;
	}
	
	public static TGClipboard getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGClipboard.class.getName(), new TGSingletonFactory<TGClipboard>() {
			public TGClipboard createInstance(TGContext context) {
				return new TGClipboard();
			}
		});
	}
}
