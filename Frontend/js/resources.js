// resources.js

function loadResourcesPage(currentPage) {
  // Use the test path if given, otherwise fall back to real browser path
  const page = currentPage || (typeof window !== 'undefined'
    ? window.location.pathname
    : ""
  );

  if (page.toLowerCase().includes("resources")) {
    const content = document.querySelector('#content');
    if (!content) return;

    // --- Section 1: Credit Card Payoff ---
    const div1 = document.createElement('div');
    const heading1 = document.createElement('h3');
    heading1.textContent = "Credit Card Payoff";

    const image1 = document.createElement('img');
    image1.setAttribute('src', './images/creditpayoff_infographics1.jpg');
    image1.setAttribute('width', '320px');

    div1.appendChild(heading1);
    div1.appendChild(image1);

    // --- Section 2: Student Loan Payoff ---
    const div2 = document.createElement('div');
    div2.style.marginTop = '100px';

    const heading2 = document.createElement('h3');
    heading2.textContent = "Student Loan Payoff";

    const image2 = document.createElement('img');
    image2.setAttribute('src', './images/student_loan_payoff2.png');
    image2.setAttribute('width', '320px');

    div2.appendChild(heading2);
    div2.appendChild(image2);

    content.appendChild(div1);
    content.appendChild(div2);
  }
}

// ✅ Make it still run automatically in the browser
if (typeof window !== 'undefined') {
  loadResourcesPage();
}

// ✅ Make it importable in Jest tests (Node)
if (typeof module !== 'undefined') {
  module.exports = { loadResourcesPage };
}

