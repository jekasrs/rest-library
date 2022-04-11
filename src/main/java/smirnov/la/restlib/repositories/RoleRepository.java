package smirnov.la.restlib.repositories;

import smirnov.la.restlib.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}