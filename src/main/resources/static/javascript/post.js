async function post() {
    const post = {
        title: document.getElementById('message-text').value
    };

    const response = await fetch('http://localhost:8000/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        },
        body: JSON.stringify(post),
    });

    try {
        if (response.status !== 201) {
            let text = JSON.parse(response.text).message;
            throw new Error(text);
        }

        const toast = document.getElementById('postToast');
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast);
        toastBootstrap.show();

        return;
    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}