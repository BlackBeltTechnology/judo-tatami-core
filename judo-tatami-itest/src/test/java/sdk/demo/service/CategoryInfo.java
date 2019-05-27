package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo {
	private java.util.UUID __identifier;

	private java.lang.String categoryName;

    @java.beans.ConstructorProperties({"__identifier", "categoryName"})
    CategoryInfo(java.util.UUID __identifier, java.lang.String categoryName) {
		this.__identifier = __identifier;
        this.categoryName = categoryName;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getCategoryName() {
		return this.categoryName;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setCategoryName(java.lang.String categoryName) {
		this.categoryName = categoryName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (categoryName != null) {
		    ret.put("categoryName", this.categoryName);
	    }
		return ret;
	}

	public static CategoryInfo fromMap(Map<String, Object> map) {
		CategoryInfoBuilder builder = categoryInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("categoryName")) {
			builder.categoryName((java.lang.String) map.get("categoryName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo(identifier=" + this.__identifier + ", categoryName=" + this.categoryName + ")";
	}

    // Builder
	public static CategoryInfoBuilder categoryInfoBuilder() {
		return new CategoryInfoBuilder();
	}

	public static class CategoryInfoBuilder {
		private java.util.UUID __identifier;
        private java.lang.String categoryName;

		CategoryInfoBuilder() {
		}
		public CategoryInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}

		public CategoryInfoBuilder categoryName(java.lang.String categoryName) {
			this.categoryName = categoryName;
			return this;
		}

		public CategoryInfo build() {
			return new CategoryInfo(this.__identifier, this.categoryName);
		}

		public String toString() {
    	    return "CategoryInfoBuilder.CategoryInfo(identifier=" + __identifier + ", categoryName=" + this.categoryName + ")";
		}
	}
}