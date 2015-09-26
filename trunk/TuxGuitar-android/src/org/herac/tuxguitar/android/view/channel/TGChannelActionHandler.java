package org.herac.tuxguitar.android.view.channel;

import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.menu.context.impl.TGChannelListItemMenu;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGChannelActionHandler {

	private TGChannelListView channelList;
	
	public TGChannelActionHandler(TGChannelListView channelList) {
		this.channelList = channelList;
	}
	
	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.channelList.findContext(), actionId);
	}
	
	public TGActionProcessorListener createAddChannelAction() {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGAddNewChannelAction.NAME);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createRemoveChannelAction(TGChannel channel) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGRemoveChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createUpdateVolumeAction(TGChannel channel, short volume) {
		TGActionProcessorListener tgActionProcessor = this.createUpdateChannelAction(channel);
		tgActionProcessor.setAttribute(TGUpdateChannelAction.ATTRIBUTE_VOLUME, volume);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createUpdateChannelAction(TGChannel channel) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGUpdateChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createEditChannelAction(TGChannel channel) {
		TGActionProcessorListener tgActionProcessor = this.createOpenDialogAction(new TGChannelEditDialogController());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.channelList.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createOpenMenuAction(TGContextMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.channelList.findActivity());
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createChannelItemMenuAction(TGChannel channel) {
		return this.createOpenMenuAction(new TGChannelListItemMenu(this.channelList.findActivity(), channel));
	}
	
	public void processConfirmableAction(final TGActionProcessor actionProcessor, final String confirmMessage) {
		TGActionProcessor tgActionProcessor = this.createOpenDialogAction(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				actionProcessor.process();
			}
		});
		tgActionProcessor.process();
	}
}
