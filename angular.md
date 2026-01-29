# 🧠 Angular Frontend Request Lifecycle (Mental Model)

This README is a **mental model** for understanding how Angular components, HTTP requests, signals, SSR, and the browser all work together — without magic, reloads, or infinite loops.

---

## 1️⃣ What happens when a page is loaded?

Angular applications go through **two distinct phases** when using SSR:

* **Server-side rendering (SSR)**
* **Client-side hydration (Browser takeover)**

These are **not loops** — they are **two different runtimes**.

---

## 2️⃣ Phase 1 — Server-Side Rendering (SSR)

This happens on **Node.js**, before the browser sees anything.

### Timeline

1. Browser requests `/home`
2. Node.js server receives the request
3. Angular **server app** starts
4. `HomeComponent` instance is created
5. `inject()` resolves services (DI)
6. Signals are created (empty initial state)
7. `ngOnInit()` runs **on the server**
8. HTTP request is sent to the backend
9. Backend responds with data
10. Signals are updated with response
11. Angular renders **FULL HTML** (posts included)
12. Server sends HTML to the browser

✅ At this point:

* Backend **has already been contacted**
* HTML already contains real data
* No browser JS has run yet

---

## 3️⃣ Phase 2 — Browser Hydration (Client-Side)

This happens **after HTML arrives** in the browser.

### Timeline

13. Browser paints HTML instantly
14. Angular **client app** boots
15. `HomeComponent` is created AGAIN (new instance)
16. `inject()` runs again
17. Signals are created AGAIN (empty by default)
18. Angular **hydrates** existing DOM
19. `ngOnInit()` runs AGAIN (browser-side)

🚨 Important:

* Angular logic **does run in the browser**
* This is not optional
* This is how the app becomes interactive

---

## 4️⃣ Why this does NOT cause an infinite loop

### Key rule

> **Lifecycle hooks run on lifecycle events, NOT on state changes**

### `ngOnInit()` runs ONLY when:

* A component instance is created
* A route changes
* A component is destroyed & recreated (`*ngIf`, routing)

### `ngOnInit()` does NOT run when:

* A signal changes
* An observable emits
* The DOM updates
* A user clicks a button

Signals update the **view**, not the **lifecycle**.

---

## 5️⃣ Signals vs Lifecycle (Critical distinction)

### Signals

* Hold reactive state
* Trigger **template re-rendering**
* Do NOT trigger lifecycle hooks

```ts
this.posts.set(newPosts); // UI updates only
```

### Lifecycle hooks

* Control component creation/destruction
* Run once per component instance

```ts
ngOnInit() { this.loadPosts(); }
```

➡️ These systems are **intentionally separated**.

---

## 6️⃣ Why Angular does NOT auto-loop

Angular internally separates concerns:

| System               | Responsibility              |
| -------------------- | --------------------------- |
| Lifecycle            | Create / destroy components |
| Reactivity (signals) | Track state changes         |
| Rendering            | Patch the DOM               |

None of these systems recursively call each other.

Infinite loops only happen if **YOU create one**.

---

## 7️⃣ The Double Request Problem (SSR gotcha)

Without protection:

```
SERVER ngOnInit → HTTP
BROWSER ngOnInit → HTTP again
```

This is **not an infinite loop**, but it **is redundant**.

### Solution

Use `TransferState` to reuse server-fetched data in the browser.

---

## 8️⃣ Navigation & Component Recreation

```ts
this.router.navigate(['/profile']);
```

What happens:

1. Current component is destroyed
2. New component instance is created
3. `ngOnInit()` runs
4. New HTTP requests happen

✔ No page reload
✔ No full refresh
✔ Clean SPA behavior

---

## 9️⃣ Final Mental Model (Remember This)

```
Component created
→ ngOnInit() runs ONCE
→ HTTP request sent
→ Signals update
→ UI re-renders
→ Component lives
→ Component destroyed (route change)
```

SSR simply means:

```
Server runs this once
Browser runs it once
```

Not a loop — a handoff.

---

## 🔑 One-sentence truth

> **Signals change views, lifecycle hooks change structure. Angular never mixes the two.**

---

 