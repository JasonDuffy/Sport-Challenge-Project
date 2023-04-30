import { fireEvent, render } from "@testing-library/react";
import Button from "../Button";

describe(Button, () => {
    test("render color correctly", () => {
        const { getByRole } = render(<Button color="orange" txt="Hello World" action={test} />);
        expect(getByRole("button").classList).toContain("button_orange");
    });

    test("render text correctly", () => {
        const { getByRole } = render(<Button color="orange" txt="Hello World" action={test} />);
        expect(getByRole("button").textContent).toBe("Hello World");
    });

    test("should call testFunction", () => {
        const testFunctionSpy = jest.fn();
        const { getByRole } = render(<Button color="orange" txt="Hello World" action={testFunctionSpy} />);

        fireEvent.click(getByRole("button"));
        expect(testFunctionSpy).toHaveBeenCalled();
    });
});