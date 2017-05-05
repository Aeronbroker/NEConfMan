package eu.neclab.iotplatform.confman.commons.datatype;

import java.util.HashMap;
import java.util.Map;

public class RegistrationsFilter {

	// private boolean takeThemAll = false;

	private Map<String, ContextRegistrationFilter> registrationFilter = new HashMap<String, ContextRegistrationFilter>();

	private Map<String, ContextRegistrationFilter> preStageRegistrationsFilter = new HashMap<String, ContextRegistrationFilter>();

	public void addEntityIdIndex(EntityIdIndex entityIdIndex) {

		if (registrationFilter.containsKey(entityIdIndex
				.getContextRegistrationIndex().getRegistrationId())) {

			registrationFilter.get(
					entityIdIndex.getContextRegistrationIndex()
							.getRegistrationId()).addEntityIdIndex(
					entityIdIndex.getContextRegistrationIndex()
							.getContextRegistrationIndex(),
					entityIdIndex.getEntityIdIndex());
		} else {

			if (preStageRegistrationsFilter.containsKey(entityIdIndex
					.getContextRegistrationIndex().getRegistrationId())) {

				ContextRegistrationFilter contextRegistrationFilter = preStageRegistrationsFilter
						.get(entityIdIndex.getContextRegistrationIndex()
								.getRegistrationId());

				if (contextRegistrationFilter.addEntityIdIndex(entityIdIndex
						.getContextRegistrationIndex()
						.getContextRegistrationIndex(), entityIdIndex
						.getEntityIdIndex())) {

					registrationFilter.put(entityIdIndex
							.getContextRegistrationIndex().getRegistrationId(),
							preStageRegistrationsFilter.remove(entityIdIndex
									.getContextRegistrationIndex()
									.getRegistrationId()));

				}

			} else {

				ContextRegistrationFilter contextRegistrationFilter = new ContextRegistrationFilter();

				preStageRegistrationsFilter.put(entityIdIndex
						.getContextRegistrationIndex().getRegistrationId(),
						contextRegistrationFilter);

				contextRegistrationFilter.addEntityIdIndex(entityIdIndex
						.getContextRegistrationIndex()
						.getContextRegistrationIndex(), entityIdIndex
						.getEntityIdIndex());
			}
		}

	}

	public void addContextRegistrationAttributeIndex(ContextRegistrationAttributeIndex attributeIndex) {

		if (registrationFilter.containsKey(attributeIndex
				.getContextRegistrationIndex().getRegistrationId())) {

			registrationFilter.get(
					attributeIndex.getContextRegistrationIndex()
							.getRegistrationId()).addAttributeIndex(
					attributeIndex.getContextRegistrationIndex()
							.getContextRegistrationIndex(),
					attributeIndex.getContextRegistrationAttributeIndex());
		} else {

			if (preStageRegistrationsFilter.containsKey(attributeIndex
					.getContextRegistrationIndex().getRegistrationId())) {

				ContextRegistrationFilter contextRegistrationFilter = preStageRegistrationsFilter
						.get(attributeIndex.getContextRegistrationIndex()
								.getRegistrationId());

				if (contextRegistrationFilter.addAttributeIndex(attributeIndex
						.getContextRegistrationIndex()
						.getContextRegistrationIndex(), attributeIndex
						.getContextRegistrationAttributeIndex())) {

					registrationFilter.put(attributeIndex
							.getContextRegistrationIndex().getRegistrationId(),
							preStageRegistrationsFilter.remove(attributeIndex
									.getContextRegistrationIndex()
									.getRegistrationId()));

				}

			} else {

				ContextRegistrationFilter contextRegistrationFilter = new ContextRegistrationFilter();

				preStageRegistrationsFilter.put(attributeIndex
						.getContextRegistrationIndex().getRegistrationId(),
						contextRegistrationFilter);

				contextRegistrationFilter.addEntityIdIndex(attributeIndex
						.getContextRegistrationIndex()
						.getContextRegistrationIndex(), attributeIndex
						.getContextRegistrationAttributeIndex());
			}
		}

	}

	// public boolean isTakeThemAll() {
	// return takeThemAll;
	// }
	//
	// public void setTakeThemAll(boolean takeThemAll) {
	// this.takeThemAll = takeThemAll;
	// }

	public Map<String, ContextRegistrationFilter> getRegistrationFilterMap() {
		return registrationFilter;
	}

	public void setRegistrationFilterMap(
			Map<String, ContextRegistrationFilter> registrationFilter) {
		this.registrationFilter = registrationFilter;
	}

	public void addUnfilteredRegistration(String id) {
		registrationFilter.put(id, null);
	}

}
