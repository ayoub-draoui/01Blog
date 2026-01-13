# 01Blog - Social Blogging Platform

A fullstack social blogging platform where students can share their learning journey, follow others, and engage with content.

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security (JWT Authentication)
- Spring Data JPA
- PostgreSQL
- Maven

### Frontend
- Angular 
- Angular Material / Bootstrap

## Features

- ✅ User authentication (Register/Login with JWT)
- ✅ User profiles with avatar, bio, and stats
- ✅ Create posts with images/videos
- ✅ Like and comment on posts
- ✅ Follow/unfollow users
- ✅ Personalized feed (posts from followed users)
- ✅ Notifications
- ✅ Report inappropriate content
- ✅ Admin panel for moderation
- ✅ Dashboard analytics

## How to Run

### Prerequisites
- Java 17+
- PostgreSQL
- Maven

### Backend Setup

1. Clone the repository
2. Create PostgreSQL database: `createdb blogdb`
3. Update `application.properties` 
4. Run: `mvn spring-boot:run`
5. Backend runs on `http://localhost:8080`

### API Documentation
- Auth: `/auth/register`, `/auth/login`
- Posts: `/posts`, `/feed/home`
- Users: `/users/me`, `/users/{id}`
- Admin: `/admin/users`, `/admin/reports`

## Database Schema
- Users (authentication, profiles)
- Posts (blog content with media)
- Subscriptions (follow relationships)
- Likes & Comments (engagement)
- Notifications (user alerts)
- Reports (moderation)

## Security
- JWT-based authentication
- Role-based access control (USER/ADMIN)
- Password encryption (BCrypt)
- Protected routes
<!-- -->