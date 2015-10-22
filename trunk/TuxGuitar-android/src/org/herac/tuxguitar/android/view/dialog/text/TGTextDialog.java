package org.herac.tuxguitar.android.view.dialog.text;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.song.models.TGBeat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class TGTextDialog extends TGDialog {

	public TGTextDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_text_dialog, null);
		
		this.fillTextValue(view, beat);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.text_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				doInsertText(view, beat, findTextValue(view));
				dialog.dismiss();
			}
		});
		builder.setNeutralButton(R.string.global_button_clean, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				doRemoveText(view, beat);
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
	
	public void fillTextValue(View view, TGBeat beat) {
		this.setTextFieldValue(view, R.id.text_dlg_value, beat.getText() != null ? beat.getText().getValue() : new String());
	}
	
	public String findTextValue(View view) {
		return this.getTextFieldValue(view, R.id.text_dlg_value);
	}
	
	public void doInsertText(View view, TGBeat beat, String textValue) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGInsertTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, textValue);
		tgActionProcessor.processOnNewThread();
	}
	
	public void doRemoveText(View view, TGBeat beat) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRemoveTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.processOnNewThread();
	}
}
