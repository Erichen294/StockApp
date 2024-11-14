import * as React from "react"
import "./StockApp.css"
import { Outlet, useNavigate } from "react-router-dom"
import { Navigation } from "./Navigation"

// StockApp is the component that is mounted at the top of the routing hierarchy.
export const StockApp: React.FC = () => {
  const navigate = useNavigate()

  const goHome = React.useCallback(() => {
    navigate("/")
  }, [navigate])

  return (
    <>
      <header className="nav-header">
        <h1 onClick={goHome}>Stock App</h1>
        <Navigation />
      </header>
      <main id="main">
        {/* 
          Outlet marks the spot that React Router will place the output of 
          the rendered child route, if any.
        */}
        <Outlet />
      </main>
    </>
  )
}
