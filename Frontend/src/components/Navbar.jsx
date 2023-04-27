import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import React from "react";
import "./css/Navbar.css";

function Navbar() {
  return (
    <header className="top_nav">
      <nav className="top_nav_item_container">
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
                className="top_nav_dropdown_icon"
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
                  Add Sportart
                </a>
                <a className="top_nav_dropdown_item" href="/Profile">
                  Userprofile
                </a>
              </div>
            </div>
          </li>
        </ul>
      </nav>
    </header>
  );
}

export default Navbar;
