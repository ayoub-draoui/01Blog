# Angular Server-Side Rendering (SSR)

This README explains **what SSR is**, **why it exists**, **how Angular SSR works**, and **how to configure and debug it**—with a focus on Angular **v21** and real-world issues like **CORS**, **API integration**, and **hydration**.

---

## 1. What is Server-Side Rendering (SSR)?

**Server-Side Rendering** means your Angular app is **rendered on the server first**, then sent to the browser as **fully formed HTML**.

Instead of:
```
Browser → JS bundle → render HTML
```

You get:
```
Browser ← HTML (already rendered) ← Server
```

After that, Angular runs in the browser and **hydrates** the page (attaches event listeners and state).

---

## 2. Why Use SSR?

### ✅ Benefits
- **Faster first page load** (especially on slow devices)
- **Better SEO** (search engines see real HTML)
- **Better social sharing previews**
- **Improved perceived performance**

### ❌ Costs
- More complex setup
- You must separate **server-safe code** from **browser-only code**
- Debugging is harder

---

## 3. How Angular SSR Works (High Level)

Angular SSR uses **Node.js + Express** under the hood.

Flow:
1. Browser requests a page
2. Node.js server runs Angular
3. Angular renders components to HTML
4. HTML is sent to browser
5. Browser hydrates the app

---

## 4. Key Concepts You MUST Know

### 4.1 Two Environments Exist

| Environment | Has DOM? | Has window? | Has document? |
|------------|---------|-------------|---------------|
| Server     | ❌ No   | ❌ No       | ❌ No         |
| Browser    | ✅ Yes  | ✅ Yes      | ✅ Yes        |

**Rule:**
> If you touch `window`, `document`, `localStorage`, or `sessionStorage`, you MUST guard it.

---

### 4.2 Platform Detection

Use Angular helpers:

- `isPlatformBrowser()`
- `isPlatformServer()`

Example:
```ts
if (isPlatformBrowser(this.platformId)) {
  localStorage.setItem('token', value);
}
```

---

### 4.3 Hydration

Hydration means:
> Angular takes over the server-rendered HTML and makes it interactive.

If hydration fails, you’ll see:
- Duplicate DOM
- Console warnings
- Broken events

---

## 5. Angular SSR Configuration Explained

### Your Server Config File

```ts
import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';
```

### Line-by-Line Explanation

#### `mergeApplicationConfig`
- Merges **client config** and **server-only config**
- Prevents duplication

#### `ApplicationConfig`
- Angular’s new standalone app configuration
- Replaces `NgModule`

#### `provideServerRendering()`
- Enables SSR mode
- Tells Angular: "You are running on the server"

#### `withRoutes(serverRoutes)`
- Allows **server-only routes**
- Used for redirects, pre-rendering, or auth logic

---

### Final Config Object

```ts
const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes))
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
```

✅ Result:
- Client app runs normally in browser
- Server app runs in Node.js

---

## 6. SSR + Backend Communication

### ❗ Important Rule

**The browser talks to APIs → NOT the server** (unless explicitly coded).

### Correct Setup

```
Browser → Angular → API (HTTP)
```

SSR only renders HTML — it should NOT:
- Use cookies unsafely
- Access browser storage
- Depend on user-specific tokens

---

## 7. CORS & SSR (Your Error Explained)

### Error
```
Not allowed to define cross-origin object as property on XrayWrapper
```

### Cause
- Mixing browser objects (`window`, `document`, `localStorage`)
- Cross-origin API calls without proper headers

### Backend MUST Send These Headers

```http
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET,POST,PUT,DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
```

### Express Example

```js
app.use(cors({
  origin: 'http://localhost:4200',
  credentials: true
}));
```

---

## 8. Auth in SSR (CRITICAL)

### ❌ What NOT To Do

```ts
localStorage.getItem('token'); // ❌ breaks SSR
```

### ✅ Correct Way

- Use **HttpOnly cookies**
- Or check auth only in browser

---

## 9. SSR Best Practices

- ✅ Keep services **platform-aware**
- ✅ Avoid side effects during render
- ✅ Keep server logic stateless
- ❌ Never access DOM directly
- ❌ Never assume browser APIs exist

---

## 10. When Should You Use SSR?

| Project Type | SSR? |
|-------------|------|
| Blog        | ✅ Yes |
| Landing Page| ✅ Yes |
| Dashboard   | ⚠️ Maybe |
| Admin Panel | ❌ No |

---

## 11. Summary

SSR:
- Renders Angular on the server
- Improves SEO & performance
- Requires discipline & clean separation

**Rule of Thumb:**
> If it touches the browser → guard it.

---

## 12. Next Steps

- Fix CORS correctly
- Separate auth logic
- Validate hydration
- Connect frontend to backend cleanly

---

✌️ You’re doing the right thing continuing with Angular 21.
SSR is hard — but you’re on the correct path.

