function(doc) {
if (doc.registerContextRequest.contextRegistrationList){

	var contextRegistrationList = doc.registerContextRequest.contextRegistrationList;
	var length = contextRegistrationList.contextRegistration.length;
	
	if (length != null){
	
		for (i=0; i<length; i++){
		
			if (contextRegistrationList.contextRegistration[i].entityIdList){
			
				var entityIdList = contextRegistrationList.contextRegistration[i].entityIdList;
				var entityIdLength = entityIdList.entityId.length;
				
				if (entityIdLength == null){
					emit(entityIdList.entityId,doc._id+"_-_"+doc._rev+"_-_"+i+"_-_0");
				} else {
					for(j=0; j<entityIdLength; j++){
						emit(entityIdList.entityId[j],doc._id+"_-_"+doc._rev+"_-_"+i+"_-_"+j);
					}
				}
				
			} else {
				emit(null,doc._id+"_-_"+doc._rev+"_-_"+i+"_-_-1");
			}
			
		}
		
	} else {
	
		if (contextRegistrationList.contextRegistration.entityIdList){
		
			var entityIdList = contextRegistrationList.contextRegistration.entityIdList;
			var entityIdLength = entityIdList.entityId.length;
			
			if (entityIdLength == null){
				emit(entityIdList.entityId, doc._id+"_-_"+doc._rev+"_-_0_-_0");
			} else {
				for(j=0; j<entityIdLength; j++){
					emit(entityIdList.entityId[j],doc._id+"_-_"+doc._rev+"_-_0_-_"+j);
				}
			}
			
		} else {
			emit(null,doc._id+"_-_"+doc._rev+"_-_0_-_-1");
		}
	
	}
}


}
