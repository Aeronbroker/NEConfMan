package eu.neclab.iotplatform.confman.reset;

import eu.neclab.iotplatform.confman.commons.interfaces.Resettable;

public class Reset {
	
	private Resettable resettable;
	
	public Resettable getResettable() {
		return resettable;
	}


	public void setResettable(Resettable resettable) {
		this.resettable = resettable;
		
		System.out.println("\n\n\nGOING TO WIPE OUT ALL THE DATABASES!!!\n\n\n");
		resettable.reset();
	}


}
