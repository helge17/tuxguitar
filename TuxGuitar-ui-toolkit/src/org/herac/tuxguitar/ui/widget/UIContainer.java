package org.herac.tuxguitar.ui.widget;

import java.util.List;

public interface UIContainer extends UIControl {
	
	List<UIControl> getChildren();
}
