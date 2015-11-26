package org.herac.tuxguitar.android.drawer.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

public class TGMainDrawerTrackListAdapter extends TGMainDrawerListAdapter {

	private TGTrack selection;
	private TGEventListener eventListener;
	private List<TGMainDrawerTrackListItem> items;
	
	public TGMainDrawerTrackListAdapter(TGMainDrawer mainDrawer) {
		super(mainDrawer);
		
		this.items = new ArrayList<TGMainDrawerTrackListItem>();
		this.eventListener = new TGMainDrawerTrackListListener(this);
	}
	
	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		if( position >= 0 && position < this.items.size() ) {
			return this.items.get(position);
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TGMainDrawerTrackListItem item = (TGMainDrawerTrackListItem) this.getItem(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_main_drawer_check_item, parent, false));
		view.setOnClickListener(getMainDrawer().getActionHandler().createGoToTrackAction(item.getTrack()));
		
		CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.main_drawer_check_item);
		checkedTextView.setText(item.getLabel());
		checkedTextView.setChecked(Boolean.TRUE.equals(item.getSelected()));
		
		return view;
	}
	
	public boolean isUpdateRequired() {
		TGSong song = TGDocumentManager.getInstance(getMainDrawer().findContext()).getSong();
		if( song != null ) {
			int count = song.countTracks();
			if( count != this.getCount()) {
				return true;
			}
			for(int i = 0 ; i < count ; i++) {
				TGTrack track = song.getTrack(i);
				TGMainDrawerTrackListItem item = (TGMainDrawerTrackListItem) this.getItem(i);
				if( track == null || item == null ) {
					return true;
				}
				
				// Order changed
				if(!track.equals(item.getTrack())) {
					return true;
				}
				
				// Name changed
				if(!track.getName().equals(item.getLabel())) {
					return true;
				}
				
				// Selection changed
				if(!Boolean.valueOf(this.isSelected(track)).equals(item.getSelected())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSelected(TGTrack track) {
		return (this.selection != null && track != null && this.selection.equals(track) );
	}
	
	public void updateTrackItems() {
		this.items.clear();
		
		TGSong song = TGDocumentManager.getInstance(getMainDrawer().findContext()).getSong();
		if( song != null ) {
			Iterator<TGTrack> tracks = song.getTracks();
			while(tracks.hasNext()) {
				TGTrack track = tracks.next();
				
				TGMainDrawerTrackListItem item = new TGMainDrawerTrackListItem();
				item.setTrack(track);
				item.setLabel(track.getName());
				item.setSelected(isSelected(track));
				this.items.add(item);
			}
		}
	}
	
	public void updateSelection() {
		this.selection = TGSongViewController.getInstance(getMainDrawer().findContext()).getCaret().getTrack();
		if( this.isUpdateRequired() ) {
			this.updateTracks();
		}
	}
	
	public void updateTracks() {
		this.updateTrackItems();
		this.notifyDataSetChanged();
	}
	
	public void attachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).addUpdateListener(this.eventListener);
	}
	
	public void detachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).removeUpdateListener(this.eventListener);
	}
}
