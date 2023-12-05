package org.herac.tuxguitar.android.view.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.view.util.TGProcess;
import org.herac.tuxguitar.android.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class TGChannelListView extends RelativeLayout {
	
	private TGChannelActionHandler actionHandler;
	private TGProcess updateItemsProcess;
	
	public TGChannelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.actionHandler = new TGChannelActionHandler(this);
		this.createSyncProcesses();
	}
	
	public void onFinishInflate() {
		this.fillListView();
		this.addListeners();
		this.updateItems();
	}

	public void fillListView() {
		ListView listView = (ListView) findViewById(R.id.channel_list_items);
		listView.setAdapter(new TGChannelListAdapter(this));
	}
	
	public void addListeners() {
		TGEditorManager.getInstance(this.findContext()).addUpdateListener(new TGChannelEventListener(this));
	}
	
	public void updateItems() {
		List<TGChannel> channels = new ArrayList<TGChannel>();
		TGDocumentManager documentManager = TGDocumentManager.getInstance(this.findContext());
		if( documentManager.getSong() != null ) {
			Iterator<TGChannel> it = documentManager.getSong().getChannels();
			while( it.hasNext() ) {
				channels.add(it.next());
			}
		}
		this.refreshListView(channels);
	}
	
	public void refreshListView(List<TGChannel> channels) {
		ListView listView = (ListView) findViewById(R.id.channel_list_items);
		
		TGChannelListAdapter tgChannelListAdapter = ((TGChannelListAdapter) listView.getAdapter());
		tgChannelListAdapter.setChannels(channels);
		tgChannelListAdapter.notifyDataSetChanged();
	}
	
	public void fireUpdateProcess() {
		this.updateItemsProcess.process();
	}
	
	public TGContext findContext() {
		return TGApplicationUtil.findContext(this);
	}

	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}

	public TGChannelActionHandler getActionHandler() {
		return actionHandler;
	}
	
	public void createSyncProcesses() {
		this.updateItemsProcess = new TGSyncProcessLocked(this.findContext(), new Runnable() {
			public void run() {
				updateItems();
			}
		});
	}
}
