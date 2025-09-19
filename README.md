# Nighty Relief Application  

Nighty Relief is a ScalaFX-based application designed to streamline aid distribution and support sustainable agriculture, aligning with **United Nations SDG Goal 2: Zero Hunger**. The platform connects **recipients, farmers, donors, supervisors, and administrators** in a secure and transparent ecosystem to ensure aid reaches those who need it most.  

## 🌍 Background  

Food insecurity has risen in Malaysia following the COVID-19 pandemic, with families struggling due to stagnant incomes and rising food prices. Farmers face additional pressure from modernization gaps. Inspired by Pakistan’s **Ehsaas Program (2019–2023)**, Nighty Relief expands the concept by adding:  

- Direct donor contributions  
- Integrated farmer support (grants, aid requests)  
- Supervisor-level oversight for rural coordination  
- Secure, role-based digital management of aid and users  

---

## ✨ Key Features  

- **Role-Based Secure Login**: Separate login flows for recipients, farmers, donors, supervisors, and admins.  
- **Recipient Dashboard**: View aid history, confirm received packages, and access personal details.  
- **Farmer Dashboard**: Apply for grants, request support, and contribute back to the community.  
- **Donor Dashboard**: Seamlessly donate cash/aid, generate receipts, and track contributions.  
- **Admin Dashboard**: Manage users, donations, aid distribution, and oversee the system.  
- **Supervisor Tools**: Regional oversight of aid delivery and farmer engagement.  
- **Data Visualization**: Interactive graphs from official Malaysian government statistics and system data.  
- **File & Menu System**: Shortcuts for navigation (Alt+F, Ctrl+W, etc.), user management, and help screens.  

---

## 🖥️ System Functionality  

1. **Welcome Page**  
   - Role-based login options (recipient, farmer, donor, admin).  

2. **Login & Sign-Up**  
   - Input validation, error handling, password recovery.  
   - Secure registration flows by user type.  

3. **Dashboards**  
   - **Recipient**: Manage personal info, track and confirm aid packages.  
   - **Farmer**: Request and track grants, donate to others.  
   - **Donor**: Submit donations, generate receipts, and track history.  
   - **Admin**: Add, edit, delete users; manage aid; oversee system performance.  
   - **Supervisor**: Oversee aid coordination in rural regions.  

4. **Visualization Module**  
   - Line, pie, and bar charts for aid impact and distribution stats.  

5. **File Menu & Shortcuts**  
   - Logout, Quit, New User, Delete, Edit, Help (About, Impact, Contact).  

---

## 📐 UML & Architecture  

- Built with **ScalaFX GUI**.  
- Applies **OOP principles**:  
  - **Inheritance**: User roles extend from a base `User` class.  
  - **Polymorphism**: Unified login and view controllers.  
  - **Abstract Classes & Generics**: Enforce structure and reduce redundancy.  
- **MVC Pattern**: Clear separation of model, view, and controller for maintainability.  

---

## 🎥 Demo  
 
- **Demo Video**: [YouTube Link](https://youtu.be/FwI86wIAxjg)  

---

## 📝 Personal Reflection  

> Building this project taught me persistence, problem-solving, and the importance of solid software fundamentals. Applying OOP concepts like inheritance, polymorphism, abstraction, and generics allowed me to create maintainable and scalable code. Challenges such as ScalaFX handling, database integration, and chart visualization were overcome through practice, research, and iteration.  

---

## 📚 References  

1. [Government of Malaysia – Poverty Data](https://data.gov.my/data-catalogue/hh_poverty)  
2. [iMoney – Malaysia’s Food Inflation](https://www.imoney.my/articles/malaysias-food-inflation)  
3. [Ehsaas 8171 Program Analysis](https://trendygh.com/pakistans-ehsaas-8171-program-a-comprehensive-analysis/)  
4. [The Malaysian Reserve – Agriculture Modernisation Gap](https://themalaysianreserve.com/2024/10/14/agriculture-industry-suffers-from-modernisation-gap/)  
5. [Flaticon – Vector Icons](https://www.flaticon.com/)  

---

## 🚀 Future Improvements  

- Enhanced **database integration** for real-time analytics.  
- Mobile-friendly app deployment.  
- Advanced supervisor coordination tools.  
- AI-assisted donor–recipient matching for optimized aid distribution.  
