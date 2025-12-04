// If backend runs locally:
const API_BASE = 'https://gradgoals-i74s.onrender.com/api';

function computePercentFromStats(stats) {
  if (!stats) return 0;

  // CASE 1: backend already sends a percent
  if (typeof stats.percent === 'number') {
    return Math.max(0, Math.min(100, Math.round(stats.percent)));
  }

  // CASE 2: backend sends attempts + correct
  if (typeof stats.attempts === 'number' && stats.attempts > 0 &&
      typeof stats.correct === 'number') {

    // If correct > attempts, assume correct is already a percent
    if (stats.correct > stats.attempts) {
      return Math.max(0, Math.min(100, Math.round(stats.correct)));
    }

    return Math.max(
      0,
      Math.min(100, Math.round((stats.correct / stats.attempts) * 100))
    );
  }

  return 0;
}

// -----------------------------
// STATE
// -----------------------------
let categories = [];
let currentCategoryId = null;
let currentQuestion = null;
let progress = {}; // Now loaded from SERVER, not localStorage
let masteredQuestions = {}; 

// -----------------------------
// INIT (UPDATED FOR LOGIN CHECK)
// -----------------------------
document.addEventListener('DOMContentLoaded', () => {
  checkAuthAndInit();
});

function checkAuthAndInit() {
  // 1. Get the username string directly (e.g., "itest")
  const userId = localStorage.getItem('gradGoalsUser');
  
  const warningEl = document.getElementById('login-warning');
  const appRoot = document.getElementById('challenge-app');

  // Simple check: is there a string in localStorage?
  if (userId && userId.trim() !== "") {
      // --- LOGGED IN ---
      console.log("User authorized:", userId);
      if (warningEl) warningEl.style.display = 'none';
      if (appRoot) {
          appRoot.style.display = 'block';
          // Initialize the actual app logic, passing the USER ID
          initChallengeApp(appRoot, userId);
      }
  } else {
      // --- NOT LOGGED IN ---
      console.log("User not logged in. Access denied.");
      if (warningEl) warningEl.style.display = 'block';
      if (appRoot) appRoot.style.display = 'none';
  }
}

