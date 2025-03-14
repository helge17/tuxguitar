package app.tuxguitar.android.drawer.main;

import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.android.fragment.impl.TGChannelListFragmentController;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.android.view.dialog.info.TGSongInfoDialogController;
import app.tuxguitar.android.view.tablature.TGSongViewSmartMenu;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.song.models.TGTrack;

public class TGMainDrawerActionHandler {

	private TGMainDrawer mainDrawer;

	public TGMainDrawerActionHandler(TGMainDrawer mainDrawer) {
		this.mainDrawer = mainDrawer;
	}

	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.mainDrawer.findContext(), actionId);
	}

	public TGActionProcessorListener createGoToTrackAction(TGTrack track) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGGoToTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createGoToTrackWithSmartMenuAction(TGTrack track) {
		TGActionProcessorListener tgActionProcessor = this.createGoToTrackAction(track);
		tgActionProcessor.setAttribute(TGSongViewSmartMenu.REQUEST_SMART_MENU, true);
		tgActionProcessor.setAttribute(TGSongViewSmartMenu.TRACK_AREA_SELECTED, true);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createNewFileAction() {
		return this.createAction(TGLoadTemplateAction.NAME);
	}

	public TGActionProcessorListener createOpenFileAction() {
		return this.createAction(TGOpenDocumentAction.NAME);
	}

	public TGActionProcessorListener createSaveFileAsAction() {
		return this.createAction(TGSaveDocumentAsAction.NAME);
	}

	public TGActionProcessorListener createSaveFileAction() {
		return this.createAction(TGSaveDocumentAction.NAME);
	}

	public TGActionProcessorListener createAddTrackAction() {
		return this.createAction(TGAddNewTrackAction.NAME);
	}

	public TGActionProcessorListener createOpenInstrumentsAction() {
		return this.createFragmentAction(TGChannelListFragmentController.getInstance(this.mainDrawer.findContext()));
	}

	public TGActionProcessorListener createOpenInfoAction() {
		return this.createDialogAction(new TGSongInfoDialogController());
	}

	public TGActionProcessorListener createFragmentAction(TGFragmentController<?> controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, this.mainDrawer.findActivity());
		return tgActionProcessor;
	}

	public TGActionProcessorListener createDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.mainDrawer.findActivity());
		return tgActionProcessor;
	}
}
