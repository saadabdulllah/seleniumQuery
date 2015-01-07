package io.github.seleniumquery.by.xpath;

public enum CssCombinationType {
	
	/*
	 * if CONDITIONAL_SIMPLE, then the expr can be just appended to other, such as:
	 * //*[@other][@thisSelector]
	 * 
	 * if CONDITIONAL_TO_ALL, then this must be applied to the whole result of the other, such as:
	 * (//*[@other])[@thisSelector]
	 */
	CONDITIONAL_SIMPLE {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			if (sourceXPathExpression.endsWith("]")) {
				// because the previous was merged as a conditional, and we are a conditional as well, so we just 'AND it
				return sourceXPathExpression.substring(0, sourceXPathExpression.length()-1) + " and " + otherXPathExpression.substring(1);
			}
			return sourceXPathExpression + otherXPathExpression;
		}
		@Override
		public String mergeAsCondition(String sourceXPathExpression, String otherXPathExpression) {
			if (sourceXPathExpression.equals(MATCH_EVERYTHING_XPATH_CONDITIONAL)) {
				return removeBraces(otherXPathExpression);
			}
			return sourceXPathExpression + " and " + removeBraces(otherXPathExpression);
		}
	},
	CONDITIONAL_TO_ALL {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			return "(" + sourceXPathExpression + ")" + otherXPathExpression;
		}
		@Override
		public String mergeAsCondition(String sourceXPathExpression, String otherXPathExpression) {
			if (sourceXPathExpression.equals(MATCH_EVERYTHING_XPATH_CONDITIONAL)) {
				return removeBraces(otherXPathExpression);
			}
			return sourceXPathExpression + " and " + removeBraces(otherXPathExpression);
		}
	},

	// "//"
	DESCENDANT_GENERAL {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			if ("*".equals(otherXPathExpression)) {
				return sourceXPathExpression + "//*";
			}
			return sourceXPathExpression + "//*[self::" + otherXPathExpression + "]";
		}
		@Override
		public String mergeAsCondition(String sourceXPathExpression, String otherXPathExpression) {
			return unsupported("general descendant");
		}
	},
	
	// "/"
	DESCENDANT_DIRECT {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			if ("*".equals(otherXPathExpression)) {
				return sourceXPathExpression + "/*";
			}
			return sourceXPathExpression + "/*[self::" + otherXPathExpression + "]";
		}
		@Override
		public String mergeAsCondition(String sourceXPathExpression, String otherXPathExpression) {
			return unsupported("direct descendant");
		}
	},
	
	// "/following-sibling::"
	// This one will be used by the "General Adjacent" and the "Direct Adjacent" selectors
	// (in order to differentiate, the "Direct Adjacent" selector will itself add a [position()=1] to its expression)
	ADJACENT {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			return sourceXPathExpression + "/following-sibling::" + otherXPathExpression;
		}
	},
	
	TAG {
		@Override
		public String merge(String sourceXPathExpression, String otherXPathExpression) {
			if ("*".equals(otherXPathExpression)) {
				return CONDITIONAL_SIMPLE.merge(sourceXPathExpression, "["+MATCH_EVERYTHING_XPATH_CONDITIONAL+"]");
			}
			return CONDITIONAL_SIMPLE.merge(sourceXPathExpression, "[self::"+otherXPathExpression+"]");
		}
	};

	public static final String MATCH_EVERYTHING_XPATH_CONDITIONAL = "true()";

	/**
	 * @return The merged expressions.
	 */
	public abstract String merge(String sourceXPathExpression, String otherXPathExpression);

	public String mergeAsCondition(String sourceXPathExpression, String otherXPathExpression) {
		return this.merge(sourceXPathExpression, otherXPathExpression);
	}

	private static String unsupported(String cssSelectorType) {
		throw new UnsupportedConditionalSelector("The "+cssSelectorType+" selector is not supported as condition inside selectors.");
	}

	private static String removeBraces(String src) {
		return src.substring(1, src.length()-1);
	}

}