import * as React from "react"
import { Link } from "react-router-dom"

export const Navigation: React.FC = () => {
  return (
    <nav>
      <ul>
        <li>
          <Link to="/symbols">Symbols</Link>
        </li>
        <li>
          <Link to="/mostactive">Most Active</Link>
        </li>
      </ul>
    </nav>
  )
}
