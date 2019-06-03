package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfo {
	private java.util.UUID __identifier;

	private java.lang.String productName;
	private java.lang.Integer quantityPerUnit;
	private java.lang.Double unitPrice;
	private java.lang.Boolean discounted;
	private java.lang.Double weight;
	private sdk.demo.service.CategoryInfo category;

    @java.beans.ConstructorProperties({"__identifier", "productName", "quantityPerUnit", "unitPrice", "discounted", "weight", "category"})
    ProductInfo(java.util.UUID __identifier, java.lang.String productName, java.lang.Integer quantityPerUnit, java.lang.Double unitPrice, java.lang.Boolean discounted, java.lang.Double weight, sdk.demo.service.CategoryInfo category) {
		this.__identifier = __identifier;
        this.productName = productName;
        this.quantityPerUnit = quantityPerUnit;
        this.unitPrice = unitPrice;
        this.discounted = discounted;
        this.weight = weight;
        this.category = category;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getProductName() {
		return this.productName;
	}
	public java.lang.Integer getQuantityPerUnit() {
		return this.quantityPerUnit;
	}
	public java.lang.Double getUnitPrice() {
		return this.unitPrice;
	}
	public java.lang.Boolean getDiscounted() {
		return this.discounted;
	}
	public java.lang.Double getWeight() {
		return this.weight;
	}
	public sdk.demo.service.CategoryInfo getCategory() {
		return this.category;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}
	public void setQuantityPerUnit(java.lang.Integer quantityPerUnit) {
		this.quantityPerUnit = quantityPerUnit;
	}
	public void setUnitPrice(java.lang.Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setDiscounted(java.lang.Boolean discounted) {
		this.discounted = discounted;
	}
	public void setWeight(java.lang.Double weight) {
		this.weight = weight;
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
	    if (quantityPerUnit != null) {
		    ret.put("quantityPerUnit", this.quantityPerUnit);
	    }
	    if (unitPrice != null) {
		    ret.put("unitPrice", this.unitPrice);
	    }
	    if (discounted != null) {
		    ret.put("discounted", this.discounted);
	    }
	    if (weight != null) {
		    ret.put("weight", this.weight);
	    }
	    if (category != null) {
		    ret.put("category", this.category.toMap());
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
	    if (map.containsKey("quantityPerUnit")) {
			builder.quantityPerUnit((java.lang.Integer) map.get("quantityPerUnit"));
		}
	    if (map.containsKey("unitPrice")) {
			builder.unitPrice((java.lang.Double) map.get("unitPrice"));
		}
	    if (map.containsKey("discounted")) {
			builder.discounted((java.lang.Boolean) map.get("discounted"));
		}
	    if (map.containsKey("weight")) {
			builder.weight((java.lang.Double) map.get("weight"));
		}
	    if (map.containsKey("category")) {
		    builder.category(sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "ProductInfo(identifier=" + this.__identifier + ", productName=" + this.productName + ", quantityPerUnit=" + this.quantityPerUnit + ", unitPrice=" + this.unitPrice + ", discounted=" + this.discounted + ", weight=" + this.weight + ", category=" + this.category + ")";
	}

    // Builder
	public static ProductInfoBuilder productInfoBuilder() {
		return new ProductInfoBuilder();
	}

	public static class ProductInfoBuilder {
		private java.util.UUID __identifier;
        private java.lang.String productName;
        private java.lang.Integer quantityPerUnit;
        private java.lang.Double unitPrice;
        private java.lang.Boolean discounted;
        private java.lang.Double weight;
        private sdk.demo.service.CategoryInfo category;

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
		public ProductInfoBuilder quantityPerUnit(java.lang.Integer quantityPerUnit) {
			this.quantityPerUnit = quantityPerUnit;
			return this;
		}
		public ProductInfoBuilder unitPrice(java.lang.Double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}
		public ProductInfoBuilder discounted(java.lang.Boolean discounted) {
			this.discounted = discounted;
			return this;
		}
		public ProductInfoBuilder weight(java.lang.Double weight) {
			this.weight = weight;
			return this;
		}
		public ProductInfoBuilder category(sdk.demo.service.CategoryInfo category) {
			this.category = category;
			return this;
		}

		public ProductInfo build() {
			return new ProductInfo(this.__identifier, this.productName, this.quantityPerUnit, this.unitPrice, this.discounted, this.weight, this.category);
		}

		public String toString() {
    	    return "ProductInfoBuilder.ProductInfo(identifier=" + __identifier + ", productName=" + this.productName + ", quantityPerUnit=" + this.quantityPerUnit + ", unitPrice=" + this.unitPrice + ", discounted=" + this.discounted + ", weight=" + this.weight + ", category=" + this.category + ")";
		}
	}
}