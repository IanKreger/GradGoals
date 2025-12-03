// Check if the current page path contains "profile"
if (currentPage.toLowerCase().includes("profile")) {
    console.log("Profile page loaded.");

    const contentDiv = document.getElementById('content');
    
    // Safety check: Stop if content div is missing (prevents crashes)
    if (!contentDiv) {
        console.error("Error: No element with id 'content' found on Profile page.");
    } else {

        // Your Render Backend URL
        const BACKEND_URL = "https://gradgoals-i74s.onrender.com"; 

        // --- FUNCTION 1: RENDER LOGIN FORM ---
        function renderLoginForm(message = "") {
            contentDiv.innerHTML = `
                <div class="login-container" style="max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                    <h2 style="text-align: center; color: #333;">Login</h2>
                    
                    ${message ? `<div style="color: green; text-align: center; margin-bottom: 15px; font-weight: bold;">${message}</div>` : ''}

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
                        
                        <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">
                        
                        <div style="text-align: center;">
                            <p style="margin-bottom: 5px;">Don't have an account?</p>
                            <button id="showCreateAccountBtn" type="button" style="background: none; border: none; color: #007bff; text-decoration: underline; cursor: pointer; font-size: 14px;">Create New Account</button>
                        </div>
                    </form>
                </div>
            `;

            document.getElementById('loginForm').addEventListener('submit', handleLogin);
            document.getElementById('showCreateAccountBtn').addEventListener('click', renderCreateAccountForm);
        }

        // --- FUNCTION 2: RENDER CREATE ACCOUNT FORM ---
        function renderCreateAccountForm() {
            contentDiv.innerHTML = `
                <div class="login-container" style="max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                    <h2 style="text-align: center; color: #333;">Create Account</h2>
                    <form id="createAccountForm">
                        <div style="margin-bottom: 15px;">
                            <label for="newUsername" style="display: block; font-weight: bold; margin-bottom: 5px;">Choose Username:</label>
                            <input type="text" id="newUsername" name="username" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                        </div>
                        
                        <div style="margin-bottom: 20px;">
                            <label for="newPassword" style="display: block; font-weight: bold; margin-bottom: 5px;">Choose Password:</label>
                            <input type="password" id="newPassword" name="password" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                        </div>
                        
                        <button type="submit" style="width: 100%; padding: 12px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;">Create Account</button>
                        
                        <div id="createMessage" style="margin-top: 15px; text-align: center; min-height: 20px;"></div>
                        
                        <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">
                        
                        <div style="text-align: center;">
                            <button id="showLoginBtn" type="button" style="background: none; border: none; color: #007bff; text-decoration: underline; cursor: pointer; font-size: 14px;">Back to Login</button>
                        </div>
                    </form>
                </div>
            `;

            document.getElementById('createAccountForm').addEventListener('submit', handleCreateAccount);
            document.getElementById('showLoginBtn').addEventListener('click', () => renderLoginForm());
        }

        // --- FUNCTION 3: RENDER USER PROFILE ---
        function renderUserProfile(username) {
            contentDiv.innerHTML = `
                <div style="max-width: 600px; margin: 50px auto; text-align: center; padding: 20px;">
                    <h1 style="color: #28a745;">Welcome back, ${username}!</h1>
                    <p style="font-size: 18px; margin-top: 20px;">You are currently logged in.</p>
                    
                    <div style="margin-top: 40px; display: flex; justify-content: center; gap: 20px;">
                        <button onclick="window.location.href='Budget.html'" style="padding: 15px 30px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer;">Go to Budget Tool</button>
                        <button id="logoutBtn" style="padding: 15px 30px; background-color: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;">Logout</button>
                    </div>
                </div>
            `;
            
            // LOGOUT LOGIC: Remove from localStorage
            document.getElementById('logoutBtn').addEventListener('click', () => {
                localStorage.removeItem('gradGoalsUser'); // <--- CLEARS SESSION
                location.reload(); 
            });
        }

        // --- FUNCTION 4: HANDLE LOGIN ---
        async function handleLogin(e) {
            e.preventDefault();
            const messageDiv = document.getElementById('loginMessage');
            messageDiv.textContent = "Verifying...";
            messageDiv.style.color = "#666";

            const formData = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch(`${BACKEND_URL}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                const result = await response.json();

                if (response.ok) {
                    // SAVE TO STORAGE ON SUCCESS
                    localStorage.setItem('gradGoalsUser', result.username); // <--- SAVES SESSION
                    renderUserProfile(result.username); 
                } else {
                    messageDiv.style.color = "red";
                    messageDiv.textContent = "Wrong username or password."; 
                }
            } catch (error) {
                console.error('Error:', error);
                messageDiv.style.color = "red";
                messageDiv.textContent = "System error.";
            }
        }

        // --- FUNCTION 5: HANDLE CREATE ACCOUNT ---
        async function handleCreateAccount(e) {
            e.preventDefault();
            const messageDiv = document.getElementById('createMessage');
            messageDiv.textContent = "Creating account...";
            messageDiv.style.color = "#666";

            const formData = {
                username: document.getElementById('newUsername').value,
                password: document.getElementById('newPassword').value
            };

            try {
                const response = await fetch(`${BACKEND_URL}/create-account`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });
                const result = await response.json();

                if (response.ok) {
                    renderLoginForm("Account created successfully! Please log in.");
                } else {
                    messageDiv.style.color = "red";
                    messageDiv.textContent = result.message || "Failed to create account."; 
                }
            } catch (error) {
                console.error('Error:', error);
                messageDiv.style.color = "red";
                messageDiv.textContent = "System error.";
            }
        }

        // --- STARTUP CHECK ---
        // Check if user is already saved in localStorage
        const savedUser = localStorage.getItem('gradGoalsUser'); // <--- CHECKS SESSION

        if (savedUser) {
            // If yes, skip login and show profile
            renderUserProfile(savedUser);
        } else {
            // If no, show login form
            renderLoginForm();
        }
    }
}