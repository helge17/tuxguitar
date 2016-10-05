package org.herac.tuxguitar.android.activity;

import android.content.Intent;

public interface TGActivityResultHandler {
	
	void onActivityResult(int resultCode, Intent data);
}
