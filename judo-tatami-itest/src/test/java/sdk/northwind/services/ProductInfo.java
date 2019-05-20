package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfo {
	private java.util.UUID identifier;

	private java.lang.String productName;

    @java.beans.ConstructorProperties({"identifier", "productName"})
    ProductInfo(java.util.UUID identifier, java.lang.String productName) {
        this.productName = productName;
    }

	// Getters
	public java.util.UUID getIdentifier() {
		return this.identifier;
	}

	public java.lang.String getProductName() {
		return this.productName;
	}

	// Setters
	public void setIdentifier(java.util.UUID identifier) {
		this.identifier = identifier;
	}
	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (getIdentifier() != null) {
			ret.put("__identifier", this.identifier);
		}

	    if (getProductName() != null) {
		    ret.put("productName", this.productName);
	    }
		return ret;
	}

	public static ProductInfo fromMap(Map<String, Object> map) {
		ProductInfoBuilder builder = productInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo(identifier=" + this.identifier + ", productName=" + this.productName + ")";
	}

    // Builder
	public static ProductInfoBuilder productInfoBuilder() {
		return new ProductInfoBuilder();
	}

	public static class ProductInfoBuilder {
		private java.util.UUID identifier;
        private java.lang.String productName;

		ProductInfoBuilder() {
		}
		public ProductInfoBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}

		public ProductInfoBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}

		public ProductInfo build() {
			return new ProductInfo(this.identifier, this.productName);
		}

		public String toString() {
    	    return "ProductInfoBuilder.ProductInfo(identifier=" + identifier + ", productName=" + this.productName + ")";
		}
	}
}