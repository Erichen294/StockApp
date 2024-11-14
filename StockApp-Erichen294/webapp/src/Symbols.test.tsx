import { createMemoryRouter, RouterProvider } from "react-router-dom"
import { routes } from "./routes"
import { test, expect, describe, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Symbols", () => {
    beforeEach(() => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/symbols"],
        });
        render(<RouterProvider router={router} />);
    });

    test("AAPL gets displayed on page", async () => {
        const item = await screen.findByTestId("AAPL")
        expect(item).toHaveTextContent("AAPL");
    })

    test("TSLA gets displayed on page", async () => {
        const item = await screen.findByTestId("TSLA")
        expect(item).toHaveTextContent("TSLA");
    })

    test("GOOGL gets displayed on page", async () => {
        const item = await screen.findByTestId("GOOGL")
        expect(item).toHaveTextContent("GOOGL");
    })

    test("clicking the back link returns to the home screen", async () => {
        const backLink = await screen.findByTestId("back-link")

        await userEvent.click(backLink)

        // Wait for the home page to be visible
        expect(await screen.findByTestId("home")).toBeVisible()
    })

    test("navigating from symbols to AAPL price", async () => {
        const priceLink = await screen.findByTestId("link-to-price-AAPL")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })

    test("navigating from symbols to TSLA price", async () => {
        const priceLink = await screen.findByTestId("link-to-price-TSLA")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })

    test("navigating from symbols to GOOGL price", async () => {
        const priceLink = await screen.findByTestId("link-to-price-GOOGL")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })

    test("navigating from symbols to AAPL average volume", async () => {
        const priceLink = await screen.findByTestId("link-to-volume-AAPL")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })

    test("navigating from symbols to TSLA average volume", async () => {
        const priceLink = await screen.findByTestId("link-to-volume-TSLA")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })

    test("navigating from symbols to GOOGL average volume", async () => {
        const priceLink = await screen.findByTestId("link-to-volume-GOOGL")

        await userEvent.click(priceLink)

        expect(await screen.findByTestId("back-link to symbols")).toBeVisible()
    })
})