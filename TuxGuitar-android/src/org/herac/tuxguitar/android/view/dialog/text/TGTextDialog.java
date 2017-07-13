package org.herac.tuxguitar.android.view.dialog.text;

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
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.song.models.TGBeat;

public class TGTextDialog extends TGModalFragment {

	public TGTextDialog() {
		super(R.layout.view_text_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.text_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTextDialog.this.doInsertText();
				TGTextDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.menu_modal_fragment_button_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTextDialog.this.doRemoveText();
				TGTextDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillTextValue();
	}

	public void setTextFieldValue(int textFieldId, String value) {
		((EditText) this.getView().findViewById(textFieldId)).getText().append(value);
	}
	
	public String getTextFieldValue(int textFieldId) {
		return ((EditText) this.getView().findViewById(textFieldId)).getText().toString();
	}
	
	public void fillTextValue() {
		TGBeat beat = this.getBeat();
		this.setTextFieldValue(R.id.text_dlg_value, beat.getText() != null ? beat.getText().getValue() : new String());
	}
	
	public String findTextValue() {
		return this.getTextFieldValue(R.id.text_dlg_value);
	}
	
	public void doInsertText() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGInsertTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, this.findTextValue());
		tgActionProcessor.process();
	}
	
	public void doRemoveText() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRemoveTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.process();
	}

	public TGBeat getBeat() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}
}