// UPDATED: Now accepts userId
function initChallengeApp(root, userId) {
  // If root wasn't passed or found (shouldn't happen with new HTML), create it
  if (!root) {
    root = document.createElement('div');
    root.id = 'challenge-app';
    document.body.appendChild(root);
  }

  // Overall page container styling
  root.innerHTML = '';
  root.style.maxWidth = '1000px';
  root.style.margin = '2rem auto';
  root.style.padding = '1rem 1.5rem';
  root.style.boxSizing = 'border-box';
  root.style.fontFamily = 'system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif';
  root.style.color = '#000';

  // -----------------------------
  // HEADER
  // -----------------------------
  const title = document.createElement('h1');
  title.textContent = 'GradGoals Challenges';
  title.style.margin = '0 0 0.25rem 0';
  title.style.fontSize = '2rem';
  title.style.fontWeight = '700';
  title.style.color = '#000';

  const subtitle = document.createElement('p');
  subtitle.textContent = 'Pick a module, answer a quick question, and track your progress.';
  subtitle.style.margin = '0 0 1.5rem 0';
  subtitle.style.color = '#444';

  root.appendChild(title);
  root.appendChild(subtitle);

    // -----------------------------
  // OVERALL SUMMARY: CIRCLE + BADGES
  // -----------------------------
  const summarySection = document.createElement('section');
  summarySection.style.marginTop = '1.25rem';
  summarySection.style.display = 'flex';
  summarySection.style.flexWrap = 'wrap';
  summarySection.style.gap = '1.5rem';
  summarySection.style.alignItems = 'center';

  // Left side: circular overall progress
  const overallBox = document.createElement('div');
  overallBox.id = 'overall-progress-box';
  overallBox.style.display = 'flex';
  overallBox.style.flexDirection = 'column';
  overallBox.style.alignItems = 'center';
  overallBox.style.justifyContent = 'center';
  overallBox.style.minWidth = '130px';

  // Right side: badges earned / left
  const badgesContainer = document.createElement('div');
  badgesContainer.id = 'challenge-badges';
  badgesContainer.style.display = 'flex';
  badgesContainer.style.flexDirection = 'column';
  badgesContainer.style.gap = '0.4rem';

  const badgesTitle = document.createElement('div');
  badgesTitle.textContent = 'Badges';
  badgesTitle.style.fontWeight = '600';
  badgesTitle.style.fontSize = '0.95rem';

  const badgesEarnedTitle = document.createElement('div');
  badgesEarnedTitle.textContent = 'Badges earned';
  badgesEarnedTitle.style.fontSize = '0.85rem';
  badgesEarnedTitle.style.color = '#333';

  const badgesEarnedRow = document.createElement('div');
  badgesEarnedRow.id = 'badges-earned';
  badgesEarnedRow.style.display = 'flex';
  badgesEarnedRow.style.flexWrap = 'wrap';
  badgesEarnedRow.style.gap = '0.35rem';

  const badgesLockedTitle = document.createElement('div');
  badgesLockedTitle.textContent = 'Badges left to earn';
  badgesLockedTitle.style.fontSize = '0.85rem';
  badgesLockedTitle.style.color = '#555';
  badgesLockedTitle.style.marginTop = '0.3rem';

  const badgesLockedRow = document.createElement('div');
  badgesLockedRow.id = 'badges-locked';
  badgesLockedRow.style.display = 'flex';
  badgesLockedRow.style.flexWrap = 'wrap';
  badgesLockedRow.style.gap = '0.35rem';

  badgesContainer.appendChild(badgesTitle);
  badgesContainer.appendChild(badgesEarnedTitle);
  badgesContainer.appendChild(badgesEarnedRow);
  badgesContainer.appendChild(badgesLockedTitle);
  badgesContainer.appendChild(badgesLockedRow);

  summarySection.appendChild(overallBox);
  summarySection.appendChild(badgesContainer);
  root.appendChild(summarySection);
// -----------------------------
// PROGRESS BY TOPIC SECTION
// -----------------------------
const globalSection = document.createElement('section');
globalSection.style.marginTop = '1.5rem';
globalSection.style.padding = '1rem 1rem 1.25rem 1rem';
globalSection.style.borderRadius = '0.75rem';
globalSection.style.border = '1px solid #e2e2e2';
globalSection.style.background = '#fafafa';
globalSection.style.boxShadow = '0 2px 6px rgba(0,0,0,0.03)';

// Title
const globalHeader = document.createElement('h2');
globalHeader.textContent = 'Progress by Topic';
globalHeader.style.margin = '0 0 0.75rem 0';
globalHeader.style.fontSize = '1.25rem';
globalHeader.style.color = '#000';

// Only category progress bars (no overallBox)
const globalProgressList = document.createElement('div');
globalProgressList.id = 'global-progress-list';
globalProgressList.style.display = 'flex';
globalProgressList.style.flexDirection = 'column';
globalProgressList.style.gap = '0.4rem';

globalSection.appendChild(globalHeader);
globalSection.appendChild(globalProgressList);

root.appendChild(globalSection);

  // -----------------------------
  // MAIN CONTENT LAYOUT
  // -----------------------------
  const content = document.createElement('div');
  content.style.display = 'flex';
  content.style.gap = '2rem';
  content.style.alignItems = 'flex-start';
  content.style.marginTop = '2rem';
  content.style.flexWrap = 'wrap';

  // ---- Topics / modules section ----
  const topicsSection = document.createElement('section');
  topicsSection.style.flex = '1 1 40%';

  const topicsHeader = document.createElement('h2');
  topicsHeader.textContent = 'Topics';
  topicsHeader.style.margin = '0 0 0.5rem 0';
  topicsHeader.style.fontSize = '1.25rem';

  const grid = document.createElement('div');
  grid.id = 'challenge-category-grid';
  grid.style.display = 'grid';
  grid.style.gridTemplateColumns = 'repeat(auto-fit, minmax(220px, 1fr))';
  grid.style.gap = '0.8rem';
  grid.style.marginTop = '0.5rem';

  topicsSection.appendChild(topicsHeader);
  topicsSection.appendChild(grid);

  // ---- Main panel (selected module) ----
  const mainPanel = document.createElement('section');
  mainPanel.id = 'challenge-main-panel';
  mainPanel.style.flex = '1 1 55%';
  mainPanel.style.minWidth = '280px';

  const categoryInfo = document.createElement('div');
  categoryInfo.id = 'challenge-category-info';

  const questionBox = document.createElement('div');
  questionBox.id = 'challenge-question-box';
  questionBox.style.marginTop = '1rem';

  const feedbackBox = document.createElement('div');
  feedbackBox.id = 'challenge-feedback';
  feedbackBox.style.marginTop = '1rem';

  const progressBox = document.createElement('div');
  progressBox.id = 'challenge-progress';
  progressBox.style.marginTop = '1rem';
  progressBox.style.fontSize = '0.95rem';

  mainPanel.appendChild(categoryInfo);
  mainPanel.appendChild(questionBox);
  mainPanel.appendChild(feedbackBox);
  mainPanel.appendChild(progressBox);

  content.appendChild(topicsSection);
  content.appendChild(mainPanel);

  root.appendChild(content);

  // -----------------------------
  // PAGE-LEVEL ACTIONS (Restart)
  // -----------------------------
  const pageActions = document.createElement('div');
  pageActions.style.marginTop = '2.5rem';
  pageActions.style.display = 'flex';
  pageActions.style.justifyContent = 'flex-start';

  const restartBtn = document.createElement('button');
  restartBtn.type = 'button';
  restartBtn.textContent = 'Restart All Progress';
  restartBtn.style.padding = '0.45rem 0.9rem';
  restartBtn.style.borderRadius = '999px';
  restartBtn.style.border = '1px solid #c62828';
  restartBtn.style.background = '#ffffff';
  restartBtn.style.color = '#c62828';
  restartBtn.style.cursor = 'pointer';
  restartBtn.style.fontSize = '0.9rem';
  restartBtn.style.fontWeight = '600';
  restartBtn.style.boxShadow = '0 1px 3px rgba(0,0,0,0.05)';
  restartBtn.addEventListener('mouseover', () => {
    restartBtn.style.background = '#ffecec';
  });
  restartBtn.addEventListener('mouseout', () => {
    restartBtn.style.background = '#ffffff';
  });

  restartBtn.addEventListener('click', () => {
    if (confirm('Are you sure you want to reset ALL challenge progress?')) {
      resetAllProgress(userId); // Pass userID
    }
  });

  pageActions.appendChild(restartBtn);
  root.appendChild(pageActions);

  // Load categories from backend
  fetchCategories(userId); // Pass userID
}

