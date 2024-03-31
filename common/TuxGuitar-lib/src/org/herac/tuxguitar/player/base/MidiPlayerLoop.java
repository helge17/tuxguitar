package org.herac.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGMessagesManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class MidiPlayerLoop {
	private String title;
	private int loopSHeader;
	private int loopEHeader;
	
	public MidiPlayerLoop() {
		this.title = "";
		this.loopSHeader = -1;
		this.loopEHeader = -1;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStart(Integer loopSHeader) {
		this.loopSHeader = loopSHeader;
	}

	public void setEnd(Integer loopEHeader) {
		this.loopEHeader = loopEHeader;
	}

	public String getTitle() {
		return this.title;
	}

	public Integer getLoopSHeader() {
		return this.loopSHeader;
	}

	public Integer getLoopEHeader() {
		return this.loopEHeader;
	}
}


