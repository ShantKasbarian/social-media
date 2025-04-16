async function changeUsername() {
    const username = {
        username: await document.getElementById('username-input').value
    };

    const response = await fetch('http://localhost:8000/user/update/username', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        },
        body: JSON.stringify(username)
    });

    try {
        if(!response.ok) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
        window.location.href = 'http://localhost:8000/login.html';
        
    } catch (error) {
        alert(`Error: ${error.message}`);
    }
}