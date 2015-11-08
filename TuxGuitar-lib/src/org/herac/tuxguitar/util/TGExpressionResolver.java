package org.herac.tuxguitar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGExpressionResolver {
	
	private Map<String, Object> variables;
	
	public TGExpressionResolver() {
		this.variables = new HashMap<String, Object>();
	}
	
	public void setVariable(String key, Object value) {
		this.variables.put(key, value);
	}
	
	public void removeVariable(String key) {
		this.variables.remove(key);
	}
	
	public String resolve(String source) {
		if( source != null ) {
			Pattern pattern = Pattern.compile("\\$+\\{+([a-zA-Z0-9_\\-\\.]+)\\}+");
			Matcher matcher = pattern.matcher(source);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String property = matcher.group(1);
				Object value = this.findProperty(property);
				if( value != null ) {
					String stringValue = value.toString();
					if( stringValue != null ) {
						matcher.appendReplacement(sb, Matcher.quoteReplacement(stringValue));
					}
				}
			}
			matcher.appendTail(sb);
			
			return sb.toString();
		}
		return null;
	}
	
	private final Object findProperty(String key) {
		Object value = null;
		if( this.variables.containsKey(key) ) {
			value = this.variables.get(key);
		}
		
		if( value == null ) {
			value = System.getProperty(key);
		}
		
		if( value == null ) {
			value = System.getenv(key);
		}
		return value;
	}
	
	public static TGExpressionResolver getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGExpressionResolver.class.getName(), new TGSingletonFactory<TGExpressionResolver>() {
			public TGExpressionResolver createInstance(TGContext context) {
				return new TGExpressionResolver();
			}
		});
	}
}
