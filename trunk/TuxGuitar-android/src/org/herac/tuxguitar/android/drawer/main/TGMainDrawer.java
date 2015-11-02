package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.util.TGContext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TGMainDrawer extends RelativeLayout {
	
	private TGMainDrawerActionHandler actionHandler;
	private TGMainDrawerListAdapter fileListAdapter;
	private TGMainDrawerListAdapter trackListAdapter;
	
	public TGMainDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.actionHandler = new TGMainDrawerActionHandler(this);
		this.fileListAdapter = new TGMainDrawerFileListAdapter(this);
		this.trackListAdapter = new TGMainDrawerTrackListAdapter(this);
	}
	
	public TGContext findContext() {
		return TGApplicationUtil.findContext(this);
	}
	
	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}
	
	public void onFinishInflate() {
		this.createTabs();
		this.fillFileListView();
		this.fillTrackListView();
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
		this.findViewById(R.id.main_drawer_song_info).setOnClickListener(this.getActionHandler().createOpenInfoAction());
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
	
	public void attachListeners() {
		this.fileListAdapter.attachListeners();
		this.trackListAdapter.attachListeners();
	}
	
	public void detachListeners() {
		this.fileListAdapter.detachListeners();
		this.trackListAdapter.detachListeners();
	}
	
	public TGMainDrawerActionHandler getActionHandler() {
		return actionHandler;
	}
}

