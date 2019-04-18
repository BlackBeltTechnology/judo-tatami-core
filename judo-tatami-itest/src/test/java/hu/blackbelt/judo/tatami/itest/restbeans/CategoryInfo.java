package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo {
	private java.lang.String categoryName;
	private List<ProductInfo> products;

    @java.beans.ConstructorProperties({"categoryName", "products"})
    CategoryInfo(java.lang.String categoryName, List<ProductInfo> products) {
        this.categoryName = categoryName;
        this.products = products;
    }

	// Getters
	public java.lang.String getCategoryName() {
		return this.categoryName;
	}
	public List<ProductInfo> getProducts() {
		return this.products;
	}

	// Setters
	public void setCategoryName(java.lang.String categoryName) {
		this.categoryName = categoryName;
	}
	public void setProducts(List<ProductInfo> products) {
		this.products = products;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
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
	    if (map.containsKey("categoryName")) {
			builder.categoryName((java.lang.String) map.get("categoryName"));
		}
	    if (map.containsKey("products")) {
		    builder.products((List<ProductInfo>) ((List) map.get("products")).stream().map(i -> ProductInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "CategoryInfo(categoryName=" + this.categoryName + ", products=" + this.products + ")";
	}

    // Builder
	public static CategoryInfoBuilder categoryInfoBuilder() {
		return new CategoryInfoBuilder();
	}

	public static class CategoryInfoBuilder {
        private java.lang.String categoryName;
        private List<ProductInfo> products;

		CategoryInfoBuilder() {
		}

		public CategoryInfoBuilder categoryName(java.lang.String categoryName) {
			this.categoryName = categoryName;
			return this;
		}
		public CategoryInfoBuilder products(List<ProductInfo> products) {
			this.products = products;
			return this;
		}

		public CategoryInfo build() {
			return new CategoryInfo(this.categoryName, this.products);
		}

		public String toString() {
    	    return "CategoryInfoBuilder.CategoryInfo(categoryName=" + this.categoryName + ", products=" + this.products + ")";
		}
	}
}