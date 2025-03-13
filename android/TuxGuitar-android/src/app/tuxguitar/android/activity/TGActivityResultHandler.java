package app.tuxguitar.android.activity;

import android.content.Intent;

public interface TGActivityResultHandler {

	void onActivityResult(int resultCode, Intent data);
}
