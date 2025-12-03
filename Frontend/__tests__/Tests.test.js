// Frontend/__tests__/Tests.test.js

const fs = require('fs');
const path = require('path');
const { JSDOM } = require('jsdom');

//
// Helpers
//

function loadChallengeHtmlDom() {
  // Arrange
  const filePath = path.join(__dirname, '..', 'Challenge.html');
  const html = fs.readFileSync(filePath, 'utf-8');

  // Act
  const dom = new JSDOM(html);

  // Assert (implicit via tests that use dom)
  return dom.window.document;
}

function loadChallengeJsDom() {
  // Arrange: simple HTML shell + jsdom window
  const html = `
    <!DOCTYPE html>
    <html>
      <head></head>
      <body>
        <div id="challenge-app"></div>
      </body>
    </html>
  `;
  const dom = new JSDOM(html, {
    url: 'http://localhost',
    runScripts: 'outside-only'
  });

  const scriptPath = path.join(__dirname, '..', 'js', 'challenge.js');
  const scriptContent = fs.readFileSync(scriptPath, 'utf-8');

  // Act: evaluate challenge.js in the jsdom window
  dom.window.eval(scriptContent);

  // Assert (implicit via tests that use dom)
  return dom;
}

//
// 10 tests for Challenge.html (AAA)
//

describe('Challenge.html structure', () => {
  test('has HTML5 doctype', () => {
    // Arrange
    const filePath = path.join(__dirname, '..', 'Challenge.html');
    const html = fs.readFileSync(filePath, 'utf-8');

    // Act
    const dom = new JSDOM(html);

    // Assert
    expect(dom.window.document.doctype.name).toBe('html');
  });

  test('sets language to English', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const lang = document.documentElement.lang;

    // Assert
    expect(lang).toBe('en');
  });

  test('includes UTF-8 charset meta tag', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const metaCharset = document.querySelector('meta[charset]');

    // Assert
    expect(metaCharset).not.toBeNull();
    expect(metaCharset.getAttribute('charset').toUpperCase()).toBe('UTF-8');
  });

  test('includes viewport meta tag for responsiveness', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const viewportMeta = document.querySelector(
      'meta[name="viewport"]'
    );

    // Assert
    expect(viewportMeta).not.toBeNull();
    expect(viewportMeta.getAttribute('content')).toContain('width=device-width');
  });

  test('has the correct page title', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const title = document.querySelector('title')?.textContent;

    // Assert
    expect(title).toBe('GradGoals Challenge');
  });

  test('includes two stylesheet links', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const links = document.querySelectorAll('link[rel="stylesheet"]');

    // Assert
    expect(links.length).toBe(2);
  });

  test('first stylesheet is style.css', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const firstHref = document
      .querySelectorAll('link[rel="stylesheet"]')[0]
      .getAttribute('href');

    // Assert
    expect(firstHref).toBe('style.css');
  });

  test('second stylesheet is stylechallenge.css', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const secondHref = document
      .querySelectorAll('link[rel="stylesheet"]')[1]
      .getAttribute('href');

    // Assert
    expect(secondHref).toBe('stylechallenge.css');
  });

  test('includes main.js script', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const scripts = Array.from(document.querySelectorAll('script')).map(
      (s) => s.getAttribute('src')
    );

    // Assert
    expect(scripts).toContain('js/main.js');
  });

  test('includes challenge.js script after main.js', () => {
    // Arrange
    const document = loadChallengeHtmlDom();

    // Act
    const scripts = Array.from(document.querySelectorAll('script')).map(
      (s) => s.getAttribute('src')
    );
    const mainIndex = scripts.indexOf('js/main.js');
    const challengeIndex = scripts.indexOf('js/challenge.js');

    // Assert
    expect(challengeIndex).toBeGreaterThan(mainIndex);
  });
});

//
// 10 tests for challenge.js behavior (AAA)
//

describe('challenge.js behavior', () => {
  test('initChallengeApp creates root container when missing', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { document, initChallengeApp } = dom.window;
    document.getElementById('challenge-app').remove();

    // Act
    initChallengeApp();
    const root = document.getElementById('challenge-app');

    // Assert
    expect(root).not.toBeNull();
  });

  test('initChallengeApp renders main header text', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { document, initChallengeApp } = dom.window;

    // Act
    initChallengeApp();
    const heading = document.querySelector('h1');

    // Assert
    expect(heading).not.toBeNull();
    expect(heading.textContent).toBe('GradGoals Challenges');
  });

  test('initChallengeApp adds global progress list container', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { document, initChallengeApp } = dom.window;

    // Act
    initChallengeApp();
    const globalList = document.getElementById('global-progress-list');

    // Assert
    expect(globalList).not.toBeNull();
  });

  test('loadProgress returns empty object when no saved data', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { localStorage, loadProgress } = dom.window;
    localStorage.removeItem('gradgoals_challenge_progress');

    // Act
    const result = loadProgress();

    // Assert
    expect(result).toEqual({});
  });

  test('loadProgress returns parsed object when data exists', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { localStorage, loadProgress } = dom.window;
    const stored = { budgeting: { attempts: 3, correct: 2 } };
    localStorage.setItem(
      'gradgoals_challenge_progress',
      JSON.stringify(stored)
    );

    // Act
    const result = loadProgress();

    // Assert
    expect(result).toEqual(stored);
  });

  test('getCategoryProgressSummary returns "No attempts yet" when none', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { getCategoryProgressSummary } = dom.window;

    // Act
    const summary = getCategoryProgressSummary('budgeting');

    // Assert
    expect(summary).toBe('No attempts yet');
  });

  test('getCategoryProgressSummary returns formatted stats when attempts exist', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const { updateProgress, getCategoryProgressSummary } = dom.window;

    // Act
    updateProgress('budgeting', true); // 1 correct out of 1
    const summary = getCategoryProgressSummary('budgeting');

    // Assert
    expect(summary).toBe('1/1 correct (100%)');
  });

  test('resetAllProgress clears localStorage and progress text', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const {
      document,
      localStorage,
      initChallengeApp,
      updateProgress,
      resetAllProgress,
      getCategoryProgressSummary
    } = dom.window;

    initChallengeApp();
    updateProgress('budgeting', true);
    localStorage.setItem(
      'gradgoals_challenge_progress',
      JSON.stringify({ budgeting: { attempts: 1, correct: 1 } })
    );

    // Act
    resetAllProgress();
    const stored = localStorage.getItem('gradgoals_challenge_progress');
    const summary = getCategoryProgressSummary('budgeting');
    const infoEl = document.getElementById('challenge-category-info');

    // Assert
    expect(stored).toBeNull();
    expect(summary).toBe('No attempts yet');
    expect(infoEl.innerHTML).toBe('');
  });

  test('updateProgress tracks correct answers', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const {
      updateProgress,
      getCategoryProgressSummary
    } = dom.window;

    // Act
    updateProgress('saving', true);  // 1/1
    updateProgress('saving', true);  // 2/2
    const summary = getCategoryProgressSummary('saving');

    // Assert
    expect(summary).toBe('2/2 correct (100%)');
  });

  test('updateProgress counts attempts even when incorrect', () => {
    // Arrange
    const dom = loadChallengeJsDom();
    const {
      updateProgress,
      getCategoryProgressSummary
    } = dom.window;

    // Act
    updateProgress('debt', true);   // 1 correct
    updateProgress('debt', false);  // 1 incorrect
    const summary = getCategoryProgressSummary('debt');

    // Assert
    expect(summary).toBe('1/2 correct (50%)');
  });
});
