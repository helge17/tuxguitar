package app.tuxguitar.android.view.dialog.transport;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.caret.TGMoveToAction;
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

public class TGTransportModeDialog extends TGModalFragment {

	protected static final int MIN_SELECTION = 1;
	protected static final int MAX_SELECTION = 500;

	protected RadioButton simple;
	protected RadioButton custom;

	protected TextView simpleLabel;
	protected TextView simplePercentLabel;
	protected Spinner simplePercent;
	protected CheckBox simpleLoop;

	protected TextView customLabel;
	protected TextView customFromLabel;
	protected Spinner customFrom;
	protected TextView customToLabel;
	protected Spinner customTo;
	protected TextView customIncrementLabel;
	protected Spinner customIncrement;

	protected TextView loopLabel;
	protected TextView loopFromLabel;
	protected Spinner loopFrom;
	protected TextView loopToLabel;
	protected Spinner loopTo;

	private final int bgcolorErr = Color.rgb(230,110,110);
	private final int bgcolorOK = Color.TRANSPARENT;
	private final int colorErr = Color.GRAY;
	private final int colorOK = Color.TRANSPARENT;

	protected boolean ok;

	public TGTransportModeDialog() {
		super(R.layout.view_transport_mode);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.transport_mode_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				//------ Set transport mode ------

				Integer type = (custom.isChecked() ? MidiPlayerMode.TYPE_CUSTOM : MidiPlayerMode.TYPE_SIMPLE );
				Boolean loop = (type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SIMPLE && simpleLoop.isChecked()));
				Integer simplePcInt = (int) simplePercent.getSelectedItem();
				Integer loopSHeader = ((int) loopFrom.getSelectedItemId() == 0 ? -1 : getMeasureNb(loopFrom));
				Integer loopEHeader = ((int) loopTo.getSelectedItemId() == 0 ? -1 : getMeasureNb(loopTo));

				// move caret to loop start if loop defined
				if (loop) {
					TGTrack track = MidiPlayer.getInstance(findContext()).getSong().getTrack(0);
					TGBeat beat = track.getMeasure(loopSHeader > 0 ? loopSHeader-1 : 0).getBeat(0);
					TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGMoveToAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
					tgActionProcessor.process();
				}

				MidiPlayerMode mode = MidiPlayer.getInstance(findContext()).getMode();
				mode.setType(type);
				mode.setLoop(loop);
				mode.setSimplePercent(simplePcInt);
				mode.setCustomPercentFrom((int) customFrom.getSelectedItem());
				mode.setCustomPercentTo((int) customTo.getSelectedItem());
				mode.setCustomPercentIncrement((int) customIncrement.getSelectedItem());
				mode.setLoopSHeader(loopSHeader);
				mode.setLoopEHeader(loopEHeader);

				TGTransportModeDialog.this.close();

