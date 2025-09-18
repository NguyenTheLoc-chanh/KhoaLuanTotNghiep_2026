import { createBrowserRouter } from "react-router-dom"
import Home from "./pages/home/home"
import NotFound from "./pages/404"
import Dashboard from "./pages/admin/Dashboard"

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "*",
    element: <NotFound />,
  },
  {
    path: "/admin",
    element: <Dashboard />,
  },
])

export default router