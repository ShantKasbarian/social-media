package com.social_media.repository;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
  @Query(
      """
        SELECT COUNT(f) = 1 FROM FriendRequest f
        WHERE (f.user.id = :userId AND f.targetUser.id = :targetUserId) OR
        (f.user.id = :targetUserId AND f.targetUser.id = :userId)
    """)
  boolean existsByUserIdTargetUserId(
      @Param("userId") UUID userId, @Param("targetUserId") UUID targetUserId);

  @Query(
      """
        FROM FriendRequest f
        WHERE (f.user = :user OR f.targetUser = :user) AND
        f.status = :status
    """)
  Page<FriendRequest> findByUserStatus(
      @Param("user") User user, @Param("status") FriendRequest.Status status, Pageable pageable);
}
