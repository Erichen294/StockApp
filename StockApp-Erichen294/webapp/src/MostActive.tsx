import * as React from "react"
import { useLoaderData, Link} from "react-router-dom"

export const MostActive: React.FC = () => {
  // Get the symbol value returned by the route's loader as a string
  const sym = useLoaderData() as string
  // Show a header that says "Most Active" followed by the symbol returned
  // by the web service.
  return (
    <div>
    <h2>Most Active</h2> 
    <div data-testid="most-active-symbol">{sym}</div>
    <Link to="/" data-testid="back-link">Back to home page</Link>
    </div>
  )
}
