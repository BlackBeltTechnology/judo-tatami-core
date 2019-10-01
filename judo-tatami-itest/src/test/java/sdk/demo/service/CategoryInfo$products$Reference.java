package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo$products$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    CategoryInfo$products$Reference(java.util.UUID __identifier) {
		this.__identifier = __identifier;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

		return ret;
	}

	public static CategoryInfo$products$Reference fromMap(Map<String, Object> map) {
		CategoryInfo$products$ReferenceBuilder builder = categoryInfo$products$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo$products$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static CategoryInfo$products$ReferenceBuilder categoryInfo$products$ReferenceBuilder() {
		return new CategoryInfo$products$ReferenceBuilder();
	}

	public static class CategoryInfo$products$ReferenceBuilder {
		private java.util.UUID __identifier;

		CategoryInfo$products$ReferenceBuilder() {
		}

		public CategoryInfo$products$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public CategoryInfo$products$Reference build() {
			return new CategoryInfo$products$Reference(this.__identifier);
		}

		public String toString() {
    	    return "CategoryInfo$products$ReferenceBuilder.CategoryInfo$products$Reference(identifier=" + __identifier + ")";
		}
	}
}
