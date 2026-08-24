package com.social_media.repository;

import com.social_media.entity.UserBlock;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, UUID> {
  @Query(
      """
        SELECT COUNT(b) > 0
        FROM UserBlock b
        WHERE (b.user.id = :userA AND b.targetUser.id = :userB)
           OR (b.user.id = :userB AND b.targetUser.id = :userA)
        """)
  boolean existsBlockBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);
}
