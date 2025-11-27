package com.social_media.repository;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    @Query("""
        SELECT COUNT(f) = 1 FROM FriendRequest f
        WHERE (f.user.id = :userId AND f.targetUser.id = :targetUserId) OR
        (f.user.id = :targetUserId AND f.targetUser.id = :userId)
    """)
    boolean existsByUserIdTargetUserId(@Param("userId") UUID userId, @Param("targetUserId") UUID targetUserId);

    @Query("""
        SELECT COUNT(f) > 0 FROM FriendRequest f
        WHERE (f.user.id = :currentUserId AND f.targetUser.id = :targetUserId) OR
        (f.user.id = :targetUserId AND f.targetUser.id = :currentUserId) AND
        f.status = :status
    """)
    boolean existsByUserIdTargetUserIdStatus(UUID currentUserId, UUID targetUserId, FriendRequest.Status status);

    @Query("""
        FROM FriendRequest f
        WHERE (f.user.id = :userId AND f.targetUser.id = :targetUserId) OR
        (f.user.id = :targetUserId AND f.targetUser.id = :userId)
    """)
    Optional<FriendRequest> findByUserIdTargetUserId(@Param("userId") UUID userId, @Param("targetUserId") UUID targetUserId);

    @Query("""
        FROM FriendRequest f
        WHERE (f.user = :user OR f.targetUser = :user) AND
        f.status = :status
    """)
    Page<FriendRequest> findByUserStatus(User user, FriendRequest.Status status, Pageable pageable);
}
