package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.android.editor.TGEditorManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TGMainDrawer extends RelativeLayout {
	
	private TGMainDrawerActionHandler actionHandler;
	private TGMainDrawerListAdapter fileListAdapter;
	private TGMainDrawerListAdapter trackListAdapter;
	private TGEventListener eventListener;
	private Drawable transportPlayDrawable;
	private Drawable transportStopDrawable;
	
	public TGMainDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.actionHandler = new TGMainDrawerActionHandler(this);
		this.fileListAdapter = new TGMainDrawerFileListAdapter(this);
		this.trackListAdapter = new TGMainDrawerTrackListAdapter(this);
		this.eventListener = new TGMainDrawerListener(this);
	}
	
	public TGContext findContext() {
		return ((TGApplication) getContext().getApplicationContext()).getContext();
	}
	
	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}
	
	public void onFinishInflate() {
		this.createTabs();
		this.fillFileListView();
		this.fillTrackListView();
		this.fillTransportDrawables();
		this.addTransportListeners();
	}
	
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		this.attachListeners();
	}
	
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		this.detachListeners();
	}
	
	public void createTabs() {
		TabHost tabHost = (TabHost)findViewById(R.id.main_drawer_tabHost);
		tabHost.setup();
		
		this.createTab(tabHost, R.id.main_drawer_file_items, this.findActivity().getString(R.string.main_drawer_file));
		this.createTab(tabHost, R.id.main_drawer_track_items, this.findActivity().getString(R.string.main_drawer_tracks));
	}
	
	public void createTab(TabHost tabHost, int layoutId, String indicator) {
		 TabSpec tabSpec = tabHost.newTabSpec(Integer.toString(layoutId));
		 tabSpec.setIndicator(indicator);
		 tabSpec.setContent(layoutId);
		 tabHost.addTab(tabSpec);
	}
	
	public void addTransportListeners() {
		this.findViewById(R.id.main_drawer_transport_mixer).setOnClickListener(this.getActionHandler().createOpenInstrumentsAction());
		this.findViewById(R.id.main_drawer_transport_play).setOnClickListener(this.getActionHandler().createTransportPlayAction());
	}
	
	public void fillFileListView() {
		this.fillListView(R.id.main_drawer_file_items, this.fileListAdapter);
	}
	
	public void fillTrackListView() {
		this.fillListView(R.id.main_drawer_track_items, this.trackListAdapter);
	}
	
	public void fillListView(int id, TGMainDrawerListAdapter adapter) {
		ListView listView = (ListView) findViewById(id);
		listView.setAdapter(adapter);
	}
	
	public void fillTransportDrawables() {
		this.transportPlayDrawable = this.findStyledDrawable(R.style.MainDrawerImageButtonPlay);
		this.transportStopDrawable = this.findStyledDrawable(R.style.MainDrawerImageButtonStop);
	}
	
	public Drawable findStyledDrawable(int style) {
		TypedArray typedArray = getContext().obtainStyledAttributes(style, new int[] {android.R.attr.src});
		if( typedArray != null ) {
			return typedArray.getDrawable(0);
		}
		return null;
	}
	
	public void updateTransport() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		Drawable drawable = (running ? this.transportStopDrawable : this.transportPlayDrawable);
		if( drawable != null ) {
			ImageButton imageButton = (ImageButton) this.findViewById(R.id.main_drawer_transport_play);
			if( imageButton.getDrawable() == null || !imageButton.getDrawable().equals(drawable) ) {
				imageButton.setImageDrawable(drawable);
			}
		}
	}
	
	public void attachListeners() {
		this.fileListAdapter.attachListeners();
		this.trackListAdapter.attachListeners();
		TGEditorManager.getInstance(findContext()).addUpdateListener(this.eventListener);
	}
	
	public void detachListeners() {
		this.fileListAdapter.detachListeners();
		this.trackListAdapter.detachListeners();
		TGEditorManager.getInstance(findContext()).removeUpdateListener(this.eventListener);
	}
	
	public TGMainDrawerActionHandler getActionHandler() {
		return actionHandler;
	}
}

