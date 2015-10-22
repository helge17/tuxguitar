package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackNameAction;
import org.herac.tuxguitar.song.models.TGTrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class TGTrackNameDialog extends TGDialog {

	public TGTrackNameDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGTrack track = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_track_name_dialog, null);
		
		this.fillTrackName(view, track);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.track_name_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				updateTrackName(view, track);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

	public void setTextFieldValue(View view, int textFieldId, String value) {
		((EditText) view.findViewById(textFieldId)).getText().append(value);
	}
	
	public String getTextFieldValue(View view, int textFieldId) {
		return ((EditText) view.findViewById(textFieldId)).getText().toString();
	}
	
	public void fillTrackName(View view, TGTrack track) {
		setTextFieldValue(view, R.id.track_name_dlg_name_value, track.getName());
	}
	
	public void updateTrackName(View view, TGTrack track) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGSetTrackNameAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGSetTrackNameAction.ATTRIBUTE_TRACK_NAME, getTextFieldValue(view, R.id.track_name_dlg_name_value));
		tgActionProcessor.processOnNewThread();
	}
}
