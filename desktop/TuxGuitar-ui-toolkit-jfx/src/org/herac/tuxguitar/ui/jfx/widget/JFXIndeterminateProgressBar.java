package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIIndeterminateProgressBar;

public class JFXIndeterminateProgressBar extends JFXControl<ProgressBar> implements UIIndeterminateProgressBar {
	
	public JFXIndeterminateProgressBar(JFXContainer<? extends Region> parent) {
		super(new ProgressBar(), parent);
		
		this.getControl().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	}
}
