// Check if the current page path contains "profile" (so this script only runs on the profile page)
if (currentPage.toLowerCase().includes("profile")) {
    console.log("Profile page loaded.");

    // Get the main container where content will be injected
    const contentDiv = document.getElementById('content');
    
    // Safety check: ensure the container exists before trying to modify it
    if (!contentDiv) {
        console.error("Error: No element with id 'content' found on Profile page.");
    } else {

        // Your Render Backend URL used for all API requests
        const BACKEND_URL = "https://gradgoals-i74s.onrender.com"; 

        // --- FUNCTION 1: RENDER LOGIN FORM ---
        // Dynamically inserts the Login HTML form into the page.
        // Accepts an optional 'message' parameter to show success messages (e.g., after logout).
        function renderLoginForm(message = "") {
            contentDiv.innerHTML = `
                <div class="login-container">
                    <h2>Login</h2>
                    
                    ${message ? `<div class="message-box message-success">${message}</div>` : ''}

                    <form id="loginForm">
                        <div class="form-group">
                            <label for="username">Username</label>
                            <input type="text" id="username" name="username" required placeholder="Enter your username">
                        </div>
                        
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" required placeholder="Enter your password">
                        </div>
                        
                        <button type="submit" class="btn-submit">Sign In</button>
                        
                        <div id="loginMessage" class="message-box"></div>
                        
                        <hr>
                        
                        <div class="center-text">
                            <p style="margin-bottom: 5px; color: #666;">Don't have an account?</p>
                            <button id="showCreateAccountBtn" type="button" class="toggle-link">Create New Account</button>
                        </div>
                    </form>
                </div>
            `;

            // Attach event listeners for form submission and switching views
            document.getElementById('loginForm').addEventListener('submit', handleLogin);
            document.getElementById('showCreateAccountBtn').addEventListener('click', renderCreateAccountForm);
        }

        // --- FUNCTION 2: RENDER CREATE ACCOUNT FORM ---
        // Dynamically inserts the Registration HTML form into the page.
        function renderCreateAccountForm() {
            contentDiv.innerHTML = `
                <div class="login-container">
                    <h2>Create Account</h2>
                    <form id="createAccountForm">
                        <div class="form-group">
                            <label for="newUsername">Choose Username</label>
                            <input type="text" id="newUsername" name="username" required placeholder="Pick a username">
                        </div>
                        
                        <div class="form-group">
                            <label for="newPassword">Choose Password</label>
                            <input type="password" id="newPassword" name="password" required placeholder="Pick a secure password">
                        </div>
                        
                        <button type="submit" class="btn-submit">Create Account</button>
                        
                        <div id="createMessage" class="message-box"></div>
                        
                        <hr>
                        
                        <div class="center-text">
                            <button id="showLoginBtn" type="button" class="toggle-link">Back to Login</button>
                        </div>
                    </form>
                </div>
            `;

            // Attach event listeners
            document.getElementById('createAccountForm').addEventListener('submit', handleCreateAccount);
            document.getElementById('showLoginBtn').addEventListener('click', () => renderLoginForm());
        }

        // --- FUNCTION 3: RENDER USER PROFILE ---
        // Displays the user's dashboard when logged in.
        function renderUserProfile(username) {
            contentDiv.innerHTML = `
                <div style="max-width: 900px; margin: 50px auto; text-align: center; padding: 20px;">
                    <h1 style="color: #0b6623;">Welcome back, ${username}!</h1>
                    <p style="font-size: 1.1rem; margin-top: 10px; color: #555;">You are currently logged in.</p>
                    
                    <div style="margin-top: 40px; display: flex; justify-content: center; gap: 15px; flex-wrap: wrap;">
                        
                        <button onclick="window.location.href='index.html'" 
                                class="btn-submit" 
                                style="width: auto; padding: 12px 25px; margin: 0; background-color: #342f8f !important;">
                            Go to Savings Goals
                        </button>

                        <button onclick="window.location.href='Resources.html'" 
                                class="btn-submit" 
                                style="width: auto; padding: 12px 25px; margin: 0; background-color: #342f8f !important;">
                            Go to Resources
                        </button>

                        <button onclick="window.location.href='budget.html'" 
                                class="btn-submit" 
                                style="width: auto; padding: 12px 25px; margin: 0; background-color: #342f8f !important;">
                            Go to Budget Tool
                        </button>

                        <button onclick="window.location.href='Challenge.html'" 
                                class="btn-submit" 
                                style="width: auto; padding: 12px 25px; margin: 0; background-color: #342f8f !important;">
                            Go to Challenges
                        </button>
                    </div>

                    <div style="margin-top: 20px; display: flex; justify-content: center;">
                        <button id="logoutBtn" 
                                class="btn-submit" 
                                style="width: auto; padding: 12px 30px; margin: 0; background-color: #6c757d;">
                            Logout
                        </button>
                    </div>
                </div>
            `;
            
            // LOGOUT LOGIC: Clears local storage and reloads page to show login form
            document.getElementById('logoutBtn').addEventListener('click', () => {
                localStorage.removeItem('gradGoalsUser'); 
                location.reload(); 
            });
        }

        // --- FUNCTION 4: HANDLE LOGIN ---
        // Async function to process login submissions.
        async function handleLogin(e) {
            e.preventDefault(); // Prevent standard form submission refresh
            const messageDiv = document.getElementById('loginMessage');
            messageDiv.textContent = "Verifying...";
            messageDiv.className = "message-box"; 

            const formData = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };

            try {
                // Send login credentials to backend
                const response = await fetch(`${BACKEND_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                const result = await response.json();

                if (response.ok) {
                    // On success: Save username to Local Storage and render profile
                    localStorage.setItem('gradGoalsUser', result.username); 
                    renderUserProfile(result.username); 
                } else {
                    // On failure: Show error message
                    messageDiv.classList.add("message-error");
                    messageDiv.textContent = "Wrong username or password."; 
                }
            } catch (error) {
                console.error('Error:', error);
                messageDiv.classList.add("message-error");
                messageDiv.textContent = "System error. Is backend running?";
            }
        }

        // --- FUNCTION 5: HANDLE CREATE ACCOUNT ---
        // Async function to process new account creation.
        async function handleCreateAccount(e) {
            e.preventDefault();
            const messageDiv = document.getElementById('createMessage');
            messageDiv.textContent = "Creating account...";
            messageDiv.className = "message-box";

            const formData = {
                username: document.getElementById('newUsername').value,
                password: document.getElementById('newPassword').value
            };

            try {
                // Send new account data to backend
                const response = await fetch(`${BACKEND_URL}/create-account`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                const result = await response.json();

                if (response.ok) {
                    // On success: Auto-login by saving username and showing profile immediately
                    localStorage.setItem('gradGoalsUser', formData.username);
                    renderUserProfile(formData.username);
                } else {
                    // On failure: Show error message from backend
                    messageDiv.classList.add("message-error");
                    messageDiv.textContent = result.message || "Failed to create account."; 
                }
            } catch (error) {
                console.error('Error:', error);
                messageDiv.classList.add("message-error");
                messageDiv.textContent = "System error.";
            }
        }

        // --- STARTUP CHECK ---
        // When page loads, check if user is already logged in (saved in LocalStorage)
        const savedUser = localStorage.getItem('gradGoalsUser'); 

        if (savedUser) {
            renderUserProfile(savedUser); // Show dashboard if logged in
        } else {
            renderLoginForm(); // Show login form if not logged in
        }
    }
}