package com.mystore.order;

import atg.commerce.order.HardgoodShippingGroup;
import atg.repository.RepositoryItem;

public class ShipSaleItemsShippingGroup extends HardgoodShippingGroup {
	
	private RepositoryItem salePriceList;

	public void setSalePriceId(RepositoryItem salePriceId) {
		this.salePriceList = salePriceId;
	}

	public RepositoryItem getSalePriceId() {
		return salePriceList;
	}
}
