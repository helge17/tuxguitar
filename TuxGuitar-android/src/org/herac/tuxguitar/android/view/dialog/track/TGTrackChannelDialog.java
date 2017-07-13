package org.herac.tuxguitar.android.view.dialog.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.fragment.impl.TGChannelListFragmentController;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGTrackChannelDialog extends TGModalFragment {

	private TGSongManager songManager;
	private TGSong song;
	private TGTrack track;

	public TGTrackChannelDialog() {
		super(R.layout.view_track_channel_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.channel_edit_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_track_channel, menu);
		menu.findItem(R.id.track_channel_dlg_settings_button).setOnMenuItemClickListener(createConfigureInstrumentsAction());
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTrackChannelDialog.this.updateTrackInstrument();
				TGTrackChannelDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		this.song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		this.track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}

	@Override
	public void onShowView() {
		this.fillTrackInstrument();
	}

	public TGSelectableItem[] createSelectableChannels() {
		List<TGSelectableItem> selectableChannels = new ArrayList<TGSelectableItem>();
		selectableChannels.add(new TGSelectableItem(null, getString(R.string.global_spinner_select_option)));
		
		Iterator<TGChannel> it = this.songManager.getChannels(this.song).iterator();
		while( it.hasNext() ) {
			TGChannel channel = it.next();
			selectableChannels.add(new TGSelectableItem(channel, channel.getName()));
		}
		
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableChannels.size()];
		selectableChannels.toArray(builtItems);
		
		return builtItems;
	}
	
	public TGChannel findSelectedChannel() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_channel_dlg_channel_value);
		
		return (TGChannel) ((TGSelectableItem)spinner.getSelectedItem()).getItem();
	}
	
	public void fillTrackInstrument() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableChannels());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_channel_dlg_channel_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(this.songManager.getChannel(this.song, this.track.getChannelId()), null)));
	}

	public TGActionProcessorListener createConfigureInstrumentsAction() {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(this.findContext(), TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, TGChannelListFragmentController.getInstance(this.findContext()));
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, this.findActivity());
		return tgActionProcessor;
	}

	public void updateTrackInstrument() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGSetTrackChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, findSelectedChannel());
		tgActionProcessor.processOnNewThread();
	}
}
