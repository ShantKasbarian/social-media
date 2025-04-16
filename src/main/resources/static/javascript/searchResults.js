let searchResultsPageNo = 0;
let searchResultsTotalPages;
let input;
let searchResults;

async function getSearchResults() {
    input = await localStorage.getItem('item-to-search');

    if(input === null || input === undefined) {
        alert('Error: the name of the resource you are searching for cannot be empty');
        return;
    }

    const response = await fetch(`http://localhost:8000/user/${input}/search?page=${searchResultsPageNo}&size=5`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
    });

    try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }
        
        const data = await response.json();
        searchResultsTotalPages = data.totalPages;

        searchResults = await data.content;
        let searchResultsContainer = document.getElementById('results');

        if(searchResults.length === 0) {
            let message = document.createElement('p');
            message.innerHTML = 'no results were found';

            searchResultsContainer.appendChild(message);
            return;
        }

        let html = '';

        searchResults.forEach(element => {
            let userId = element.id;

            html += 
            `<div class="card" style="width: 18rem;">
                <div class="card-body" style="display: flex; flex-direction: column;">
                    <button onclick="getUserPostsById('${userId}')" type="reset" style="border:transparent">username: ${element.username}</button>
                    <P>name: ${element.name}</p>
                    <p>lastname: ${element.lastname}</p>
                    <div style="display:flex; flex-direction: row; gap: 5px">
                        <div>
                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#block-modal">block</button>

                            <div class="modal fade" id="block-modal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">Block User</h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            Are you sure that you want to block this user?
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                                            <button type="button" class="btn btn-primary" onclick="blockUser('${userId}')">Yes</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                        <div>
                            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#add-friend-modal">add friend</button>

                            <div class="modal fade" id="add-friend-modal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">Add User As Friend</h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            Are you sure that you want to add this user as friend?
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                                            <button type="button" class="btn btn-primary" onclick="addFriend('${userId}')">Yes</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;
        });

        searchResultsContainer.innerHTML = html;

    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}

window.addEventListener('close', () => {
    localStorage.removeItem('item-to-search')
});

window.addEventListener('scroll', () => {
    if(window.scrollY + window.innerHeight >= document.documentElement.scrollHeight &&
        searchResultsPageNo++ <= searchResultsTotalPages && 
        (searchResults !== null || searchResults !== undefined) &&
        searchResults.length === 5
    ) {
        getSearchResults();
    }

    return;
});

getSearchResults();