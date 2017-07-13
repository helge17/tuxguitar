package org.herac.tuxguitar.android.view.dialog.repeat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import java.util.Iterator;

public class TGRepeatAlternativeDialog extends TGModalFragment {

	public TGRepeatAlternativeDialog() {
		super(R.layout.view_repeat_alternative);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.repeat_alternative_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.menu_modal_fragment_button_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGRepeatAlternativeDialog.this.cleanRepeatAlternative();
				TGRepeatAlternativeDialog.this.close();
				return true;
			}
		});
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGRepeatAlternativeDialog.this.changeRepeatAlternative();
				TGRepeatAlternativeDialog.this.close();
				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		TGSong song = this.getSong();
		TGMeasureHeader header = this.getHeader();
		int existentEndings = this.getExistentEndings(song, header);
		int selectedEndings = (header.getRepeatAlternative() > 0) ? header.getRepeatAlternative() : this.getDefaultEndings(existentEndings);

		this.updateSelections(this.getCheckBoxes(), existentEndings, selectedEndings);
	}

	public CheckBox[] getCheckBoxes() {
		return new CheckBox[] {
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_1),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_2),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_3),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_4),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_5),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_6),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_7),
			(CheckBox) this.getView().findViewById(R.id.repeat_alternative_dlg_alt_8),
		};
	}

	protected int getExistentEndings(TGSong song, TGMeasureHeader measureHeader){
		int existentEndings = 0;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.getNumber() == measureHeader.getNumber()){
				break;
			}
			if(header.isRepeatOpen()){
				existentEndings = 0;
			}
			existentEndings |= header.getRepeatAlternative();
		}
		return existentEndings;
	}
	
	protected int getDefaultEndings(int existentEndings){
		for(int i = 0; i < 8; i ++){
			if((existentEndings & (1 << i)) == 0){
				return (1 << i);
			}
		}
		return -1;
	}
	
	public void updateSelections(CheckBox[] selections, int existentEndings, int selectedEndings) {
		for(int i = 0; i < selections.length; i ++){
			boolean enabled = ((existentEndings & (1 << i)) == 0);
			selections[i].setEnabled(enabled);
			selections[i].setChecked(enabled && ((selectedEndings & (1 << i)) != 0));
		}
	}
	
	public Integer parseRepeatAlternative() {
		int repeatAlternative = 0;
		CheckBox[] selections = this.getCheckBoxes();
		for(int i = 0; i < selections.length; i ++){
			repeatAlternative |= ( (selections[i].isChecked()) ? (1 << i) : 0 );
		}
		return Integer.valueOf(repeatAlternative);
	}

	public void cleanRepeatAlternative() {
		this.changeRepeatAlternative(0);
	}

	public void changeRepeatAlternative() {
		this.changeRepeatAlternative(this.parseRepeatAlternative());
	}

	public void changeRepeatAlternative(int repeatAlternative) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRepeatAlternativeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative);
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
