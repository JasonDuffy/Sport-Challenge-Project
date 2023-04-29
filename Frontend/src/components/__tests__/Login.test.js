import { render } from "@testing-library/react";
import Login from "../Login";

describe(Login, () => {
    test("Image source and alternative text is set", () => {
        const { getByRole } = render(<Login />);
        expect(getByRole("img").getAttribute("src")).not.toBeNull();
        expect(getByRole("img").getAttribute("alt")).not.toBeNull();
    });

    test("should Render Button", () => {
        const { getByRole } = render(<Login />);
        expect(getByRole("button")).toBeInTheDocument();
    });
});