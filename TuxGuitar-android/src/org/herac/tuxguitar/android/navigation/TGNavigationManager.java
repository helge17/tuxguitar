package org.herac.tuxguitar.android.navigation;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;

import android.app.FragmentManager;

public class TGNavigationManager {
	
	private TGActivity activity;
	private List<TGNavigationFragment> navigationFragments;
	
	public TGNavigationManager(TGActivity activity) {
		this.activity = activity;
		this.navigationFragments = new ArrayList<TGNavigationFragment>();
	}

	public void initialize() {
		this.navigationFragments.clear();
	}

	public void processLoadFragment(TGFragmentController<?> controller, String tagId) {
		TGNavigationFragment tgNavigationFragment = new TGNavigationFragment();
		tgNavigationFragment.setController(controller);
		tgNavigationFragment.setTagId(tagId);
		
		this.processLoadFragment(tgNavigationFragment);
	}
	
	public void processLoadFragment(TGNavigationFragment nf) {
		FragmentManager fragmentManager = this.activity.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, nf.getController().getFragment()).commitAllowingStateLoss();
		
		TGNavigationFragment backFrom = null;
		if( this.navigationFragments.contains(nf) ) {
			int index = this.navigationFragments.indexOf(nf);
			if( index >= 0 && index < this.navigationFragments.size() ) {
				while(this.navigationFragments.size() > index) {
					TGNavigationFragment removed = this.navigationFragments.remove( this.navigationFragments.size() - 1 );
					if(!removed.equals(nf)) {
						backFrom = removed;
					}
				}
			}
		}
		
		this.navigationFragments.add( nf );
		this.fireNavigationEvent(nf, backFrom);
    }
	
	public TGNavigationFragment getCurrentFragment() {
		if( this.navigationFragments.size() > 0 ) {
			return this.navigationFragments.get(this.navigationFragments.size() - 1);
		}
		return null;
	}
	
	public TGNavigationFragment getPreviousFragment() {
		if( this.navigationFragments.size() > 1 ) {
			return this.navigationFragments.get(this.navigationFragments.size() - 2);
		}
		return null;
	}
	
	public void removeLastFragment() {
		if( this.navigationFragments.size() > 0 ) {
			this.navigationFragments.remove(this.navigationFragments.size() - 1);
		}
	}
	
	public boolean hasPreviousFragment() {
		return (this.getPreviousFragment() != null);
	}
	
	public TGContext findContext() {
		return this.activity.findContext();
	}
	
	public boolean callOpenPreviousFragment() {
		if( this.hasPreviousFragment() ) {
			this.callOpenFragment(this.getPreviousFragment());
			
			return true;
		}
		return false;
	}
	
	public void callOpenFragment(TGNavigationFragment nf) {
		this.callOpenFragment(nf.getController(), nf.getTagId());
	}
	
	public void callOpenFragment(TGFragmentController<?> controller) {
		this.callOpenFragment(controller, null);
	}
	
	public void callOpenFragment(TGFragmentController<?> controller, String tagId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_TAG_ID, tagId);
		tgActionProcessor.processOnNewThread();
	}
	
	public void addNavigationListener(TGEventListener listener){
		TGEventManager.getInstance(findContext()).addListener(TGNavigationEvent.EVENT_TYPE, listener);
	}
	
	public void removeNavigationListener(TGEventListener listener){
		TGEventManager.getInstance(findContext()).removeListener(TGNavigationEvent.EVENT_TYPE, listener);
	}
	
	public void fireNavigationEvent(TGNavigationFragment nf, TGNavigationFragment backFrom) throws TGActionException{
		TGEventManager.getInstance(findContext()).fireEvent(new TGNavigationEvent(nf, backFrom));
	}
}
