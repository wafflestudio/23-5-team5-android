# 🎓 University Student Team Recruitment Platform

Wafflestudio 23.5기 토이프로젝트 by team 5
A modern, mobile-first web application designed for university students to find and recruit team members for study groups, civil service exam preparation, job hunting, and extracurricular activities.

## ✨ Features

### 📋 Post Management
- **Browse & Search** - Discover recruitment posts filtered by category
- **Detailed Posts** - View comprehensive information including field, date, location, and description
- **Create Posts** - Share your team recruitment needs with other students
- **Join Teams** - Participate in posts with a single click
- **Manage Posts** - Close or delete your own posts, view participant lists

### 👤 User Profiles
- **Custom Profiles** - Upload profile images and set personalized nicknames
- **Edit Anytime** - Update your nickname and profile picture whenever needed
- **Participant Info** - View contact details of team members (nickname & email)

### 🔐 Authentication
- **Secure Sign Up** - Three-step registration process (email → password → nickname)
- **Simple Login** - Email and password authentication
- **Session Management** - Secure logout functionality

### 📱 Mobile-Optimized
- **Bottom Navigation** - Easy thumb-reach navigation with 3 main tabs
- **Responsive Design** - Works seamlessly on all device sizes
- **Touch-Friendly** - Large tap targets and smooth interactions

## 🎨 Categories

Posts are organized into four main categories:

- **스터디 (Study Groups)** - Academic study sessions and peer learning
- **고시 (Civil Service)** - Government exam preparation groups
- **취준 (Job Prep)** - Career preparation and job hunting teams
- **대외활동 (Extracurricular)** - Clubs, competitions, and activities

## 🛠️ Tech Stack

- **Framework**: 
- **Styling**: 
- **Icons**: 
- **State Management**: 
- **Code Quality**: 

## 📁 Project Structure


## 🚀 Getting Started

This project is currently a **frontend-only implementation** using mock data. It demonstrates the complete user flow and UI/UX without requiring a backend server.

### Current Implementation
- ✅ Full UI/UX implementation
- ✅ All user interactions working
- ✅ Mock authentication system
- ✅ Local state management
- ⏳ Backend integration (planned)

### For Developers

If you want to extend this project:

1. **Review the code structure** in `/components`
2. **Check type definitions** in `/types/index.ts`
3. **See mock data examples** in `/data/mockData.ts`
4. **Read the detailed guide** in `PROJECT_GUIDE.md`

## 📸 Screenshots

> Add screenshots here to showcase your app's interface

## 🔮 Future Roadmap

### Backend Integration
- [ ] Connect to Supabase/Firebase or custom backend
- [ ] Real-time database for posts and users
- [ ] Image storage solution (AWS S3, Cloudinary)
- [ ] JWT-based authentication

### Enhanced Features
- [ ] Real-time notifications for new posts
- [ ] Direct messaging between participants
- [ ] Advanced search with tags and filters
- [ ] Email verification system
- [ ] Social login (Google, Kakao)
- [ ] PWA support for mobile installation

### UI/UX Improvements
- [ ] Dark mode support
- [ ] Post templates
- [ ] Image galleries for posts
- [ ] User ratings and reviews
- [ ] Bookmarking favorite posts

## 💾 Database Design

The app is designed to work with the following database schema:

### Tables
- **users** - User accounts with credentials and profiles
- **posts** - Recruitment announcements
- **post_participants** - Junction table for user-post relationships

See `AGENTS.md` for complete SQL schema definitions.

## 🎯 Use Cases

This platform is perfect for:

- 📚 **Study Groups** - Find classmates for exam preparation
- 💼 **Project Teams** - Recruit members for school projects
- 🏆 **Competition Teams** - Form teams for hackathons and contests
- 🎭 **Club Activities** - Organize university club events
- 📖 **Language Exchange** - Connect with language learning partners

## 🤝 Contributing

Contributions are welcome! This is a learning project and improvements are appreciated.

### Areas for Contribution
- Backend implementation (Node.js, Python, Go)
- Additional features (chat, notifications)
- UI/UX enhancements
- Internationalization (i18n)
- Testing (Jest, React Testing Library)
- Documentation improvements

## 📝 License

This project is open source. Please add your preferred license.

## 👥 Target Audience

- University students looking for team members
- Study group organizers
- Project team leaders
- Campus activity coordinators

## 📞 Contact & Support

For questions or suggestions, please open an issue in this repository.

---

**Note**: This is a demo/portfolio project showcasing Android development practices. The current version uses mock data and is ready for backend integration.

**Built with** ❤️ **for university students**
