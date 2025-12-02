const API = "https://gradgoals-i74s.onrender.com/goals"; 

const content = document.getElementById("content");
content.innerHTML = `
    <div class="page-header">
        <h1>Welcome to GradGoals!</h1>
        <p>Create a savings goal and track your progress!</p>
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
        <div id="goalList"></div>
    </div>
`;

// --------------------------
// Map percent to dynamic color
// --------------------------
function getProgressColor(percent) {
    // Red 0° → Yellow 60° → Green 120°
    // Smooth gradient by interpolating hue continuously
    let hue;
    if (percent <= 50) {
        // Red to Yellow
        hue = (percent / 50) * 60;
    } else {
        // Yellow to Green
        hue = 60 + ((percent - 50) / 50) * 60;
    }
    return `hsl(${hue}, 100%, 50%)`;
}

async function loadGoals() {
    const res = await fetch(`${API}/all`);
    const goals = await res.json();
    const list = document.getElementById("goalList");
    list.innerHTML = "";

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

        const addBtn = goalDiv.querySelector(".add-btn");
        const addInput = goalDiv.querySelector(".add-input");
        addBtn.addEventListener("click", async () => {
            const amount = parseFloat(addInput.value);
            if (!isNaN(amount) && amount > 0) {
                await fetch(`${API}/add`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ id: goal.id, amount })
                });
                loadGoals();
            }
        });

        const delBtn = goalDiv.querySelector(".del-btn");
        delBtn.addEventListener("click", async () => {
            await fetch(`${API}/delete/${goal.id}`, { method: "DELETE" });
            loadGoals();
        });
    });
}

document.getElementById("createGoalBtn").onclick = async () => {
    const name = document.getElementById("goalName").value.trim();
    const target = parseFloat(document.getElementById("goalTarget").value);
    if (!name || isNaN(target) || target <= 0) {
        alert("Please fill all fields correctly.");
        return;
    }
    await fetch(`${API}/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, targetAmount: target })
    });
    document.getElementById("goalName").value = "";
    document.getElementById("goalTarget").value = "";
    loadGoals();
};

loadGoals();
