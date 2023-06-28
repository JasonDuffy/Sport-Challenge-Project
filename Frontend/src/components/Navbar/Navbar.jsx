import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import React, { useState } from "react";
import "./Navbar.css";
import AnimateHeight from "react-animate-height";
import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const [height, setHeight] = useState(0);
  const navigate = useNavigate();

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

  function test() {
    navigate("/Bonus/add", { state: { id: 5 } });
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
            <Link to="/">Slash Challenge</Link>
          </li>
          <li>
            <Link to="/my-challenges">Meine Challenges</Link>
          </li>
          <li>
            <div className="top_nav_dropdown" id="top_nav_management" onClick={changeDropDownState}>
              <span>Management</span>
              <FontAwesomeIcon className="nav_dropdown_icon" icon={faCaretDown} />
              <AnimateHeight duration={200} height={height} className="top_nav_dropdown_menu">
                <Link className="top_nav_dropdown_item" to="/challenge/edit" state={{ id: 2 }}>
                  Neue Challenge
                </Link>
                <Link className="top_nav_dropdown_item" to="/team/edit" state={{ id: 2 }}>
                  Neues Team
                </Link>
                <Link className="top_nav_dropdown_item" to="/bonus/edit" state={{ id: 2 }}>
                  Neuer Bonus
                </Link>
                <Link className="top_nav_dropdown_item" to="/sports">
                  Sportarten
                </Link>
                <Link className="top_nav_dropdown_item" to="/profile">
                  Benutzerprofil
                </Link>
              </AnimateHeight>
            </div>
          </li>
        </ul>
      </nav>
      <nav className="mobile_nav" id="mobile_nav">
        <div className="mobile_nav_list">
          <div className="mobile_nav_list_item">
            <Link to="/">Slash Challenge</Link>
          </div>
          <div className="mobile_nav_list_item">
            <Link to="/my-challenges">Meine Challenges</Link>
          </div>
          <div className="mobile_nav_list_item">
            <span className="mobile_nav_dropdown" onClick={changeDropDownState}>
              Management
              <FontAwesomeIcon className="nav_dropdown_icon" icon={faCaretDown} />
            </span>
            <AnimateHeight duration={200} height={height} className="mobile_nav_dropdown_menu">
              <ul>
                <li>
                  <Link to="/challenge/add" state={{ id: 0 }}>
                    Neue Challenge
                  </Link>
                </li>
                <li>
                  <Link to="/team/add" state={{ id: 0 }}>
                    Neues Team
                  </Link>
                </li>
                <li>
                  <Link to="/bonus/add" state={{ id: 0 }}>
                    Neuer Bonus
                  </Link>
                </li>
                <li>
                  <Link to="/sports">Sportarten</Link>
                </li>
                <li>
                  <Link to="/profile">Benutzerprofil</Link>
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
