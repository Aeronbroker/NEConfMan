function(doc) {
    if (doc.registerContextRequest.contextRegistrationList) {
        var contextRegistrationList = doc.registerContextRequest.contextRegistrationList;
        var length = contextRegistrationList.contextRegistration.length;
        if (length != null) {
            for (i = 0; i < length; i++) {
                if (contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList) {
                    var contextRegistrationAttributeList = contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList;
                    var contextRegistrationAttributeListLength = contextRegistrationAttributeList.contextRegistrationAttribute.length;
                    if (contextRegistrationAttributeListLength == null) {
                        emit(contextRegistrationAttributeList.contextRegistrationAttribute.name, doc._id + "_-_" + doc._rev + "_-_" + i + "_-_0");
                    } else {
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
                var contextRegistrationAttributeListLength = contextRegistrationAttributeList.contextRegistrationAttribute.length;
                if (contextRegistrationAttributeListLength == null) {
                    emit(contextRegistrationAttributeList.contextRegistrationAttribute.name, doc._id + "_-_" + doc._rev + "_-_0_-_0");
                } else {
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
