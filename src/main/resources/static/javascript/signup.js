document.addEventListener('submit', async function (event) {
    event.preventDefault();

    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (password !== confirmPassword) {
        alert('The fields password and confirm password should be equals!');
        return;
    }

    const passwordLowercaseRegex = /[a-z]/;
    const passwordUppercaseRegex = /[A-Z]/;
    const passwordNumberRegex = /[0-9]/
    const passwordSpecialCharactersRegex = /[!@#$%^&*()_+\-=<>?.,;:|'{}[\]~]/

    if (
        !passwordLowercaseRegex.test(password) ||
        !passwordUppercaseRegex.test(password) ||
        !passwordNumberRegex.test(password) ||
        !passwordSpecialCharactersRegex.test(password)
    ) {
        alert('password must contain at least 1 uppercase, 1 lowercase, 1 number and 1 special charcter');
        return;
    }

    const email = document.getElementById('email').value;
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!emailRegex.test(email)) {
        alert('email is invalid');
        return;
    }

    let username = document.getElementById('username').value.trim();

    if (username.length < 6) {
        alert('username must be at least 6 characters long');
        return;
    }

    if (username.includes(' ')) {
        alert('username cannot contain spaces');
        return;
    }

    const user = {
        name: document.getElementById('name').value,
        lastname: document.getElementById('lastname').value,
        username: username,
        email: email,
        password: password,
    };

    try {
        const response = await fetch('http://localhost:8080/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user)
        });

        if (response.status !== 201) {
            let text = JSON.parse(await response.text()).message;
            throw new Error(text);
        }

        window.location.href = 'http://localhost:8080/login.html';
    } catch (error) {
        alert(`Error: ${error.message}`);
    }
});