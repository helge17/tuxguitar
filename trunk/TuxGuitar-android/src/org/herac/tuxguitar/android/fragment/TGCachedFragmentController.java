package org.herac.tuxguitar.android.fragment;

public abstract class TGCachedFragmentController<T extends TGCachedFragment> implements TGFragmentController<T> {
	
	private T instance;
	
	public TGCachedFragmentController() {
		super();
	}
	
	public abstract T createNewInstance();
	
	public T findOrCreateInstance() {
		synchronized (TGCachedFragmentController.class) {
			if( this.instance == null ) {
				this.instance = this.createNewInstance();
			}
			return this.instance;
		}
	}
	
	public void attachInstance(T instance) {
		synchronized (TGCachedFragmentController.class) {
			if( this.instance != instance ) {
				this.instance = instance;
			}
		}
	}
	
	public void detachInstance(T instance) {
		synchronized (TGCachedFragmentController.class) {
			if( this.instance == instance ) {
				this.instance = null;
			}
		}
	}
	
	public T getFragment() {
		return this.findOrCreateInstance();
	}
}
