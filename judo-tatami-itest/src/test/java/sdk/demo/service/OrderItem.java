package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItem {
	private java.util.UUID __identifier;

	private java.lang.String productName;
	private java.lang.Double unitPrice;
	private java.lang.Integer quantity;
	private java.lang.Double discount;
	private sdk.demo.service.ProductInfo product;

    @java.beans.ConstructorProperties({"__identifier", "productName", "unitPrice", "quantity", "discount", "product"})
    OrderItem(java.util.UUID __identifier, java.lang.String productName, java.lang.Double unitPrice, java.lang.Integer quantity, java.lang.Double discount, sdk.demo.service.ProductInfo product) {
		this.__identifier = __identifier;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
        this.product = product;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getProductName() {
		return this.productName;
	}
	public java.lang.Double getUnitPrice() {
		return this.unitPrice;
	}
	public java.lang.Integer getQuantity() {
		return this.quantity;
	}
	public java.lang.Double getDiscount() {
		return this.discount;
	}
	public sdk.demo.service.ProductInfo getProduct() {
		return this.product;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}
	public void setUnitPrice(java.lang.Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setQuantity(java.lang.Integer quantity) {
		this.quantity = quantity;
	}
	public void setDiscount(java.lang.Double discount) {
		this.discount = discount;
	}
	public void setProduct(sdk.demo.service.ProductInfo product) {
		this.product = product;
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
		return ret;
	}

	public static OrderItem fromMap(Map<String, Object> map) {
		OrderItemBuilder builder = orderItemBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}
	    if (map.containsKey("unitPrice")) {
			builder.unitPrice((java.lang.Double) map.get("unitPrice"));
		}
	    if (map.containsKey("quantity")) {
			builder.quantity((java.lang.Integer) map.get("quantity"));
		}
	    if (map.containsKey("discount")) {
			builder.discount((java.lang.Double) map.get("discount"));
		}
	    if (map.containsKey("product")) {
		    builder.product(sdk.demo.service.ProductInfo.fromMap((Map<String, Object>) map.get("product")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderItem(identifier=" + this.__identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ")";
	}

    // Builder
	public static OrderItemBuilder orderItemBuilder() {
		return new OrderItemBuilder();
	}

	public static class OrderItemBuilder {
		private java.util.UUID __identifier;
        private java.lang.String productName;
        private java.lang.Double unitPrice;
        private java.lang.Integer quantity;
        private java.lang.Double discount;
        private sdk.demo.service.ProductInfo product;

		OrderItemBuilder() {
		}
		public OrderItemBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}

		public OrderItemBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}
		public OrderItemBuilder unitPrice(java.lang.Double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}
		public OrderItemBuilder quantity(java.lang.Integer quantity) {
			this.quantity = quantity;
			return this;
		}
		public OrderItemBuilder discount(java.lang.Double discount) {
			this.discount = discount;
			return this;
		}
		public OrderItemBuilder product(sdk.demo.service.ProductInfo product) {
			this.product = product;
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this.__identifier, this.productName, this.unitPrice, this.quantity, this.discount, this.product);
		}

		public String toString() {
    	    return "OrderItemBuilder.OrderItem(identifier=" + __identifier + ", productName=" + this.productName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ")";
		}
	}
}