package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.fragment.TGChannelListFragment;
import org.herac.tuxguitar.android.fragment.TGFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.song.models.TGTrack;

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
	
	public TGActionProcessorListener createBrowserAction(String actionId) {
		TGActionProcessorListener tgActionProcessor = this.createAction(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), this.findBrowserSession());
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createNewFileAction() {
		return this.createAction(TGNewSongAction.NAME);
	}
	
	public TGActionProcessorListener createOpenFileAction() {
		return this.createBrowserAction(TGBrowserPrepareForReadAction.NAME);
	}
	
	public TGActionProcessorListener createSaveFileAsAction() {
		return this.createBrowserAction(TGBrowserPrepareForWriteAction.NAME);
	}
	
	public TGActionProcessorListener createSaveFileAction() {
		return this.createBrowserAction(TGBrowserSaveCurrentElementAction.NAME);
	}
	
	public TGActionProcessorListener createFragmentAction(TGFragment fragment) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_FRAGMENT, fragment);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, this.mainDrawer.findActivity());
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createOpenInstrumentsAction() {
		return this.createFragmentAction(TGChannelListFragment.getInstance(this.mainDrawer.findContext()));
	}
	
	public TGActionProcessorListener createTransportPlayAction() {
		return this.createBrowserAction(TGTransportPlayAction.NAME);
	}
	
	public TGBrowserSession findBrowserSession() {
		return TGBrowserManager.getInstance(this.mainDrawer.findContext()).getSession();
	}
}
