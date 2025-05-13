async function likePost(id) {
    const response = await fetch(`http://localhost:8080/post/${id}/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });

    try {
        if(response.status !== 201) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        let likeCount = document.getElementById(`like-count-${id}`);
        likeCount.innerHTML = Number(likeCount.innerHTML) + 1;

        const toast = document.getElementById('likeToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();
    } 
    catch (error) {
        alert(`Error: ${error.message}`);
    }
}