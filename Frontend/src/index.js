import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { BrowserRouter } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import "./components/css/Index.css";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <>
    <div id="page_loading" className="page_loading_container" style={{ display: "flex" }}>
      <div className="page_loading_ring">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <div id="page" style={{ display: "none" }}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </div>
  </>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
