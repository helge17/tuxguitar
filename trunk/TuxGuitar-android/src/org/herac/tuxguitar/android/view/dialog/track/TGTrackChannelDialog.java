package org.herac.tuxguitar.android.view.dialog.track;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TGTrackChannelDialog extends TGDialog implements OnShowListener {
	
	private View dialogView;
	private Dialog dialog;
	private TGSongManager songManager;
	private TGSong song;
	private TGTrack track;
	private TGTrackChannelInstrumentsEditor instrumentsEditor;
	
	public TGTrackChannelDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		this.songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		this.song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		this.track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		this.dialogView = getActivity().getLayoutInflater().inflate(R.layout.view_track_channel_dialog, null);
		this.instrumentsEditor = new TGTrackChannelInstrumentsEditor(this);
		
		this.fillTrackInstrument();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.track_channel_dlg_title);
		builder.setView(this.dialogView);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				updateTrackInstrument();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		this.dialog = builder.create();
		this.dialog.setOnShowListener(this);
		
		return this.dialog;
	}
	
	public void onShow(DialogInterface dialog) {
		this.appendListeners();
	}
	
	public void onDismiss(DialogInterface dialog) {
		this.removeListeners();
		
		super.onDismiss(dialog);
	}
	
	public void appendListeners() {
		this.dialogView.findViewById(R.id.track_channel_dlg_settings_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TGTrackChannelDialog.this.instrumentsEditor.configureInstruments();
			}
		});
		this.findActivity().getNavigationManager().addNavigationListener(this.instrumentsEditor);
	}
	
	public void onOpenInstruments() {
		if( this.dialog != null ) {
			this.dialog.hide();
		}
	}
	
	public void onBackFromInstruments() {
		if( this.dialog != null ) {
			this.dialog.show();
			this.fillTrackInstrument();
		}
	}
	
	public void removeListeners() {
		this.findActivity().getNavigationManager().removeNavigationListener(this.instrumentsEditor);
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
		Spinner spinner = (Spinner) this.dialogView.findViewById(R.id.track_channel_dlg_channel_value);
		
		return (TGChannel) ((TGSelectableItem)spinner.getSelectedItem()).getItem();
	}
	
	public void fillTrackInstrument() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createSelectableChannels());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.dialogView.findViewById(R.id.track_channel_dlg_channel_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(this.songManager.getChannel(this.song, this.track.getChannelId()), null)));
	}
	
	public TGActionProcessor createActionProcessor(String actionId) {
		return new TGActionProcessor(findContext(), actionId);
	}
	
	public void updateTrackInstrument() {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGSetTrackChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, findSelectedChannel());
		tgActionProcessor.processOnNewThread();
	}
}
