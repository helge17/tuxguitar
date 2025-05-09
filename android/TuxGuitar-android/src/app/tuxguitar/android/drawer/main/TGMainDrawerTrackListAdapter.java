package app.tuxguitar.android.drawer.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import app.tuxguitar.android.R;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.android.view.util.TGProcess;
import app.tuxguitar.android.view.util.TGSyncProcessLocked;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGMainDrawerTrackListAdapter extends TGMainDrawerListAdapter {

	private TGTrack selection;
	private TGEventListener eventListener;
	private List<TGMainDrawerTrackListItem> items;
	private TGProcess updateSelectionProcess;
	private TGProcess updateTracksProcess;

	public TGMainDrawerTrackListAdapter(TGMainDrawer mainDrawer) {
		super(mainDrawer);

		this.items = new ArrayList<TGMainDrawerTrackListItem>();
		this.eventListener = new TGMainDrawerTrackListListener(this);
		this.createSyncProcesses();
		this.processUpdateSelection();
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
		view.setOnLongClickListener(getMainDrawer().getActionHandler().createGoToTrackWithSmartMenuAction(item.getTrack()));

		CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.main_drawer_check_item);
		checkedTextView.setText(item.getLabel());
		checkedTextView.setChecked(Boolean.TRUE.equals(item.getSelected()));

		return view;
	}

	private boolean isUpdateRequired() {
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

	private boolean isSelected(TGTrack track) {
		return (this.selection != null && track != null && this.selection.equals(track) );
	}

	private void updateTrackItems() {
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

	private void updateSelection() {
		this.selection = TGSongViewController.getInstance(getMainDrawer().findContext()).getCaret().getTrack();
		if( this.isUpdateRequired() ) {
			this.updateTracks();
		}
	}

	private void updateTracks() {
		this.updateTrackItems();
		this.notifyDataSetChanged();
	}

	private void createSyncProcesses() {
		this.updateSelectionProcess = new TGSyncProcessLocked(this.getMainDrawer().findContext(), new Runnable() {
			public void run() {
				TGMainDrawerTrackListAdapter.this.updateSelection();
			}
		});

		this.updateTracksProcess = new TGSyncProcessLocked(this.getMainDrawer().findContext(), new Runnable() {
			public void run() {
				TGMainDrawerTrackListAdapter.this.updateTracks();
			}
		});
	}

	public void processUpdateSelection() {
		this.updateSelectionProcess.process();
	}

	public void processUpdateTracks() {
		this.updateTracksProcess.process();
	}

	public void attachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).addUpdateListener(this.eventListener);
	}

	public void detachListeners() {
		TGEditorManager.getInstance(getMainDrawer().findContext()).removeUpdateListener(this.eventListener);
	}
}
