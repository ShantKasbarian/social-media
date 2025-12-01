```application.propertis
In the application.properties file change the following:

    server.port=your-chosen-port (optional)
    spring.datasource.username=your-dasource-username
    spring.datasource.password=your-dasource-password
    spring.datasource.url=your-dasource-url
    spring.datasource.driver-class-name=your-dasource-driver-class-name
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.your-rdbms-dialect

```endpoints:
    http://localhost:8080/signup
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
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0ODAwMC",
        "username": "your-username",
        "userId": "your-id"
    }
        
    note: username and email must be unique and password must include
    at least 1 uppercase, 1 lowercase, 1 number, 1 special character

    http://localhost:8080/login
    Method: POST
    Description: Allows user to login
    Payload:
        {
            "username": "your-username",
            "password": "your-password"
        }
    Response: {
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0ODAwMC",
        "username": "your-username",
        "userId": "your-id"
    }
    
    http://localhost:8080/users
    Method: GET
    Description: Allows user to check profile
    Response: {
        "id": "your-id",
        "email": "your-email",
        "username": "your-username",
        "name": "your-name",
        "lastname": "your-lastname"
    }

    http://localhost:8080/users
    Method: PUT
    Description: Allows user to update credentials
    Payload: {
        "username": "new-username",
        "email": "new-email",
        "password": "password"
    }
    
    http://localhost:8080/users/{username}
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

    http://localhost:8080/posts
    Method: POST
    Description: Allows user to create a post
    Payload: {
        "text": "some-text"
    }
    Response: {
        "id": "post-id",
        "userId": "your-id",
        "username": "your-username",
        "text": "post-text",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8080/posts/{postId}
    Method: GET
    Description: Allows user to get a post by id
    Payload: String postId: "postId"
    Response: {
        "id": "post-id",
        "userId": "user-id",
        "username": "user-username",
        "text": "post-text",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8080/posts
    Method: PUT
    Description: Allows user to update post
    Payload: {
        "id": "post-id",
        "text": "new-post-text"
    }
    Response: {
        "id": "post-id",
        "userId": "user-id",
        "username": "user-username",
        "text": "post-text",
        "likes": like-count,
        "postedTime": "posted-time"
    }
    
    http://localhost:8080/posts/{postId}
    Method: DELETE
    Description: Allows user to delete post
    Payload: String postId: "post-id"
    Response: status-code-204
    
    http://localhost:8080/posts
    Description: Allows user to check friends posts
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
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
    
    http://localhost:8080/posts/users/{userId}
    Method: GET
    Description: Allows user to get a user's posts by user id
    Payload: String userId: "userIdId"
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
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
    
    http://localhost:8080/posts/likes
    Method: GET
    Description: Allows user to get their liked posts
    Response: {
        "content": [
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
                "likes": like-count,
                "postedTime": "posted-time"
            },
            {
                "id": "post-id",
                "userId": "user-id",
                "username": "user-username",
                "text": "post-text",
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

    http://localhost:8080/likes/posts/{postId}
    Method: POST
    Description: Allows user to like post
    Payload: String postId: "post-id"
    Response: {
        "id": "like-id",
        "userId": "your-id",
        "username": "your-username",
        "postId": "post-id"
    }
    
    http://localhost:8080/likes/posts/{postId}
    Method: DELETE
    Description: Allows user to remove like
    Payload: String postId: "post-id"
    Response: status-code-204

    http://localhost:8080/comments
        Method: POST
        Description: Allows user to create comment
        Payload: {
            "postId": "243fd4b0-f8e3-4ed9-a0ae-46fbb8c048e6",
            "text": "some comment"
        }
        Response: {            
            "id": "comment-id",
            "postId": "post-id",
            "text": "some comment",
            "userId": "user-id",
            "username": "user-username",
            "commentedTime": "commented-time"
        }
        
    http://localhost:8080/comments
    Method: PUT
    Description: Allows user to update comment
    Payload: {
        "id": "comment-id",
        "text": "some updated comment"
    }
    Response: {
        "id": "comment-id",
        "postId": "post-id",
        "text": "some updated comment",
        "userId": "user-id",
        "username": "user-username",
        "commentedTime": "commented-time"
    }
    
    http://localhost:8080/comments/{commentId}
    Method: DELETE
    Description: Allows user to delete comment
    Payload: String commentId: "comment-id"
    Response: status-code-204

    http://localhost:8080/comments/posts/{postId}
    Method: GET
    Description: Allows to check post comments
    Payload: String postId: "post-id"
    Response: {
        "content": [
            {
                "id": "comment-id",
                "postId": "post-id",
                "text": "some comment",
                "userId": "user-id",
                "username": "user-username",
                "commentedTime": "commented-time"
            },
            {
                "id": "comment-id",
                "postId": "post-id",
                "text": "some comment",
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

    http://localhost:8080/friend-requests/users/{userId}
    Method: POST
    Description: Allows user to send user a friend request
    Payload: String userId: "user-id"
    Response: {
        "id": "friend-request-id",
        "userId": "user-id",
        "username": "user-username",
        "targetUserId": "target-user-id",
        "targetUsername": "target-username",
        "status": "PENDING"
    }
    
    http://localhost:8080/friend-requests/{id}/status/{status}
    Method: PATCH
    Description: Allows user to update friend request status
    Payload: String requestId: "request-id", FriendRequest.Status status: "target-status"
    Response: {
        "id": "friend-request-id",
        "userId": "user-id",
        "username": "user-username",
        "targetUserId": "target-user-id",
        "targetUsername": "target-username",
        "status": "target-status"
    }
    
    http://localhost:8080/friend-requests/{id}
    Method: DELETE
    Description: Allows user to delete a friend request
    Payload: id: "friend-request-id"
    Response: status-code-204
    
    http://localhost:8080/friend-requests/{status}
    Method: GET
    Description: Allows user to check for friend requests with target status
    Payload: FriendRequest.Status status: "target-status"
    Response: {
        "content": [
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "targetUserId": "target-user-id",
                "targetUsername": "target-username",
                "status": "target-status"
            },
            {
                "id": "friend-request-id",
                "userId": "user-id",
                "username": "user-username",
                "targetUserId": "target-user-id",
                "targetUsername": "target-username",
                "status": "target-status"
            }
        ],
        "pageNo": 0,
        "pageSize": 10,
        "totalElements": 2,
        "totalPages": 1,
        "empty": false
    }
