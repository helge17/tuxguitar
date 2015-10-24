package org.herac.tuxguitar.android.drawer.main;

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
	
	public TGMainDrawerTrackListAdapter(TGMainDrawer mainDrawer) {
		super(mainDrawer);
		
		this.eventListener = new TGMainDrawerTrackListListener(this);
	}
	
	@Override
	public int getCount() {
		TGSong song = TGDocumentManager.getInstance(getMainDrawer().findContext()).getSong();
		if( song != null ) {
			return song.countTracks();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		TGSong song = TGDocumentManager.getInstance(getMainDrawer().findContext()).getSong();
		if( song != null && song.countTracks() > position ) {
			return song.getTrack(position);
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TGTrack track = (TGTrack) this.getItem(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_main_drawer_check_item, parent, false));
		view.setOnClickListener(getMainDrawer().getActionHandler().createGoToTrackAction(track));
		
		CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.main_drawer_check_item);
		checkedTextView.setText(track.getName());
		checkedTextView.setChecked(isSelected(track));
		
		return view;
	}
	
	public boolean isSelected(TGTrack track) {
		return (this.selection != null && track != null && this.selection.equals(track) );
	}
	
	public void updateSelection() {
		TGTrack track = TGSongViewController.getInstance(getMainDrawer().findContext()).getCaret().getTrack();
		if(!this.isSelected(track)) {
			this.updateTracks();
		}
		this.selection = track;
	}
	
	public void updateTracks() {
		this.notifyDataSetChanged();
	}
	
	public void attachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).addUpdateListener(this.eventListener);
	}
	
	public void detachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).removeUpdateListener(this.eventListener);
	}
}
