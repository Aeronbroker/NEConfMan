function(doc) {
    if (doc.subscribeContextAvailabilityRequest) {
		
        var restriction = doc.subscribeContextAvailabilityRequest.restriction;
	
		if (restriction) {
			
			if (restriction.attributeExpression == ""){
			
				emit(doc._id + "_-_" + doc._rev, null);

			} else {
			
				emit(doc._id + "_-_" + doc._rev, restriction.attributeExpression);
			}
		}
        
    }
}
