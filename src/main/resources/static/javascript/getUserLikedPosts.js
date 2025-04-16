let likedPosts;
let totalLikedPostsPages = 0;
let currentLikedPostsPageNo = 0;

async function loadLikedPosts() {
    const response = await fetch(`http://localhost:8000/post/liked?page=${currentLikedPostsPageNo}&size=10`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });

    try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const data = await response.json();

        likedPosts = await data.content;

        totalLikedPostsPages = data.totalPages;

        const postsElement = document.getElementById('posts');
        
        likedPosts.forEach(element => {
            let parentDiv = document.createElement('div');
            parentDiv.classList.add('col-sm-6', 'mb-3', 'mb-sm-0');

            let cardContainer = document.createElement('div');
            cardContainer.classList.add('card');

            let cardBodyDiv = document.createElement('div');
            cardBodyDiv.classList.add('card-body');

            let usernameP = document.createElement('p');
            usernameP.innerHTML = element.username;

            let cardTextP = document.createElement('p');
            cardTextP.innerHTML = element.title;

            let postedTimeP = document.createElement('p');
            postedTimeP.innerHTML = element.postedTime;

            let iconDiv = document.createElement('div');
            iconDiv.classList.add('icon-container');
            iconDiv.style = 'display: flex; gap: 7px; margin-bottom: 10px; align-items: flex-start;';
            iconDiv.id = 'icon-container';

            let likeIcon = document.createElement('img');
            likeIcon.src = '../images/heart.png';
            likeIcon.style = 'cursor: pointer; flex-grow: none';

            let postId = element.id;

            likeIcon.addEventListener('click', () => likePost(postId));

            let likeCount = document.createElement('span');
            likeCount.innerHTML = element.likes;
            likeCount.id = `like-count-${postId}`;

            let commentIcon = document.createElement('img');
            commentIcon.src = '../images/comment.png';
            commentIcon.style = 'cursor: pointer;';

            commentIcon.addEventListener('click', () => getComments(postId));
            
            let commentInputBox = document.createElement('input');
            commentInputBox.id = `comment-input-${postId}`;
            commentInputBox.classList.add('form-control');
            commentInputBox.style = 'background-color: #fff; max-height: 30px; max-width: 250px';

            let commentButton = document.createElement('button');
            commentButton.classList.add('btn', 'btn-primary');
            commentButton.innerHTML = 'Comment';
            commentButton.style = 'cursor: pointer;';
            commentButton.id = `comment-button-${postId}`;
            commentButton.addEventListener('click', () => postComment(postId));

            cardBodyDiv.appendChild(usernameP);
            cardBodyDiv.appendChild(cardTextP);
            cardBodyDiv.appendChild(postedTimeP);

            iconDiv.appendChild(likeIcon);
            iconDiv.appendChild(likeCount);
            iconDiv.appendChild(commentIcon);
            iconDiv.appendChild(commentInputBox);
            iconDiv.appendChild(commentButton);

            commentIcon.setAttribute('data-bs-toggle', 'collapse');
            const targetId = `commentContainer_${element.id}`;
            commentIcon.setAttribute('data-bs-target', `#${targetId}`);
            commentIcon.setAttribute('aria-expanded', 'false');
            commentIcon.setAttribute('aria-controls', targetId);

            cardBodyDiv.appendChild(iconDiv);

            let childContainer = document.createElement('div');
            childContainer.id = targetId;
            childContainer.classList.add('collapse');

            let comContainer = document.createElement('div');
            comContainer.id = `com-container_${postId}`;
            console.log(`com-container_${postId}`);
            comContainer.style = 'display: flex;flex-direction: column; gap: 5px;';

            childContainer.appendChild(comContainer);
            cardBodyDiv.appendChild(childContainer);

            cardContainer.appendChild(cardBodyDiv);
            parentDiv.appendChild(cardContainer);
            postsElement.appendChild(parentDiv);
        });

        return;
    } 
    catch (error) {
        alert(`Error: ${error.message}`);
    }
}

window.addEventListener('scroll', () => {
    if(window.scrollY + window.innerHeight >= document.documentElement.scrollHeight &&
        currentLikedPostsPageNo++ <= totalLikedPostsPages
    ) {
        loadLikedPosts();
    }

    return;
});

loadLikedPosts();