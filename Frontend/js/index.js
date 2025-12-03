// Switch to Localhost for testing, then back to Render for deployment
// const API = "http://localhost:8080/goals"; 
const API = "https://gradgoals-i74s.onrender.com/goals";

const content = document.getElementById("content");

// ---------------------------------------------------------
// 1. AUTH CHECK
// ---------------------------------------------------------
// Get the username (e.g. "itest") directly from storage
const userId = localStorage.getItem('gradGoalsUser');

// ---------------------------------------------------------
// 2. LAYOUT SETUP
// ---------------------------------------------------------
// Wrap everything in a main container for left/right layout
const mainContainer = document.createElement("div");
mainContainer.id = "main-container";
mainContainer.style.display = "flex";
mainContainer.style.alignItems = "flex-start";
mainContainer.style.justifyContent = "flex-start";
mainContainer.style.gap = "50px"; // space between left and right

// Left section: Goals Block
const leftSection = document.createElement("div");
leftSection.id = "left-section";

// Right section: About GradGoals
const rightSection = document.createElement("div");
rightSection.id = "right-section";
rightSection.style.maxWidth = "400px"; 
rightSection.innerHTML = `
    <h2>About Grad Goals</h2>
    <p>
    We are an organization dedicated to making personal finance simple and approachable for young adults. 
    Through GradGoals, users can explore essential financial topics, work with an easy-to-use budgeting tool, create and track savings goals, and participate in engaging challenges that build financial confidence. 
    Our platform is designed to help you take control of your money, make smarter financial decisions, and develop healthy habits that will last a lifetime. 
    Sign up and start your journey today to achieve your financial goals with confidence.
    </p>
`;

// ---------------------------------------------------------
// 3. RENDER CONTENT BASED ON LOGIN STATUS
// ---------------------------------------------------------
if (userId && userId.trim() !== "") {
    // --- USER IS LOGGED IN ---
    console.log("Goals authorized for:", userId);

    leftSection.innerHTML = `
        <div class="page-header">
            <h1>Welcome, ${userId}!</h1>
            <p>Track your savings goals below.</p>
        </div>

        <div class="card">
            <h2>Create a New Savings Goal</h2>
            <div class="form-row">
                <input id="goalName" type="text" placeholder="Goal Name" />
                <input id="goalTarget" type="number" placeholder="Target Amount" />
                <button id="createGoalBtn" class="btn-primary">Create Goal</button>
            </div>
        </div>

        <div class="card">
            <h2>Your Current Savings Goals</h2>
            <div id="goalList">Loading goals...</div>
        </div>
    `;

    // Append sections
    mainContainer.appendChild(leftSection);
    mainContainer.appendChild(rightSection);
    content.appendChild(mainContainer);

    // Initialize Logic
    setupGoalLogic(userId);

} else {
    // --- USER IS NOT LOGGED IN ---
    console.log("User not logged in.");

    leftSection.innerHTML = `
        <div class="page-header">
            <h1>Welcome to GradGoals!</h1>
            <p>Please <a href="Profile.html">Log In</a> to create and track savings goals.</p>
        </div>
        <div class="card" style="opacity: 0.6;">
            <h2>Your Current Savings Goals</h2>
            <p><em>Log in to see your goals.</em></p>
        </div>
    `;

    mainContainer.appendChild(leftSection);
    mainContainer.appendChild(rightSection);
    content.appendChild(mainContainer);
}

// ---------------------------------------------------------
// 4. GOAL LOGIC (Only runs if logged in)
// ---------------------------------------------------------
function setupGoalLogic(currentUser) {

    // Map percent to dynamic color
    function getProgressColor(percent) {
        let hue;
        if (percent <= 50) {
            hue = (percent / 50) * 60;
        } else {
            hue = 60 + ((percent - 50) / 50) * 60;
        }
        return `hsl(${hue}, 100%, 50%)`;
    }

    async function loadGoals() {
        try {
            // UPDATED: Send userId in query param
            const res = await fetch(`${API}/all?userId=${currentUser}`);
            const goals = await res.json();
            
            const list = document.getElementById("goalList");
            list.innerHTML = "";

            if (goals.length === 0) {
                list.innerHTML = "<p>No goals yet. Create one above!</p>";
                return;
            }

            goals.forEach(goal => {
                const percent = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);

                const goalDiv = document.createElement("div");
                goalDiv.className = "goal-card";
                goalDiv.innerHTML = `
                    <strong>${goal.name}</strong>   $${goal.currentAmount.toFixed(2)} / $${goal.targetAmount.toFixed(2)}<br>
                    <div class="progress-bar">
                        <div style="width: ${percent}%; background-color: ${getProgressColor(percent)};"></div>
                    </div>
                    <input type="number" class="add-input" placeholder="Add Amount" />
                    <button class="add-btn btn-primary">Add</button>
                    <button class="del-btn btn-primary" style="margin-left:10px;">Delete</button>
                    <hr>
                `;
                list.appendChild(goalDiv);

                // Add Funds Button
                const addBtn = goalDiv.querySelector(".add-btn");
                const addInput = goalDiv.querySelector(".add-input");
                addBtn.addEventListener("click", async () => {
                    const amount = parseFloat(addInput.value);
                    if (!isNaN(amount) && amount > 0) {
                        // UPDATED: Send userId in body
                        await fetch(`${API}/add`, {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({ id: goal.id, amount, userId: currentUser })
                        });
                        loadGoals();
                    }
                });

                // Delete Button
                const delBtn = goalDiv.querySelector(".del-btn");
                delBtn.addEventListener("click", async () => {
                    // UPDATED: Send userId in query param
                    await fetch(`${API}/delete/${goal.id}?userId=${currentUser}`, { method: "DELETE" });
                    loadGoals();
                });
            });
        } catch (err) {
            console.error("Error loading goals:", err);
            document.getElementById("goalList").innerHTML = "<p>Error loading goals. Is the server running?</p>";
        }
    }

    // Create Goal Button
    document.getElementById("createGoalBtn").onclick = async () => {
        const name = document.getElementById("goalName").value.trim();
        const target = parseFloat(document.getElementById("goalTarget").value);
        if (!name || isNaN(target) || target <= 0) {
            alert("Please fill all fields correctly.");
            return;
        }
        // UPDATED: Send userId in body
        await fetch(`${API}/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, targetAmount: target, userId: currentUser })
        });
        document.getElementById("goalName").value = "";
        document.getElementById("goalTarget").value = "";
        loadGoals();
    };

    // Initial Load
    loadGoals();
}