				return true;
			}
		});
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_ok).setEnabled(this.ok);
		menu.findItem(R.id.action_ok).getIcon().setColorFilter(new BlendModeColorFilter(ok ? colorOK : colorErr, BlendMode.SRC_ATOP));
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {

		//------ Initialize menu items ------

		ArrayAdapter<Integer> percentArrayAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, createPercentValues());
		ArrayAdapter<String>loopFromArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, createLoopFromValues());
		ArrayAdapter<String>loopToArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, createLoopToValuesFrom(1));

		//---Simple/Trainer selector---
		this.simple = (RadioButton) this.getView().findViewById(R.id.transport_mode_dlg_simple);
		this.custom = (RadioButton) this.getView().findViewById(R.id.transport_mode_dlg_trainer);

		//---Simple---
		this.simpleLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_simple_label);
		this.simplePercentLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_simple_tempo_percent_label);
		this.simplePercent = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_simple_tempo_percent_value);
		this.simpleLoop = (CheckBox) this.getView().findViewById(R.id.transport_mode_dlg_simple_loop);

		//---Trainer---
		this.customLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_trainer_label);
		this.customFromLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_from_label);
		this.customFrom = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_from_value);
		this.customToLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_to_label);
		this.customTo = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_percent_to_value);
		this.customIncrementLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_increment_label);
		this.customIncrement = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_trainer_tempo_increment_value);

		//--- Loop Range ---
		this.loopLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_loop_range_label);
		this.loopFromLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_loop_range_from_label);
		this.loopFrom = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_loop_range_from_value);
		this.loopToLabel = (TextView) this.getView().findViewById(R.id.transport_mode_dlg_loop_range_to_label);
		this.loopTo = (Spinner) this.getView().findViewById(R.id.transport_mode_dlg_loop_range_to_value);

		Integer type = (this.custom.isChecked() ? MidiPlayerMode.TYPE_CUSTOM : MidiPlayerMode.TYPE_SIMPLE );
		Boolean loop = (type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SIMPLE && this.simpleLoop.isChecked()));

		enableTrainerMode(type == MidiPlayerMode.TYPE_CUSTOM);
		enableLoop(loop);

		this.custom.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enableTrainerMode(isChecked);
				enableLoop(isChecked || simpleLoop.isChecked());
			}
		});

		this.simpleLoop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enableLoop(isChecked);
			}
		});

		this.customFrom.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				trainerTempoOnSelect(parent);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		this.customTo.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				trainerTempoOnSelect(parent);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		this.customIncrement.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				trainerTempoOnSelect(parent);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		this.loopFrom.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String oldToItem = loopTo.getSelectedItem().toString();
				Integer oldToItemId = (int) loopTo.getSelectedItemId();

				// fill "loop to" spinner with values >= from
				loopToArrayAdapter.clear();
				loopToArrayAdapter.addAll(createLoopToValuesFrom((int) loopFrom.getSelectedItemId() < 1 ? 1 : getMeasureNb(loopFrom)));

				// restore old "lopp to" spinner selection
				if (oldToItemId == 0) {
					// to the end
					loopTo.setSelection(0);
				} else if (loopToArrayAdapter.getPosition(oldToItem) > -1) {
					// same measure as before
					loopTo.setSelection(loopToArrayAdapter.getPosition(oldToItem));
				} else {
					// same measure as from
					loopTo.setSelection(loopToArrayAdapter.getPosition(loopFrom.getSelectedItem().toString()));
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		MidiPlayerMode mode = MidiPlayer.getInstance(findContext()).getMode();

		this.simple.setChecked(mode.getType() == MidiPlayerMode.TYPE_SIMPLE);
		this.custom.setChecked(mode.getType() == MidiPlayerMode.TYPE_CUSTOM);

		this.simplePercent.setAdapter(percentArrayAdapter);
		this.simplePercent.setSelection(percentArrayAdapter.getPosition(mode.getSimplePercent()));

		this.simpleLoop.setChecked(mode.isLoop());

		this.customFrom.setAdapter(percentArrayAdapter);
		this.customFrom.setSelection(percentArrayAdapter.getPosition(mode.getCustomPercentFrom()));

		this.customTo.setAdapter(percentArrayAdapter);
		this.customTo.setSelection(percentArrayAdapter.getPosition(mode.getCustomPercentTo()));

		this.customIncrement.setAdapter(percentArrayAdapter);
		this.customIncrement.setSelection(percentArrayAdapter.getPosition(mode.getCustomPercentIncrement()));

		this.loopFrom.setAdapter(loopFromArrayAdapter);
		this.loopFrom.setSelection(mode.getLoopSHeader() > 0 ? mode.getLoopSHeader() : 0);

		this.loopTo.setAdapter(loopToArrayAdapter);
		this.loopTo.setSelection(mode.getLoopEHeader() > 0 ? mode.getLoopEHeader() : 0);
	}

	public void trainerTempoOnSelect (AdapterView<?> parent) {
		boolean ok = true;
		if (parent.equals(customFrom)) {
			if ((int) customFrom.getSelectedItem() >= (int) customTo.getSelectedItem()) {
				// invalid
				ok = false;
				customFrom.setBackgroundColor(bgcolorErr);
				customTo.setBackgroundColor(bgcolorOK);
			}
		} else if (parent.equals(customTo)) {
			if ((int) customTo.getSelectedItem() <= (int) customFrom.getSelectedItem()) {
				// invalid
				ok = false;
				customTo.setBackgroundColor(bgcolorErr);
				customFrom.setBackgroundColor(bgcolorOK);
			}
		}
		if (ok) {
			customTo.setBackgroundColor(bgcolorOK);
			customFrom.setBackgroundColor(bgcolorOK);
			if ((int) customIncrement.getSelectedItem() > ((int) customTo.getSelectedItem() - (int) customFrom.getSelectedItem())) {
				ok = false;
				customIncrement.setBackgroundColor(bgcolorErr);
			} else {
				customIncrement.setBackgroundColor(bgcolorOK);
			}
		}
		this.ok = ok;
		// update menu with onPrepareOptionsMenu
		getActivity().invalidateOptionsMenu();
	}

	public Integer[] createPercentValues() {
		int length = ((MAX_SELECTION - MIN_SELECTION) + 1);
		Integer[] items = new Integer[length];
		for (int i = 0; i < length; i++) {
			items[i] = i + 1;
		}
		return items;
	}

	public List<String> createLoopFromValues() {
		List<String> items = new ArrayList<String>();
		items.add(getActivity().getString(R.string.transport_mode_dlg_loop_range_from_default));	// from the beginning
		for (int i = 1; i <= MidiPlayer.getInstance(findContext()).getSong().countMeasureHeaders(); i++) {
			items.add(getItemText(i));
		}
		return items;
	}

	public List<String> createLoopToValuesFrom(Integer firstMeasure) {
		List<String> items = new ArrayList<String>();
		items.add(getActivity().getString(R.string.transport_mode_dlg_loop_range_to_default));		// to the end
		for (int i = firstMeasure; i <= MidiPlayer.getInstance(findContext()).getSong().countMeasureHeaders(); i++) {
			items.add(getItemText(i));
		}
		return items;
	}

	private String getItemText(Integer i) {
		TGMeasureHeader header = MidiPlayer.getInstance(findContext()).getSong().getMeasureHeader(i - 1);
		String text = "#" + i;
		if( header.hasMarker() ){
			text += (" (" + header.getMarker().getTitle() + ")");
		}
		return text;
	}

	private Integer getMeasureNb (Spinner loopSpinner) {
		return Integer.parseInt(loopSpinner.getSelectedItem().toString().replaceAll("#(\\d+) ?.*","$1"));
	}

	private void enableTrainerMode(boolean isTrainer) {
		this.simpleLabel.setEnabled(!isTrainer);
		this.simplePercentLabel.setEnabled(!isTrainer);
		this.simplePercent.setEnabled(!isTrainer);
		this.simpleLoop.setEnabled(!isTrainer);

		this.customLabel.setEnabled(isTrainer);
		this.customFromLabel.setEnabled(isTrainer);
		this.customFrom.setEnabled(isTrainer);
		this.customToLabel.setEnabled(isTrainer);
		this.customTo.setEnabled(isTrainer);
		this.customIncrementLabel.setEnabled(isTrainer);
		this.customIncrement.setEnabled(isTrainer);
	}

	private void enableLoop(boolean enable) {
		this.loopLabel.setEnabled(enable);
		this.loopFromLabel.setEnabled(enable);
		this.loopFrom.setEnabled(enable);
		this.loopToLabel.setEnabled(enable);
		this.loopTo.setEnabled(enable);
	}
}
