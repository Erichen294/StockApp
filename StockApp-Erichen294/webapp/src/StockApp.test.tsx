import { test, expect, describe } from "vitest"
import { render, screen } from "@testing-library/react"
import "@testing-library/jest-dom"
import { createMemoryRouter, RouterProvider } from "react-router-dom"
import { routes } from "./routes"

describe("StockApp", () => {
  // Note that the function provided to the test is marked async
  // which allows us to use the await keyword for functions that
  // return Promises inside the test.
  test("loads the app root", async () => {
    // ARRANGE
    //
    // Sets up the router with the app's routes and renders the app.
    // When you want to start the test in different parts of the app,
    // change the initial entries to the path that routes to the
    // desired place.
    const router = createMemoryRouter(routes, {
      // Controls what URL/route the test starts at
      initialEntries: ["/"],
    })
    render(<RouterProvider router={router} />)

    // ACT
    //
    // Because loading data is asynchronous, it's best to have a step
    // that waits for something representative to be found on the screen
    // that indicates that you're ready to starting making assertions.
    //
    // You can also simulate user actions like clicks, typing, etc.
    await screen.findAllByText("Stock App")

    // ASSERT
    //
    // Check the screen for the expected outcome for your setup.
    expect(screen.getByText("Symbols")).toBeVisible()
    expect(screen.getByText("Most Active")).toBeVisible()
  })
})
