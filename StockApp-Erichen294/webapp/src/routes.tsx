import type { RouteObject } from "react-router-dom"
import { StockApp } from "./StockApp"
import { MostActive } from "./MostActive"
import { Symbols } from "./Symbols"
import { Price } from './Price';
import { AverageVolume } from "./AverageVolume";
import { Home } from "./Home"
import { API_URL } from "./constants"

// This object describes the structure of the app: its possible screens, parameters,
// and data loading operations.
export const routes: RouteObject[] = [
  {
    path: "",
    element: <StockApp />,
    // Child routes are given a place to render within the parent via the <Outlet /> component
    children: [
      {
        path: "",
        element: <Home />,
      },
      {
        path: "mostactive",
        element: <MostActive />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          try {
            const response = await fetch(`${API_URL}/mostactive`)
            if (!response.ok) {
              throw new Error("Failed to fetch the most active stock.")
            }
            const data = await response.json()
            return data.mostActiveStock
          } catch (error) {
            console.error("Error loading the most active stock:", error)
            throw error;
          }
        },
        // Provide JSX to show if the data fails to load
        errorElement: <div>Failed to load the most active stock.</div>,
      },
      {
        path: "symbols",
        element: <Symbols />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          try {
            const response = await fetch(`${API_URL}/symbols`)
            if (!response.ok) {
              throw new Error("Failed to fetch symbols.")
            }
            const data = await response.json()
            return data.symbols
          } catch (error) {
            console.error("Error loading symbols:", error)
            throw error
          }
        },
        // Provide JSX to show if the data fails to load
        errorElement: <div>Failed to load symbols</div>,
        // Child routes can continue to be nested
        children: [
          {
            path: ":symbol/price",
            element: <Price />,
            loader: async({params}) => {
              const {symbol} = params
              try {
                const response = await fetch(`${API_URL}/${symbol}/price`)
                if (!response.ok) {
                  throw new Error("Failed to fetch price.")
                }
                const {price} = await response.json()
                return price
              } catch (error) {
                console.error("Error fetching price:", error)
                throw error
              }
            }
          },
          {
            path:":symbol/averagevolume",
            element: <AverageVolume />,
            loader: async({params}) => {
              const {symbol} = params
              try {
                const response = await fetch(`${API_URL}/${symbol}/averagevolume`)
                if (!response.ok) {
                  throw new Error("Failed to fetch average volume.")
                }
                const {volume} = await response.json()
                return volume
              } catch (error) {
                console.error("Error fetching average volume:", error)
                throw error
              }
            }
          }
        ],
      },
    ],
  },
]
