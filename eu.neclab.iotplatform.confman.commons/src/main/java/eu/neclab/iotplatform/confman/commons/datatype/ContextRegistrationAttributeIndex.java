package eu.neclab.iotplatform.confman.commons.datatype;

public class ContextRegistrationAttributeIndex {

	private ContextRegistrationIndex contextRegistrationIndex;
	private int contextRegistrationAttributeIndex;

	public ContextRegistrationAttributeIndex(String registrationId,
			int contextRegistrationIndex, int contextRegistrationAttributeIndex) {
		super();
		this.contextRegistrationIndex = new ContextRegistrationIndex(
				registrationId, contextRegistrationIndex);
		this.contextRegistrationAttributeIndex = contextRegistrationAttributeIndex;
	}

	public ContextRegistrationIndex getContextRegistrationIndex() {
		return contextRegistrationIndex;
	}

	public void setContextRegistrationIndex(
			ContextRegistrationIndex contextRegistrationIndex) {
		this.contextRegistrationIndex = contextRegistrationIndex;
	}

	public int getContextRegistrationAttributeIndex() {
		return contextRegistrationAttributeIndex;
	}

	public void setContextRegistrationAttributeIndex(
			int contextRegistrationAttributeIndex) {
		this.contextRegistrationAttributeIndex = contextRegistrationAttributeIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + contextRegistrationAttributeIndex;
		result = prime
				* result
				+ ((contextRegistrationIndex == null) ? 0
						: contextRegistrationIndex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ContextRegistrationAttributeIndex))
			return false;
		ContextRegistrationAttributeIndex other = (ContextRegistrationAttributeIndex) obj;
		if (contextRegistrationAttributeIndex != other.contextRegistrationAttributeIndex)
			return false;
		if (contextRegistrationIndex == null) {
			if (other.contextRegistrationIndex != null)
				return false;
		} else if (!contextRegistrationIndex
				.equals(other.contextRegistrationIndex))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContextRegistrationAttributeIndex [contextRegistrationIndex="
				+ contextRegistrationIndex
				+ ", contextRegistrationAttributeIndex="
				+ contextRegistrationAttributeIndex + "]";
	}
}
