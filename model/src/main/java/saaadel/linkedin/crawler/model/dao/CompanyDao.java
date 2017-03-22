package saaadel.linkedin.crawler.model.dao;

import saaadel.linkedin.crawler.model.entity.Company;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;

public class CompanyDao extends AbstractDao<Company, Long> {
    private final TypedQuery<Company> findNotProcessedQuery;
    private final Query markAsProcessedQuery;

    public CompanyDao(EntityManager em) {
        super(em, Company.class);

        this.findNotProcessedQuery = prepareFindNotProcessedQuery();
        this.markAsProcessedQuery = prepareMarkAsProcessed();
    }

    private TypedQuery<Company> prepareFindNotProcessedQuery() {
        final CriteriaBuilder cb = unwrapEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Company> criteria = cb.createQuery(Company.class);
        criteria.where(cb.equal(criteria.from(Company.class).get("processed"), 0));
        final TypedQuery<Company> query = unwrapEntityManager().createQuery(criteria).setFirstResult(0).setMaxResults(1);
        query.setLockMode(LockModeType.NONE);
        return query;
    }

    private Query prepareMarkAsProcessed() {
        final CriteriaBuilder cb = unwrapEntityManager().getCriteriaBuilder();
        final CriteriaUpdate<Company> criteria = cb.createCriteriaUpdate(Company.class).set("processed", 1);
        final ParameterExpression<Long> entityIdParam = cb.parameter(Long.class, "entityId");
        criteria.where(cb.equal(criteria.from(Company.class).get("id"), entityIdParam));
        return unwrapEntityManager().createQuery(criteria).setMaxResults(1);
    }

    public boolean markAsProcessed(Long entityId) {
        if (entityId == null) {
            throw new IllegalArgumentException(new NullPointerException("entityId"));
        }
        markAsProcessedQuery.setParameter("entityId", entityId);
        return markAsProcessedQuery.executeUpdate() > 0;
    }

    public Company findNotProcessed() {
        try {
            return findNotProcessedQuery.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }


}
