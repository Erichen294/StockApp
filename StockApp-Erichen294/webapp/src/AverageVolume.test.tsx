import { createMemoryRouter, RouterProvider } from "react-router-dom"
import { routes } from "./routes"
import { test, expect, describe } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Average Volume", () => {
    test("renders the average volume for AAPL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/AAPL/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("volume")
        expect(priceElement).toHaveTextContent("The average volume for AAPL is 20.78")
    })

    test("renders the average volume for TSLA", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/TSLA/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("volume")
        expect(priceElement).toHaveTextContent("The average volume for TSLA is 75.25")
    })

    test("renders the average volume for GOOGL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/GOOGL/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const priceElement = await screen.findByTestId("volume")
        expect(priceElement).toHaveTextContent("The average volume for GOOGL is 250.25")
    })

    test("clicking the back link returns to the symbols screen starting from AAPL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/AAPL/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })

    test("clicking the back link returns to the symbols screen starting from TSLA", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/TSLA/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })

    test("clicking the back link returns to the symbols screen starting from GOOGL", async () => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols/GOOGL/averagevolume"],
        })
        render(<RouterProvider router={router} />)

        const backLink = await screen.findByTestId("back-link to symbols")

        await userEvent.click(backLink)

        // Wait for the symbols page to be visible
        expect(await screen.findByTestId("back-link")).toBeVisible()
    })
})