/* (C)2025 */
package org.affinity.rdating.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  List<UserEntity> findByRegistrationMessageSentAtIsNull();
}
