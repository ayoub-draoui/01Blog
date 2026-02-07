# File Tree: backend

**Generated:** 2/5/2026, 11:30:49 AM
**Root Path:** `/home/adraoui/Desktop/01Blog/backend`

```
├── 📁 .mvn
│   └── 📁 wrapper
│       └── 📄 maven-wrapper.properties
├── 📁 src
│   ├── 📁 main
│   │   ├── 📁 java
│   │   │   └── 📁 _blog
│   │   │       └── 📁 demo
│   │   │           ├── 📁 apiDocs
│   │   │           │   └── ☕ Api.java
│   │   │           ├── 📁 controllers
│   │   │           │   ├── ☕ AdminController.java
│   │   │           │   ├── ☕ AuthController.java
│   │   │           │   ├── ☕ CommentController.java
│   │   │           │   ├── ☕ FeedController.java
│   │   │           │   ├── ☕ FileController.java
│   │   │           │   ├── ☕ LikeController.java
│   │   │           │   ├── ☕ NotificationController.java
│   │   │           │   ├── ☕ PostController.java
│   │   │           │   ├── ☕ ReportController.java
│   │   │           │   ├── ☕ SubscriptionController.java
│   │   │           │   └── ☕ UserController.java
│   │   │           ├── 📁 dto
│   │   │           │   ├── ☕ AuthResponse.java
│   │   │           │   ├── ☕ ChangePasswordRequest.java
│   │   │           │   ├── ☕ CommentCreateRequest.java
│   │   │           │   ├── ☕ CommentResponse.java
│   │   │           │   ├── ☕ CommentUpdateRequest.java
│   │   │           │   ├── ☕ ErrorResponse.java
│   │   │           │   ├── ☕ LoginRequest.java
│   │   │           │   ├── ☕ NotificationResponse.java
│   │   │           │   ├── ☕ PostResponse.java
│   │   │           │   ├── ☕ PostUpdateRequest.java
│   │   │           │   ├── ☕ RegisterRequest.java
│   │   │           │   ├── ☕ ReportPostRequest.java
│   │   │           │   ├── ☕ ReportResponse.java
│   │   │           │   ├── ☕ ReportUReq.java
│   │   │           │   ├── ☕ UpdateProfileRequest.java
│   │   │           │   ├── ☕ UpdateReportRequest.java
│   │   │           │   └── ☕ UserProfileResponse.java
│   │   │           ├── 📁 exceptions
│   │   │           │   ├── ☕ BadRequest.java
│   │   │           │   ├── ☕ GlobalExceptionHandler.java
│   │   │           │   ├── ☕ InvalidCredentialsException.java
│   │   │           │   ├── ☕ ResourceNotFoundException.java
│   │   │           │   ├── ☕ UnauthorizedException.java
│   │   │           │   └── ☕ UserAlreadyExistsException.java
│   │   │           ├── 📁 model
│   │   │           │   ├── ☕ Comment.java
│   │   │           │   ├── ☕ Like.java
│   │   │           │   ├── ☕ Notification.java
│   │   │           │   ├── ☕ NotificationType.java
│   │   │           │   ├── ☕ Post.java
│   │   │           │   ├── ☕ Report.java
│   │   │           │   ├── ☕ ReportStatus.java
│   │   │           │   ├── ☕ ReportType.java
│   │   │           │   ├── ☕ Role.java
│   │   │           │   ├── ☕ Subscription.java
│   │   │           │   └── ☕ User.java
│   │   │           ├── 📁 repository
│   │   │           │   ├── ☕ CommentRepository.java
│   │   │           │   ├── ☕ LikeRepository.java
│   │   │           │   ├── ☕ NotificationRepository.java
│   │   │           │   ├── ☕ PostRepository.java
│   │   │           │   ├── ☕ ReportRepository.java
│   │   │           │   ├── ☕ SubscriptionRepository.java
│   │   │           │   └── ☕ UserRepository.java
│   │   │           ├── 📁 security
│   │   │           │   ├── ☕ CustomUserDetails.java
│   │   │           │   ├── ☕ CustomUserDetailsService.java
│   │   │           │   ├── ☕ JwtFilter.java
│   │   │           │   ├── ☕ JwtProperty.java
│   │   │           │   ├── ☕ JwtUtil.java
│   │   │           │   └── ☕ SecurityConfig.java
│   │   │           ├── 📁 service
│   │   │           │   ├── ☕ AdminService.java
│   │   │           │   ├── ☕ AuthService.java
│   │   │           │   ├── ☕ CommentService.java
│   │   │           │   ├── ☕ FeedService.java
│   │   │           │   ├── ☕ FileStorageService.java
│   │   │           │   ├── ☕ LikeService.java
│   │   │           │   ├── ☕ NotificationService.java
│   │   │           │   ├── ☕ PostService.java
│   │   │           │   ├── ☕ ReportService.java
│   │   │           │   ├── ☕ SubscriptionService.java
│   │   │           │   └── ☕ UserService.java
│   │   │           └── ☕ DemoApplication.java
│   │   └── 📁 resources
│   │       ├── 📄 application.properties
│   │       └── ⚙️ application.yml
│   └── 📁 test
│       └── 📁 java
│           └── 📁 _blog
│               └── 📁 demo
│                   └── ☕ DemoApplicationTests.java
├── 📁 uploads
│   ├── 🖼️ 0e978a9e-fa9a-4977-9d4b-2507986061de.jpg
│   ├── 🖼️ 1b663a37-29d3-4762-bbf4-4897e45b96a6.jpeg
│   ├── 🖼️ 2a40b05f-ae08-4ede-94d8-7afa25fef3ed.jpeg
│   ├── 🖼️ 3c0ba6d1-f6af-4023-854d-f8bb84365124.jpeg
│   ├── 🎬 3f15cf80-4835-435c-8501-b086b16ec4f2.mp4
│   ├── 🖼️ 4c12246f-a6c8-4249-a126-658c9590d146.jpg
│   ├── 🖼️ 6332a878-2736-46b3-943e-89c94024765c.png
│   ├── 🖼️ 66ce7a17-eb2e-4919-83ab-75cc88f7fc87.jpeg
│   ├── 🖼️ 67e8d87e-d050-4c54-bdba-c720b40caae4.jpg
│   ├── 🖼️ 6af34382-b663-4b3c-bae9-6b9608b8623e.jpg
│   ├── 🎬 7fdb8d4f-1fd2-49af-b7d0-b0246c43e674.mp4
│   ├── 🖼️ a1476408-f7ae-4730-87ac-ff0190e82eb6.png
│   ├── 🖼️ ba758caf-08cf-4700-a3a7-6bdbcd41013d.jpg
│   ├── 🖼️ c0803d55-5608-4b4f-9a81-dbbc450c387d.jpeg
│   └── 🖼️ default_image.jpg
├── ⚙️ .gitattributes
├── ⚙️ .gitignore
├── 📄 mvnw
├── 📄 mvnw.cmd
├── ⚙️ pom.xml
├── 📝 readme.md
└── 📄 run.sh
```

---
*Generated by FileTree Pro Extension*