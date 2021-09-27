package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.util.QueryHelper;

public abstract class AbstractInfrastructureAdoService<A extends InfrastructureAdo, C extends BaseCriteria> extends AdoServiceWithUserFilter<A> {

	public AbstractInfrastructureAdoService(Class<A> elementClass) {
		super(elementClass);
	}

	public void archive(A archiveme) {

		archiveme.setArchived(true);
		em.persist(archiveme);
		em.flush();
	}

	public Predicate createBasicFilter(CriteriaBuilder cb, Root<A> root) {
		return cb.isFalse(root.get(InfrastructureAdo.ARCHIVED));
	}

	public List<A> getAllActive() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<A> cq = cb.createQuery(getElementClass());
		Root<A> from = cq.from(getElementClass());
		cq.where(createBasicFilter(cb, from));
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<A> getAllActive(String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<A> cq = cb.createQuery(getElementClass());
		Root<A> from = cq.from(getElementClass());
		cq.where(createBasicFilter(cb, from));
		cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));

		return em.createQuery(cq).getResultList();
	}

	public <T extends InfrastructureAdo> boolean isUsedInInfrastructureData(String uuid, String adoAttribute, Class<T> targetElementClass) {

		List<String> uuidList = new ArrayList<>();
		uuidList.add(uuid);
		return isUsedInInfrastructureData(uuidList, adoAttribute, targetElementClass);
	}

	public <T extends InfrastructureAdo> boolean isUsedInInfrastructureData(
		Collection<String> uuids,
		String adoAttribute,
		Class<T> targetElementClass) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(targetElementClass);
		Root<T> root = cq.from(targetElementClass);
		Join<T, A> join = root.join(adoAttribute);

		cq.where(
			cb.and(
				cb.or(cb.isNull(root.get(InfrastructureAdo.ARCHIVED)), cb.isFalse(root.get(InfrastructureAdo.ARCHIVED))),
				join.get(InfrastructureAdo.UUID).in(uuids)));

		cq.select(join.get(InfrastructureAdo.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	protected Predicate addRelevancePredicate(CriteriaBuilder cb, Root<?> from, Predicate filter, EntityRelevanceStatus relevanceStatus) {
		if (relevanceStatus != null) {
			if (relevanceStatus == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(InfrastructureAdo.ARCHIVED), false), cb.isNull(from.get(InfrastructureAdo.ARCHIVED))));
			} else if (relevanceStatus == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(InfrastructureAdo.ARCHIVED), true));
			}
		}
		return filter;
	}

	// todo remove columnName later and handle this completely here. This is not possible due to #6549 now.
	protected List<A> getByExternalId(String externalId, String columnName, boolean includeArchived) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<A> cq = cb.createQuery(getElementClass());
		Root<A> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.ilikePrecise(cb, from.get(columnName), externalId.trim());
		if (!includeArchived) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();

	}

	public abstract List<A> getByExternalId(String externalId, boolean includeArchived);

}
