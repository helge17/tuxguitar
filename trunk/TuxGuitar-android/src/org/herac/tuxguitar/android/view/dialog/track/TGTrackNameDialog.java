package org.herac.tuxguitar.android.view.dialog.track;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackNameAction;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTrackNameDialog extends TGModalFragment {

	public TGTrackNameDialog() {
		super(R.layout.view_track_name_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.track_name_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTrackNameDialog.this.updateTrackName();
				TGTrackNameDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillTrackName();
	}

	public void setTextFieldValue(int textFieldId, String value) {
		((EditText) this.getView().findViewById(textFieldId)).getText().append(value);
	}
	
	public String getTextFieldValue(int textFieldId) {
		return ((EditText) this.getView().findViewById(textFieldId)).getText().toString();
	}
	
	public void fillTrackName() {
		setTextFieldValue(R.id.track_name_dlg_name_value, this.getTrack().getName());
	}

	public void updateTrackName() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGSetTrackNameAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.getTrack());
		tgActionProcessor.setAttribute(TGSetTrackNameAction.ATTRIBUTE_TRACK_NAME, getTextFieldValue(R.id.track_name_dlg_name_value));
		tgActionProcessor.processOnNewThread();
	}

	public TGTrack getTrack() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}
}
