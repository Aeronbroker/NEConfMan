package eu.neclab.iotplatform.confman.commons.datatype;

public class ContextRegistrationIndex {

	private String registrationId;
	private int contextRegistrationIndex;

	public ContextRegistrationIndex(String registrationId,
			int contextRegistrationIndex) {
		super();
		this.registrationId = registrationId;
		this.contextRegistrationIndex = contextRegistrationIndex;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public int getContextRegistrationIndex() {
		return contextRegistrationIndex;
	}

	public void setContextRegistrationIndex(int contextRegistrationIndex) {
		this.contextRegistrationIndex = contextRegistrationIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + contextRegistrationIndex;
		result = prime * result
				+ ((registrationId == null) ? 0 : registrationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextRegistrationIndex other = (ContextRegistrationIndex) obj;
		if (contextRegistrationIndex != other.contextRegistrationIndex)
			return false;
		if (registrationId == null) {
			if (other.registrationId != null)
				return false;
		} else if (!registrationId.equals(other.registrationId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContextRegistrationIndex [registrationId=" + registrationId
				+ ", contextRegistrationIndex=" + contextRegistrationIndex
				+ "]";
	}

	
	
	

}
