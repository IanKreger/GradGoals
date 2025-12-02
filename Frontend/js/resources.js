
 if (currentPage.toLowerCase().includes("resources")) {
    const infographicsImage1 = './images/creditpayoff_infographics1.jpg';
    const content = document.querySelector('#content');

    const image1 = document.createElement('img');
    image1.setAttribute('src', infographicsImage1);
    image1.setAttribute('width', '320px')

    const div1 = document.createElement('div')    
    const heading1 = document.createElement('h3')
    heading1.textContent = "Credit Card Payoff"
    div1.appendChild(heading1)  
    div1.appendChild(image1) 


    const div2 = document.createElement('div')
    div2.style.marginTop = '100px'
    const heading2 = document.createElement('h3');
    heading2.textContent = "Student Loan Payoff"
    const image2 = document.createElement('img')
    image2.setAttribute('src', './images/student_loan_payoff2.png')
    image2.setAttribute('width', '320px');
    div2.appendChild(heading2)
    div2.appendChild(image2)
    content.appendChild(div1);  
    content.appendChild(div2)
 }
