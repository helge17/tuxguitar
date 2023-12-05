package org.herac.tuxguitar.android.view.dialog.tremoloPicking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;

public class TGTremoloPickingDialog extends TGModalFragment {

	public TGTremoloPickingDialog() {
		super(R.layout.view_tremolo_picking_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.tremolo_picking_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTremoloPickingDialog.this.updateEffect();
				TGTremoloPickingDialog.this.close();

				return true;
			}
		});
		menu.findItem(R.id.action_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGTremoloPickingDialog.this.cleanEffect();
				TGTremoloPickingDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillDurations();
	}
	
	public void fillDurations() {
		int duration = TGDuration.EIGHTH;

		TGNote note = this.getNote();
		if( note != null && note.getEffect().isTremoloPicking() ){
			duration = note.getEffect().getTremoloPicking().getDuration().getValue();
		}
		
		this.fillDuration(R.id.tremolo_picking_dlg_duration_8, TGDuration.EIGHTH, duration);
		this.fillDuration(R.id.tremolo_picking_dlg_duration_16, TGDuration.SIXTEENTH, duration);
		this.fillDuration(R.id.tremolo_picking_dlg_duration_32, TGDuration.THIRTY_SECOND, duration);
	}

	public void fillDuration(int id, int value, int selection) {
		RadioButton radioButton = (RadioButton) this.getView().findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration() {
		RadioGroup optionsGroup = (RadioGroup) this.getView().findViewById(R.id.tremolo_picking_dlg_duration_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGDuration.EIGHTH;
	}
	
	public TGEffectTremoloPicking createTremoloPicking(){
		TGEffectTremoloPicking tgEffectTremoloPicking = this.getSongManager().getFactory().newEffectTremoloPicking();
		tgEffectTremoloPicking.getDuration().setValue(findSelectedDuration());
		return tgEffectTremoloPicking;
	}

	public void cleanEffect() {
		this.updateEffect(null);
	}

	public void updateEffect() {
		this.updateEffect(this.createTremoloPicking());
	}

	public void updateEffect(TGEffectTremoloPicking effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTremoloPickingAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, this.getMeasure());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, this.getBeat());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, this.getString());
		tgActionProcessor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}

	public TGSongManager getSongManager() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}

	public TGMeasure getMeasure() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
	}

	public TGBeat getBeat() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
	}

	public TGNote getNote() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
	}

	public TGString getString() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
	}
}
