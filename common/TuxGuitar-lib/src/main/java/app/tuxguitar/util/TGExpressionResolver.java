package app.tuxguitar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGExpressionResolver {

	// ${name}, optionally with a quoted prefix and suffix: ${'prefix'name'suffix'}
	private static final Pattern EXPRESSION = Pattern.compile("\\$+\\{+('+.+'+)?([a-zA-Z0-9_\\-\\.]+)+('+.+'+)?\\}+");

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
			Matcher matcher = EXPRESSION.matcher(source);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(sb, this.getReplacement(matcher, customPropertyResolver));
			}
			matcher.appendTail(sb);

			return sb.toString();
		}
		return null;
	}

	// the value with its prefix and suffix, or nothing when there is no value
	private String getReplacement(Matcher matcher, PropertyResolver customPropertyResolver) {
		Object value = this.findProperty(matcher.group(2), customPropertyResolver);
		String stringValue = (value != null ? value.toString() : null);
		if( stringValue == null ) {
			return "";
		}
		return (unquote(matcher.group(1)) + Matcher.quoteReplacement(stringValue) + unquote(matcher.group(3)));
	}

	private static String unquote(String quoted) {
		return (quoted != null ? quoted.substring(1, quoted.length() -1) : "");
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
