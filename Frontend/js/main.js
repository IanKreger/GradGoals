// -----------------------------
// CURRENT PAGE DETECTION
// -----------------------------
// 1. Get the filename
// 2. Convert to lowercase so we don't worry about "Budget" vs "budget"
let currentPage = window.location.pathname.split("/").pop().toLowerCase();

// 3. Handle the root URL (empty string)
if (!currentPage) {
  currentPage = "index.html";
}

// -----------------------------
// HEADER SETUP
// -----------------------------

const header = document.createElement('header');

// Logo
const logoLink = document.createElement('a');
logoLink.href = 'Index.html';
logoLink.id = 'logo';

const logoImg = document.createElement('img');
logoImg.src = 'images/GRAD_GOALS.png';
logoImg.alt = 'Site Logo';
logoImg.width = 100;
logoImg.height = 100;

logoLink.appendChild(logoImg);
header.appendChild(logoLink);

// Navigation
const nav = document.createElement('nav');
const navList = document.createElement('ul');

const menuItems = [
  { name: 'Home', link: 'Index.html' },
  { name: 'Resources', link: 'Resources.html' },
  { name: 'Budget Tool', link: 'Budget.html' },
  { name: 'Challenges', link: 'Challenge.html'}
];

// Build nav items
menuItems.forEach(item => {
  const li = document.createElement('li');
  const a = document.createElement('a');
  a.href = item.link;
  a.textContent = item.name;

  // FIX: Compare both in lowercase so casing doesn't break it
  if (item.link.toLowerCase() === currentPage) {
    a.classList.add('active');
  }

  li.appendChild(a);
  navList.appendChild(li);
});

// Profile icon
const profileLi = document.createElement('li');
const profileLink = document.createElement('a');
profileLink.href = 'Profile.html';

const profileImg = document.createElement('img');
profileImg.src = 'images/profile-icon-placeholder.png';
profileImg.alt = 'Profile';
profileImg.width = 40;
profileImg.height = 40;

// FIX: Add the check for the Profile page here too!
if ('profile.html' === currentPage) {
    profileLink.classList.add('active');
}

profileLink.appendChild(profileImg);
profileLi.appendChild(profileLink);
navList.appendChild(profileLi);

nav.appendChild(navList);
header.appendChild(nav);

// -----------------------------
// FIX: HEADER INSERTION
// -----------------------------
// OLD: document.body.appendChild(header); (This put it at the bottom)
// NEW: Prepend puts it at the very top, before your lock screen logic
document.body.prepend(header);


// -----------------------------
// MAIN CONTENT AREA
// -----------------------------

// FIX: DUPLICATE CONTENT CHECK
// Since your HTML files now have <div id="content"> manually added,
// we should only create one if it DOESN'T exist (like on Index.html).

let main = document.getElementById("content");

if (!main) {
    main = document.createElement('main');
    main.id = "content"; 
    // If we are creating it from scratch, put it after the header
    header.after(main);
}


// -----------------------------
// FOOTER
// -----------------------------

const footer = document.createElement('footer');
footer.innerHTML = '<p>&copy; 2025 GradGoals</p>';

// Append is fine for footer, it should be at the bottom
document.body.appendChild(footer);