// -----------------------------
// PROGRESS - MOVED TO SERVER
// -----------------------------
// We deleted loadProgress() and saveProgress() because 
// we now fetch it from the server in fetchCategories()

async function resetAllProgress(userId) {
  try {
      await fetch(`${API_BASE}/progress?userId=${userId}`, { method: 'DELETE' });
      progress = {};
      currentCategoryId = null;
      currentQuestion = null;

      // Clear UI areas
      const infoEl = document.getElementById('challenge-category-info');
      const questionBox = document.getElementById('challenge-question-box');
      const feedbackBox = document.getElementById('challenge-feedback');
      const progressBox = document.getElementById('challenge-progress');

      if (infoEl) infoEl.innerHTML = '';
      if (questionBox) questionBox.innerHTML = '';
      if (feedbackBox) feedbackBox.innerHTML = '';
      if (progressBox) progressBox.innerHTML = '';

      renderGlobalProgress();
      renderCategories(userId);
  } catch(e) {
      console.error("Failed to reset progress", e);
  }
}

function getCategoryProgressSummary(categoryId) {
  const stats = progress[categoryId];
  if (!stats) return 'No attempts yet';

  // find the category so we know how many questions it has
  const cat = categories.find(c => c.id === categoryId);
  const total = cat?.questionCount || 1;  // fallback to 1 just in case

  const correct = stats.correct || 0;
  const percent = Math.round((correct / total) * 100);

  return `${correct}/${total} correct (${percent}%)`;
}


