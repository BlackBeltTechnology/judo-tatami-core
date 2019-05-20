package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo$products$Reference {
	private java.util.UUID identifier;


    @java.beans.ConstructorProperties({"identifier"})
    CategoryInfo$products$Reference(java.util.UUID identifier) {
    }

	// Getters
	public java.util.UUID getIdentifier() {
		return this.identifier;
	}


	// Setters
	public void setIdentifier(java.util.UUID identifier) {
		this.identifier = identifier;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (getIdentifier() != null) {
			ret.put("__identifier", this.identifier);
		}

		return ret;
	}

	public static CategoryInfo$products$Reference fromMap(Map<String, Object> map) {
		CategoryInfo$products$ReferenceBuilder builder = categoryInfo$products$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo$products$Reference(identifier=" + this.identifier + ")";
	}

    // Builder
	public static CategoryInfo$products$ReferenceBuilder categoryInfo$products$ReferenceBuilder() {
		return new CategoryInfo$products$ReferenceBuilder();
	}

	public static class CategoryInfo$products$ReferenceBuilder {
		private java.util.UUID identifier;

		CategoryInfo$products$ReferenceBuilder() {
		}
		public CategoryInfo$products$ReferenceBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}


		public CategoryInfo$products$Reference build() {
			return new CategoryInfo$products$Reference(this.identifier);
		}

		public String toString() {
    	    return "CategoryInfo$products$ReferenceBuilder.CategoryInfo$products$Reference(identifier=" + identifier + ")";
		}
	}
}