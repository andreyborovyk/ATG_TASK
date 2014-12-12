package com.mystore.order.purchase;

import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupInitializationException;
import atg.commerce.order.purchase.ShippingGroupInitializer;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.order.purchase.ShippingGroupMatcher;
import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;
import atg.commerce.order.ShippingGroupManager;
import atg.repository.RepositoryItem;
import atg.beans.DynamicBeans;
import atg.commerce.order.RepositoryContactInfo;
import java.util.Set;
import java.util.Iterator;
import com.mystore.order.ShipSaleItemsShippingGroup;

public class ShipSaleItemsShippingGroupInitializer extends GenericService
		implements ShippingGroupInitializer, ShippingGroupMatcher {

	private String salePriceListPropertyName;
	private ShippingGroupManager shippingGroupManager;
	private String shippingGroupType;
	private String shippingMethod;
	
	@Override
	public String matchShippingGroup(ShippingGroup pShippingGroup,
			ShippingGroupMapContainer pShippingGroupMapContainer) {
		if (!pShippingGroup.getShippingGroupClassType().equals(
				"shipSaleItemsShippingGroup"))
			return null;
		
		Set shippingGroupNames = pShippingGroupMapContainer.getShippingGroupNames();
		
		if (shippingGroupNames == null) {
			return null;
		}
		
		Iterator nameIter = shippingGroupNames.iterator(); 
		String shippingGroupName = null; 
		boolean found = false;
		
		ShipSaleItemsShippingGroup sg = (ShipSaleItemsShippingGroup) pShippingGroup;
		RepositoryContactInfo address = (RepositoryContactInfo) sg
		.getShippingAddress();
		
		String thisShippingGroupName = address.getCompanyName();
		
		while (nameIter.hasNext() && !found) {
			shippingGroupName = (String) nameIter.next();
			if (shippingGroupName.equals(thisShippingGroupName)) {
				found = true;
			}
		}
		if (found) 
			return shippingGroupName; 
		else 
			return null;
	}

	@Override
	public String getNewShippingGroupName(ShippingGroup pShippingGroup) {
		if (!pShippingGroup.getShippingGroupClassType().equals(
				"shipSaleItemsShippingGroup"))
			return null;
		ShipSaleItemsShippingGroup sg = (ShipSaleItemsShippingGroup) pShippingGroup;
		RepositoryContactInfo address = (RepositoryContactInfo) sg
				.getShippingAddress();
		return address.getCompanyName();
	}

	@Override
	public void initializeShippingGroups(Profile pProfile,
			ShippingGroupMapContainer pShippingGroupMapContainer,
			DynamoHttpServletRequest pRequest)
			throws ShippingGroupInitializationException {
		
		String salePriceListPropertyName = getSalePriceListPropertyName();
		if (salePriceListPropertyName == null) {
			throw new ShippingGroupInitializationException("No sale price list name set");
		}
		
		try {
			RepositoryItem salePriceList = (RepositoryItem) DynamicBeans.
			getSubPropertyValue(pProfile, salePriceListPropertyName);
			
			if (salePriceList == null)
				return;
			
			ShipSaleItemsShippingGroup sg = (ShipSaleItemsShippingGroup) 
			getShippingGroupManager().
			createShippingGroup(getShippingGroupType());
			
			RepositoryContactInfo sgAddress = (RepositoryContactInfo) sg.getShippingAddress(); 
			sg.setShippingMethod(getShippingMethod());
			sg.setSalePriceId(salePriceList);
			
			sgAddress.setAddress1((String)pProfile.getPropertyValue ("address1"));
			sgAddress.setAddress2((String)pProfile.getPropertyValue ("address2"));
			sgAddress.setAddress3((String)pProfile.getPropertyValue ("address3"));
			sgAddress.setCity((String)pProfile.getPropertyValue("city"));
			sgAddress.setCounty((String)pProfile.getPropertyValue("county"));
			sgAddress.setCountry((String)pProfile.getPropertyValue("country"));
			sgAddress.setFaxNumber((String)pProfile.getPropertyValue ("faxNumber"));
			sgAddress.setPhoneNumber((String)pProfile.getPropertyValue ("phoneNumber"));
			sgAddress.setPostalCode((String)pProfile.getPropertyValue ("postalCode"));
			sgAddress.setState((String)pProfile.getPropertyValue ("state"));
			sgAddress.setCompanyName((String)pProfile.getPropertyValue ("companyName"));
			sgAddress.setFirstName((String) pProfile.getPropertyValue("firstName"));
			sgAddress.setMiddleName((String) pProfile.getPropertyValue("middleName"));
			sgAddress.setLastName((String) pProfile.getPropertyValue("lastName"));
			
			pShippingGroupMapContainer.addShippingGroup("shipSaleItemsShippingGroup", sg);
		} catch (Exception e) {
			throw new ShippingGroupInitializationException(e);
		}
	}

	public void setSalePriceListPropertyName(String salePriceListPropertyName) {
		this.salePriceListPropertyName = salePriceListPropertyName;
	}

	public String getSalePriceListPropertyName() {
		return salePriceListPropertyName;
	}

	public void setShippingGroupManager(ShippingGroupManager shippingGroupManager) {
		this.shippingGroupManager = shippingGroupManager;
	}

	public ShippingGroupManager getShippingGroupManager() {
		return shippingGroupManager;
	}

	public void setShippingGroupType(String shippingGroupType) {
		this.shippingGroupType = shippingGroupType;
	}

	public String getShippingGroupType() {
		return shippingGroupType;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public String getShippingMethod() {
		return shippingMethod;
	}

}