// -----------------------------
// API CALLS (UPDATED FOR USER ID)
// -----------------------------
async function fetchCategories(userId) {
  const gridEl = document.getElementById('challenge-category-grid');
  if (gridEl) {
    gridEl.textContent = 'Loading topics...';
  }

  try {
    const url = `${API_BASE}/categories`;
    console.log('Fetching categories from:', url);

    const res = await fetch(url);
    const bodyText = await res.text();

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}: ${bodyText}`);
    }

    categories = JSON.parse(bodyText);

    // NEW: Fetch user progress from server right after categories
    const resProg = await fetch(`${API_BASE}/progress?userId=${userId}`);
    progress = await resProg.json();

    renderCategories(userId); // Pass userId for click handlers
  } catch (err) {
    console.error('Failed to load categories:', err);
    if (gridEl) {
      gridEl.textContent = `Unable to load categories: ${err.message}`;
    }
  }
}

async function fetchRandomQuestion(categoryId, userId, retries = 0) {
  const questionBox = document.getElementById('challenge-question-box');
  const feedbackBox = document.getElementById('challenge-feedback');

  if (feedbackBox) feedbackBox.innerHTML = '';

  const cat = categories.find(c => c.id === categoryId);
  const total = cat?.questionCount || 3;  // assume 3 if not provided
  const masteredSet = masteredQuestions[categoryId];

  // ✅ If user has already mastered all questions in this category, stop here
  if (masteredSet && masteredSet.size >= total) {
    if (questionBox) {
      questionBox.innerHTML = `<p>🎉 You’ve mastered all ${total} questions in this topic!</p>`;
    }
    return;
  }

  if (questionBox) questionBox.textContent = 'Loading question...';

  try {
    const res = await fetch(
      `${API_BASE}/challenge?category=${encodeURIComponent(categoryId)}`
    );
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }
    const q = await res.json();

    // If we got a question we've already mastered AND we still have more to learn,
    // try again a few times to get a new one.
    if (
      masteredSet &&
      masteredSet.has(q.id) &&
      masteredSet.size < total &&
      retries < 5
    ) {
      return fetchRandomQuestion(categoryId, userId, retries + 1);
    }

    currentQuestion = q;
    renderQuestion(userId);
  } catch (err) {
    console.error(err);
    if (questionBox) {
      questionBox.textContent = 'Could not load a question. Try again or pick another topic.';
    }
  }
}

async function checkAnswer(answerText, userId) {
  if (!currentQuestion) return;

  const feedbackBox = document.getElementById('challenge-feedback');
  if (feedbackBox) {
    feedbackBox.innerHTML = '<p>Checking your answer...</p>';
  }

  try {
    const res = await fetch(`${API_BASE}/challenge/check?userId=${userId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        questionId: currentQuestion.id,
        answer: answerText
      })
    });

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }

    const data = await res.json();

    // ✅ If correct, mark this question as mastered for this category (in this session)
    if (data.correct) {
      const catId = data.categoryId || currentCategoryId;
      if (catId && currentQuestion && currentQuestion.id != null) {
        if (!masteredQuestions[catId]) {
          masteredQuestions[catId] = new Set();
        }
        masteredQuestions[catId].add(currentQuestion.id);
      }
    }
    
    // Re-fetch server-side progress to stay in sync
    const resProg = await fetch(`${API_BASE}/progress?userId=${userId}`);
    progress = await resProg.json();

    renderFeedback(data, userId);
    
    renderProgress();
    renderGlobalProgress();
    renderCategories(userId); 
  } catch (err) {
    console.error(err);
    if (feedbackBox) {
      feedbackBox.innerHTML =
        '<p style="color: red;">Error checking answer. Make sure the backend is running.</p>';
    }
  }
}


