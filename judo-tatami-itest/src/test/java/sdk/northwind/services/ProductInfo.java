package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfo {
	private java.util.UUID __identifier;

	private java.lang.String productName;

    @java.beans.ConstructorProperties({"__identifier", "productName"})
    ProductInfo(java.util.UUID __identifier, java.lang.String productName) {
		this.__identifier = __identifier;
        this.productName = productName;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getProductName() {
		return this.productName;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (productName != null) {
		    ret.put("productName", this.productName);
	    }
		return ret;
	}

	public static ProductInfo fromMap(Map<String, Object> map) {
		ProductInfoBuilder builder = productInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo(identifier=" + this.__identifier + ", productName=" + this.productName + ")";
	}

    // Builder
	public static ProductInfoBuilder productInfoBuilder() {
		return new ProductInfoBuilder();
	}

	public static class ProductInfoBuilder {
		private java.util.UUID __identifier;
        private java.lang.String productName;

		ProductInfoBuilder() {
		}
		public ProductInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}

		public ProductInfoBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}

		public ProductInfo build() {
			return new ProductInfo(this.__identifier, this.productName);
		}

		public String toString() {
    	    return "ProductInfoBuilder.ProductInfo(identifier=" + __identifier + ", productName=" + this.productName + ")";
		}
	}
}