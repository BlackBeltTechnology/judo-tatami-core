package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo {
	private java.util.UUID identifier;

	private java.lang.String categoryName;
	private List<sdk.northwind.services.ProductInfo> products;

    @java.beans.ConstructorProperties({"identifier", "categoryName", "products"})
    CategoryInfo(java.util.UUID identifier, java.lang.String categoryName, List<sdk.northwind.services.ProductInfo> products) {
        this.categoryName = categoryName;
        this.products = products;
    }

	// Getters
	public java.util.UUID getIdentifier() {
		return this.identifier;
	}

	public java.lang.String getCategoryName() {
		return this.categoryName;
	}
	public List<sdk.northwind.services.ProductInfo> getProducts() {
		return this.products;
	}

	// Setters
	public void setIdentifier(java.util.UUID identifier) {
		this.identifier = identifier;
	}
	public void setCategoryName(java.lang.String categoryName) {
		this.categoryName = categoryName;
	}
	public void setProducts(List<sdk.northwind.services.ProductInfo> products) {
		this.products = products;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (getIdentifier() != null) {
			ret.put("__identifier", this.identifier);
		}

	    if (getCategoryName() != null) {
		    ret.put("categoryName", this.categoryName);
	    }
	    if (getProducts() != null) {
		    ret.put("products", getProducts().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static CategoryInfo fromMap(Map<String, Object> map) {
		CategoryInfoBuilder builder = categoryInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}
	    if (map.containsKey("categoryName")) {
			builder.categoryName((java.lang.String) map.get("categoryName"));
		}
	    if (map.containsKey("products")) {
		    builder.products((List<sdk.northwind.services.ProductInfo>) ((List) map.get("products")).stream().map(i -> sdk.northwind.services.ProductInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo(identifier=" + this.identifier + ", categoryName=" + this.categoryName + ", products=" + this.products + ")";
	}

    // Builder
	public static CategoryInfoBuilder categoryInfoBuilder() {
		return new CategoryInfoBuilder();
	}

	public static class CategoryInfoBuilder {
		private java.util.UUID identifier;
        private java.lang.String categoryName;
        private List<sdk.northwind.services.ProductInfo> products;

		CategoryInfoBuilder() {
		}
		public CategoryInfoBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}

		public CategoryInfoBuilder categoryName(java.lang.String categoryName) {
			this.categoryName = categoryName;
			return this;
		}
		public CategoryInfoBuilder products(List<sdk.northwind.services.ProductInfo> products) {
			this.products = products;
			return this;
		}

		public CategoryInfo build() {
			return new CategoryInfo(this.identifier, this.categoryName, this.products);
		}

		public String toString() {
    	    return "CategoryInfoBuilder.CategoryInfo(identifier=" + identifier + ", categoryName=" + this.categoryName + ", products=" + this.products + ")";
		}
	}
}