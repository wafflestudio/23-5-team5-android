# University Student Team Recruitment Platform - Project Guide

## Project Overview

A mobile web application for university students to recruit team members. Users can post and join recruitment announcements for various categories including study groups, civil service exam prep, job preparation, and extracurricular activities.

## Tech Stack

- **Frontend**: 
- **Styling**: 
- **Icons**: 
- **State Management**: 
- **Data Storage**: Currently Mock Data (frontend-only)

## Key Features

### 1. Authentication System
- **Sign Up**: 3-step process (Email verification → Password setup → Nickname setup)
- **Login**: Email/Password authentication
- **Logout**: Session termination and redirect to login screen

### 2. Post Management
- **Browse Posts**: Category filtering (All/Study/Civil Service/Job Prep/Extracurricular)
- **Search Posts**: Search by title, description, and field
- **Create Post**: Input title, category, field, date, location, description
- **Join Post**: Join/Cancel participation
- **Close Post**: Only author can close posts
- **Delete Post**: Only author can delete posts
- **Auto Filtering**: Closed posts are automatically excluded from list

### 3. My Page
- **Profile Management**:
    - Change profile image (file upload)
    - Change nickname
    - View email (read-only)
- **My Posts**: View participants list (nickname, email display)

### 4. Navigation
- **3 Tab Structure**:
    1. Home (Post list)
    2. My Posts (Manage created posts)
    3. My Page (Profile settings)

## File Structure


## Database Schema (For Backend Implementation)

### users table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(100) NOT NULL,
  profile_image TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

### posts table
```sql
CREATE TABLE posts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(50) NOT NULL,
  field VARCHAR(100),
  date VARCHAR(100),
  location VARCHAR(255),
  is_closed BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

### post_participants table (junction table)
```sql
CREATE TABLE post_participants (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(post_id, user_id)
);
```

## Key Business Logic

### Post List Filtering
```typescript
// Show only non-closed posts
const activePosts = posts.filter(post => !post.isClosed);

// Category filter
const filteredPosts = activePosts.filter(post => 
  selectedCategory === '전체' || post.category === selectedCategory
);

// Search
const searchedPosts = filteredPosts.filter(post =>
  post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
  post.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
  (post.field && post.field.toLowerCase().includes(searchQuery.toLowerCase()))
);
```

### Join/Cancel Post Participation
```typescript
const handleParticipate = (postId: string) => {
  setPosts(posts.map(post => {
    if (post.id === postId) {
      const isParticipating = post.participants.some(p => p.userId === currentUser.id);
      
      if (isParticipating) {
        // Cancel participation
        return {
          ...post,
          participants: post.participants.filter(p => p.userId !== currentUser.id)
        };
      } else {
        // Join
        return {
          ...post,
          participants: [...post.participants, {
            userId: currentUser.id,
            nickname: currentUser.nickname,
            email: currentUser.email
          }]
        };
      }
    }
    return post;
  }));
};
```

### Close Post
```typescript
const handleClosePost = (postId: string) => {
  setPosts(posts.map(post => 
    post.id === postId ? { ...post, isClosed: true } : post
  ));
};
```

## API Endpoints (Required for Backend Implementation)

### Authentication
- `POST /api/auth/signup` - Sign up
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### Users
- `GET /api/users/me` - Get current user info
- `PATCH /api/users/me` - Update profile (nickname, profile image)
- `POST /api/users/upload-image` - Upload profile image

### Posts
- `GET /api/posts` - Get post list (query: category, search, isClosed=false)
- `GET /api/posts/:id` - Get post detail
- `POST /api/posts` - Create post
- `PATCH /api/posts/:id` - Update post
- `DELETE /api/posts/:id` - Delete post
- `PATCH /api/posts/:id/close` - Close post
- `GET /api/posts/my` - Get my created posts

### Participation
- `POST /api/posts/:id/participate` - Join post
- `DELETE /api/posts/:id/participate` - Cancel participation
- `GET /api/posts/:id/participants` - Get participants list

## State Management


## UI/UX Features

### Mobile Optimization
- Fixed bottom navigation bar
- Touch-friendly button sizes
- Responsive layout

### Design System
- **Primary Color**: Blue (#3B82F6)
- **Category Colors**:
    - Study (스터디): Blue
    - Civil Service (고시): Green
    - Job Prep (취준): Purple
    - Extracurricular (대외활동): Orange

### Animations
- Smooth hover effects
- Modal fade in/out
- Button transitions

## Security Considerations (For Backend Implementation)

1. **Passwords**: Hash with bcrypt before storing
2. **Authentication**: Use JWT tokens, httpOnly cookies recommended
3. **CORS**: Allow only frontend domain
4. **SQL Injection**: Use prepared statements
5. **XSS**: Sanitize user inputs
6. **File Upload**: Validate file type and size

## Development Guide

### Adding New Features


### Styling


### Icons


## Future Improvements

1. **Backend Integration**: Supabase, Firebase, or custom Node.js server
2. **Real-time Features**: WebSocket for new post notifications
3. **Chat Feature**: Messaging between participants
4. **Notification System**: Join requests, post closure alerts
5. **Advanced Search**: Tags, date range filters
6. **Image Optimization**: Image compression, CDN usage
7. **PWA**: Offline support, push notifications
8. **Social Login**: Google, Kakao login

## Troubleshooting

### Common Issues
- **Login not persisting**: Currently resets on refresh → Need localStorage or backend session
- **Image Upload**: Currently base64 encoding → Need file storage when backend is connected
- **Search Performance**: May slow down with large lists → Need server-side search, pagination

## Category Values

The app uses the following Korean category values:
- `'스터디'` - Study groups
- `'고시'` - Civil service exam preparation
- `'취준'` - Job preparation
- `'대외활동'` - Extracurricular activities
- `'전체'` - All (for filtering)

These values are displayed in Korean in the UI but can be translated or localized as needed.

## Notes for AI Agents

### Current Implementation Status
- ✅ Full frontend implementation with mock data
- ✅ All CRUD operations working locally
- ✅ Profile image upload (file to base64)
- ✅ Nickname editing functionality
- ✅ Post closing and deletion features
- ⏳ Backend integration pending
- ⏳ Real database connection pending

### When Modifying the Code


### Important Files to Review Before Changes


## License and Contribution

Project license and contribution guidelines should be defined separately.

---

**Last Updated**: January 9, 2026
**Version**: v1.0.0
