// js/challenge.js

document.addEventListener("DOMContentLoaded", () => {

  // -----------------------------
  // Only run this script on Challenge.html
  // -----------------------------
  if (!window.currentPage || !currentPage.toLowerCase().includes("challenge")) {
    return; // If not on Challenge page, exit
  }

  console.log("Challenge page loaded");

  const API_BASE = "http://localhost:8080/api";

  // -----------------------------
  // Use the shared main container from main.js
  // -----------------------------
  const content = document.getElementById("content");

  content.innerHTML = `
    <section class="challenge-layout">
      <aside class="challenge-sidebar">
        <h1>GradGoals Challenges</h1>
        <p class="challenge-intro">
          Choose a topic to practice real-world money decisions for college & young professionals.
        </p>
        <div id="category-list"></div>
      </aside>

      <main class="challenge-main">
        <div id="topic-header" class="topic-header hidden">
          <h2 id="topic-title"></h2>
          <p id="topic-blurb"></p>
        </div>

        <div id="question-card" class="question-card hidden">
          <p id="question-text"></p>

          <form id="answer-form">
            <input
              type="text"
              id="answer-input"
              placeholder="Type your answer (numbers only, no $)"
              required
            />
            <button type="submit">Check answer</button>
          </form>

          <p id="feedback" class="feedback hidden"></p>
          <p id="explanation" class="explanation hidden"></p>

          <button id="next-question" class="next-btn hidden">Next question</button>
        </div>
      </main>
    </section>
  `;

  // -----------------------------
  // Element references
  // -----------------------------
  const categoryListEl = document.getElementById("category-list");
  const topicHeaderEl = document.getElementById("topic-header");
  const topicTitleEl = document.getElementById("topic-title");
  const topicBlurbEl = document.getElementById("topic-blurb");

  const questionCardEl = document.getElementById("question-card");
  const questionTextEl = document.getElementById("question-text");
  const answerForm = document.getElementById("answer-form");
  const answerInput = document.getElementById("answer-input");
  const feedbackEl = document.getElementById("feedback");
  const explanationEl = document.getElementById("explanation");
  const nextBtn = document.getElementById("next-question");

  // -----------------------------
  // State
  // -----------------------------
  let currentCategoryId = null;
  let currentQuestionId = null;

  // -----------------------------
  // Load Categories
  // -----------------------------
  async function loadCategories() {
    try {
      const res = await fetch(`${API_BASE}/categories`);
      const categories = await res.json();

      categoryListEl.innerHTML = "";

      categories.forEach(cat => {
        const btn = document.createElement("button");
        btn.classList.add("category-btn");
        btn.innerHTML = `
          <span class="category-name">${cat.name}</span>
          <span class="category-count">${cat.questionCount} questions</span>
        `;
        btn.addEventListener("click", () => selectCategory(cat));
        categoryListEl.appendChild(btn);
      });

    } catch (err) {
      console.error(err);
      categoryListEl.innerHTML = "<p>Failed to load categories.</p>";
    }
  }

  // -----------------------------
  // Select Category
  // -----------------------------
  function selectCategory(cat) {
    currentCategoryId = cat.id;

    topicTitleEl.textContent = cat.name;
    topicBlurbEl.textContent = cat.blurb;

    topicHeaderEl.classList.remove("hidden");

    feedbackEl.classList.add("hidden");
    explanationEl.classList.add("hidden");
    nextBtn.classList.add("hidden");

    loadQuestionForCurrentCategory();
  }

  // -----------------------------
  // Load Question
  // -----------------------------
  async function loadQuestionForCurrentCategory() {
    if (!currentCategoryId) return;

    try {
      const res = await fetch(
        `${API_BASE}/challenge?category=${encodeURIComponent(currentCategoryId)}`
      );

      if (!res.ok) throw new Error("Failed to load question");

      const q = await res.json();

      currentQuestionId = q.id;
      questionTextEl.textContent = q.prompt;

      questionCardEl.classList.remove("hidden");
      feedbackEl.classList.add("hidden");
      explanationEl.classList.add("hidden");
      nextBtn.classList.add("hidden");

      answerInput.value = "";
      answerInput.focus();

    } catch (err) {
      console.error(err);
      questionTextEl.textContent = "Could not load a question for this topic.";
      questionCardEl.classList.remove("hidden");
    }
  }

  // -----------------------------
  // Check Answer
  // -----------------------------
  answerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!currentQuestionId) return;

    const answer = answerInput.value.trim();
    if (!answer) return;

    feedbackEl.classList.remove("hidden");
    feedbackEl.textContent = "Checking...";

    explanationEl.classList.add("hidden");
    nextBtn.classList.add("hidden");

    try {
      const res = await fetch(`${API_BASE}/challenge/check`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          questionId: currentQuestionId,
          answer: answer
        })
      });

      const data = await res.json();

      feedbackEl.textContent = data.message;
      feedbackEl.classList.toggle("correct", data.correct);
      feedbackEl.classList.toggle("incorrect", !data.correct);

      explanationEl.textContent = data.explanation;
      explanationEl.classList.remove("hidden");

      nextBtn.classList.remove("hidden");

    } catch (err) {
      console.error(err);
      feedbackEl.textContent = "Something went wrong. Please try again.";
      feedbackEl.classList.remove("hidden");
    }
  });

  // -----------------------------
  // Next Question
  // -----------------------------
  nextBtn.addEventListener("click", () => {
    loadQuestionForCurrentCategory();
  });

  // -----------------------------
  // Initial Load
  // -----------------------------
  loadCategories();
});

