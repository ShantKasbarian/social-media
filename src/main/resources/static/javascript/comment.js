// let commentPageNo = 0;
// let totalCommentPages;
let postId;
// let isCommentLoading = false;

const commentMap = new Map();

async function getComments(id) {
    let commentPageNo = commentMap.get(id)?.commentPageNo;

    if(commentPageNo === null || commentPageNo === undefined) {
        commentPageNo = 0;
    }

    const response = await fetch(`http://localhost:8080/post/${id}/comments?page=${commentPageNo}&size=3`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        }
    });

    postId = id;

    try {
        if (!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const data = await response.json();
        let contents = await data.content;
        totalCommentPages = data.totalPages;


        if(commentMap.get(id) === null || commentMap.get(id) === undefined) {
            commentMap.set(id, {
                commentPageNo: 0,
                totalCommentPages: data.totalPages,
                isCommentLoading: false
            });
        }

        let comContainer = document.getElementById(`com-container_${id}`);

        let comContainerP = document.getElementById(`com-container_p_${id}_such_empty`);

        if (contents.length === 0 && comContainerP === null && !comContainer.hasChildNodes()) {
            let commentP = document.createElement('p');
            commentP.id = `com-container_p_${id}_such_empty`;
            commentP.innerHTML = 'wow such empty be the first to comment';

            comContainer.appendChild(commentP);
            return;
        }

        else if (contents.length === 0) {
            return;
        }

        let childContainer = document.getElementById(`com-container_div_${id}_child_pageNo_${commentPageNo}`);

        if (childContainer !== null) {
            return;
        }

        contents.forEach(element => {
            childContainer = document.createElement('div');
            childContainer.id = `com-container_div_${id}_child_pageNo_${commentPageNo}`;
            childContainer.classList.add('card', 'card-body');

            let usernameP = document.createElement('p');
            usernameP.innerHTML = element.username;

            let commentP = document.createElement('p');
            commentP.innerHTML = element.comment;

            let commentedTimeP = document.createElement('p');
            commentedTimeP.innerHTML = element.commentedTime;

            childContainer.appendChild(usernameP);
            childContainer.appendChild(commentP);
            childContainer.appendChild(commentedTimeP);


            if (comContainer.lastElementChild) {
                comContainer.insertBefore(childContainer, comContainer.lastElementChild);
            } else {
                comContainer.appendChild(childContainer);
            }

        });

        comContainer.addEventListener('scroll', function (event) {
            var objectToObserve = document.getElementById(`object-to-observe-${id}`);

            if (objectToObserve === null) {
                objectToObserve = document.createElement('div');
                objectToObserve.id = `object-to-observe-${id}`;
                comContainer.appendChild(objectToObserve);
                return;
            }
            
            checkIsAtBottom(comContainer);
        });

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

var checkIsAtBottom = async function (maindiv) {
    const isAtBottom = maindiv.scrollTop + maindiv.clientHeight >= maindiv.scrollHeight;
    const element = commentMap.get(postId);

    if(element === null || element === undefined) {
        return;
    }

    if (isAtBottom && !element.isCommentLoading) {
        isCommentLoading = true;
        let page = element.commentPageNo;
        commentMap.get(postId).commentPageNo = ++page;

        if (page > (element.totalCommentPages - 1)) {
            return;
        }

        await getComments(postId);
        isCommentLoading = false;
    }
};

async function postComment(id) {
    const comment = {
        'postId': id,
        'comment': document.getElementById(`comment-input-${id}`).value,
    };

    const response = await fetch('http://localhost:8080/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`,
        },
        body: JSON.stringify(comment)
    });

    try {
        if (response.status !== 201) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        let comContainerP = document.getElementById(`com-container_p_${id}_such_empty`);
        let comContainer = document.getElementById(`com-container_${id}`);

        if (comContainerP !== null) {
            comContainerP.remove();
        }

        let childContainer = document.createElement('div');
        childContainer.id = `com-container_div_${id}_child`;
        childContainer.classList.add('card', 'card-body');

        let usernameP = document.createElement('p');
        usernameP.innerHTML = await localStorage.getItem('username');

        let commentP = document.createElement('p');
        commentP.innerHTML = comment.comment;

        let commentedTimeP = document.createElement('p');
        commentedTimeP.innerHTML = 'now';

        childContainer.appendChild(usernameP);
        childContainer.appendChild(commentP);
        childContainer.appendChild(commentedTimeP);

        comContainer.appendChild(childContainer);

        const toast = document.getElementById('commentToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();


        return;

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}