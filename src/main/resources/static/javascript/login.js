document.addEventListener('submit', async function (event) {
    event.preventDefault();

    const user = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    const response = await fetch('http://localhost:8000/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    try {
        if(response.status !== 200) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        const data = JSON.parse(await response.text());

        localStorage.setItem('token', data.token);
        localStorage.setItem('username', data.username);
        localStorage.setItem('userId', data.userId);

        window.location.href = 'http://localhost:8000/feed.html';
    }
    catch(error) {
        alert(`Error: ${error.message}`);
    }
});