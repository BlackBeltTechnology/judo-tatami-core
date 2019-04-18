package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderItemProduct {
	private java.lang.String productName;

    @java.beans.ConstructorProperties({"productName"})
    OrderItemProduct(java.lang.String productName) {
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

	public static OrderItemProduct fromMap(Map<String, Object> map) {
		OrderItemProductBuilder builder = orderItemProductBuilder();
	    if (map.containsKey("productName")) {
			builder.productName((java.lang.String) map.get("productName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderItemProduct(productName=" + this.productName + ")";
	}

    // Builder
	public static OrderItemProductBuilder orderItemProductBuilder() {
		return new OrderItemProductBuilder();
	}

	public static class OrderItemProductBuilder {
        private java.lang.String productName;

		OrderItemProductBuilder() {
		}

		public OrderItemProductBuilder productName(java.lang.String productName) {
			this.productName = productName;
			return this;
		}

		public OrderItemProduct build() {
			return new OrderItemProduct(this.productName);
		}

		public String toString() {
    	    return "OrderItemProductBuilder.OrderItemProduct(productName=" + this.productName + ")";
		}
	}
}