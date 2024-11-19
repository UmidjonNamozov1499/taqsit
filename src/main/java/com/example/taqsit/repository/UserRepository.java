package com.example.taqsit.repository;

import com.example.taqsit.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByIdAndDeletedFalse(Integer id);

    List<User> findAllByIdInAndDeletedFalse(List<Integer> ids);

    boolean existsByUsernameAndDeletedFalse(String username);

    class UserSpecifications {
        public static Specification<User> searchByFirstName(String search) {
            return (root, query, criteriaBuilder) -> search != null ? criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + search.toLowerCase() + "%") : query.getGroupRestriction();
        }

        public static Specification<User> searchByLastName(String search) {
            return (root, query, criteriaBuilder) -> search != null ? criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + search.toLowerCase() + "%") : query.getGroupRestriction();
        }

        public static Specification<User> searchByMiddleName(String search) {
            return (root, query, criteriaBuilder) -> search != null ? criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), "%" + search.toLowerCase() + "%") : query.getGroupRestriction();
        }

        public static Specification<User> searchByUsername(String search) {
            return (root, query, criteriaBuilder) -> search != null ? criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + search.toLowerCase() + "%") : query.getGroupRestriction();
        }

        public static Specification<User> notAdmin() {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("username"), "admin_123");
        }

        public static Specification<User> deletedFalse() {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
        }

    }
}
