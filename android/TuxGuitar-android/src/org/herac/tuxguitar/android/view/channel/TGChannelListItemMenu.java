package org.herac.tuxguitar.android.view.channel;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public class TGChannelListItemMenu extends TGMenuBase {

	private TGChannel channel;
	
	public TGChannelListItemMenu(TGActivity activity, TGChannel channel) {
		super(activity);
		
		this.channel = channel;
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_channel_list_item, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		this.initializeItem(menu, R.id.action_channel_list_item_edit, this.createEditChannelAction(), true);
		if( this.isRemovableChannel()) {
			this.initializeItem(menu, R.id.action_channel_list_item_remove, this.createRemoveChannelAction(), true);
		}
	}
	
	public boolean isRemovableChannel() {
		TGDocumentManager documentManager = TGDocumentManager.getInstance(this.findContext());
		TGSong song = documentManager.getSong();
		TGSongManager songManager = documentManager.getSongManager();
		
		return (!songManager.isAnyTrackConnectedToChannel(song, this.channel.getChannelId()));
	}
	
	public TGActionProcessorListener createEditChannelAction() {
		TGActionProcessorListener tgActionProcessor = this.createDialogActionProcessor(new TGChannelEditDialogController());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createRemoveChannelAction() {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGRemoveChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel);
		return this.createConfirmableActionProcessor(tgActionProcessor, this.getActivity().getString(R.string.action_channel_list_item_remove_confirm_question));
	}
}