function(doc) {
    if (doc.subscribeContextAvailabilityRequest) {
		
        var subscription = doc.subscribeContextAvailabilityRequest;
	
		if (subscription.attributeList) {
			var subscriptionAttributeList = subscription.attributeList;
						
			if (Object.prototype.toString.call(subscriptionAttributeList) !== '[object Array]') {
				
				emit(subscriptionAttributeList, doc._id + "_-_" + doc._rev);
				
			} else {

				var subscriptionAttributeListLength = subscriptionAttributeList.length;
				for (j = 0; j < subscriptionAttributeListLength; j++) {
					emit(subscriptionAttributeList[j], doc._id + "_-_" + doc._rev);
				}
				
			}
		} else {
			emit(null, doc._id + "_-_" + doc._rev);
		}
        
    }
}
