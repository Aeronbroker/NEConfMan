function(doc) {
    if (doc.subscribeContextAvailabilityRequest) {
		
        var reference = doc.subscribeContextAvailabilityRequest.reference;
	
		if (reference) {
			
			emit(doc._id + "_-_" + doc._rev, reference);
		}
        
    }
}
