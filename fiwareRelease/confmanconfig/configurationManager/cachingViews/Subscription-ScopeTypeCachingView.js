function(doc) {
    if (doc.subscribeContextAvailabilityRequest) {
        var subscription = doc.subscribeContextAvailabilityRequest;
        if (subscription.restriction && subscription.restriction.scope && subscription.restriction.scope.operationScope) {
            var subscriptionScopes = subscription.restriction.scope.operationScope;
            if (Object.prototype.toString.call(subscriptionScopes) !== '[object Array]') {
                emit(doc._id + "_-_" + doc._rev, subscriptionScopes.scopeType);
            } else {
                var subscriptionScopesLength = subscriptionScopes.length;
                for (j = 0; j < subscriptionScopesLength; j++) {
                    emit(doc._id + "_-_" + doc._rev, subscriptionScopes[j].scopeType);
                }
            }
        } else {
			emit(doc._id + "_-_" + doc._rev, null);
		}
    }
}
