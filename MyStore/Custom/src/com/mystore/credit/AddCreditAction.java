package com.mystore.credit;

import java.util.Map;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;
import atg.process.action.ActionImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class AddCreditAction extends ActionImpl {

	private AddCreditActionConfig config;
	private MutableRepository claimableRepository;

	@Override
	public void configure(Object pConfiguration) throws ProcessException,
			UnsupportedOperationException {
		this.config = (AddCreditActionConfig) pConfiguration;

	}

	@Override
	public void initialize(Map pParameters) throws ProcessException {
		storeRequiredParameter(pParameters, "credit", Double.class);
		claimableRepository = config.getClaimableRepository();
	}

	@Override
	protected void executeAction(ProcessExecutionContext context)
			throws ProcessException {

		Double credit = (Double) getParameterValue("credit", context);

		MutableRepositoryItem profile = (MutableRepositoryItem) context
				.getSubject();

		try {
			RepositoryView storeCredits = (RepositoryView) claimableRepository
					.getView("StoreCreditClaimable");
			RqlStatement statement = RqlStatement
					.parseRqlStatement("ownerId = ?0");
			Object params[] = new Object[1];
			params[0] = profile.getRepositoryId();

			RepositoryItem[] foundStoreCredit = statement.executeQuery(
					storeCredits, params);
			MutableRepositoryItem storeCredit = null;
			if (foundStoreCredit == null || foundStoreCredit.length == 0) {
				storeCredit = claimableRepository
						.createItem("StoreCreditClaimable");
				storeCredit.setPropertyValue("amountRemaining", credit);
				storeCredit.setPropertyValue("amount", credit);
				storeCredit.setPropertyValue("ownerId",
						profile.getRepositoryId());
				claimableRepository.addItem(storeCredit);
				config.logDebug("adding store credit  " + credit
						+ " to profile with id " + params[0]);
			} else {
				storeCredit = (MutableRepositoryItem) foundStoreCredit[0];
				storeCredit.setPropertyValue("amount", credit);
				storeCredit.setPropertyValue("amountRemaining", credit);
				claimableRepository.updateItem(storeCredit);
				config.logDebug("updating store credit " + credit
						+ " to profile with id " + params[0]);
			}
		} catch (RepositoryException e) {
			if (config.isLoggingError())
				config.logError("Cannot add credit to profile \n" + e);
		}
	}
}
