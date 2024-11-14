import React from "react";
import { useLoaderData, useParams, Link} from "react-router-dom";

export const Price: React.FC = () => {
  const price = useLoaderData() as number;

  const {symbol} = useParams<{symbol: string}>()

  // Format price as USD
  const formattedPrice = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price)

  return (
    <div>
      <h2>Price</h2>
      <p data-testid="price">
        The current price for <strong>{symbol}</strong> is <strong>{formattedPrice}</strong>
      </p>
      <Link to="/symbols" data-testid="back-link to symbols">Back to Symbols</Link>
    </div>
  );
};
