package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo {
	private java.time.ZonedDateTime orderDate;
	private java.time.ZonedDateTime shippedDate;
	private java.lang.String shipperName;
	private java.lang.Double totalPrice;
	private java.lang.Double discount;
	private java.lang.Integer discountedItemsWithSingleManufacturer;
	private List<OrderItemProduct> itemsWithCategoryPicture;
	private List<OrderItem> items;
	private List<Comment> comments;

    @java.beans.ConstructorProperties({"orderDate", "shippedDate", "shipperName", "totalPrice", "discount", "discountedItemsWithSingleManufacturer", "itemsWithCategoryPicture", "items", "comments"})
    OrderInfo(java.time.ZonedDateTime orderDate, java.time.ZonedDateTime shippedDate, java.lang.String shipperName, java.lang.Double totalPrice, java.lang.Double discount, java.lang.Integer discountedItemsWithSingleManufacturer, List<OrderItemProduct> itemsWithCategoryPicture, List<OrderItem> items, List<Comment> comments) {
        this.orderDate = orderDate;
        this.shippedDate = shippedDate;
        this.shipperName = shipperName;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.discountedItemsWithSingleManufacturer = discountedItemsWithSingleManufacturer;
        this.itemsWithCategoryPicture = itemsWithCategoryPicture;
        this.items = items;
        this.comments = comments;
    }

	// Getters
	public java.time.ZonedDateTime getOrderDate() {
		return this.orderDate;
	}
	public java.time.ZonedDateTime getShippedDate() {
		return this.shippedDate;
	}
	public java.lang.String getShipperName() {
		return this.shipperName;
	}
	public java.lang.Double getTotalPrice() {
		return this.totalPrice;
	}
	public java.lang.Double getDiscount() {
		return this.discount;
	}
	public java.lang.Integer getDiscountedItemsWithSingleManufacturer() {
		return this.discountedItemsWithSingleManufacturer;
	}
	public List<OrderItemProduct> getItemsWithCategoryPicture() {
		return this.itemsWithCategoryPicture;
	}
	public List<OrderItem> getItems() {
		return this.items;
	}
	public List<Comment> getComments() {
		return this.comments;
	}

	// Setters
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public void setShippedDate(java.time.ZonedDateTime shippedDate) {
		this.shippedDate = shippedDate;
	}
	public void setShipperName(java.lang.String shipperName) {
		this.shipperName = shipperName;
	}
	public void setTotalPrice(java.lang.Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public void setDiscount(java.lang.Double discount) {
		this.discount = discount;
	}
	public void setDiscountedItemsWithSingleManufacturer(java.lang.Integer discountedItemsWithSingleManufacturer) {
		this.discountedItemsWithSingleManufacturer = discountedItemsWithSingleManufacturer;
	}
	public void setItemsWithCategoryPicture(List<OrderItemProduct> itemsWithCategoryPicture) {
		this.itemsWithCategoryPicture = itemsWithCategoryPicture;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getOrderDate() != null) {
		    ret.put("orderDate", this.orderDate);
	    }
	    if (getShippedDate() != null) {
		    ret.put("shippedDate", this.shippedDate);
	    }
	    if (getShipperName() != null) {
		    ret.put("shipperName", this.shipperName);
	    }
	    if (getTotalPrice() != null) {
		    ret.put("totalPrice", this.totalPrice);
	    }
	    if (getDiscount() != null) {
		    ret.put("discount", this.discount);
	    }
	    if (getDiscountedItemsWithSingleManufacturer() != null) {
		    ret.put("discountedItemsWithSingleManufacturer", this.discountedItemsWithSingleManufacturer);
	    }
	    if (getItemsWithCategoryPicture() != null) {
		    ret.put("itemsWithCategoryPicture", getItemsWithCategoryPicture().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (getItems() != null) {
		    ret.put("items", getItems().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (getComments() != null) {
		    ret.put("comments", getComments().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static OrderInfo fromMap(Map<String, Object> map) {
		OrderInfoBuilder builder = orderInfoBuilder();
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("shippedDate")) {
			builder.shippedDate((java.time.ZonedDateTime) map.get("shippedDate"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((java.lang.String) map.get("shipperName"));
		}
	    if (map.containsKey("totalPrice")) {
			builder.totalPrice((java.lang.Double) map.get("totalPrice"));
		}
	    if (map.containsKey("discount")) {
			builder.discount((java.lang.Double) map.get("discount"));
		}
	    if (map.containsKey("discountedItemsWithSingleManufacturer")) {
			builder.discountedItemsWithSingleManufacturer((java.lang.Integer) map.get("discountedItemsWithSingleManufacturer"));
		}
	    if (map.containsKey("itemsWithCategoryPicture")) {
		    builder.itemsWithCategoryPicture((List<OrderItemProduct>) ((List) map.get("itemsWithCategoryPicture")).stream().map(i -> OrderItemProduct.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("items")) {
		    builder.items((List<OrderItem>) ((List) map.get("items")).stream().map(i -> OrderItem.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("comments")) {
		    builder.comments((List<Comment>) ((List) map.get("comments")).stream().map(i -> Comment.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo(orderDate=" + this.orderDate + ", shippedDate=" + this.shippedDate + ", shipperName=" + this.shipperName + ", totalPrice=" + this.totalPrice + ", discount=" + this.discount + ", discountedItemsWithSingleManufacturer=" + this.discountedItemsWithSingleManufacturer + ", itemsWithCategoryPicture=" + this.itemsWithCategoryPicture + ", items=" + this.items + ", comments=" + this.comments + ")";
	}

    // Builder
	public static OrderInfoBuilder orderInfoBuilder() {
		return new OrderInfoBuilder();
	}

	public static class OrderInfoBuilder {
        private java.time.ZonedDateTime orderDate;
        private java.time.ZonedDateTime shippedDate;
        private java.lang.String shipperName;
        private java.lang.Double totalPrice;
        private java.lang.Double discount;
        private java.lang.Integer discountedItemsWithSingleManufacturer;
        private List<OrderItemProduct> itemsWithCategoryPicture;
        private List<OrderItem> items;
        private List<Comment> comments;

		OrderInfoBuilder() {
		}

		public OrderInfoBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}
		public OrderInfoBuilder shippedDate(java.time.ZonedDateTime shippedDate) {
			this.shippedDate = shippedDate;
			return this;
		}
		public OrderInfoBuilder shipperName(java.lang.String shipperName) {
			this.shipperName = shipperName;
			return this;
		}
		public OrderInfoBuilder totalPrice(java.lang.Double totalPrice) {
			this.totalPrice = totalPrice;
			return this;
		}
		public OrderInfoBuilder discount(java.lang.Double discount) {
			this.discount = discount;
			return this;
		}
		public OrderInfoBuilder discountedItemsWithSingleManufacturer(java.lang.Integer discountedItemsWithSingleManufacturer) {
			this.discountedItemsWithSingleManufacturer = discountedItemsWithSingleManufacturer;
			return this;
		}
		public OrderInfoBuilder itemsWithCategoryPicture(List<OrderItemProduct> itemsWithCategoryPicture) {
			this.itemsWithCategoryPicture = itemsWithCategoryPicture;
			return this;
		}
		public OrderInfoBuilder items(List<OrderItem> items) {
			this.items = items;
			return this;
		}
		public OrderInfoBuilder comments(List<Comment> comments) {
			this.comments = comments;
			return this;
		}

		public OrderInfo build() {
			return new OrderInfo(this.orderDate, this.shippedDate, this.shipperName, this.totalPrice, this.discount, this.discountedItemsWithSingleManufacturer, this.itemsWithCategoryPicture, this.items, this.comments);
		}

		public String toString() {
    	    return "OrderInfoBuilder.OrderInfo(orderDate=" + this.orderDate + ", shippedDate=" + this.shippedDate + ", shipperName=" + this.shipperName + ", totalPrice=" + this.totalPrice + ", discount=" + this.discount + ", discountedItemsWithSingleManufacturer=" + this.discountedItemsWithSingleManufacturer + ", itemsWithCategoryPicture=" + this.itemsWithCategoryPicture + ", items=" + this.items + ", comments=" + this.comments + ")";
		}
	}
}