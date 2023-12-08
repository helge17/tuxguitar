package org.herac.tuxguitar.android.navigation;

import org.herac.tuxguitar.android.fragment.TGFragmentController;

public class TGNavigationFragment {
	
	private String tagId;
	private TGFragmentController<?> controller;
	
	public TGNavigationFragment() {
		super();
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public TGFragmentController<?> getController() {
		return controller;
	}

	public void setController(TGFragmentController<?> controller) {
		this.controller = controller;
	}

	public int hashCode() {
		if( this.getTagId() != null ) {
			return (TGNavigationFragment.class.getName() + "-" + this.getTagId().hashCode()).hashCode();
		}
		if( this.getController() != null ) {
			return (TGNavigationFragment.class.getName() + "-fr-" + this.getController().hashCode()).hashCode();
		}
		return super.hashCode();
	}
	
	public boolean equals(Object o) {
		if( o instanceof TGNavigationFragment ) {
			return (this.hashCode() == o.hashCode());
		}
		return false;
	}
}
