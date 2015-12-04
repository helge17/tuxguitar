package org.herac.tuxguitar.android.menu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.util.TGProcess;
import org.herac.tuxguitar.android.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("UseSparseArrays")
public class TGToggleStyledIconHelper implements TGEventListener {
	
	private TGContext context;
	private TGActivity activity;
	private TGProcess updateIcons;
	private Menu menu;
	private Map<Integer, Drawable> styledIcons;
	private List<TGToggleStyledIconHandler> handlers;
	
	
	public TGToggleStyledIconHelper(TGContext context) {
		this.context = context;
		this.styledIcons = new HashMap<Integer, Drawable>();
		this.handlers = new ArrayList<TGToggleStyledIconHandler>();
		this.createSyncProcesses();
		this.appendListeners();
	}
	
	public void initialize(TGActivity activity, Menu menu) {
		this.activity = activity;
		this.menu = menu;
		this.updateIcons.process();
	}
	
	public void addHandler(TGToggleStyledIconHandler handler) {
		this.handlers.add(handler);
	}
	
	public void appendListeners() {
		TGEditorManager.getInstance(this.context).addUpdateListener(this);
	}
	
	public Drawable findStyledDrawable(Integer style) {
		if( this.styledIcons.containsKey(style) ) {
			return this.styledIcons.get(style);
		}
		
		Drawable drawable = null;
		TypedArray typedArray = this.activity.obtainStyledAttributes(style, new int[] {android.R.attr.src});
		if( typedArray != null ) {
			drawable = typedArray.getDrawable(0);
		}
		this.styledIcons.put(style, drawable);
		
		return drawable;
	}
	
	public void updateIcon(TGToggleStyledIconHandler handler) {
		if( this.menu != null && handler != null ) {
			Integer style = handler.resolveStyle();
			MenuItem menuItem = this.menu.findItem(handler.getMenuItemId());
			if( style != null && menuItem != null ) {
				Drawable drawable = this.findStyledDrawable(style);
				if( drawable != null ) {
					if( menuItem.getIcon() == null || !menuItem.getIcon().equals(drawable) ) {
						menuItem.setIcon(drawable);
					}
				}
			}
		}
	}
	
	public void updateIcons() {
		if( this.menu != null ) {
			for(TGToggleStyledIconHandler handler : this.handlers) {
				this.updateIcon(handler);
			}
		}
	}
	
	public void createSyncProcesses() {
		this.updateIcons = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateIcons();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateIcons.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}