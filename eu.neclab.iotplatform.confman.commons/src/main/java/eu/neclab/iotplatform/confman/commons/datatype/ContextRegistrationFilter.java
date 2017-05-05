package eu.neclab.iotplatform.confman.commons.datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import eu.neclab.iotplatform.confman.commons.datatype.Pair;

public class ContextRegistrationFilter {

	private Map<Integer, Pair<Set<Integer>, Set<Integer>>> fullFilter = new HashMap<Integer, Pair<Set<Integer>, Set<Integer>>>();

	// In order to go into the full filter, the map needs to have at least one
	// element for each of the set
	private Map<Integer, Pair<Set<Integer>, Set<Integer>>> preStageFilter = new HashMap<Integer, Pair<Set<Integer>, Set<Integer>>>();;

	/**
	 * It return true when after this adding the filter becomes a full filter
	 * 
	 * @param contextRegistrationIndex
	 * @param entityIdIndex
	 * @return
	 */
	public boolean addEntityIdIndex(int contextRegistrationIndex,
			int entityIdIndex) {

		if (fullFilter.containsKey(contextRegistrationIndex)) {

			// if we are here it means that in the fullFilter map there is
			// already a non-empty set for entityIdIndex and a non-empty set for
			// attributeIndex
			fullFilter.get(contextRegistrationIndex).getElement1()
					.add(entityIdIndex);

			return true;

		} else {

			if (preStageFilter.containsKey(contextRegistrationIndex)) {

				// if we are it means that we are there is a preStage filter,
				// likely not complete (either missing a non-empty set for
				// entityIdIndex or a non-empty set for
				// attributeIndex)

				Pair<Set<Integer>, Set<Integer>> pair = preStageFilter
						.get(contextRegistrationIndex);

				Set<Integer> entityIdSet = pair.getElement1();

				if (entityIdSet == null) {

					entityIdSet = new TreeSet<Integer>();
					entityIdSet.add(entityIdIndex);
					pair.setElement1(entityIdSet);

					if (pair.getElement2() != null
							|| !pair.getElement2().isEmpty()) {

						// if we are here it means that the missing non-empty
						// set was the EntityIdSet. So we create it and then
						// move filter from the pre-stage to the full filter

						fullFilter
								.put(contextRegistrationIndex, preStageFilter
										.remove(contextRegistrationIndex));

						return true;

					}

				} else {

					// if we are here it means that the EntityIdSet was already
					// created and the checking of the other set has been
					// already done previously

					entityIdSet.add(entityIdIndex);

					return false;

				}

			} else {

				// if we are here it means that we have to still create the pair
				// in the pre-stage filter

				Set<Integer> entityIdSet = new TreeSet<Integer>();

				preStageFilter
						.put(contextRegistrationIndex,
								new Pair<Set<Integer>, Set<Integer>>(
										entityIdSet, null));

				entityIdSet.add(entityIdIndex);

				return false;

			}

			return false;

		}
	}

	/**
	 * It return true when after this adding the filter becomes a full filter
	 * 
	 * @param contextRegistrationIndex
	 * @param attributeIndex
	 * @return
	 */
	public boolean addAttributeIndex(int contextRegistrationIndex,
			int attributeIndex) {

		if (fullFilter.containsKey(contextRegistrationIndex)) {

			// if we are here it means that in the fullFilter map there is
			// already a non-empty set for entityIdIndex and a non-empty set for
			// attributeIndex
			fullFilter.get(contextRegistrationIndex).getElement2()
					.add(attributeIndex);

			return true;

		} else {

			if (preStageFilter.containsKey(contextRegistrationIndex)) {

				// if we are it means that we are there is a preStage filter,
				// likely not complete (either missing a non-empty set for
				// entityIdIndex or a non-empty set for
				// attributeIndex)

				Pair<Set<Integer>, Set<Integer>> pair = preStageFilter
						.get(contextRegistrationIndex);

				Set<Integer> attributeSet = pair.getElement2();

				if (attributeSet == null) {

					attributeSet = new TreeSet<Integer>();
					attributeSet.add(attributeIndex);
					pair.setElement2(attributeSet);

					if (pair.getElement1() != null
							|| !pair.getElement1().isEmpty()) {

						// if we are here it means that the missing non-empty
						// set was the EntityIdSet. So we create it and then
						// move filter from the pre-stage to the full filter

						fullFilter
								.put(contextRegistrationIndex, preStageFilter
										.remove(contextRegistrationIndex));

						return true;

					}

				} else {

					// if we are here it means that the EntityIdSet was already
					// created and the checking of the other set has been
					// already done previously

					attributeSet.add(attributeIndex);

					return false;

				}

			} else {

				// if we are here it means that we have to still create the pair
				// in the pre-stage filter

				Set<Integer> attributeSet = new TreeSet<Integer>();

				preStageFilter
						.put(contextRegistrationIndex,
								new Pair<Set<Integer>, Set<Integer>>(null,
										attributeSet));

				attributeSet.add(attributeIndex);

				return false;

			}

			return false;

		}
	}

	public Map<Integer, Pair<Set<Integer>, Set<Integer>>> getFullFilter() {
		return fullFilter;
	}

	public void setFullFilter(
			Map<Integer, Pair<Set<Integer>, Set<Integer>>> fullFilter) {
		this.fullFilter = fullFilter;
	}

	@Override
	public String toString() {
		return "ContextRegistrationFilter [fullFilter=" + fullFilter
				+ ", preStageFilter=" + preStageFilter + "]";
	}

	
	
}