// -----------------------------
// RENDERING (MOSTLY UNCHANGED, JUST PASSING USERID)
// -----------------------------
function renderGlobalProgress() {
  const list = document.getElementById('global-progress-list');
  if (!list || categories.length === 0) return;

  list.innerHTML = '';

  let totalCorrect = 0;
  let totalQuestions = 0;

  categories.forEach(cat => {
    const stats = progress[cat.id] || { correct: 0 };

    const catTotal = cat.questionCount || 1;  // 2, 3, etc.
    const catCorrect = Math.min(catTotal, stats.correct || 0);

    totalQuestions += catTotal;
    totalCorrect += catCorrect;

    const percent = Math.round((catCorrect / catTotal) * 100);

    // Wrapper
    const row = document.createElement('div');
    row.style.display = 'flex';
    row.style.flexDirection = 'column';
    row.style.gap = '0.1rem';

    // Label
    const label = document.createElement('span');
    label.textContent = `${cat.name} — ${percent}%`;
    label.style.color = '#000';
    label.style.fontSize = '0.85rem';

    // Bar background
    const barBg = document.createElement('div');
    barBg.style.height = '6px';
    barBg.style.background = '#e2e2e2';
    barBg.style.borderRadius = '999px';
    barBg.style.overflow = 'hidden';

    // Bar fill
    const barFill = document.createElement('div');
    barFill.style.height = '100%';
    barFill.style.width = percent + '%';
    barFill.style.background = '#003366'; // dark blue
    barFill.style.borderRadius = '999px';
    barFill.style.transition = 'width 0.3s ease';

    barBg.appendChild(barFill);
    row.appendChild(label);
    row.appendChild(barBg);

    list.appendChild(row);
  });

  renderOverallProgressAndBadges(totalCorrect, totalQuestions);
}

function renderOverallProgressAndBadges(totalCorrect, totalQuestions) {
  const overallBox = document.getElementById('overall-progress-box');
  const earnedRow = document.getElementById('badges-earned');
  const lockedRow = document.getElementById('badges-locked');

  const percent = totalQuestions > 0
    ? Math.round((totalCorrect / totalQuestions) * 100)
    : 0;

  // ---------- Overall Progress CIRCLE ----------
  if (overallBox) {
    overallBox.innerHTML = '';

    const label = document.createElement('div');
    label.textContent = 'Overall challenge progress';
    label.style.fontSize = '0.9rem';
    label.style.marginBottom = '0.35rem';
    label.style.color = '#000';

    const circle = document.createElement('div');
    circle.style.width = '90px';
    circle.style.height = '90px';
    circle.style.borderRadius = '50%';
    circle.style.display = 'flex';
    circle.style.alignItems = 'center';
    circle.style.justifyContent = 'center';
    circle.style.fontWeight = '600';
    circle.style.fontSize = '0.9rem';
    circle.style.color = '#003366';
    circle.style.background = `conic-gradient(#003366 ${percent}%, #e2e2e2 0)`; // donut ring

    const inner = document.createElement('div');
    inner.textContent = `${percent}%`;
    inner.style.width = '64px';
    inner.style.height = '64px';
    inner.style.borderRadius = '50%';
    inner.style.background = '#ffffff';
    inner.style.display = 'flex';
    inner.style.alignItems = 'center';
    inner.style.justifyContent = 'center';
    inner.style.boxShadow = '0 0 4px rgba(0,0,0,0.1)';

    circle.appendChild(inner);

    overallBox.appendChild(label);
    overallBox.appendChild(circle);
  }

  // ---------- Badges (earned vs locked) ----------
  if (earnedRow && lockedRow) {
    earnedRow.innerHTML = '';
    lockedRow.innerHTML = '';

    const allBadges = [
      { threshold: 0,   label: 'Getting Started' },
      { threshold: 25,  label: 'Budget Beginner' },
      { threshold: 50,  label: 'Money Mover' },
      { threshold: 75,  label: 'Savings Star' },
      { threshold: 100, label: 'GradGoals Master' }
    ];

    allBadges.forEach(badge => {
      const isEarned = percent >= badge.threshold;

      const pill = document.createElement('div');
      pill.style.display = 'inline-flex';
      pill.style.alignItems = 'center';
      pill.style.gap = '0.25rem';
      pill.style.padding = '0.25rem 0.6rem';
      pill.style.borderRadius = '999px';
      pill.style.fontSize = '0.8rem';
      pill.style.border = '1px solid #003366';

      if (isEarned) {
        pill.style.background = '#e8f0fa';
        pill.style.color = '#003366';
      } else {
        pill.style.background = '#f5f5f5';
        pill.style.color = '#777';
        pill.style.opacity = 0.8;
      }

      // cute badge icons
      const icon = document.createElement('span');
      icon.textContent = isEarned ? '🏅' : '🔒';

      const text = document.createElement('span');
      text.textContent = badge.label;

      pill.appendChild(icon);
      pill.appendChild(text);

      if (isEarned) {
        earnedRow.appendChild(pill);
      } else {
        lockedRow.appendChild(pill);
      }
    });
  }
}


