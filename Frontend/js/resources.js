// Wrap in an IIFE to avoid polluting global scope
(function () {
  // Determine if we are on the Resources page
  // Prefer currentPage from main.js if it exists; otherwise use pathname
  let isResourcesPage = false;

  if (typeof currentPage !== "undefined") {
    isResourcesPage = currentPage.toLowerCase().includes("resources");
  } else {
    isResourcesPage = window.location.pathname.toLowerCase().includes("resources");
  }

  if (!isResourcesPage) return;

  // -------------------------------
  // 1. Static list of resources
  // -------------------------------

  const RESOURCES = [
    {
      title: "What is budgeting:",
      type: "video",
      url: "https://www.youtube.com/watch?v=CbhjhWleKGE",
    },
    {
      title: "Budgeting basics",
      type: "video",
      url: "https://www.youtube.com/watch?v=sVKQn2I4HDM",
    },
    {
      title: "Budgeting for beginners",
      type: "video",
      url: "https://www.youtube.com/watch?v=xfPbT7HPkKA",
    },
    {
      title: "How to make a budget and stick to it",
      type: "video",
      url: "https://www.youtube.com/watch?v=4Eh8QLcB1UQ",
    },
    {
      title: "How to manage money like the 1%",
      type: "video",
      url: "https://www.youtube.com/watch?v=NEzqHbtGa9U",
    },
    {
      title: "You need a written budget",
      type: "video",
      url: "https://www.youtube.com/watch?v=8F0mH84w6e4",
    },
    {
      title: "Budgeting",
      type: "textbook",
      url: "https://research.ebsco.com/c/evkh36/ebook-viewer/pdf/qthbl2jd2b/page/pp_11?location=https%3A%2F%2Fresearch.ebsco.com%2Fc%2Fevkh36%2Fsearch%2Fdetails%2Fqthbl2jd2b%3Fdb%3De000xna",
    },
  ];

  // -------------------------------
  // 2. Render function
  // -------------------------------

  function renderResources() {
    const contentEl = document.getElementById("content");
    if (!contentEl) return;

    // Clear anything that might already be inside
    contentEl.innerHTML = "";

    // Heading
    const heading = document.createElement("h1");
    heading.className = "resources-heading";
    heading.textContent = "Budgeting Resources";
    contentEl.appendChild(heading);

    // Wrapper for all resource cards
    const wrapperEl = document.createElement("div");
    wrapperEl.className = "resources";

    let counter = 1;

    for (const resource of RESOURCES) {
      const isVideo = resource.type === "video";

      const linkElement = document.createElement("a");
      linkElement.href = resource.url;
      linkElement.target = "_blank";
      linkElement.rel = "noopener noreferrer";
      // Build card content with SVG icon + text
      linkElement.innerHTML = `
        <span class="resource-title">${resource.title}</span>

        <span class="resource-cta">
          ${
            isVideo
              ? `
                <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" d="M8 5v14l11-7z" />
                </svg>
                <span>Watch</span>
              `
              : `
                <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" d="M18 2H6a2 2 0 0 0-2 2v16l7-3l7 3V4a2 2 0 0 0-2-2z" />
                </svg>
                <span>Read</span>
              `
          }
        </span>

        <span class="resource-counter">${counter}</span>
      `;

      wrapperEl.appendChild(linkElement);
      counter++;
    }

    contentEl.appendChild(wrapperEl);
  }

  // -------------------------------
  // 3. Run on DOM ready
  // -------------------------------

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", renderResources);
  } else {
    renderResources();
  }
})();
// Check if we are on the resources page
if (currentPage.toLowerCase().includes("resources")) {

    // 1. Hard-coded resources (no backend)
    const RESOURCES = [
        {
            title: "What is budgeting:",
            type: "video",
            url: "https://www.youtube.com/watch?v=CbhjhWleKGE"
        },
        {
            title: "Budgeting Basics",
            type: "video",
            url: "https://www.youtube.com/watch?v=sVKQn2I4HDM"
        },
        {
            title: "Budgeting for Beginners",
            type: "video",
            url: "https://www.youtube.com/watch?v=xfPbT7HPkKA"
        },
        {
            title: "How to make a budget and stick to it",
            type: "video",
            url: "https://www.youtube.com/watch?v=4Eh8QLcB1UQ"
        },
        {
            title: "How to manage money like the 1%",
            type: "video",
            url: "https://www.youtube.com/watch?v=NEzqHbtGa9U"
        },
        {
            title: "You need a written budget",
            type: "video",
            url: "https://www.youtube.com/watch?v=8F0mH84w6e4"
        },
        {
            title: "Budgeting",
            type: "textbook",
            url: "https://research.ebsco.com/c/evkh36/ebook-viewer/pdf/qthbl2jd2b/page/pp_11?location=https%3A%2F%2Fresearch.ebsco.com%2Fc%2Fevkh36%2Fsearch%2Fdetails%2Fqthbl2jd2b%3Fdb%3De000xna"
        }
    ];

    // 2. Check Auth before rendering
    checkAuthAndRender();

    function checkAuthAndRender() {
        // Get the logged-in user from localStorage
        const user = localStorage.getItem('gradGoalsUser');
        
        const warningEl = document.getElementById('login-warning');
        const contentEl = document.getElementById('content');

        if (user) {
            // --- LOGGED IN ---
            // Hide warning, Show content, Render videos
            if (warningEl) warningEl.style.display = 'none';
            if (contentEl) {
                contentEl.style.display = 'block';
                renderResources();
            }
        } else {
            // --- NOT LOGGED IN ---
            // Show warning, Hide content
            if (warningEl) warningEl.style.display = 'block';
            if (contentEl) contentEl.style.display = 'none';
        }
    }

    function renderResources() {
        const contentEl = document.getElementById('content');
        if (!contentEl) return;

        const wrapperEl = document.createElement('div');
        wrapperEl.setAttribute('class', 'resources');

        let counter = 1;
        for (const resource of RESOURCES) {
            const linkElement = document.createElement('a');
            linkElement.setAttribute('href', resource.url);
            linkElement.setAttribute('target', '_blank');
            linkElement.innerHTML = `
                <span class="resource-title">${resource.title}</span>
                <span class="resource-cta">${resource.type === 'video' ? 'watch..' : 'read...'}</span>
                <span class="resource-counter">${counter}</span>
            `;
            wrapperEl.appendChild(linkElement);
            counter++;
        }

        contentEl.appendChild(wrapperEl);
    }
}
