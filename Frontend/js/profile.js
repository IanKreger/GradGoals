// Check if the current page path contains "profile"
if (currentPage.toLowerCase().includes("profile")) {
    

    const contentDiv = document.getElementById('content');
    const BACKEND_URL = "https://gradgoals-i74s.onrender.com";

    // --- FUNCTION 1: RENDER LOGIN FORM ---
    function renderLoginForm() {
        contentDiv.innerHTML = `
            <div class="login-container" style="max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                <h2 style="text-align: center; color: #333;">Login to GradGoals</h2>
                <form id="loginForm">
                    <div style="margin-bottom: 15px;">
                        <label for="username" style="display: block; font-weight: bold; margin-bottom: 5px;">Username:</label>
                        <input type="text" id="username" name="username" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                    </div>
                    
                    <div style="margin-bottom: 20px;">
                        <label for="password" style="display: block; font-weight: bold; margin-bottom: 5px;">Password:</label>
                        <input type="password" id="password" name="password" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                    </div>
                    
                    <button type="submit" style="width: 100%; padding: 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;">Sign In</button>
                    
                    <div id="loginMessage" style="margin-top: 15px; text-align: center; min-height: 20px;"></div>
                </form>
            </div>
        `;

        // Attach the event listener AFTER creating the HTML
        const loginForm = document.getElementById('loginForm');
        const messageDiv = document.getElementById('loginMessage');

        loginForm.addEventListener('submit', handleLogin);
    }

    // --- FUNCTION 2: RENDER USER PROFILE (Success State) ---
    function renderUserProfile(username) {
        contentDiv.innerHTML = `
            <div style="max-width: 600px; margin: 50px auto; text-align: center; padding: 20px;">
                <h1 style="color: #28a745;">Welcome back, ${username}!</h1>
                <p style="font-size: 18px; margin-top: 20px;">You are now successfully logged in.</p>
                
                <div style="margin-top: 40px; display: flex; justify-content: center; gap: 20px;">
                    <button onclick="alert('Budget Tool coming soon!')" style="padding: 15px 30px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer;">Go to Budget Tool</button>
                    <button id="logoutBtn" style="padding: 15px 30px; background-color: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;">Logout</button>
                </div>
            </div>
        `;

        // Add Logout Logic
        document.getElementById('logoutBtn').addEventListener('click', () => {
            // Reload the page to reset everything back to the login form
            location.reload(); 
        });
    }

    // --- FUNCTION 3: HANDLE LOGIN LOGIC ---
    async function handleLogin(e) {
        e.preventDefault();
        
        const messageDiv = document.getElementById('loginMessage');
        const usernameInput = document.getElementById('username').value;
        const passwordInput = document.getElementById('password').value;

        // Visual feedback that something is happening
        messageDiv.textContent = "Verifying credentials...";
        messageDiv.style.color = "#666";

        const formData = {
            username: usernameInput,
            password: passwordInput
        };

        try {
            const response = await fetch(`${BACKEND_URL}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            const result = await response.json();

            if (response.ok) {
                // --- SUCCESS: Show the profile view ---
                // We pass the username from the backend result to our new function
                renderUserProfile(result.username); 
            } else {
                // --- FAILURE: Stay on form, show error ---
                messageDiv.style.color = "red";
                // If backend sends a message use it, otherwise say "Wrong username or password"
                messageDiv.textContent = "Wrong username or password."; 
            }

        } catch (error) {
            console.error('Error:', error);
            messageDiv.style.color = "red";
            messageDiv.textContent = "System error. Please try again later.";
        }
    }

    // --- INITIALIZE ---
    // Start by showing the login form
    renderLoginForm();
}