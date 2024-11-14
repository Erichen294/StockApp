import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { routes } from "./routes";
import { test, expect, describe, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

describe("MostActive", () => {
    beforeEach(() => {
        const router = createMemoryRouter(routes, {
            initialEntries: ["/mostactive"],
        });
        render(<RouterProvider router={router} />);
    });

    test("symbol returned by mock most active data gets displayed on page", async () => {
        const symbolElement = await screen.findByTestId("most-active-symbol");
        expect(symbolElement).toHaveTextContent("TSLA");
    });

    test("clicking the back link returns to the home screen", async () => {
        const backLink = await screen.findByTestId("back-link");

        await userEvent.click(backLink);

        // Wait for the home page to be visible and check for an element specific to it
        expect(await screen.findByTestId("home")).toBeVisible();
    });
});
