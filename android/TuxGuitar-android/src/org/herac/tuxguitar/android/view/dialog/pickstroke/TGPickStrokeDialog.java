package org.herac.tuxguitar.android.view.dialog.pickstroke;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import org.herac.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGPickStroke;

public class TGPickStrokeDialog extends TGModalFragment {

	public TGPickStrokeDialog() {
		super(R.layout.view_pickstroke_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.pickstroke_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGPickStrokeDialog.this.processAction();
				TGPickStrokeDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillDirection();
	}

	public TGSelectableItem[] createDirectionValues() {
		TGSelectableItem[] selectableItems = new TGSelectableItem[] {
			new TGSelectableItem(Integer.valueOf( TGPickStroke.PICK_STROKE_NONE ), getString(R.string.pickstroke_dlg_direction_none)),
			new TGSelectableItem(Integer.valueOf( TGPickStroke.PICK_STROKE_UP ), getString(R.string.pickstroke_dlg_direction_up)),
			new TGSelectableItem(Integer.valueOf( TGPickStroke.PICK_STROKE_DOWN ), getString(R.string.pickstroke_dlg_direction_down))
		};
		return selectableItems;
	}

	public void fillDirection() {
		TGBeat beat = this.getBeat();

		int selection = (beat != null ? beat.getPickStroke().getDirection() : TGPickStroke.PICK_STROKE_NONE);

		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createDirectionValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) this.getView().findViewById(R.id.pickstroke_dlg_direction_value);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(new TGSelectableItem(Integer.valueOf(selection), null)), false);

	}

	public int findSelectedDirection() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.pickstroke_dlg_direction_value);

		return ((Integer) ((TGSelectableItem)spinner.getSelectedItem()).getItem()).intValue();
	}

	public void processAction() {
		int direction = this.findSelectedDirection();
		if (direction != TGPickStroke.PICK_STROKE_NONE) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(),
					direction == TGPickStroke.PICK_STROKE_UP ? TGChangePickStrokeUpAction.NAME : TGChangePickStrokeDownAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
			tgActionProcessor.process();
		}
	}

	public TGMeasure getMeasure() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}

	public TGBeat getBeat() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}
}
