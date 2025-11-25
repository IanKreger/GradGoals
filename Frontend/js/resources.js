// This file only runs on the Resources page
if (currentPage === "Resources.html") {

  // Insert challenge + rating UI into the main content area
  document.getElementById("content").innerHTML = `
    <h2>Budget Challenge</h2>
    <p>If you earn $500 in a week and save 20%, how much do you save?</p>

    <input id="challengeInput" placeholder="Enter your answer" />

    <button id="submitChallenge">Submit Answer</button>

    <p id="challengeResult" style="font-weight: bold; margin-top: 10px;"></p>

    <hr>

    <h3>Rate This Resource</h3>

    <label>Stars: 
      <input id="stars" type="number" min="1" max="5" />
    </label><br>

    <button id="submitRating">Submit Rating</button>

    <p>Average Rating: <span id="avg">0</span></p>
  `;

  // --------------------------
  // CHALLENGE LOGIC
  // --------------------------
  function submitChallenge() {
    const userAnswer = document.getElementById("challengeInput").value;

    fetch("http://localhost:8080/challenge", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ answer: userAnswer })
    })
      .then(res => res.json())
      .then(data => {
        const resultBox = document.getElementById("challengeResult");
        resultBox.textContent = data.message;
        resultBox.style.color = data.correct ? "green" : "red";
      });
  }

  // Listen for challenge button click
  document.getElementById("submitChallenge")
    .addEventListener("click", submitChallenge);


  // --------------------------
  // RATING LOGIC
  // --------------------------
  function submitRating() {
    const stars = document.getElementById("stars").value;

    const resourceId = "resources_page";  
    const userId = "guestUser"; // Replace later when login is built

    fetch("http://localhost:8080/ratings", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        resourceId,
        userId,
        stars: Number(stars),
        comment: "" // No comment box anymore
      })
    })
    .then(res => {
      alert("Rating saved!");
      loadAverage();
    });
  }

  function loadAverage() {
    fetch("http://localhost:8080/ratings/average?resourceId=resources_page")
      .then(res => res.json())
      .then(avg => {
        document.getElementById("avg").innerText = avg.toFixed(2);
      });
  }

  // Load the average rating when page loads
  loadAverage();

  // Listen for rating button click
  document.getElementById("submitRating")
    .addEventListener("click", submitRating);
}
