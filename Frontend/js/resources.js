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

    // 2. Render immediately – no login checks
    renderResources();

    function renderResources() {
        const contentEl = document.getElementById('content');
        if (!contentEl) return;

        // Make sure content is visible
        contentEl.style.display = 'block';

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
