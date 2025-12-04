(function () {
  // Determine if we are on the Resources page
  let isResourcesPage = false;

  if (typeof currentPage !== "undefined") {
    isResourcesPage = currentPage.toLowerCase().includes("resources");
  } else {
    isResourcesPage = window.location.pathname.toLowerCase().includes("resources");
  }

  if (!isResourcesPage) return;

  // -------------------------------
  // AUTH CHECK & INITIALIZATION
  // -------------------------------
  function checkAuthAndRender() {
    const user = localStorage.getItem('gradGoalsUser');
    const warningEl = document.getElementById('login-warning');
    const contentEl = document.getElementById('content');

    if (user) {
        // --- LOGGED IN ---
        if (warningEl) warningEl.style.display = 'none';
        if (contentEl) {
            contentEl.style.display = 'block';
            renderResources(); // Call the new render function
        }
    } else {
        // --- NOT LOGGED IN ---
        if (warningEl) warningEl.style.display = 'block';
        if (contentEl) contentEl.style.display = 'none';
    }
  }

  // -------------------------------
  // Static list of resources
  // -------------------------------
  const RESOURCES = [
    {
      title: "What is budgeting:",
      type: "video",
      url: "https://www.youtube.com/watch?v=CbhjhWleKGE",
      meta: "YouTube • 4 min",
    },
    {
      title: "Budgeting Basics",
      type: "video",
      url: "https://www.youtube.com/watch?v=sVKQn2I4HDM",
      meta: "YouTube • Beginner friendly",
    },
    {
      title: "Budgeting for Beginners",
      type: "video",
      url: "https://www.youtube.com/watch?v=xfPbT7HPkKA",
      meta: "YouTube • Step-by-step",
    },
    {
      title: "How to make a budget and stick to it",
      type: "video",
      url: "https://www.youtube.com/watch?v=4Eh8QLcB1UQ",
      meta: "YouTube • Practical tips",
    },
    {
      title: "How to manage money like the 1%",
      type: "video",
      url: "https://www.youtube.com/watch?v=NEzqHbtGa9U",
      meta: "YouTube • Mindset + tactics",
    },
    {
      title: "You need a written budget",
      type: "video",
      url: "https://www.youtube.com/watch?v=8F0mH84w6e4",
      meta: "YouTube • Why budgeting matters",
    },
    {
      title: "Budgeting",
      type: "textbook",
      url: "https://research.ebsco.com/c/evkh36/ebook-viewer/pdf/qthbl2jd2b/page/pp_11?location=https%3A%2F%2Fresearch.ebsco.com%2Fc%2Fevkh36%2Fsearch%2Fdetails%2Fqthbl2jd2b%3Fdb%3De000xna",
      meta: "E-book chapter • Foundations",
    },
  ];

  // -------------------------------
  // Render function (New Design)
  // -------------------------------
  function renderResources() {
    const contentEl = document.getElementById("content");
    if (!contentEl) return;

    contentEl.innerHTML = "";

    // Outer section
    const section = document.createElement("section");
    section.className = "resources-section";

    // Heading
    const heading = document.createElement("h1");
    heading.className = "resources-heading";
    heading.textContent = "Budgeting Resources";
    section.appendChild(heading);

    // Grid
    const grid = document.createElement("div");
    grid.className = "resources-grid";

    let index = 1;

    for (const resource of RESOURCES) {
      const isVideo = resource.type === "video";

      const card = document.createElement("a");
      card.className = "resource-card";
      card.href = resource.url;
      card.target = "_blank";
      card.rel = "noopener noreferrer";

      // Left: thumb with icon + index badge
      const thumbWrapper = document.createElement("div");
      thumbWrapper.className = "resource-thumb-wrapper";

      const thumb = document.createElement("div");
      thumb.className = "resource-thumb";

      const thumbIcon = document.createElement("div");
      thumbIcon.className = "resource-thumb-icon";
      thumbIcon.textContent = isVideo ? "▶" : "📘";

      const indexBadge = document.createElement("div");
      indexBadge.className = "resource-index";
      indexBadge.textContent = index;

      thumb.appendChild(thumbIcon);
      thumbWrapper.appendChild(thumb);
      thumbWrapper.appendChild(indexBadge);

      // Middle: text body
      const body = document.createElement("div");
      body.className = "resource-body";

      const type = document.createElement("div");
      type.className = "resource-type";
      type.textContent = isVideo ? "Video" : "Textbook";

      const title = document.createElement("div");
      title.className = "resource-title";
      title.textContent = resource.title;

      const meta = document.createElement("div");
      meta.className = "resource-meta";
      meta.textContent = resource.meta || (isVideo ? "Watch on YouTube" : "Read online");

      body.appendChild(type);
      body.appendChild(title);
      body.appendChild(meta);

      // Right: CTA ("Watch" / "Read" + tiny icon)
      const cta = document.createElement("div");
      cta.className = "resource-cta";
      cta.innerHTML = isVideo
        ? `
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path fill="currentColor" d="M8 5v14l11-7z"></path>
          </svg>
          <span>Watch</span>
        `
        : `
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path fill="currentColor" d="M18 2H6a2 2 0 0 0-2 2v16l7-3l7 3V4a2 2 0 0 0-2-2z"></path>
          </svg>
          <span>Read</span>
        `;

      // Assemble card
      card.appendChild(thumbWrapper);
      card.appendChild(body);
      card.appendChild(cta);

      grid.appendChild(card);
      index++;
    }

    section.appendChild(grid);
    contentEl.appendChild(section);
  }

  // Run when DOM is ready
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", checkAuthAndRender);
  } else {
    checkAuthAndRender();
  }
})();