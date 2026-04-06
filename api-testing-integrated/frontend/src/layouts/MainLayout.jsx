import { NavLink, Outlet } from "react-router-dom";
import "../styles/global.css";

export default function MainLayout() {
  return (
    <div className="appShell">
      <header className="topHeader">
        <h1>QA Automation Dashboard</h1>
        <p>Centralized workspace for API execution and future QA utilities</p>
      </header>

      <nav className="mainNav">
        <NavLink
          to="/api-testing"
          className={({ isActive }) =>
            `navTab ${isActive ? "navTabActive" : ""}`
          }
        >
          API Testing
        </NavLink>

        <NavLink
          to="/reports"
          className={({ isActive }) =>
            `navTab ${isActive ? "navTabActive" : ""}`
          }
        >
          Reports
        </NavLink>

        <NavLink
          to="/settings"
          className={({ isActive }) =>
            `navTab ${isActive ? "navTabActive" : ""}`
          }
        >
          Settings
        </NavLink>
      </nav>

      <main className="mainContent">
        <Outlet />
      </main>
    </div>
  );
}
