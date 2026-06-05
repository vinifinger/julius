package com.finance.app.infrastructure.persistence.repository.specification;

import com.finance.app.domain.entity.TransactionFilter;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<TransactionEntity> from(TransactionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.userId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }
            if (filter.competenceId() != null) {
                predicates.add(cb.equal(root.get("competence").get("id"), filter.competenceId()));
            }
            if (filter.accountId() != null) {
                predicates.add(cb.equal(root.get("account").get("id"), filter.accountId()));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            }
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }
            if (filter.type() != null) {
                predicates.add(cb.equal(root.get("type"), filter.type()));
            }
            if (filter.subtype() != null) {
                predicates.add(cb.equal(root.get("subtype"), filter.subtype()));
            }
            if (filter.description() != null && !filter.description().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.description().toLowerCase() + "%"));
            }
            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), filter.startDate().atStartOfDay()));
            }
            if (filter.endDate() != null) {
                predicates.add(cb.lessThan(root.get("dateTime"), filter.endDate().plusDays(1).atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
