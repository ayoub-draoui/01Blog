export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  apiEndpoints: {
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