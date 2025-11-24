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
        WHERE (f.user.id = :userId AND f.friend.id = :friendId) OR
        (f.user.id = :friendId AND f.friend.id = :userId)
    """)
    boolean existsByUserIdFriendId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    @Query("""
        FROM FriendRequest f
        WHERE (f.user.id = :userId AND f.friend.id = :friendId) OR
        (f.user.id = :friendId AND f.friend.id = :userId)
    """)
    Optional<FriendRequest> findByUserIdFriendId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    @Query("""
        FROM FriendRequest f
        WHERE (f.friend = :friend OR f.user = :friend) AND
        f.status = :status
    """)
    Page<FriendRequest> findByUserFriend_FriendAndStatus(User friend, FriendRequest.Status status, Pageable pageable);

    @Query("""
        FROM FriendRequest f
        WHERE f.friend = :friend AND
        f.status = PENDING
    """)
    Page<FriendRequest> findByFriend(User friend, Pageable pageable);

    @Query("""
        SELECT COUNT(f) > 0 FROM FriendRequest f
        WHERE (f.user.id = :currentUserId AND f.friend.id = :targetUserId) OR
        (f.user.id = :targetUserId AND f.friend.id = :currentUserId) AND
        f.status = BLOCKED
    """)
    boolean isFriendRequestBlockedByUserIdFriendId(UUID currentUserId, UUID targetUserId);

    @Query("SELECT COUNT(f) > 0 FROM FriendRequest f WHERE f.id = :id AND f.status = BLOCKED")
    boolean isFriendRequestBlocked(UUID id);
}
