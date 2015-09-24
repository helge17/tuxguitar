package org.herac.tuxguitar.android.view.tool;

import java.util.Iterator;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.TGActionProcessor;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

import android.content.Context;
import android.util.AttributeSet;

public class TGFileToolView extends TGToolView {
	
	public TGFileToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_file_new).setOnClickListener(this.createActionListener(TGNewSongAction.NAME));
		this.findViewById(R.id.menu_file_open).setOnClickListener(this.createBrowserActionListener(TGBrowserPrepareForReadAction.NAME));
		this.findViewById(R.id.menu_file_save_as).setOnClickListener(this.createBrowserActionListener(TGBrowserPrepareForWriteAction.NAME));
		this.findViewById(R.id.menu_file_save).setOnClickListener(this.createBrowserActionListener(TGBrowserSaveCurrentElementAction.NAME));
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.updateItem(R.id.menu_file_new, !running);
		this.updateItem(R.id.menu_file_open, !running);
		this.updateItem(R.id.menu_file_save_as, !running);
		this.updateItem(R.id.menu_file_save, (!running && this.isCurrentElementWritable()));
	}
	
	public TGToolViewItemListener createBrowserActionListener(String actionId) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), this.findBrowserSession());
		return new TGToolViewItemListener(tgActionProcessor);
	}
	
	public TGBrowserSession findBrowserSession() {
		return TGBrowserManager.getInstance(this.findContext()).getSession();
	}
	
	public boolean isCurrentElementWritable() {
		TGBrowserSession session = this.findBrowserSession();
		if( session.getCurrentElement() != null && session.getCurrentElement().isWritable() ) {
			TGFileFormat fileFormat = session.getCurrentFormat(); 
			if( fileFormat != null ) {
				Iterator<?> outputStreams = TGFileFormatManager.getInstance(findContext()).getOutputStreams();
				while( outputStreams.hasNext() ) {
					TGOutputStreamBase outputStream = (TGOutputStreamBase) outputStreams.next();
					if( outputStream.getFileFormat().getName().equals(fileFormat.getName()) ) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
