import React from "react";
import { useLoaderData, useParams, Link} from "react-router-dom";

export const AverageVolume: React.FC = () => {
  const volume = useLoaderData() as number;

  const {symbol} = useParams<{symbol: string}>()

  // Format the volume with up to two decimal places
  const formattedVolume = new Intl.NumberFormat('en-US', {
    maximumFractionDigits: 2,
  }).format(volume);

  return (
    <div>
      <h2>Average Volume</h2>
      <p data-testid="volume">
        The average volume for <strong>{symbol}</strong> is <strong>{formattedVolume}</strong>
      </p>
      <Link to="/symbols" data-testid="back-link to symbols">Back to Symbols</Link>
    </div>
  );
};
