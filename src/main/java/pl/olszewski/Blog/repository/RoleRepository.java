package pl.olszewski.Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.olszewski.Blog.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}