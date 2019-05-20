package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderItem {
	private java.lang.Double unitPrice;
	private java.lang.Integer quantity;
	private java.lang.Double discount;
	private ProductInfo product;

    @java.beans.ConstructorProperties({"unitPrice", "quantity", "discount", "product"})
    OrderItem(java.lang.Double unitPrice, java.lang.Integer quantity, java.lang.Double discount, ProductInfo product) {
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
        this.product = product;
    }

	// Getters
	public java.lang.Double getUnitPrice() {
		return this.unitPrice;
	}
	public java.lang.Integer getQuantity() {
		return this.quantity;
	}
	public java.lang.Double getDiscount() {
		return this.discount;
	}
	public ProductInfo getProduct() {
		return this.product;
	}

	// Setters
	public void setUnitPrice(java.lang.Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public void setQuantity(java.lang.Integer quantity) {
		this.quantity = quantity;
	}
	public void setDiscount(java.lang.Double discount) {
		this.discount = discount;
	}
	public void setProduct(ProductInfo product) {
		this.product = product;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getUnitPrice() != null) {
		    ret.put("unitPrice", this.unitPrice);
	    }
	    if (getQuantity() != null) {
		    ret.put("quantity", this.quantity);
	    }
	    if (getDiscount() != null) {
		    ret.put("discount", this.discount);
	    }
	    if (getProduct() != null) {
		    ret.put("product", getProduct().toMap());
		}
		return ret;
	}

	public static OrderItem fromMap(Map<String, Object> map) {
		OrderItemBuilder builder = orderItemBuilder();
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
		    builder.product(ProductInfo.fromMap((Map<String, Object>) map.get("product")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderItem(unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ")";
	}

    // Builder
	public static OrderItemBuilder orderItemBuilder() {
		return new OrderItemBuilder();
	}

	public static class OrderItemBuilder {
        private java.lang.Double unitPrice;
        private java.lang.Integer quantity;
        private java.lang.Double discount;
        private ProductInfo product;

		OrderItemBuilder() {
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
		public OrderItemBuilder product(ProductInfo product) {
			this.product = product;
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this.unitPrice, this.quantity, this.discount, this.product);
		}

		public String toString() {
    	    return "OrderItemBuilder.OrderItem(unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ")";
		}
	}
}