package org.herac.tuxguitar.android.view.dialog.track;

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
import org.herac.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTrackStringCountDialog extends TGModalFragment {

	public TGTrackStringCountDialog() {
		super(R.layout.view_track_string_count_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.track_string_count_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.menu_modal_fragment_button_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTrackStringCountDialog.this.updateStringCount();
				TGTrackStringCountDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createCountValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_string_count_dlg_count_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(Integer.valueOf(this.getTrack().stringCount())));
	}

	public Integer[] createCountValues() {
		Integer[] items = new Integer[TGTrack.MAX_STRINGS - TGTrack.MIN_STRINGS + 1];
		for (int i = 0; i < items.length; i ++) {
			items[i] = Integer.valueOf(TGTrack.MIN_STRINGS + i);
		}
		return items;
	}

	public int parseCount() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.track_string_count_dlg_count_value);
		return ((Integer)spinner.getSelectedItem()).intValue();
	}

	public void updateStringCount() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGSetTrackStringCountAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.getTrack());
		tgActionProcessor.setAttribute(TGSetTrackStringCountAction.ATTRIBUTE_STRING_COUNT, this.parseCount());
		tgActionProcessor.process();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	public TGTrack getTrack() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}
}
