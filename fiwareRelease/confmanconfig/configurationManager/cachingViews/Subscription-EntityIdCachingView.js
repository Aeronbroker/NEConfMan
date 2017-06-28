function(doc) {
    if (doc.subscribeContextAvailabilityRequest) {
		
        var subscription = doc.subscribeContextAvailabilityRequest;
	
		if (subscription.entityIdList) {
			
			var subscriptionEntityIdList = subscription.entityIdList;
						
			if (Object.prototype.toString.call(subscriptionEntityIdList.entityId) !== '[object Array]') {
			
				emit(subscriptionEntityIdList.entityId, doc._id + "_-_" + doc._rev);
				
			} else {

				var subscriptionEntityIdListLength = subscriptionEntityIdList.entityId.length;

				for (j = 0; j < subscriptionEntityIdListLength; j++) {
					emit(subscriptionEntityIdList.entityId[j], doc._id + "_-_" + doc._rev);
				}
			}
			
		} else {
			emit(null, doc._id + "_-_" + doc._rev);
		}
        
    }
}
