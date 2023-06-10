import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import React, { useState } from "react";
import "./css/Navbar.css";
import AnimateHeight from "react-animate-height";
import GlobalVariables from "../GlobalVariables.js"

function Navbar() {
  const [height, setHeight] = useState(0);

  function changeDropDownState() {
    if (height === 0) {
      setHeight("auto");
    } else {
      setHeight(0);
    }
  }

  function toggleMobileSidebar() {
    document.getElementById("mobile_nav_bars").classList.toggle("open");
    document.getElementById("mobile_nav").classList.toggle("mobile_nav_open");
    document.getElementById("mobile_nav_close").classList.toggle("mobile_nav_close_show");
  }

  return (
    <header className="top_nav">
      <nav className="top_nav_item_container">
        <div className="mobile_nav_icon" onClick={toggleMobileSidebar}>
          <div className="mobile_nav_bars" id="mobile_nav_bars">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
        <ul className="top_nav_item_list">
          <li>
            <a href="/">Slash Challenge</a>
          </li>
          <li>
            <a href="/My-Challenges">Meine Challenges</a>
          </li>
          <li>
            <div className="top_nav_dropdown" id="top_nav_management" onClick={changeDropDownState}>
              <span>Management</span>
              <FontAwesomeIcon className="nav_dropdown_icon" icon={faCaretDown} />
              <AnimateHeight duration={200} height={height} className="top_nav_dropdown_menu">
                <a className="top_nav_dropdown_item" href="/Add/Challenge/0">
                  Neue Challenge
                </a>
                <a className="top_nav_dropdown_item" href="/Add/Team/0">
                  Neues Team
                </a>
                <a className="top_nav_dropdown_item" href="/Add/1/Bonus/0">
                  Neuer Bonus
                </a>
                <a className="top_nav_dropdown_item" href="/Sports">
                  Sportarten
                </a>
                <a className="top_nav_dropdown_item" href="/Profile">
                  Benutzerprofil
                </a>
              </AnimateHeight>
            </div>
          </li>
        </ul>
      </nav>
      <nav className="mobile_nav" id="mobile_nav">
        <div className="mobile_nav_list">
          <div className="mobile_nav_list_item">
            <a href="/">Slash Challenge</a>
          </div>
          <div className="mobile_nav_list_item">
            <a href="/My-Challenges">My Challenges</a>
          </div>
          <div className="mobile_nav_list_item">
            <span className="mobile_nav_dropdown" onClick={changeDropDownState}>
              Management
              <FontAwesomeIcon className="nav_dropdown_icon" icon={faCaretDown} />
            </span>
            <AnimateHeight duration={200} height={height} className="mobile_nav_dropdown_menu">
              <ul>
                <li>
                  <a href="/Add/Challenge/0">Neue Challenge</a>
                </li>
                <li>
                  <a href="/Add/Team/0">Neues Team</a>
                </li>
                <li>
                  <a href="/Add/1/Bonus/0">Neuer Bonus</a>
                </li>
                <li>
                  <a href="/Sports">Sportarten</a>
                </li>
                <li>
                  <a href="/Profile">Benutzerprofil</a>
                </li>
              </ul>
            </AnimateHeight>
          </div>
        </div>
      </nav>
      <div className="mobile_nav_close" id="mobile_nav_close" onClick={toggleMobileSidebar}></div>
    </header>
  );
}

export default Navbar;
