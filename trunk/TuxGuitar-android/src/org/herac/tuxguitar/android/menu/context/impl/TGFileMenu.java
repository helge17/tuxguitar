package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGFileMenu extends TGContextMenuBase {
	
	public TGFileMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_file);
		inflater.inflate(R.menu.menu_file, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.initializeItem(menu, R.id.menu_file_new, this.createActionProcessor(TGNewSongAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_file_open, this.createBrowserActionProcessor(TGBrowserPrepareForReadAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_file_save_as, this.createBrowserActionProcessor(TGBrowserPrepareForWriteAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_file_save, this.createBrowserActionProcessor(TGBrowserSaveCurrentElementAction.NAME), !running && this.isCurrentElementWritable());
	}
	
	public TGActionProcessorListener createBrowserActionProcessor(String actionId) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), this.findBrowserSession());
		return tgActionProcessor;
	}
	
	public TGBrowserSession findBrowserSession() {
		return TGBrowserManager.getInstance(this.findContext()).getSession();
	}
	
	public boolean isCurrentElementWritable() {
		TGBrowserSession session = this.findBrowserSession();
		
		return ( session.getCurrentElement() != null && session.getCurrentElement().isWritable() );
	}
}