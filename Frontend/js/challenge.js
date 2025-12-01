// challenge.js
// commit 
// If backend runs locally:
const API_BASE = 'http://localhost:8080/api';

// -----------------------------
// STATE
// -----------------------------
let categories = [];
let currentCategoryId = null;
let currentQuestion = null;
let progress = loadProgress(); // { [categoryId]: { attempts, correct } }

// -----------------------------
// INIT
// -----------------------------
document.addEventListener('DOMContentLoaded', () => {
  initChallengeApp();
});

function initChallengeApp() {
  let root = document.getElementById('challenge-app');
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
  // GLOBAL PROGRESS SECTION
  // -----------------------------
  const globalSection = document.createElement('section');
  globalSection.style.marginTop = '1.5rem';
  globalSection.style.padding = '1rem 1rem 1.25rem 1rem';
  globalSection.style.borderRadius = '0.75rem';
  globalSection.style.border = '1px solid #e2e2e2';
  globalSection.style.background = '#fafafa';
  globalSection.style.boxShadow = '0 2px 6px rgba(0,0,0,0.03)';

  const globalHeader = document.createElement('h2');
  globalHeader.textContent = 'Your Overall Progress';
  globalHeader.style.margin = '0 0 0.75rem 0';
  globalHeader.style.fontSize = '1.25rem';
  globalHeader.style.color = '#000';

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
      resetAllProgress();
    }
  });

  pageActions.appendChild(restartBtn);
  root.appendChild(pageActions);

  // Load categories from backend
  fetchCategories();
}

// -----------------------------
// PROGRESS (localStorage)
// -----------------------------
function loadProgress() {
  try {
    const raw = localStorage.getItem('gradgoals_challenge_progress');
    if (!raw) return {};
    return JSON.parse(raw);
  } catch (e) {
    console.warn('Could not load progress from localStorage', e);
    return {};
  }
}

function saveProgress() {
  try {
    localStorage.setItem('gradgoals_challenge_progress', JSON.stringify(progress));
  } catch (e) {
    console.warn('Could not save progress to localStorage', e);
  }
}

function updateProgress(categoryId, isCorrect) {
  if (!progress[categoryId]) {
    progress[categoryId] = { attempts: 0, correct: 0 };
  }
  progress[categoryId].attempts += 1;
  if (isCorrect) {
    progress[categoryId].correct += 1;
  }
  saveProgress();
  renderProgress();
  renderGlobalProgress();
  renderCategories(); // update mini-progress on cards
}

function resetAllProgress() {
  progress = {};
  localStorage.removeItem('gradgoals_challenge_progress');
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
  renderCategories();
}

function getCategoryProgressSummary(categoryId) {
  const stats = progress[categoryId];
  if (!stats || stats.attempts === 0) return 'No attempts yet';
  const percent = Math.round((stats.correct / stats.attempts) * 100);
  return `${stats.correct}/${stats.attempts} correct (${percent}%)`;
}

// -----------------------------
// API CALLS
// -----------------------------
async function fetchCategories() {
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
    renderCategories();
  } catch (err) {
    console.error('Failed to load categories:', err);
    if (gridEl) {
      gridEl.textContent = `Unable to load categories: ${err.message}`;
    }
  }
}

async function fetchRandomQuestion(categoryId) {
  const questionBox = document.getElementById('challenge-question-box');
  const feedbackBox = document.getElementById('challenge-feedback');

  if (feedbackBox) feedbackBox.innerHTML = '';
  if (questionBox) questionBox.textContent = 'Loading question...';

  try {
    const res = await fetch(
      `${API_BASE}/challenge?category=${encodeURIComponent(categoryId)}`
    );
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }
    const q = await res.json();
    currentQuestion = q;
    renderQuestion();
  } catch (err) {
    console.error(err);
    if (questionBox) {
      questionBox.textContent = 'Could not load a question. Try again or pick another topic.';
    }
  }
}

