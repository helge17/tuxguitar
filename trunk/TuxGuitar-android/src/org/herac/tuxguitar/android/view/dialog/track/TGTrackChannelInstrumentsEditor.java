package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.fragment.impl.TGChannelListFragmentController;
import org.herac.tuxguitar.android.navigation.TGNavigationEvent;
import org.herac.tuxguitar.android.navigation.TGNavigationFragment;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGTrackChannelInstrumentsEditor implements TGEventListener {

	private String tagId;
	private TGTrackChannelDialog dialog;
	
	public TGTrackChannelInstrumentsEditor(TGTrackChannelDialog dialog) {
		this.dialog = dialog;
		this.tagId = (TGTrackChannelInstrumentsEditor.class.getName() + "-" + this.hashCode());
	}
	
	public void configureInstruments() {
		this.openInstrumentsFragment();
		this.dialog.onOpenInstruments();
	}
	
	public void onBackFromInstruments() {
		this.dialog.onBackFromInstruments();
	}
	
	public void openInstrumentsFragment() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.dialog.findContext(), TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, TGChannelListFragmentController.getInstance(this.dialog.findContext()));
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, this.dialog.findActivity());
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_TAG_ID, this.tagId);
		tgActionProcessor.processOnNewThread();
	}
	
	public void processBackEvent(TGNavigationFragment nf) {
		if( nf != null && this.tagId.equals( nf.getTagId() ) ) {
			TGTrackChannelInstrumentsEditor.this.onBackFromInstruments();
		}
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.dialog.findContext()).executeLater(new Runnable() {
			public void run() throws TGException {
				if( TGNavigationEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processBackEvent((TGNavigationFragment) event.getAttribute(TGNavigationEvent.PROPERTY_BACK_FROM));
				}
			}
		});
	}
}
