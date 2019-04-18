package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductInfo {
	private java.lang.String clazz;
	private CategoryInfo category;

    @java.beans.ConstructorProperties({"clazz", "category"})
    ProductInfo(java.lang.String clazz, CategoryInfo category) {
        this.clazz = clazz;
        this.category = category;
    }

	// Getters
	public java.lang.String getClazz() {
		return this.clazz;
	}
	public CategoryInfo getCategory() {
		return this.category;
	}

	// Setters
	public void setClazz(java.lang.String clazz) {
		this.clazz = clazz;
	}
	public void setCategory(CategoryInfo category) {
		this.category = category;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getClazz() != null) {
		    ret.put("class", this.clazz);
	    }
	    if (getCategory() != null) {
		    ret.put("category", getCategory().toMap());
		}
		return ret;
	}

	public static ProductInfo fromMap(Map<String, Object> map) {
		ProductInfoBuilder builder = productInfoBuilder();
	    if (map.containsKey("class")) {
			builder.clazz((java.lang.String) map.get("class"));
		}
	    if (map.containsKey("category")) {
		    builder.category(CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo(clazz=" + this.clazz + ", category=" + this.category + ")";
	}

    // Builder
	public static ProductInfoBuilder productInfoBuilder() {
		return new ProductInfoBuilder();
	}

	public static class ProductInfoBuilder {
        private java.lang.String clazz;
        private CategoryInfo category;

		ProductInfoBuilder() {
		}

		public ProductInfoBuilder clazz(java.lang.String clazz) {
			this.clazz = clazz;
			return this;
		}
		public ProductInfoBuilder category(CategoryInfo category) {
			this.category = category;
			return this;
		}

		public ProductInfo build() {
			return new ProductInfo(this.clazz, this.category);
		}

		public String toString() {
    	    return "ProductInfoBuilder.ProductInfo(clazz=" + this.clazz + ", category=" + this.category + ")";
		}
	}
}