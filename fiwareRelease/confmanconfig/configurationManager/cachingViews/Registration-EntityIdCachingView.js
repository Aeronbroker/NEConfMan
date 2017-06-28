function(doc) {
if (doc.registerContextRequest.contextRegistrationList){

	var contextRegistrationList = doc.registerContextRequest.contextRegistrationList;
	
	if (Object.prototype.toString.call(contextRegistrationList.contextRegistration) === '[object Array]'){
		
		var length = contextRegistrationList.contextRegistration.length;

		for (i=0; i<length; i++){
		
			if (contextRegistrationList.contextRegistration[i].entityIdList){
			
				var entityIdList = contextRegistrationList.contextRegistration[i].entityIdList;
				
				if (Object.prototype.toString.call(entityIdList.entityId) !== '[object Array]'){
					
					emit(entityIdList.entityId,doc._id+"_-_"+doc._rev+"_-_"+i+"_-_0");
					
				} else {
					var entityIdLength = entityIdList.entityId.length;
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
			
			if (Object.prototype.toString.call(entityIdList.entityId) !== '[object Array]'){
				emit(entityIdList.entityId, doc._id+"_-_"+doc._rev+"_-_0_-_0");
			} else {
				var entityIdLength = entityIdList.entityId.length;
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
