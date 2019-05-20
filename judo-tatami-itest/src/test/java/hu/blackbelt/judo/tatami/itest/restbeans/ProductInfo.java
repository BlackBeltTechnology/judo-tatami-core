package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductInfo {
	private java.lang.String productName;

    @java.beans.ConstructorProperties({"productName"})
    ProductInfo(java.lang.String productName) {
        this.productName = productName;
    }

	// Getters
	public java.lang.String getProductName() {
		return this.productName;
	}

	// Setters
	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getProductName() != null) {
		    ret.put("productName", this.productName);
	    }
		return ret;
	}

	public static ProductInfo fromMap(Map<String, Object> map) {
		ProductInfoBuilder builder = productInfoBuilder();
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo(productName=" + this.productName + ")";
	}

    // Builder
	public static ProductInfoBuilder productInfoBuilder() {
		return new ProductInfoBuilder();
	}

	public static class ProductInfoBuilder {
        private java.lang.String productName;

		ProductInfoBuilder() {
		}

		public ProductInfoBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}

		public ProductInfo build() {
			return new ProductInfo(this.productName);
		}

		public String toString() {
    	    return "ProductInfoBuilder.ProductInfo(productName=" + this.productName + ")";
		}
	}
}