async function checkAnswer(answerText) {
  if (!currentQuestion) return;

  const feedbackBox = document.getElementById('challenge-feedback');
  if (feedbackBox) {
    feedbackBox.innerHTML = '<p>Checking your answer...</p>';
  }

  try {
    const res = await fetch(`${API_BASE}/challenge/check`, {
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
    renderFeedback(data);
    if (data.categoryId) {
      updateProgress(data.categoryId, !!data.correct);
    }
  } catch (err) {
    console.error(err);
    if (feedbackBox) {
      feedbackBox.innerHTML =
        '<p style="color: red;">Error checking answer. Make sure the backend is running.</p>';
    }
  }
}

// -----------------------------
// RENDERING
// -----------------------------
function renderGlobalProgress() {
  const list = document.getElementById('global-progress-list');
  if (!list || categories.length === 0) return;

  list.innerHTML = '';

  categories.forEach(cat => {
    const stats = progress[cat.id] || { attempts: 0, correct: 0 };
    const percent = stats.attempts > 0 ? Math.round((stats.correct / stats.attempts) * 100) : 0;

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

    // Bar background (smaller / sleeker)
    const barBg = document.createElement('div');
    barBg.style.height = '6px';                  // smaller bar
    barBg.style.background = '#e2e2e2';
    barBg.style.borderRadius = '999px';
    barBg.style.overflow = 'hidden';

    // Bar fill
    const barFill = document.createElement('div');
    barFill.style.height = '100%';
    barFill.style.width = percent + '%';
    barFill.style.background = '#0b6623'; // GradGoals green
    barFill.style.borderRadius = '999px';
    barFill.style.transition = 'width 0.3s ease';

    barBg.appendChild(barFill);
    row.appendChild(label);
    row.appendChild(barBg);

    list.appendChild(row);
  });
}

function renderCategories() {
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
    card.style.textAlign = 'left';
    card.style.padding = '0.75rem 0.85rem';
    card.style.borderRadius = '0.8rem';
    card.style.border = cat.id === currentCategoryId ? '2px solid #0b6623' : '1px solid #ddd';
    card.style.background = cat.id === currentCategoryId ? '#eaf6ec' : '#ffffff';
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
      renderCategories();
      renderCategoryInfo(cat);
      fetchRandomQuestion(cat.id);
    });

    gridEl.appendChild(card);
  });

  // Auto-select first category if nothing selected yet
  if (!currentCategoryId && categories.length > 0) {
    currentCategoryId = categories[0].id;
    renderCategories(); // to re-highlight
    renderCategoryInfo(categories[0]);
    fetchRandomQuestion(categories[0].id);
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

function renderQuestion() {
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
  submitBtn.style.background = '#0b6623';
  submitBtn.style.color = 'white';
  submitBtn.style.border = 'none';
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
    checkAnswer(answerText);
  });

  questionBox.appendChild(prompt);
  questionBox.appendChild(form);
}

function renderFeedback(response) {
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
  nextBtn.style.border = '1px solid #ccc';
  nextBtn.style.background = '#fff';
  nextBtn.style.cursor = 'pointer';
  nextBtn.addEventListener('click', () => {
    if (response.categoryId) {
      fetchRandomQuestion(response.categoryId);
    } else if (currentCategoryId) {
      fetchRandomQuestion(currentCategoryId);
    }
  });

  const changeCatBtn = document.createElement('button');
  changeCatBtn.type = 'button';
  changeCatBtn.textContent = 'Choose Another Topic';
  changeCatBtn.style.padding = '0.35rem 0.7rem';
  changeCatBtn.style.borderRadius = '0.3rem';
  changeCatBtn.style.border = '1px solid #ccc';
  changeCatBtn.style.background = '#fff';
  changeCatBtn.style.cursor = 'pointer';
  changeCatBtn.addEventListener('click', () => {
    const infoEl = document.getElementById('challenge-category-info');
    const questionBox = document.getElementById('challenge-question-box');
    if (infoEl) infoEl.innerHTML = '';
    if (questionBox) questionBox.innerHTML = '';
    feedbackBox.innerHTML = '';
    currentCategoryId = null;
    currentQuestion = null;
    renderCategories(); // clears highlight
  });

  actions.appendChild(nextBtn);
  actions.appendChild(changeCatBtn);

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

  if (!stats) {
    progressBox.innerHTML = `<em>No history yet for "${
      cat ? cat.name : currentCategoryId
    }". Your progress will show up here.</em>`;
    return;
  }

  const { attempts, correct } = stats;
  const percent = attempts > 0 ? Math.round((correct / attempts) * 100) : 0;

  progressBox.innerHTML = `
    <strong>Progress for "${cat ? cat.name : currentCategoryId}"</strong><br>
    ${correct} correct out of ${attempts} attempts (${percent}%).
  `;
}
