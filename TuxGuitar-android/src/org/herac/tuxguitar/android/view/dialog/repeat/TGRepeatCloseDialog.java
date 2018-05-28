package org.herac.tuxguitar.android.view.dialog.repeat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class TGRepeatCloseDialog extends TGModalFragment {

	public TGRepeatCloseDialog() {
		super(R.layout.view_repeat_close);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.repeat_close_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGRepeatCloseDialog.this.changeRepeatClose();
				TGRepeatCloseDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		int repeatCloseDefault = this.getHeader().getRepeatClose();
		if( repeatCloseDefault < 1 ) {
			repeatCloseDefault = 1;
		}
		
		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createRepeatValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.repeat_close_dlg_count_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(Integer.valueOf(repeatCloseDefault)));
	}
	
	public Integer[] createRepeatValues() {
		Integer[] items = new Integer[101];
		for (int i = 0; i < items.length; i++) {
			items[i] = Integer.valueOf(i);
		}
		return items;
	}

	public int parseRepeatValue() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.repeat_close_dlg_count_value);
		return ((Integer)spinner.getSelectedItem()).intValue();
	}

	public void changeRepeatClose() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRepeatCloseAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.getHeader());
		tgActionProcessor.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, this.parseRepeatValue());
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGMeasureHeader getHeader() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
	}
}
