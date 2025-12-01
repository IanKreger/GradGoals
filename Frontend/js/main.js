// -----------------------------
// CURRENT PAGE DETECTION
// -----------------------------
const currentPage = window.location.pathname.split("/").pop() || "Index.html";

// -----------------------------
// HEADER SETUP
// -----------------------------

const header = document.createElement('header');

// Logo
const logoLink = document.createElement('a');
logoLink.href = 'Index.html';
logoLink.id = 'logo';

const logoImg = document.createElement('img');
logoImg.src = 'Images/GRAD_GOALS.png';
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
  { name: 'Challenges', link: 'Challenges.html' }
];

// Build nav items
menuItems.forEach(item => {
  const li = document.createElement('li');
  const a = document.createElement('a');
  a.href = item.link;
  a.textContent = item.name;

  if (item.link === currentPage) {
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
profileImg.src = 'Images/profile-icon-placeholder.png';
profileImg.alt = 'Profile';
profileImg.width = 40;
profileImg.height = 40;

profileLink.appendChild(profileImg);
profileLi.appendChild(profileLink);
navList.appendChild(profileLi);

nav.appendChild(navList);
header.appendChild(nav);

document.body.appendChild(header);


// -----------------------------
// MAIN CONTENT AREA (EMPTY)
// -----------------------------

const main = document.createElement('main');
main.id = "content"; // page scripts will use this
document.body.appendChild(main);


// -----------------------------
// FOOTER
// -----------------------------

const footer = document.createElement('footer');
footer.innerHTML = '<p>&copy; 2025 GradGoals</p>';
document.body.appendChild(footer);
