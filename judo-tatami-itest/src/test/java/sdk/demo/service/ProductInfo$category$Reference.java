package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfo$category$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    ProductInfo$category$Reference(java.util.UUID __identifier) {
		this.__identifier = __identifier;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

		return ret;
	}

	public static ProductInfo$category$Reference fromMap(Map<String, Object> map) {
		ProductInfo$category$ReferenceBuilder builder = productInfo$category$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo$category$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static ProductInfo$category$ReferenceBuilder productInfo$category$ReferenceBuilder() {
		return new ProductInfo$category$ReferenceBuilder();
	}

	public static class ProductInfo$category$ReferenceBuilder {
		private java.util.UUID __identifier;

		ProductInfo$category$ReferenceBuilder() {
		}

		public ProductInfo$category$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public ProductInfo$category$Reference build() {
			return new ProductInfo$category$Reference(this.__identifier);
		}

		public String toString() {
    	    return "ProductInfo$category$ReferenceBuilder.ProductInfo$category$Reference(identifier=" + __identifier + ")";
		}
	}
}
