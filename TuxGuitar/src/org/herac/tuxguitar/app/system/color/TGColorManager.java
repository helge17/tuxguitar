package org.herac.tuxguitar.app.system.color;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGColorManager {
	
	public static final int COLOR_WHITE = 1;
	public static final int COLOR_BLACK = 2;
	public static final int COLOR_GRAY = 3;
	public static final int COLOR_BLUE = 4;
	public static final int COLOR_RED = 5;
	public static final int COLOR_DARK_RED = 6;
	
	private TGContext context;
	private Map<Integer, UIColor> colors;
	
	private TGColorManager(TGContext context){
		this.context = context;
		this.createColors();
	}
	
	private void createColors() {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		
		this.colors = new HashMap<Integer, UIColor>();
		this.colors.put(COLOR_WHITE, uiFactory.createColor(0xff, 0xff, 0xff));
		this.colors.put(COLOR_BLACK, uiFactory.createColor(0x00, 0x00, 0x00));
		this.colors.put(COLOR_GRAY, uiFactory.createColor(0xc0, 0xc0, 0xc0));
		this.colors.put(COLOR_BLUE, uiFactory.createColor(0x00, 0x00, 0xff));
		this.colors.put(COLOR_RED, uiFactory.createColor(0xff, 0x00, 0x00));
		this.colors.put(COLOR_DARK_RED, uiFactory.createColor(0x80, 0x00, 0x00));
	}
	
	public void dispose() {
		if( this.colors != null ) {
			for(UIColor uiColor : this.colors.values()) {
				uiColor.dispose();
			}
			this.colors.clear();
		}
	}
	
	public UIColor getColor(int color) {
		return this.colors.get(color);
	}
	
	public static TGColorManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGColorManager.class.getName(), new TGSingletonFactory<TGColorManager>() {
			public TGColorManager createInstance(TGContext context) {
				return new TGColorManager(context);
			}
		});
	}
}
