import { createRoot } from "react-dom/client"
import { enableMocking } from "./mocks/enable.ts"
import "./index.css"
import { createBrowserRouter, RouterProvider } from "react-router-dom"
import { routes } from "./routes.tsx"

const router = createBrowserRouter(routes)

// If we're running with mock data enabled, setting that up is asynchronous,
// so we delay setting up the app until that's done.
enableMocking().then(() => {
  // createRoot and render are the core entry points for React
  createRoot(document.getElementById("root")!).render(<RouterProvider router={router} />)
})
