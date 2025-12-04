// Frontend/__tests__/TestResources.test.js

const fs = require('fs');
const path = require('path');
const { JSDOM } = require('jsdom');

const mockedResources = [
    {title: 'What is budgeting:', type: 'video', url: 'https://example.com/vidoe-1'},
    {title: 'Budgeting Basics', type: 'video', url: 'https://example.com/video-2'},
    {title: 'Budgeting', type: 'textbook', url: 'https://example.com/textbook'},
];

// Mocks
global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        status: 200,
        json: () => Promise.resolve({
            // 👇 This is the data your API returns
            data: mockedResources,
        }),
    })
);

//
// Helpers
//
function loadResourcesHtmlDom() {
    // Arrange
    const filePath = path.join(__dirname, '..', 'Resources.html');
    const html = fs.readFileSync(filePath, 'utf-8');

    // Act
    const dom = new JSDOM(html);

    // Assert (implicit via tests that use dom)
    return dom.window.document;
}

const API = "https://gradgoals-i74s.onrender.com/resources";
async function getResources() {
    const response = await fetch(API);
    return await response.json();
}

describe('Resources.html structure', () => {
    test('has HTML5 doctype', () => {
        // Arrange
        const filePath = path.join(__dirname, '..', 'Resources.html');
        const html = fs.readFileSync(filePath, 'utf-8');

        // Act
        const dom = new JSDOM(html);

        // Assert
        expect(dom.window.document.doctype.name).toBe('html');
    });

    test('sets language to English', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const lang = document.documentElement.lang;

        // Assert
        expect(lang).toBe('en');
    });

    test('includes UTF-8 charset meta tag', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const metaCharset = document.querySelector('meta[charset]');

        // Assert
        expect(metaCharset).not.toBeNull();
        expect(metaCharset.getAttribute('charset').toUpperCase()).toBe('UTF-8');
    });

    test('includes viewport meta tag for responsiveness', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

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
        const document = loadResourcesHtmlDom();

        // Act
        const title = document.querySelector('title')?.textContent;

        // Assert
        expect(title).toBe('Resources | GradGoals');
    });

    test('includes two stylesheet links', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const links = document.querySelectorAll('link[rel="stylesheet"]');

        // Assert
        expect(links.length).toBe(2);
    });

    test('first stylesheet is style.css', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const firstHref = document
            .querySelectorAll('link[rel="stylesheet"]')[0]
            .getAttribute('href');

        // Assert
        expect(firstHref).toBe('style.css');
    });

    test('second stylesheet is styleresouces.css', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const secondHref = document
            .querySelectorAll('link[rel="stylesheet"]')[1]
            .getAttribute('href');

        // Assert
        expect(secondHref).toBe('styleresources.css');
    });

    test('includes main.js script', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const scripts = Array.from(document.querySelectorAll('script')).map(
            (s) => s.getAttribute('src')
        );

        // Assert
        expect(scripts).toContain('js/main.js');
    });

    test('includes resources.js script after main.js', () => {
        // Arrange
        const document = loadResourcesHtmlDom();

        // Act
        const scripts = Array.from(document.querySelectorAll('script')).map(
            (s) => s.getAttribute('src')
        );
        const mainIndex = scripts.indexOf('js/main.js');
        const challengeIndex = scripts.indexOf('js/resources.js');

        // Assert
        expect(challengeIndex).toBeGreaterThan(mainIndex);
    });
});

describe('Test Resources API request', () => {
    beforeEach(() => {
        global.fetch = jest.fn();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('renders resources', async () => {
        // Mock a specific success response
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockedResources,
        });

        await getResources();

        expect(global.fetch).toHaveBeenCalledTimes(1);
    });

    test('handles empty list', async () => {
        // Mock a specific empty response
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => [],
        });

        await getResources();
    });
})
