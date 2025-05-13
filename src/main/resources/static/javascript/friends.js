let friendsPageNo = 0;
let friendsTotalPages;
let isFriendsLoading = false;

let friendRequestsTotalPages;
let pendingFriendRequestsPageNo = 0;
let isFriendRequestsLoading = false;

async function getFriends() {
    const response = await fetch(`http://localhost:8080/friend?page=${friendsPageNo}&size=3`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`,
        }
    });

    try {
        if (!response.ok) {
            let text = await JSON.parse(response.text).message;
            throw new Error(text);
        }

        const data = await response.json();
        friendsTotalPages = data.totalPages;

        const users = data.content;        

        const list = document.getElementById('friends');
        
        let listItem = document.getElementById(`list-item-container-pageNo-${friendsPageNo}`);

        if(listItem !== null) {
            return;
        }
        
        const currentUserId = localStorage.getItem('userId');

        users.forEach(item => {
            listItem = document.createElement('li');
            listItem.classList.add('dropdown-item');
            listItem.id = `list-item-container-pageNo-${friendsPageNo}`;

            let userId = item.userId;

            if(userId === currentUserId) {
                userId = item.friendId;
            }

            const link = document.createElement('button');
            link.classList.add('dropdown-item');
            link.id = `link-user-${userId}`;
            link.dataset.userId = userId;
            link.type = 'reset';

            link.addEventListener('click', () => {
                localStorage.setItem('userId-posts', userId);
                window.location.href = 'http://localhost:8080/userProfile.html';
            });
            
            let currentUserUsername = localStorage.getItem('username');
            let username = item.username;
            
            if(username === currentUserUsername) {
                username = item.friendName;
            }

            link.innerHTML = username;

            listItem.appendChild(link);
            list.appendChild(listItem);
        });

        list.addEventListener('scroll', function (event) {
            var objectToObserve = document.getElementById('object-to-observe-friends');

            if (objectToObserve === null) {
                objectToObserve = document.createElement('div');
                objectToObserve.id = 'object-to-observe-friends';
                list.appendChild(objectToObserve);
                return;
            }
            
            checkIsFriendsAtBottom(list);
        });
    }
    catch (error) {
        alert(`Error: ${error.message}`);
    }
};

var checkIsFriendsAtBottom = async function (maindiv) {
    const isAtBottom = maindiv.scrollTop + maindiv.clientHeight >= maindiv.scrollHeight;
    if (isAtBottom && !isFriendsLoading) {
        isFriendsLoading = true;
        ++friendsPageNo;

        if (friendsPageNo > (friendsTotalPages - 1)) {
            return;
        }

        await getFriends();
        isFriendsLoading = false;
    }
};

async function addFriend(userId) {
    const response = await fetch(`http://localhost:8080/friend/${userId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

    try {
        if(response.status !== 201) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }
        
        const toast = document.getElementById('addFriendToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();

        return;

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

async function blockUser(userId) {
    const response = await fetch(`http://localhost:8080/user/${userId}/block`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

    try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }
        
        const toast = document.getElementById('blockUserToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();

        return;

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

async function unblockUser(userId) {
    const response = await fetch(`http://localhost:8080/user/${userId}/unblock`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

    try {
        let text = JSON.parse(await response.text()).message;

        if(!response.ok) {
            throw new Error(text);
        }

        let toastBody = document.getElementById('unblock-toast-body');
        toastBody.innerHTML = text;

        const toast = document.getElementById('unblockUserToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();


        return;

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

async function getPendingFriendRequests() {
    const response = await fetch(`http://localhost:8080/friend/pending?page=${pendingFriendRequestsPageNo}&size=3`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    }); 

    try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const data = await response.json();
        friendRequestsTotalPages = data.totalPages;

        const users = data.content;        
        
        let listItem = document.getElementById(`pending-friend-requests-item-pageNo-${pendingFriendRequestsPageNo}`);

        if(listItem !== null) {
            return;
        }

        let pendingFriendRequestsContainer = document.getElementById('pending-friend-requests-container');
        
        users.forEach(item => {
            let friendRequestContainer = document.createElement('div');

            listItem = document.createElement('li');
            listItem.classList.add('dropdown-item');
            listItem.id = `pending-friend-requests-item-container-pageNo-${pendingFriendRequestsPageNo}`;
            listItem.style = 'display: flex; flex-direction: row; gap: 5px;';


            let userId = item.userId;
            let username = item.username;

            const getUserProfileButton = document.createElement('button');
            getUserProfileButton.classList.add('dropdown-item');
            getUserProfileButton.id = `pending-friend-requests-item-pageNo-${pendingFriendRequestsPageNo}`;
            getUserProfileButton.dataset.userId = userId;
            getUserProfileButton.type = 'reset';

            getUserProfileButton.addEventListener('click', () => {
                localStorage.setItem('userId-posts', userId);
                window.location.href = 'http://localhost:8080/userProfile.html';
            });

            getUserProfileButton.innerHTML = username;

            let declineFriendRequestButton = document.createElement('button');
            declineFriendRequestButton.type = 'submit';
            declineFriendRequestButton.innerHTML = 'Decline';
            declineFriendRequestButton.className = 'btn btn-danger';
            declineFriendRequestButton.addEventListener('click', () => {
                declineFriendRequest(item.id);
            });

            let acceptFriendRequestButton = document.createElement('button');
            acceptFriendRequestButton.type = 'submit';
            acceptFriendRequestButton.className = 'btn btn-primary';
            acceptFriendRequestButton.innerHTML = 'Accept';
            acceptFriendRequestButton.addEventListener('click', () => {
                acceptFriendRequest(item.id);
            });

            listItem.appendChild(getUserProfileButton);
            listItem.appendChild(declineFriendRequestButton);
            listItem.appendChild(acceptFriendRequestButton); 

            friendRequestContainer.appendChild(listItem);
            pendingFriendRequestsContainer.appendChild(friendRequestContainer);
        });

        pendingFriendRequestsContainer.addEventListener('scroll', function (event) {
            var objectToObserve = document.getElementById('object-to-observe-pending-friend-requests');

            if (objectToObserve === null) {
                objectToObserve = document.createElement('div');
                objectToObserve.id = 'object-to-observe-pending-friend-requests';
                pendingFriendRequestsContainer.appendChild(objectToObserve);
                return;
            }
            
            checkIsPendingFriendRequestsAtBottom(pendingFriendRequestsContainer);
        });

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

var checkIsPendingFriendRequestsAtBottom = async function (maindiv) {
    const isAtBottom = maindiv.scrollTop + maindiv.clientHeight >= maindiv.scrollHeight;
    if (isAtBottom && !isFriendRequestsLoading) {
        isFriendRequestsLoading = true;
        ++pendingFriendRequestsPageNo;

        if (pendingFriendRequestsPageNo > (friendRequestsTotalPages - 1)) {
            return;
        }

        await getPendingFriendRequests();
        isFriendRequestsLoading = false;
    }
};

async function declineFriendRequest(requestId) {
    const response = await fetch(`http://localhost:8080/friend/request/${requestId}/decline`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

   try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const toast = document.getElementById('declineFriendRequestToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();
   } catch (error) {
        alert(`Error: ${error.message}`);
   } 
}

async function acceptFriendRequest(requestId) {
    const response = await fetch(`http://localhost:8080/friend/request/${requestId}/accept`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

   try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const toast = document.getElementById('acceptFriendRequestToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();

   } catch (error) {
        alert(`Error: ${error.message}`);
   } 
}