package org.herac.tuxguitar.android.view.channel;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.android.editor.TGEditorManager;
import org.herac.tuxguitar.util.TGContext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class TGChannelListView extends RelativeLayout {
	
	private TGChannelActionHandler actionHandler;
	
	public TGChannelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.actionHandler = new TGChannelActionHandler(this);
	}

	public void onFinishInflate() {
		this.fillListView();
		this.addListeners();
	}

	public void fillListView() {
		ListView listView = (ListView) findViewById(R.id.channel_list_items);
		listView.setAdapter(new TGChannelListAdapter(this));
		
		this.refreshListView();
	}
	
	public void addListeners() {
		TGEditorManager.getInstance(this.findContext()).addUpdateListener(new TGChannelEventListener(this));
	}
	
	public void updateItems() {
		this.refreshListView();
	}
	
	public void refreshListView() {
		ListView listView = (ListView) findViewById(R.id.channel_list_items);
		((TGChannelListAdapter) listView.getAdapter()).notifyDataSetChanged();
	}
	
	public TGContext findContext() {
		return ((TGApplication) getContext().getApplicationContext()).getContext();
	}

	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}

	public TGChannelActionHandler getActionHandler() {
		return actionHandler;
	}
}
