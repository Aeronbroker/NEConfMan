function(doc) {
    if (doc.registerContextRequest.contextRegistrationList) {
        var contextRegistrationList = doc.registerContextRequest.contextRegistrationList;
        if (Object.prototype.toString.call(contextRegistrationList.contextRegistration) === '[object Array]') {
	        var length = contextRegistrationList.contextRegistration.length;
            for (i = 0; i < length; i++) {
                if (contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList) {
                    var contextRegistrationAttributeList = contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList;
                    if (Object.prototype.toString.call(contextRegistrationAttributeList.contextRegistrationAttribute) !== '[object Array]') {
                        emit(contextRegistrationAttributeList.contextRegistrationAttribute.name, doc._id + "_-_" + doc._rev + "_-_" + i + "_-_0");
                    } else {
	                    var contextRegistrationAttributeListLength = contextRegistrationAttributeList.contextRegistrationAttribute.length;
                        for (j = 0; j < contextRegistrationAttributeListLength; j++) {
                            emit(contextRegistrationAttributeList.contextRegistrationAttribute[j].name, doc._id + "_-_" + doc._rev + "_-_" + i + "_-_" + j);
                        }
                    }
                } else {
                    emit(null, doc._id + "_-_" + doc._rev + "_-_" + i + "_-_-1");
                }
            }
        } else {
            if (contextRegistrationList.contextRegistration.contextRegistrationAttributeList) {
                var contextRegistrationAttributeList = contextRegistrationList.contextRegistration.contextRegistrationAttributeList;
                if (Object.prototype.toString.call(contextRegistrationAttributeList.contextRegistrationAttribute) !== '[object Array]') {
                    emit(contextRegistrationAttributeList.contextRegistrationAttribute.name, doc._id + "_-_" + doc._rev + "_-_0_-_0");
                } else {
					var contextRegistrationAttributeListLength = contextRegistrationAttributeList.contextRegistrationAttribute.length;
                    for (j = 0; j < contextRegistrationAttributeListLength; j++) {
                        emit(contextRegistrationAttributeList.contextRegistrationAttribute[j].name, doc._id + "_-_" + doc._rev + "_-_0_-_" + j);
                    }
                }
            } else {
                emit(null, doc._id + "_-_" + doc._rev + "_-_0_-_-1");
            }
        }
    }
}
