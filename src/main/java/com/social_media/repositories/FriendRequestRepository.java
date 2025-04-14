package com.social_media.repositories;

import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    @Query("SELECT COUNT(f) = 1 from FriendRequest f where (f.user.id = :userId and f.friend.id = :friendId) or (f.user.id = :friendId and f.friend.id = :userId)")
    boolean existsByUser_idFriend_id(@Param("userId") String userId, @Param("friendId") String friendId);

    @Query("from FriendRequest f where (f.user.id = :userId and f.friend.id = :friendId) or (f.user.id = :friendId and f.friend.id = :userId)")
    Optional<FriendRequest> findByUser_idFriend_id(@Param("userId") String userId, @Param("friendId") String friendId);

    @Query("from FriendRequest f where (f.friend = :friend OR f.user = :friend) and f.status = :status ")
    Page<FriendRequest> findByUserFriend_FriendAndStatus(User friend, FriendshipStatus status, Pageable pageable);

    @Query("from FriendRequest f where f.friend = :friend and f.status = PENDING")
    Page<FriendRequest> findByFriend(User friend, Pageable pageable);
}
