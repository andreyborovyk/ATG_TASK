package com.mystore.credit;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;

public class AddCreditActionConfig extends GenericService {
	
	private String creditPropertyName;
	private MutableRepository claimableRepository;
	
	
	public void setCreditPropertyName(String creditPropertyName) {
		this.creditPropertyName = creditPropertyName;
	}

	public String getCreditPropertyName() {
		return creditPropertyName;
	}

	public void setClaimableRepository(MutableRepository claimableRepository) {
		this.claimableRepository = claimableRepository;
	}

	public MutableRepository getClaimableRepository() {
		return claimableRepository;
	}
}
