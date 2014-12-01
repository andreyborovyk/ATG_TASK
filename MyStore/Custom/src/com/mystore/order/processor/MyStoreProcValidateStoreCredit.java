package com.mystore.order.processor;

import java.util.ResourceBundle;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.StoreCredit;
import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class MyStoreProcValidateStoreCredit extends ApplicationLoggingImpl implements PipelineProcessor {

	static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
	
	private static ResourceBundle sResourceBundle = LayeredResourceBundle
			.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
	
	private final int SUCCESS = 1; 
	
	protected int[] mRetCodes = { SUCCESS };
	
	@Override
	public int[] getRetCodes() {
		return mRetCodes;
	}

	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		ValidatePaymentGroupPipelineArgs args = (ValidatePaymentGroupPipelineArgs)pParam; 
		Order order = args.getOrder(); 
		
		PaymentGroup paymentGroup = args.getPaymentGroup(); 
		
		if (paymentGroup == null) 
			throw new atg.commerce.order.InvalidParameterException (
					ResourceUtils.getMsgResource( 
							"InvalidPaymentGroupParameter", 
							RESOURCE_NAME, sResourceBundle)); 
		
		if (!(paymentGroup instanceof StoreCredit)) 
			throw new atg.commerce.order.InvalidParameterException (
					ResourceUtils.getMsgResource( "InvalidPaymentGroupParameter", 
							RESOURCE_NAME, sResourceBundle)); 
		
		if (isLoggingDebug()) 
			logDebug("Validating Store Credit Group for order " + order.getId()); 
		
		
		return SUCCESS;
	}

}
