package eu.neclab.iotplatform.confman.commons.datatype;

public class EntityIdIndex {

	private ContextRegistrationIndex contextRegistrationIndex;
	private int entityIdIndex;

	public EntityIdIndex(String registrationId, int contextRegistrationIndex,
			int entityIdIndex) {
		super();
		this.contextRegistrationIndex = new ContextRegistrationIndex(
				registrationId, contextRegistrationIndex);
		this.entityIdIndex = entityIdIndex;
	}

	public ContextRegistrationIndex getContextRegistrationIndex() {
		return contextRegistrationIndex;
	}

	public void setContextRegistrationIndex(
			ContextRegistrationIndex contextRegistrationIndex) {
		this.contextRegistrationIndex = contextRegistrationIndex;
	}

	public int getEntityIdIndex() {
		return entityIdIndex;
	}

	public void setEntityIdIndex(int entityIdIndex) {
		this.entityIdIndex = entityIdIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((contextRegistrationIndex == null) ? 0
						: contextRegistrationIndex.hashCode());
		result = prime * result + entityIdIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntityIdIndex))
			return false;
		EntityIdIndex other = (EntityIdIndex) obj;
		if (contextRegistrationIndex == null) {
			if (other.contextRegistrationIndex != null)
				return false;
		} else if (!contextRegistrationIndex
				.equals(other.contextRegistrationIndex))
			return false;
		if (entityIdIndex != other.entityIdIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntityIdIndex [contextRegistrationIndex="
				+ contextRegistrationIndex + ", entityIdIndex=" + entityIdIndex
				+ "]";
	}
	
	

}
