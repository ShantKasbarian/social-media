document.getElementById('logout-yes').addEventListener('click', function() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userId');
    window.location.href = 'http://localhost:8000/login.html';
});