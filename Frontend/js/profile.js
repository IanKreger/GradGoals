// Only run this logic if we are on the Profile page
if (currentPage === "Profile.html") {
    console.log("Profile page loaded.");

    // We grab the 'content' div that main.js created
    const contentDiv = document.getElementById('content');

    // Your actual backend URL
    const BACKEND_URL = "https://gradgoals-i74s.onrender.com";

    // 1. Define the Login Form HTML
    // We inject this string into the content div so it sits correctly between Header and Footer
    const loginHtml = `
        <div class="login-container" style="max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: white;">
            <h2 style="text-align: center;">Login</h2>
            <form id="loginForm">
                <div style="margin-bottom: 15px;">
                    <label for="username" style="display: block; font-weight: bold;">Username:</label>
                    <input type="text" id="username" name="username" required style="width: 100%; padding: 8px; margin-top: 5px;">
                </div>
                
                <div style="margin-bottom: 15px;">
                    <label for="password" style="display: block; font-weight: bold;">Password:</label>
                    <input type="password" id="password" name="password" required style="width: 100%; padding: 8px; margin-top: 5px;">
                </div>
                
                <button type="submit" style="width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">Sign In</button>
                <div id="loginMessage" style="margin-top: 15px; text-align: center;"></div>
            </form>
        </div>
    `;

    // 2. Render the form
    contentDiv.innerHTML = loginHtml;

    // 3. Attach Event Listener to the new form
    const loginForm = document.getElementById('loginForm');
    const messageDiv = document.getElementById('loginMessage');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Stop page reload

        messageDiv.textContent = "Verifying...";
        messageDiv.style.color = "black";

        const formData = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        };

        try {
            // We append /login to your specific URL
            const response = await fetch(`${BACKEND_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            // Parse the JSON response from the backend
            const result = await response.json();

            if (response.ok) {
                // SUCCESS
                messageDiv.style.color = "green";
                messageDiv.textContent = "Success! " + result.message;
                
                // Optional: Log the success to console
                console.log("Logged in user:", result.username);

            } else {
                // FAIL (Wrong password or user not found)
                messageDiv.style.color = "red";
                messageDiv.textContent = result.message;
            }
        } catch (error) {
            console.error('Error:', error);
            messageDiv.style.color = "red";
            messageDiv.textContent = "Server connection failed.";
        }
    });
}