package org.herac.tuxguitar.android.navigation;

import org.herac.tuxguitar.android.fragment.TGFragment;

public class TGNavigationFragment {
	
	private String tagId;
	private TGFragment fragment;
	
	public TGNavigationFragment() {
		super();
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public TGFragment getFragment() {
		return fragment;
	}

	public void setFragment(TGFragment fragment) {
		this.fragment = fragment;
	}
	
	public int hashCode() {
		if( this.getTagId() != null ) {
			return (TGNavigationFragment.class.getName() + "-" + this.getTagId().hashCode()).hashCode();
		}
		if( this.getFragment() != null ) {
			return (TGNavigationFragment.class.getName() + "-fr-" + this.getFragment().hashCode()).hashCode();
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
