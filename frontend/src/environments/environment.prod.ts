export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com',
  apiEndpoints: {
    // Same structure as above
    auth: {
      login: '/auth/login',
      register: '/auth/register',
    },
    users: {
      me: '/users/me',
      profile: '/users',
    },
    posts: {
      base: '/posts',
      feed: '/feed/home',
    },
    admin: {
      base: '/admin',
    }
  }
};