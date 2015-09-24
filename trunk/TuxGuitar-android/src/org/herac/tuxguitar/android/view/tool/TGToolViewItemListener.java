package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.action.TGActionProcessor;

import android.view.View;
import android.view.View.OnClickListener;

public class TGToolViewItemListener implements OnClickListener {
	
	private TGActionProcessor actionProcessor;
	
	public TGToolViewItemListener(TGActionProcessor actionProcessor) {
		this.actionProcessor = actionProcessor;
	}

	@Override
	public void onClick(View v) {
		this.actionProcessor.process();
	}
}