function renderCategories(userId) {
  const gridEl = document.getElementById('challenge-category-grid');
  if (!gridEl) return;

  gridEl.innerHTML = '';

  if (!categories || categories.length === 0) {
    gridEl.textContent = 'No categories found.';
    return;
  }

  categories.forEach((cat) => {
    const card = document.createElement('button');
    card.type = 'button';
    card.style.color = '#000';
    card.style.textAlign = 'left';
    card.style.padding = '0.75rem 0.85rem';
    card.style.borderRadius = '0.8rem';
     // 🔹 Change highlight color from green → dark blue
    card.style.border = cat.id === currentCategoryId
      ? '2px solid #003366'   // was #0b6623
      : '1px solid #ddd';
    card.style.background = cat.id === currentCategoryId
      ? '#e8f0fa'             // was #eaf6ec
      : '#ffffff';
    card.style.cursor = 'pointer';
    card.style.boxShadow = '0 1px 4px rgba(0,0,0,0.05)';
    card.style.display = 'flex';
    card.style.flexDirection = 'column';
    card.style.gap = '0.3rem';
    card.style.transition = 'transform 0.15s ease, box-shadow 0.15s ease';

    card.addEventListener('mouseover', () => {
      card.style.transform = 'translateY(-1px)';
      card.style.boxShadow = '0 3px 8px rgba(0,0,0,0.08)';
    });
    card.addEventListener('mouseout', () => {
      card.style.transform = 'none';
      card.style.boxShadow = '0 1px 4px rgba(0,0,0,0.05)';
    });

    const name = document.createElement('div');
    name.textContent = cat.name;
    name.style.fontWeight = '600';
    name.style.color = '#000';

    const blurb = document.createElement('div');
    blurb.textContent = cat.blurb;
    blurb.style.fontSize = '0.85rem';
    blurb.style.color = '#000';

    const meta = document.createElement('div');
    meta.style.fontSize = '0.8rem';
    meta.style.display = 'flex';
    meta.style.justifyContent = 'space-between';
    meta.style.marginTop = '0.35rem';

    const countSpan = document.createElement('span');
    countSpan.textContent = `${cat.questionCount || 0} questions`;
    countSpan.style.color = '#000';

    const miniProg = document.createElement('span');
    miniProg.textContent = getCategoryProgressSummary(cat.id);
    miniProg.style.color = '#000';

    meta.appendChild(countSpan);
    meta.appendChild(miniProg);

    card.appendChild(name);
    card.appendChild(blurb);
    card.appendChild(meta);

    card.addEventListener('click', () => {
      currentCategoryId = cat.id;
      renderCategories(userId);
      renderCategoryInfo(cat);
      fetchRandomQuestion(cat.id, userId);
    });

    gridEl.appendChild(card);
  });

  // Auto-select first category if nothing selected yet
  if (!currentCategoryId && categories.length > 0) {
    currentCategoryId = categories[0].id;
    renderCategories(userId); // to re-highlight
    renderCategoryInfo(categories[0]);
    fetchRandomQuestion(categories[0].id, userId);
  } else {
    renderProgress();
    renderGlobalProgress();
  }
}

function renderCategoryInfo(cat) {
  const infoEl = document.getElementById('challenge-category-info');
  if (!infoEl) return;

  infoEl.innerHTML = '';

  const title = document.createElement('h2');
  title.textContent = cat.name;
  title.style.margin = '0';
  title.style.fontSize = '1.3rem';
  title.style.color = '#000';

  const blurb = document.createElement('p');
  blurb.textContent = cat.blurb;
  blurb.style.margin = '0.35rem 0 0 0';
  blurb.style.color = '#444';

  infoEl.appendChild(title);
  infoEl.appendChild(blurb);

  renderProgress();
  renderGlobalProgress();
}

