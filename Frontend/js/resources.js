// Check if we are on the resources page
if (currentPage.toLowerCase().includes("resources")) {

    checkAuthAndRender();

    function checkAuthAndRender() {
        // 1. Check for the saved user
        const user = localStorage.getItem('gradGoalsUser');

        const warningEl = document.getElementById('login-warning');
        const contentEl = document.getElementById('content');

        if (user) {
            // --- LOGGED IN ---
            // Hide warning, show content div, and run your logic
            if (warningEl) warningEl.style.display = 'none';
            if (contentEl) {
                contentEl.style.display = 'block';
                renderResources(contentEl);
            }
        } else {
            // --- NOT LOGGED IN ---
            // Show warning, hide content div
            if (warningEl) warningEl.style.display = 'block';
            if (contentEl) contentEl.style.display = 'none';
        }
    }

    const API = "https://gradgoals-i74s.onrender.com/resources";
    async function getResources() {
        const response = await fetch(API);
        return await response.json();
    }

    // Your original logic, wrapped in a function so we only run it if logged in
    async function renderResources(content) {
        const resources = await getResources();

        const contentEl = document.getElementById('content');
        const wrapperEl = document.createElement('div');
        wrapperEl.setAttribute('class', 'resources');

        let counter = 1;
        for(const resource of resources) {
            const linkElement = document.createElement('a');
            linkElement.setAttribute('href', resource.url);
            linkElement.setAttribute('target', '_blank');
            linkElement.innerHTML = `
               <span class="resource-title">${resource.title}</span>
            <span class="resource-cta">${resource.type === 'video' ? 'watch..' : 'read...'}</span>
            <span class="resource-counter">${counter}</span>
            `
            wrapperEl.appendChild(linkElement);
            counter++;
        }
        contentEl.appendChild(wrapperEl);
    }
}
