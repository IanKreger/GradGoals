//Header

// Create header container
const header = document.createElement('header');

//Logo
const logoLink = document.createElement('a');
logoLink.href = 'index.html';
logoLink.id = 'logo';

const logoImg = document.createElement('img');
logoImg.src = 'images/logo-placeholder.png'; // fixed path
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
    <p>Track income and expenses in a spreadsheet-style layout.</p>

    <div class="budget-table-container">
      <table id="budgetTable">
        <thead>
          <tr>
            <th>Category</th>
            <th>Amount ($)</th>
            <th>Type</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><input type="text" value="Income"></td>
            <td><input type="number" value="0" class="amount"></td>
            <td>
              <select>
                <option value="income">Income</option>
                <option value="expense">Expense</option>
              </select>
            </td>
            <td><button class="delete-row">🗑️</button></td>
          </tr>
        </tbody>
      </table>

      <button id="addRow">➕ Add Row</button>

      <div class="totals">
        <p><strong>Total Income:</strong> $<span id="totalIncome">0</span></p>
        <p><strong>Total Expenses:</strong> $<span id="totalExpenses">0</span></p>
        <p><strong>Remaining Balance:</strong> $<span id="remainingBalance">0</span></p>
      </div>
    </div>
  `;

  // Spreadsheet functionality
  setTimeout(() => {
    const table = document.querySelector('#budgetTable tbody');
    const addRowBtn = document.getElementById('addRow');

    // Function to calculate totals
    function updateTotals() {
      let totalIncome = 0;
      let totalExpenses = 0;

      document.querySelectorAll('#budgetTable tbody tr').forEach(row => {
        const amount = parseFloat(row.querySelector('.amount')?.value) || 0;
        const type = row.querySelector('select')?.value;

        if (type === 'income') totalIncome += amount;
        else totalExpenses += amount;
      });

      document.getElementById('totalIncome').textContent = totalIncome.toFixed(2);
      document.getElementById('totalExpenses').textContent = totalExpenses.toFixed(2);
      document.getElementById('remainingBalance').textContent = (totalIncome - totalExpenses).toFixed(2);
    }

    // Add new row
    addRowBtn.addEventListener('click', () => {
      const newRow = document.createElement('tr');
      newRow.innerHTML = `
        <td><input type="text" placeholder="Category"></td>
        <td><input type="number" value="0" class="amount"></td>
        <td>
          <select>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
          </select>
        </td>
        <td><button class="delete-row">🗑️</button></td>
      `;
      table.appendChild(newRow);
      updateTotals();
    });

    // Delete row
    table.addEventListener('click', e => {
      if (e.target.classList.contains('delete-row')) {
        e.target.closest('tr').remove();
        updateTotals();
      }
    });

    // Update totals when typing or selecting
    table.addEventListener('input', updateTotals);
    table.addEventListener('change', updateTotals);

    updateTotals();
  }, 0);
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
