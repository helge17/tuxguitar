package org.herac.tuxguitar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGExpressionResolver {
	
	private PropertyResolver defaultResolver;
	private List<PropertyResolver> resolvers;
	
	public TGExpressionResolver() {
		this.resolvers = new ArrayList<TGExpressionResolver.PropertyResolver>();
		this.defaultResolver = new MultiplePropertyResolver(this.resolvers);
	}
	
	public void addResolver(PropertyResolver resolver) {
		this.resolvers.add(resolver);
	}
	
	public String resolve(String source) {
		return this.resolve(source, null);
	}
	
	public String resolve(String source, PropertyResolver customPropertyResolver) {
		if( source != null ) {
			Pattern pattern = Pattern.compile("\\$+\\{+('+.+'+)?([a-zA-Z0-9_\\-\\.]+)+('+.+'+)?\\}+");
			Matcher matcher = pattern.matcher(source);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String propertyPrefix = matcher.group(1);
				String property = matcher.group(2);
				String propertySuffix = matcher.group(3);
				StringBuilder replacement = new StringBuilder();
				
				Object value = this.findProperty(property, customPropertyResolver);
				if( value != null ) {
					String stringValue = value.toString();
					if( stringValue != null ) {
						if( propertyPrefix != null ) {
							replacement.append(propertyPrefix.substring(1, propertyPrefix.length() -1));
						}
						replacement.append(Matcher.quoteReplacement(stringValue));
						if( propertySuffix != null ) {
							replacement.append(propertySuffix.substring(1, propertySuffix.length() -1));
						}
					}
				}
				matcher.appendReplacement(sb, replacement.toString());
			}
			matcher.appendTail(sb);
			
			return sb.toString();
		}
		return null;
	}
	
	private final Object findProperty(String key, PropertyResolver customResolver) {
		Object value = null;
		if( customResolver != null ) {
			value = customResolver.resolveProperty(key);
		}
		
		if( value == null ) {
			value = this.defaultResolver.resolveProperty(key);
		}
		
		if( value == null ) {
			value = System.getProperty(key);
		}
		
		if( value == null ) {
			value = System.getenv(key);
		}
		return value;
	}
	
	public static interface PropertyResolver {
		
		Object resolveProperty(String property);
	}
	
	public static class MultiplePropertyResolver implements PropertyResolver {
		
		private List<PropertyResolver> resolvers;
		
		public MultiplePropertyResolver(List<PropertyResolver> resolvers) {
			this.resolvers = resolvers;
		}
		
		public Object resolveProperty(String property) {
			for(PropertyResolver resolver : this.resolvers) {
				Object value = resolver.resolveProperty(property);
				if( value != null ) {
					return value;
				}
			}
			return null;
		}
	}
	
	public static class MapPropertyResolver implements PropertyResolver {
		
		private Map<String, Object> variables;
		
		public MapPropertyResolver(Map<String, Object> variables) {
			this.variables = variables;
		}
		
		public Object resolveProperty(String property) {
			if( this.variables.containsKey(property) ) {
				return this.variables.get(property);
			}
			return null;
		}
	}
	
	public static TGExpressionResolver getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGExpressionResolver.class.getName(), new TGSingletonFactory<TGExpressionResolver>() {
			public TGExpressionResolver createInstance(TGContext context) {
				return new TGExpressionResolver();
			}
		});
	}
}
