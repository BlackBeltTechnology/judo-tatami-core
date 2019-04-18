package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderItem {
	private java.lang.String productName;
	private java.lang.String categoryName;
	private java.lang.Double unitPrice;
	private java.lang.Integer quantity;
	private java.lang.Double discount;
	private ProductInfo product;
	private CategoryInfo category;

    @java.beans.ConstructorProperties({"productName", "categoryName", "unitPrice", "quantity", "discount", "product", "category"})
    OrderItem(java.lang.String productName, java.lang.String categoryName, java.lang.Double unitPrice, java.lang.Integer quantity, java.lang.Double discount, ProductInfo product, CategoryInfo category) {
        this.productName = productName;
        this.categoryName = categoryName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
        this.product = product;
        this.category = category;
    }

	// Getters
	public java.lang.String getProductName() {
		return this.productName;
	}
	public java.lang.String getCategoryName() {
		return this.categoryName;
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
	public ProductInfo getProduct() {
		return this.product;
	}
	public CategoryInfo getCategory() {
		return this.category;
	}

	// Setters
	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}
	public void setCategoryName(java.lang.String categoryName) {
		this.categoryName = categoryName;
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
	public void setProduct(ProductInfo product) {
		this.product = product;
	}
	public void setCategory(CategoryInfo category) {
		this.category = category;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getProductName() != null) {
		    ret.put("productName", this.productName);
	    }
	    if (getCategoryName() != null) {
		    ret.put("categoryName", this.categoryName);
	    }
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
	    if (getCategory() != null) {
		    ret.put("category", getCategory().toMap());
		}
		return ret;
	}

	public static OrderItem fromMap(Map<String, Object> map) {
		OrderItemBuilder builder = orderItemBuilder();
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}
	    if (map.containsKey("categoryName")) {
			builder.categoryName((java.lang.String) map.get("categoryName"));
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
		    builder.product(ProductInfo.fromMap((Map<String, Object>) map.get("product")));
	    }
	    if (map.containsKey("category")) {
		    builder.category(CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderItem(productName=" + this.productName + ", categoryName=" + this.categoryName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ", category=" + this.category + ")";
	}

    // Builder
	public static OrderItemBuilder orderItemBuilder() {
		return new OrderItemBuilder();
	}

	public static class OrderItemBuilder {
        private java.lang.String productName;
        private java.lang.String categoryName;
        private java.lang.Double unitPrice;
        private java.lang.Integer quantity;
        private java.lang.Double discount;
        private ProductInfo product;
        private CategoryInfo category;

		OrderItemBuilder() {
		}

		public OrderItemBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}
		public OrderItemBuilder categoryName(java.lang.String categoryName) {
			this.categoryName = categoryName;
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
		public OrderItemBuilder product(ProductInfo product) {
			this.product = product;
			return this;
		}
		public OrderItemBuilder category(CategoryInfo category) {
			this.category = category;
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this.productName, this.categoryName, this.unitPrice, this.quantity, this.discount, this.product, this.category);
		}

		public String toString() {
    	    return "OrderItemBuilder.OrderItem(productName=" + this.productName + ", categoryName=" + this.categoryName + ", unitPrice=" + this.unitPrice + ", quantity=" + this.quantity + ", discount=" + this.discount + ", product=" + this.product + ", category=" + this.category + ")";
		}
	}
}