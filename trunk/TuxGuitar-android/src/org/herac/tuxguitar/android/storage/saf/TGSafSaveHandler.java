package org.herac.tuxguitar.android.storage.saf;

import android.app.Activity;
import android.content.Intent;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class TGSafSaveHandler extends TGSafBaseHandler {

	private TGFileFormat fileFormat;

	public TGSafSaveHandler(TGSafProvider provider, TGFileFormat fileFormat) {
		super(provider);

		this.fileFormat = fileFormat;
	}

	public void onActivityResult(int resultCode, Intent data) {
		super.onActivityResult(resultCode, data);

		if( resultCode == Activity.RESULT_OK && data != null ) {
			this.getProvider().getActionHandler().callWriteUri(data.getData(), this.fileFormat);
		}
	}
}
