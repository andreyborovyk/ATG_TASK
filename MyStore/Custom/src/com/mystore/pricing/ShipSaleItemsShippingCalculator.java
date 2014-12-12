package com.mystore.pricing;

import atg.commerce.pricing.ShippingCalculatorImpl;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import java.util.List;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.pricing.PricingException;

public class ShipSaleItemsShippingCalculator extends
		ShippingCalculatorImpl {
	
	private String saleItemsShippingGroupType;

	public void setSaleItemsShippingGroupType(String saleItemsShippingGroupType) {
		this.saleItemsShippingGroupType = saleItemsShippingGroupType;
	}

	public String getSaleItemsShippingGroupType() {
		return saleItemsShippingGroupType;
	}

	@Override
	public void getAvailableMethods(List pMethods,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		if (pShippingGroup.getShippingGroupClassType().equals(
				getSaleItemsShippingGroupType())) {
			super.getAvailableMethods(pMethods, pShippingGroup, pPricingModel,
					pLocale, pProfile, pExtraParameters);
		}
	}

	@Override
	protected double getAmount(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {


		boolean areAllProductsOnSale = true;
		
		MutableRepositoryItem profile = (MutableRepositoryItem) pProfile;
		String salePriceList =  (String) profile.getPropertyValue("customSalePriceList");
		
		MutableRepositoryItem order = (MutableRepositoryItem) pOrder;
		
		//TODO: retrieve list_price from each product
		// if at least one of them has price list different from  profile custom sale price list
		//call super method
		//otherwise return 0
		
		return super.getAmount(pOrder, pPriceQuote, pShippingGroup, pPricingModel,
				pLocale, pProfile, pExtraParameters);
	}


	
	
}
