# Java.Capstone-project-stage-1
Social-Media-Application
    App should do:
        Login/signup form
        Check profile
        Create posts
        Add other users as friendRequests
        Check their friendRequests posts on their feed
        Like posts
        Comment under posts
        Get post by id
        Delete a post
        Block a user
        Search for friendRequests based on username
        Delete comment
        View all comments
	Nice to have:
	  Dislikes
	  Decide real name visibility
	  Decide post visibility
	  Check who liked
	  Replies under comments
	  Forgot password feature


Movie-Tickets-Store
    App should do:
        Login/signup form
        Roles based privileges
        Payment system
        Book seats
        Have a selection of movies
        Have a filter for movie types
        Have a snacks menu
        Search for movies based on names
        Limited numbers of seats
	Nice to have:
	  Replies to comments
	  Movie ratings with comment
	  Promotions
	  Coupons


```application.propertis
In the application.properties file change the following:

    server.port=your-chosen-port (optional)
    spring.datasource.username=your-dasource-username
    spring.datasource.password=your-dasource-password
    spring.datasource.url=your-dasource-url
    spring.datasource.driver-class-name=your-dasource-driver-class-name
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.your-dasource-dialect

```endpoints:
    http://localhost:8000/signup
    Method: POST
    Description: Allows new user to create an account
    Payload:
        {
            "email": "your-email"
            "password": "your-password",
            "username": "your-username",
            "name": "your-name",
            "lastname": "your-last-name"
        }
    Response: {
        "message": "signup successful"
    }
        
    note: username must be unique and password must include
    at least 1 uppercase, 1 lowercase, 1 number, 1 special character

    http://localhost:8000/auth/login
    Method: POST
    Description: Allows user to login
    Payload:
        {
            "email": "your-email",
            "password": "your-password"
        }
    Response: {
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0ODAwMC",
        "username": "your-username",
        "userId": "your-id"
    }
    
    http://localhost:8000/user/profile
    Method: GET
    Description: Allows user to check profile
    Response: {
        "id": "your-id",
        "email": "your-email",
        "username": "your-username",
        "name": "your-name",
        "lastname": "your-lastname"
    }

    http://localhost:8000/user/update/username
    Method: PUT
    Description: Allows user to update username
    Payload: {
        "username": "new-username"
    }
    Response: {
        "id": "your-id",
        "email": "your-email",
        "username": "your-new-username",
        "name": "your-name",
        "lastname": "your-lastname"
    }
    
    http://localhost:8000/user/update/email
    Method: PUT
    Description: Allows user to update email
    Payload: {
        "email": "new-email"
    }
    Response: {
        "id": "your-id",
        "email": "your-new-email",
        "username": "your-username",
        "name": "your-name",
        "lastname": "your-lastname"
    }
    
    http://localhost:8000/user/password
    Method: PUT
    Description: Allows user to update password
    Payload: {
        "password": "new-password"
    }
    Response: {
        "id": "your-id",
        "email": "your-email",
        "username": "your-username",
        "name": "your-name",
        "lastname": "your-lastname"
    }
    
    http://localhost:8000/user/{username}/search
    Method: GET
    Description: Allows user to search other users by username
    Payload: {
        "content": [
            {
                "id": "user-1-id",
                "email": "user-1-email",
                "username": "user-1-username",
                "name": "user-1-name",
                "lastname": "user-1-lastname"
            },
            {
                "id": "user-2-id",
                "email": "user-2-email",
                "username": "user-2-username",
                "name": "user-2-name",
                "lastname": "user-2-lastname"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }

    http://localhost:8000/post
    Method: POST
    Description: Allows user to create a post
    Payload: {
        "title": "some-title"
    }
    Response: {
        "id": "post-id",
        "userId": "your-id",
        "username": "your-username",
        "title": "post-title",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8000/post/{postId}
    Method: GET
    Description: Allows user to get a post by id
    Payload: String postId: "postId"
    Response: {
        "id": "post-id",
        "userId": "user-id",
        "username": "user-username",
        "title": "post-title",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8000/post/user/{userId}
    Method: GET
    Description: Allows user to get a user's posts by user id
    Payload: String userId: "userIdId"
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }
    
    http://localhost:8000/post/update
    Method: PUT
    Description: Allows user to update post
    Payload: {
        "id": "post-id",
        "title": "new-post-title"
    }
    Response: {
        "id": "post-id",
        "userId": "user-id",
        "username": "user-username",
        "title": "post-title",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8000/post/{postId}/delete
    Method: DELETE
    Description: Allows user to delete post
    Payload: String postId: "post-id"
    Response: status-code-204

    http://localhost:8000/post/{postId}/like
    Method: POST
    Description: Allows user to like post
    Payload: String postId: "post-id"
    Response: {
        "id": "like-id",
        "userId": "your-id",
        "username": "your-username",
        "postId": "post-id"
    }

    http://localhost:8000/post/{postId}/comments
    Method: GET
    Description: Allows to check post comments
    Payload: String postId: "post-id"
    Response: {
        "content": [
            {
                "id": "comment-id",
                "postId": "post-id",
                "comment": "some comment",
                "userId": "user-id",
                "username": "user-username",
                "commentedTime": "commented-time"
            },
            {
                "id": "comment-id",
                "postId": "post-id",
                "comment": "some comment",
                "userId": "user-id",
                "username": "user-username",
                "commentedTime": "commented-time"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }

    http://localhost:8000/post
    Description: Allows user to check feed
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }
    
    http://localhost:8000/post/liked
    Method: GET
    Description: Allows user to get their liked posts
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "title": "post-title",
                "likes": like-count,
                "postedTime": "posted-time"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }

    http://localhost:8000/post/{postId}/dislike
    Method: DELETE
    Description: Allows user to remove like
    Payload: String postId: "post-id"
    Response: status-code-204

    http://localhost:8000/comment/{commentId}
    Method: DELETE
    Description: Allows user to delete comment
    Payload: String commentId: "comment-id"
    Response: status-code-204

    http://localhost:8000/comment
    Method: POST
    Description: Allows user to comment under a post
    Payload: {
        "postId": "243fd4b0-f8e3-4ed9-a0ae-46fbb8c048e6",
        "content": "some comment"
    }
    Response: {            
        "id": "comment-id",
        "postId": "post-id",
        "comment": "some comment",
        "userId": "user-id",
        "username": "user-username",
        "commentedTime": "commented-time"
    }

    http://localhost:8000/comment
    Method: PUT
    Description: Allows user to update comment
    Payload: {
        "id": "comment-id",
        "comment": "some updated comment"
    }
    Response: {
        "id": "comment-id",
        "postId": "post-id",
        "comment": "some updated comment",
        "userId": "user-id",
        "username": "user-username",
        "commentedTime": "commented-time"
    }

    http://localhost:8000/friend/{userId}
    Method: POST
    Description: Allows user to send user a friend request
    Payload: String userId: "user-id"
    Response: {
        "id": "friend-request-id",
        "userId": "user-id",
        "username": "user-username",
        "friendId": "recipient-id",
        "friendName": "recipient-name",
        "status": "PENDING"
    }
    
    http://localhost:8000/friend/request/{requestId}/accept
    Method: PUT
    Description: Allows user to accept a friend request
    Payload: String requestId: "request-id"
    Response: {
        "id": "friend-request-id",
        "userId": "user-id",
        "username": "user-username",
        "friendId": "recipient-id",
        "friendName": "recipient-name",
        "status": "ACCEPTED"
    }
    
    http://localhost:8000/friend/pending
    Method: GET
    Description: Allows user to check for pending friend requests
    Response: {
        "content": [
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "PENDING"
            },
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "PENDING"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }
    
    http://localhost:8000/user/blocked
    Method: GET
    Description: Allows user to check for blocked users
    Response: {
        "content": [
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "BLOCKED"
            },
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "BLOCKED"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }

    http://localhost:8000/friend
    Method: GET
    Description: Allows user to check for friend requests with "ACCEPTED" status
    Response: {
        "content": [
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "ACCEPTED"
            },
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "friendId": "recipient-id",
                "friendName": "recipient-name",
                "status": "ACCEPTED"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }

    http://localhost:8000/user/{userId}/block
    Method: POST
    Description: Allows user to block other user
    Payload: String userId: "user-id"
    Response: "user has been blocked"

    http://localhost:8000/user/{userId}/unblock
    Method: DELETE
    Description: Allows user to unblock a blocked user
    Payload: String userId: "user-id"
    Response: "user has been unblocked"

    http://localhost:8000/friend/request/{requestId}/decline
    Method: PUT
    Description: Allows user to decline friend request
    Payload: String requestId: "request-id"
    Response: {
        {
            "id": "friend-request-id",
            "userId": "user-id",
            "username": "user-username",
            "friendId": "recipient-id",
            "friendName": "recipient-name",
            "status": "DECLINED"
        }
    }