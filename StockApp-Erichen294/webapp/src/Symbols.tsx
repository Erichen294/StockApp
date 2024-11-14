import React from "react"
import { useLoaderData, Link, Outlet} from "react-router-dom"

export const Symbols: React.FC = () => {
  // Get the symbols as a string array from the data returned by the loader
  const symbols = useLoaderData() as string[]
  // Display a header identifying this as the "Symbols" page
  // and show a list of the symbols returned by the web service.
  // For each symbol, provide a link to its price and average volume sub-views.
  return (
    <div>
      <h2>Symbols</h2>
      <ul>
        {symbols.map((symbol => (
          <li key={symbol} data-testid={`${symbol}`}>{symbol}
            <p>
            <Link to={`${symbol}/price`} data-testid={`link-to-price-${symbol}`}>Price </Link>
            <Link to={`${symbol}/averagevolume`} data-testid={`link-to-volume-${symbol}`}>Volume</Link>
            </p>
          </li>
        )))}
      </ul>
      <Link to="/" data-testid="back-link">Back to home page</Link>
      <Outlet /> {/* This renders the child route component */}
    </div>
  )
}
