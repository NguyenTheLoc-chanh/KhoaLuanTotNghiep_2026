import HomePage from "./pages/home";
import { createBrowserRouter } from "react-router-dom";
import NotFound from "./pages/404";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: <HomePage />,
    },
    {
      path: "*",    
      element: <NotFound />,
    }
  ],
  { basename: getBasePath() }
);

export function getBasePath() {
  const urlParams = new URLSearchParams(window.location.search);
  const appEnv = urlParams.get("env");

  if (
    import.meta.env.PROD ||
    appEnv === "TESTING_LOCAL" ||
    appEnv === "TESTING" ||
    appEnv === "DEVELOPMENT"
  ) {
    return `/apps/${window.APP_ID}`;
  }

  return window.BASE_PATH || "";
}

export default router;