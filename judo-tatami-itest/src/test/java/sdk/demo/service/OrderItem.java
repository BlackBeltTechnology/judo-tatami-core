package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItem {
	private java.util.UUID __identifier;
	private String productName;
	private Double unitPrice;
	private Integer quantity;
	private Double discount;
	private sdk.demo.service.ProductInfo product;
	private sdk.demo.service.CategoryInfo category;

    @java.beans.ConstructorProperties({"__identifier", "productName", "unitPrice", "quantity", "discount", "product", "category"})
    OrderItem(java.util.UUID __identifier, String productName, Double unitPrice, Integer quantity, Double discount, sdk.demo.service.ProductInfo product, sdk.demo.service.CategoryInfo category) {
		this.__identifier = __identifier;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
        this.product = product;
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
	public Integer getQuantity() {
		return this.quantity;
	}
	public Double getDiscount() {
		return this.discount;
	}
	public sdk.demo.service.ProductInfo getProduct() {
		return this.product;
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
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public void setProduct(sdk.demo.service.ProductInfo product) {
		this.product = product;
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
	    if (quantity != null) {
		    ret.put("quantity", this.quantity);
	    }
	    if (discount != null) {
		    ret.put("discount", this.discount);
	    }
	    if (product != null) {
		    ret.put("product", this.product.toMap());
		}
	    if (category != null) {
		    ret.put("category", this.category.toMap());
		}
		return ret;
	}

	public static OrderItem fromMap(Map<String, Object> map) {
		OrderItemBuilder builder = orderItemBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("productName")) {
			builder.productName((String) map.get("productName"));
		}
	    if (map.containsKey("unitPrice")) {
			builder.unitPrice((Double) map.get("unitPrice"));
		}
	    if (map.containsKey("quantity")) {
			builder.quantity((Integer) map.get("quantity"));
		}
	    if (map.containsKey("discount")) {
			builder.discount((Double) map.get("discount"));
		}
	    if (map.containsKey("product")) {
		    builder.product(sdk.demo.service.ProductInfo.fromMap((Map<String, Object>) map.get("product")));
	    }
	    if (map.containsKey("category")) {
		    builder.category(sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "OrderItem(identifier=" + this.__identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ", category=" + this.category + ")";
	}

    // Builder
	public static OrderItemBuilder orderItemBuilder() {
		return new OrderItemBuilder();
	}

	public static class OrderItemBuilder {
		private java.util.UUID __identifier;
        private String productName;
        private Double unitPrice;
        private Integer quantity;
        private Double discount;
        private sdk.demo.service.ProductInfo product;
        private sdk.demo.service.CategoryInfo category;

		OrderItemBuilder() {
		}

		public OrderItemBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderItemBuilder productName(String productName) {
			this.productName = productName;
			return this;
		}

		public OrderItemBuilder unitPrice(Double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}

		public OrderItemBuilder quantity(Integer quantity) {
			this.quantity = quantity;
			return this;
		}

		public OrderItemBuilder discount(Double discount) {
			this.discount = discount;
			return this;
		}

		public OrderItemBuilder product(sdk.demo.service.ProductInfo product) {
			this.product = product;
			return this;
		}

		public OrderItemBuilder category(sdk.demo.service.CategoryInfo category) {
			this.category = category;
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this.__identifier, this.productName, this.unitPrice, this.quantity, this.discount, this.product, this.category);
		}

		public String toString() {
    	    return "OrderItemBuilder.OrderItem(identifier=" + __identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ", category=" + this.category + ")";
		}
	}
}
