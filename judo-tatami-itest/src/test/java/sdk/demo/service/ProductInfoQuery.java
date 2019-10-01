package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfoQuery {
	private java.util.UUID __identifier;
	private String productName;
	private Double unitPrice;
	private sdk.demo.service.CategoryInfo category;

    @java.beans.ConstructorProperties({"__identifier", "productName", "unitPrice", "category"})
    ProductInfoQuery(java.util.UUID __identifier, String productName, Double unitPrice, sdk.demo.service.CategoryInfo category) {
		this.__identifier = __identifier;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.category = category;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}
	public String getProductName() {
		return this.productName;
	}
	public Double getUnitPrice() {
		return this.unitPrice;
	}
	public sdk.demo.service.CategoryInfo getCategory() {
		return this.category;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setCategory(sdk.demo.service.CategoryInfo category) {
		this.category = category;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (productName != null) {
		    ret.put("productName", this.productName);
	    }
	    if (unitPrice != null) {
		    ret.put("unitPrice", this.unitPrice);
	    }
	    if (category != null) {
		    ret.put("category", this.category.toMap());
		}
		return ret;
	}

	public static ProductInfoQuery fromMap(Map<String, Object> map) {
		ProductInfoQueryBuilder builder = productInfoQueryBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("productName")) {
			builder.productName((String) map.get("productName"));
		}
	    if (map.containsKey("unitPrice")) {
			builder.unitPrice((Double) map.get("unitPrice"));
		}
	    if (map.containsKey("category")) {
		    builder.category(sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "ProductInfoQuery(identifier=" + this.__identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", category=" + this.category + ")";
	}

    // Builder
	public static ProductInfoQueryBuilder productInfoQueryBuilder() {
		return new ProductInfoQueryBuilder();
	}

	public static class ProductInfoQueryBuilder {
		private java.util.UUID __identifier;
        private String productName;
        private Double unitPrice;
        private sdk.demo.service.CategoryInfo category;

		ProductInfoQueryBuilder() {
		}

		public ProductInfoQueryBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public ProductInfoQueryBuilder productName(String productName) {
			this.productName = productName;
			return this;
		}

		public ProductInfoQueryBuilder unitPrice(Double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}

		public ProductInfoQueryBuilder category(sdk.demo.service.CategoryInfo category) {
			this.category = category;
			return this;
		}

		public ProductInfoQuery build() {
			return new ProductInfoQuery(this.__identifier, this.productName, this.unitPrice, this.category);
		}

		public String toString() {
    	    return "ProductInfoQueryBuilder.ProductInfoQuery(identifier=" + __identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", category=" + this.category + ")";
		}
	}
}
