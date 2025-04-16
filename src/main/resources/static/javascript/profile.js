async function getCurrentUserProfile() {
    const userInfo = await fetch('http://localhost:8000/user/profile', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${await localStorage.getItem('token')}`
        }
    });

    try {
        if(!userInfo.ok) {
            let text = JSON.parse(await userInfo.text()).message;
            throw new Error(text);
        }

        const data = await userInfo.json();
        
        let name = document.getElementById('name');
        name.innerHTML = `name: ${data.name}`;

        let lastname = document.getElementById('lastname');
        lastname.innerHTML = `lastname: ${data.lastname}`;

        let email = document.getElementById('email');
        email.innerHTML = `email: ${data.email}`;

        let currentUsername = data.username;

        let username = document.getElementById('username');
        username.innerHTML = `username: ${currentUsername}`;

        let title = document.getElementById('title');
        title.innerHTML = `${currentUsername} | profile`

        return;

    } 
    catch (error) {
        alert(`Error: ${error.message}`);
    }
}

getCurrentUserProfile();