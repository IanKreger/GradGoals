//Header

// Create header container
const header = document.createElement('header');

//Logo
const logoLink = document.createElement('a');
logoLink.href = 'index.html';
logoLink.id = 'logo';

const logoImg = document.createElement('img');
logoImg.src = 'images/GRAD_GOALS.png'; // adding the logo to the website
logoImg.alt = 'Site Logo';
logoImg.width = 100;
logoImg.height = 100;

logoLink.appendChild(logoImg);
header.appendChild(logoLink);

//Navigation
const nav = document.createElement('nav');
const navList = document.createElement('ul');

//Menu items
const menuItems = [
  { name: 'Home', link: 'index.html' },
  { name: 'Resources', link: 'resources.html' },
  { name: 'Budget Tool', link: 'budget.html' },
  { name: 'Login / Sign Up', link: 'login.html' }
];

//Underline feature
const currentPage = window.location.pathname.split("/").pop() || "index.html";

//Creates each menu item
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

//Profile icon
const profileLi = document.createElement('li');
const profileLink = document.createElement('a');
profileLink.href = 'profile.html';

const profileImg = document.createElement('img');
profileImg.src = 'images/profile-icon-placeholder.png'; // fixed path
profileImg.alt = 'Profile';
profileImg.width = 40;
profileImg.height = 40;

profileLink.appendChild(profileImg);
profileLi.appendChild(profileLink);
navList.appendChild(profileLi);

nav.appendChild(navList);
header.appendChild(nav);

//Adds header to body
document.body.appendChild(header);


//Main content
const main = document.createElement('main');


switch (currentPage) {
  case "resources.html":
    main.innerHTML = `
      <h1>Resources</h1>
      <p>Explore a curated collection of tools, guides, and materials to help you succeed academically and financially.</p>
    `;
    break;

  case "budget.html":
    main.innerHTML = `
      <h1>Budget Tool</h1>
      <p>Plan your finances, track expenses, and stay on top of your budget with GradGoals' interactive budgeting tool.</p>
    `;
    break;

  case "login.html":
    main.innerHTML = `
      <h1>Login / Sign Up</h1>
      <p>Access your GradGoals account or create a new one to track your goals and manage your progress.</p>
    `;
    break;

  case "profile.html":
    main.innerHTML = `
      <h1>Profile</h1>
      <p>This is your profile page. You’ll be able to view and manage your account information here.</p>
    `;
    break;

  default:
    main.innerHTML = `
      <h1>Welcome to GradGoals</h1>
      <p>GradGoals helps you plan your journey toward academic and financial success.</p>
      <p>Explore our resources, track your budget, and manage your progress all in one place.</p>
    `;
    break;
}

document.body.appendChild(main);


//Footer
const footer = document.createElement('footer');
footer.innerHTML = '<p>&copy; 2025 GradGoals</p>';
document.body.appendChild(footer);
