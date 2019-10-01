package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfoQuery$category$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    ProductInfoQuery$category$Reference(java.util.UUID __identifier) {
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

	public static ProductInfoQuery$category$Reference fromMap(Map<String, Object> map) {
		ProductInfoQuery$category$ReferenceBuilder builder = productInfoQuery$category$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "ProductInfoQuery$category$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static ProductInfoQuery$category$ReferenceBuilder productInfoQuery$category$ReferenceBuilder() {
		return new ProductInfoQuery$category$ReferenceBuilder();
	}

	public static class ProductInfoQuery$category$ReferenceBuilder {
		private java.util.UUID __identifier;

		ProductInfoQuery$category$ReferenceBuilder() {
		}

		public ProductInfoQuery$category$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public ProductInfoQuery$category$Reference build() {
			return new ProductInfoQuery$category$Reference(this.__identifier);
		}

		public String toString() {
    	    return "ProductInfoQuery$category$ReferenceBuilder.ProductInfoQuery$category$Reference(identifier=" + __identifier + ")";
		}
	}
}
