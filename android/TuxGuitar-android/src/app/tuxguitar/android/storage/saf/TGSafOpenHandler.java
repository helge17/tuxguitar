package org.herac.tuxguitar.android.storage.saf;

import android.app.Activity;
import android.content.Intent;

public class TGSafOpenHandler extends TGSafBaseHandler {

	public TGSafOpenHandler(TGSafProvider provider) {
		super(provider);
	}

	public void onActivityResult(int resultCode, Intent data) {
		super.onActivityResult(resultCode, data);

		if( resultCode == Activity.RESULT_OK && data != null ) {
			this.getProvider().getActionHandler().callReadUri(data.getData());
		}
	}
}
