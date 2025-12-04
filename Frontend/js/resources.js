(function () {
  // Determine if we are on the Resources page
  let isResourcesPage = false;

  if (typeof currentPage !== "undefined") {
    isResourcesPage = currentPage.toLowerCase().includes("resources");
  } else {
    isResourcesPage = window.location.pathname.toLowerCase().includes("resources");
  }

  if (!isResourcesPage) return;

  // API Base URL (Switch to localhost if testing locally)
  const API_BASE = "https://gradgoals-i74s.onrender.com";
  // const API_BASE = "http://localhost:8080"; 

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
            renderResources(user); 
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
      id: "res-1",
      title: "What is budgeting:",
      type: "video",
      url: "https://www.youtube.com/watch?v=CbhjhWleKGE",
      meta: "YouTube • 4 min",
    },
    {
      id: "res-2",
      title: "Budgeting Basics",
      type: "video",
      url: "https://www.youtube.com/watch?v=sVKQn2I4HDM",
      meta: "YouTube • Beginner friendly",
    },
    {
      id: "res-3",
      title: "Budgeting for Beginners",
      type: "video",
      url: "https://www.youtube.com/watch?v=xfPbT7HPkKA",
      meta: "YouTube • Step-by-step",
    },
    {
      id: "res-4",
      title: "How to make a budget and stick to it",
      type: "video",
      url: "https://www.youtube.com/watch?v=4Eh8QLcB1UQ",
      meta: "YouTube • Practical tips",
    },
    {
      id: "res-5",
      title: "How to manage money like the 1%",
      type: "video",
      url: "https://www.youtube.com/watch?v=NEzqHbtGa9U",
      meta: "YouTube • Mindset + tactics",
    },
    {
      id: "res-6",
      title: "You need a written budget",
      type: "video",
      url: "https://www.youtube.com/watch?v=8F0mH84w6e4",
      meta: "YouTube • Why budgeting matters",
    },
    {
      id: "res-7",
      title: "Budgeting",
      type: "textbook",
      url: "https://research.ebsco.com/c/evkh36/ebook-viewer/pdf/qthbl2jd2b/page/pp_11",
      meta: "E-book chapter • Foundations",
    },
  ];

  // -------------------------------
  // Render function
  // -------------------------------
  function renderResources(currentUser) {
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

      // 1. CONTAINER
      const container = document.createElement("div");
      container.className = "resource-card-container";

      // 2. LINK AREA
      const linkArea = document.createElement("a");
      linkArea.className = "resource-card-link";
      linkArea.href = resource.url;
      linkArea.target = "_blank";
      linkArea.rel = "noopener noreferrer";

      // -- Thumb --
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

      // -- Body --
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

      // -- CTA --
      const cta = document.createElement("div");
      cta.className = "resource-cta";
      cta.innerHTML = isVideo
        ? `<svg viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M8 5v14l11-7z"></path></svg><span>Watch</span>`
        : `<svg viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M18 2H6a2 2 0 0 0-2 2v16l7-3l7 3V4a2 2 0 0 0-2-2z"></path></svg><span>Read</span>`;

      // Append to Link Area
      linkArea.appendChild(thumbWrapper);
      linkArea.appendChild(body);
      linkArea.appendChild(cta);

      // 3. RATING SECTION
      const ratingSection = document.createElement("div");
      ratingSection.className = "rating-section";
      // We don't strictly need the ID anymore since we pass the element directly, 
      // but keeping it is fine for debugging.
      ratingSection.id = `rating-${resource.id}`; 
      ratingSection.innerHTML = `
        <div class="star-container">
            <span class="star" data-val="1">★</span>
            <span class="star" data-val="2">★</span>
            <span class="star" data-val="3">★</span>
            <span class="star" data-val="4">★</span>
            <span class="star" data-val="5">★</span>
        </div>
        <div class="avg-score">Avg: <span class="avg-val">--</span></div>
      `;

      // Assemble Container
      container.appendChild(linkArea);
      container.appendChild(ratingSection);
      grid.appendChild(container);

      // 4. ACTIVATE LOGIC - FIXED: Passing the element directly!
      setupRatingLogic(ratingSection, resource.id, currentUser);

      index++;
    }

    section.appendChild(grid);
    contentEl.appendChild(section);
  }

  // -------------------------------
  // Rating Logic (FIXED)
  // -------------------------------
  async function setupRatingLogic(container, resourceId, userId) {
      // Look inside the specific container we just built
      const stars = container.querySelectorAll('.star');
      const avgLabel = container.querySelector('.avg-val');

      // Helper: visual update
      const highlightStars = (val) => {
          stars.forEach(s => {
              const starVal = parseInt(s.getAttribute('data-val'));
              if (starVal <= val) s.classList.add('active');
              else s.classList.remove('active');
          });
      };

      // 1. Fetch Average
      try {
          const res = await fetch(`${API_BASE}/ratings/average?resourceId=${resourceId}`);
          const avg = await res.json();
          avgLabel.textContent = avg > 0 ? avg.toFixed(1) : "N/A";
      } catch (e) { console.error("Avg fetch error:", e); }

      // 2. Fetch User Rating
      try {
          const res = await fetch(`${API_BASE}/ratings/user?resourceId=${resourceId}&userId=${userId}`);
          // Server might return empty body if no rating, so verify json
          const text = await res.text();
          if (text) {
              const data = JSON.parse(text);
              if (data && data.stars) {
                  highlightStars(data.stars);
              }
          }
      } catch (e) { console.error("User rating error:", e); }

      // 3. Click Handler
      stars.forEach(star => {
          star.addEventListener('click', async () => {
              const val = parseInt(star.getAttribute('data-val'));
              console.log(`User ${userId} clicked ${val} stars for ${resourceId}`);

              // Optimistic UI update
              highlightStars(val);

              // Send to Server
              try {
                  await fetch(`${API_BASE}/ratings`, {
                      method: 'POST',
                      headers: { 'Content-Type': 'application/json' },
                      body: JSON.stringify({
                          resourceId: resourceId,
                          userId: userId,
                          stars: val
                      })
                  });

                  // Update Average after saving
                  const res = await fetch(`${API_BASE}/ratings/average?resourceId=${resourceId}`);
                  const avg = await res.json();
                  avgLabel.textContent = avg.toFixed(1);
                  
              } catch (e) { console.error("Save error:", e); }
          });
      });
  }

  // Run when DOM is ready
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", checkAuthAndRender);
  } else {
    checkAuthAndRender();
  }
})();