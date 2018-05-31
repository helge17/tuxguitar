package org.herac.tuxguitar.android.drawer.main;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.util.TGContext;

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
		this.addActionListeners();
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
		TabLayout tabLayout = (TabLayout) findViewById(R.id.main_drawer_tabHost);

		this.createTabSelectionListener(tabLayout);
		this.createTab(tabLayout, R.id.main_drawer_file_tab, this.findActivity().getString(R.string.main_drawer_file));
		this.createTab(tabLayout, R.id.main_drawer_track_tab, this.findActivity().getString(R.string.main_drawer_tracks));
	}

	public void createTab(TabLayout tabLayout, int layoutId, String indicator) {
		TabLayout.Tab tab = tabLayout.newTab();
		tab.setTag(layoutId);
		tab.setText(indicator);
		tabLayout.addTab(tab);
	}

	public void createTabSelectionListener(TabLayout tabLayout) {
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			public void onTabSelected(TabLayout.Tab tab) {
				updateVisibility((Integer) tab.getTag(), View.VISIBLE);
			}

			public void onTabUnselected(TabLayout.Tab tab){
				updateVisibility((Integer) tab.getTag(), View.GONE);
			}
			public void onTabReselected(TabLayout.Tab tab){
			}
		});
	}

	public void updateVisibility(Integer tabId, Integer visibility) {
		if( tabId != null ) {
			findViewById(tabId).setVisibility(visibility);
		}
	}

	public void addActionListeners() {
		this.findViewById(R.id.main_drawer_transport_mixer).setOnClickListener(this.getActionHandler().createOpenInstrumentsAction());
		this.findViewById(R.id.main_drawer_song_info).setOnClickListener(this.getActionHandler().createOpenInfoAction());
		this.findViewById(R.id.main_drawer_track_add_button).setOnClickListener(this.getActionHandler().createAddTrackAction());

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

