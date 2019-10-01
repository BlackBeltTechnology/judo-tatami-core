package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo {
	private java.util.UUID __identifier;
	private String categoryName;
	private List<sdk.demo.service.ProductInfo> products;

    @java.beans.ConstructorProperties({"__identifier", "categoryName", "products"})
    CategoryInfo(java.util.UUID __identifier, String categoryName, List<sdk.demo.service.ProductInfo> products) {
		this.__identifier = __identifier;
        this.categoryName = categoryName;
        this.products = products;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}
	public String getCategoryName() {
		return this.categoryName;
	}
	public List<sdk.demo.service.ProductInfo> getProducts() {
		return this.products;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public void setProducts(List<sdk.demo.service.ProductInfo> products) {
		this.products = products;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (categoryName != null) {
		    ret.put("categoryName", this.categoryName);
	    }
	    if (products != null) {
		    ret.put("products", this.products.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static CategoryInfo fromMap(Map<String, Object> map) {
		CategoryInfoBuilder builder = categoryInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("categoryName")) {
			builder.categoryName((String) map.get("categoryName"));
		}
	    if (map.containsKey("products")) {
		    builder.products((List<sdk.demo.service.ProductInfo>) ((List) map.get("products")).stream().map(i -> sdk.demo.service.ProductInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo(identifier=" + this.__identifier + ", categoryName=" + this.categoryName + ", products=" + this.products + ")";
	}

    // Builder
	public static CategoryInfoBuilder categoryInfoBuilder() {
		return new CategoryInfoBuilder();
	}

	public static class CategoryInfoBuilder {
		private java.util.UUID __identifier;
        private String categoryName;
        private List<sdk.demo.service.ProductInfo> products;

		CategoryInfoBuilder() {
		}

		public CategoryInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public CategoryInfoBuilder categoryName(String categoryName) {
			this.categoryName = categoryName;
			return this;
		}

		public CategoryInfoBuilder products(List<sdk.demo.service.ProductInfo> products) {
			this.products = products;
			return this;
		}

		public CategoryInfo build() {
			return new CategoryInfo(this.__identifier, this.categoryName, this.products);
		}

		public String toString() {
    	    return "CategoryInfoBuilder.CategoryInfo(identifier=" + __identifier + ", categoryName=" + this.categoryName + ", products=" + this.products + ")";
		}
	}
}