function renderQuestion(userId) {
  const questionBox = document.getElementById('challenge-question-box');
  const feedbackBox = document.getElementById('challenge-feedback');
  if (feedbackBox) feedbackBox.innerHTML = '';
  if (!questionBox) return;

  if (!currentQuestion) {
    questionBox.textContent = 'No question loaded yet.';
    return;
  }

  questionBox.innerHTML = '';

  const prompt = document.createElement('p');
  prompt.textContent = currentQuestion.prompt;
  prompt.style.margin = '0';

  const form = document.createElement('form');
  form.id = 'challenge-answer-form';
  form.style.marginTop = '0.75rem';

  const label = document.createElement('label');
  label.textContent = 'Your answer: ';
  label.setAttribute('for', 'challenge-answer-input');

  const input = document.createElement('input');
  input.type = 'text';
  input.id = 'challenge-answer-input';
  input.name = 'answer';
  input.style.marginRight = '0.5rem';
  input.style.padding = '0.35rem 0.5rem';
  input.style.borderRadius = '0.3rem';
  input.style.border = '1px solid #ccc';
  input.required = true;

  const submitBtn = document.createElement('button');
  submitBtn.type = 'submit';
  submitBtn.textContent = 'Check';
  submitBtn.style.padding = '0.4rem 0.8rem';
  submitBtn.style.background = '#003366';  // dark blue
  submitBtn.style.color = 'white';
  submitBtn.style.border = '1px solid #003366';
  submitBtn.style.borderRadius = '0.3rem';
  submitBtn.style.cursor = 'pointer';

  form.appendChild(label);
  form.appendChild(input);
  form.appendChild(submitBtn);

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const answerText = input.value.trim();
    if (!answerText) {
      input.focus();
      return;
    }
    checkAnswer(answerText, userId);
  });

  questionBox.appendChild(prompt);
  questionBox.appendChild(form);
}
function renderFeedback(response, userId) {
  const feedbackBox = document.getElementById('challenge-feedback');
  if (!feedbackBox) return;

  feedbackBox.innerHTML = '';

  const msg = document.createElement('p');
  msg.textContent = response.message || (response.correct ? 'Correct!' : 'Not quite.');
  msg.style.fontWeight = '600';
  msg.style.color = response.correct ? 'green' : 'red';
  msg.style.margin = '0 0 0.35rem 0';

  const expl = document.createElement('p');
  expl.textContent = response.explanation || '';
  expl.style.margin = '0';

  const actions = document.createElement('div');
  actions.style.marginTop = '0.75rem';
  actions.style.display = 'flex';
  actions.style.gap = '0.5rem';

  const nextBtn = document.createElement('button');
  nextBtn.type = 'button';
  nextBtn.textContent = 'Next Question';
  nextBtn.style.padding = '0.35rem 0.7rem';
  nextBtn.style.borderRadius = '0.3rem';
  nextBtn.style.border = '1px solid #003366';
  nextBtn.style.background = '#003366';
  nextBtn.style.color = 'white';
  nextBtn.style.cursor = 'pointer';
  nextBtn.addEventListener('click', () => {
    if (response.categoryId) {
      fetchRandomQuestion(response.categoryId, userId);
    } else if (currentCategoryId) {
      fetchRandomQuestion(currentCategoryId, userId);
    }
  });

  actions.appendChild(nextBtn);

  feedbackBox.appendChild(msg);
  feedbackBox.appendChild(expl);
  feedbackBox.appendChild(actions);
}

function renderProgress() {
  const progressBox = document.getElementById('challenge-progress');
  if (!progressBox) return;

  if (!currentCategoryId || !categories || categories.length === 0) {
    progressBox.innerHTML = '';
    return;
  }

  const stats = progress[currentCategoryId];
  const cat = categories.find((c) => c.id === currentCategoryId);

  if (!stats || !cat) {
    progressBox.innerHTML = `<em>No history yet for "${
      cat ? cat.name : currentCategoryId
    }". Your progress will show up here.</em>`;
    return;
  }

  const correct = stats.correct || 0;
  const total = cat.questionCount || 1;
  const percent = Math.round((correct / total) * 100);

  progressBox.innerHTML = `
    <strong>Progress for "${cat.name}"</strong><br>
    ${correct} correct out of ${total} questions (${percent}%).
  `;
}
