import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import React from "react";
import "./css/Navbar.css";

function Navbar() {
  return (
    <header className="top_nav">
      <nav className="top_nav_item_container">
        <div className="mobile_nav_icon">
          <div className="mobile_nav_bars">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
        <ul className="top_nav_item_list">
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <a href="/My-Challenges">My Challenges</a>
          </li>
          <li>
            <div className="top_nav_dropdown" id="top_nav_management">
              <span>Management</span>
              <FontAwesomeIcon
                className="nav_dropdown_icon"
                icon={faCaretDown}
              />
              <div
                className="top_nav_dropdown_menu"
                id="top_nav_management_menu"
              >
                <a className="top_nav_dropdown_item" href="/Add/Challenge">
                  Add Challenge
                </a>
                <a className="top_nav_dropdown_item" href="/Add/Team">
                  Add Team
                </a>
                <a className="top_nav_dropdown_item" href="/Add/Bonus">
                  Add Bonus
                </a>
                <a className="top_nav_dropdown_item" href="/Add/Sport">
                  Add Sport
                </a>
                <a className="top_nav_dropdown_item" href="/Profile">
                  Userprofile
                </a>
              </div>
            </div>
          </li>
        </ul>
      </nav>
      <nav className="mobile_nav">
        <div className="mobile_nav_list">
          <div className="mobile_nav_list_item">
            <a href="/">Home</a>
          </div>
          <div className="mobile_nav_list_item">
            <a href="/My-Challenges">My Challenges</a>
          </div>
          <div className="mobile_nav_list_item">
            <span className="mobile_nav_dropdown">
              Management
              <FontAwesomeIcon
                className="nav_dropdown_icon"
                icon={faCaretDown}
              />
            </span>
            <div className="mobile_nav_dropdown_menu">
              <ul>
                <li>
                  <a href="/Add/Challenge">Add Challenge</a>
                </li>
                <li>
                  <a href="/Add/Team">Add Team</a>
                </li>
                <li>
                  <a href="/Add/Bonus">Add Bonus</a>
                </li>
                <li>
                  <a href="/Add/Sport">Add Sport</a>
                </li>
                <li>
                  <a href="/Profile">Userprofile</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </nav>
      <div className="moible_nav_close"></div>
    </header>
  );
}

export default Navbar;
