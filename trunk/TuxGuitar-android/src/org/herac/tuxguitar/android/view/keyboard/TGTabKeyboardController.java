package org.herac.tuxguitar.android.view.keyboard;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTabKeyboardController {

	private TGTabKeyboard view;
	
	public TGTabKeyboardController() {
		super();
	}
	
	public TGTabKeyboard getView() {
		return view;
	}

	public void setView(TGTabKeyboard view) {
		this.view = view;
	}

	public void toggleVisibility() {
		if( this.getView() != null ) {
			this.getView().toggleVisibility();
		}
	}
	
	public static TGTabKeyboardController getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTabKeyboardController.class.getName(), new TGSingletonFactory<TGTabKeyboardController>() {
			public TGTabKeyboardController createInstance(TGContext context) {
				return new TGTabKeyboardController();
			}
		});
	}
}
