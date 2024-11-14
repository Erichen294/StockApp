import { http, HttpResponse } from "msw"
import { API_URL } from "../constants"

export const handlers = [
  http.get(`${API_URL}/symbols`, () => {
    return HttpResponse.json({
      symbols: ["AAPL", "TSLA", "GOOGL"],
    })
  }),
  http.get(`${API_URL}/mostactive`, () => {
    return HttpResponse.json({
      mostActiveStock: ["TSLA"],
    })
  }),
  http.get(`${API_URL}/:symbol/price`, (req) => {
    const {symbol} = req.params
    const prices = {
      AAPL: 150.74,
      TSLA: 170.82,
      GOOGL: 250.01,
    }
    const price = prices[symbol as keyof typeof prices] || 0

    return HttpResponse.json({
      symbol, 
      price,
    })
  }),
  http.get(`${API_URL}/:symbol/averagevolume`, (req) => {
    const {symbol} = req.params
    const volumes = {
      AAPL: 20.78,
      TSLA: 75.25,
      GOOGL: 250.25,
    }
    const volume = volumes[symbol as keyof typeof volumes] || 0

    return HttpResponse.json({
      symbol,
      volume
    })
  }),
]
