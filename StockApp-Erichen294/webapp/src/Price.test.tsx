import { createMemoryRouter, RouterProvider } from "react-router-dom"
import { routes } from "./routes"
import { test, expect, describe } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Price", () => {
    test("renders the price for AAPL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/AAPL/price"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("price")
        expect(priceElement).toHaveTextContent("The current price for AAPL is $150.74")
    })

    test("renders the price for TSLA", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/TSLA/price"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("price")
        expect(priceElement).toHaveTextContent("The current price for TSLA is $170.82")
    })

    test("renders the price for GOOGL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/GOOGL/price"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("price")
        expect(priceElement).toHaveTextContent("The current price for GOOGL is $250.01")
    })

    test("clicking the back link returns to the symbols screen starting from AAPL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/AAPL/price"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })

    test("clicking the back link returns to the symbols screen starting from TSLA", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/TSLA/price"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })

    test("clicking the back link returns to the symbols screen starting from GOOGL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/GOOGL/price"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })
})