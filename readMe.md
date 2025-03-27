# Java.Capstone-project-stage-1
Social-Media-Application
    App should do:
        Login/signup form
        Check profile
        Create posts
        Add other users as friends
        Check their friends posts on their feed
        Like posts
        Comment under posts
        Get post by id
        Delete a post
        Block a user
        Search for friends based on username
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

# Java.Capstone-project-stage-2
User: 
    
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "user")
    private List<Like> likes;
    
    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Friend> friends;

Post:

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @Column(name = "posted_time")
    private LocalDateTime postedTime;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @ManyToOne
    private User user;

Comment:

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

Like:

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

Friend:
    
    UserFriend { // embeddable
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;
    
        @ManyToOne
        @JoinColumn(name = "friend_id")
        private User friend;
    }

User one to many -> Post
User one to many -> Like
User one to many -> Comment
User many to many -> Friend

Post one to many -> Like
Post one to many -> Comment
Post many to one -> User

Comment many to one -> Post
Comment many to one -> User

Like many to one -> User
Like many to one -> Post

Friend.UserFriend many to one -> User
Friend.UserFriend many to one -> User (friend)