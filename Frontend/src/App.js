import React from "react";
import { Routes, Route, useLocation, useNavigate } from "react-router-dom";
import AddChallenge from "./pages/AddChallenge";
import AddTeam from "./pages/AddTeam";
import Challenge from "./pages/Challenge";
import "./assets/css/root.css";
import "./assets/css/index.css";
import Home from "./pages/Home";
import Login from "./pages/Login";
import MyChallenges from "./pages/MyChallenges";
import Navbar from "./components/Navbar/Navbar";
import Userprofile from "./pages/Profile";
import AddSport from "./pages/AddSport";
import AddBonus from "./pages/AddBonus";
import Sports from "./pages/Sports";
import apiFetch from "./utils/api";
import { useState } from "react";
import { useEffect } from "react";

function App() {
  document.title = "Slash Challenge"; // Default title

  const[isLoggedIn, setIsLoggedIn] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    checkLogin();
    updateLoginState();
  }, [location.pathname, isLoggedIn]);

  async function updateLoginState() {
    let response = await apiFetch("/saml/", "GET", {}, null);
  
    if(response.status === 403){
      setIsLoggedIn(false);
    } else {
      setIsLoggedIn(true);
    }
  }
  
  function checkLogin(){
    if(isLoggedIn === false){
      if(window.location.pathname !== "/login"){
        navigate("/login");
        window.location.reload()
      }
    } else {
      if(window.location.pathname === "/login"){
        navigate("/");
      }
    }
  }

  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/challenge" element={<Challenge />} />
        <Route path="/my-challenges" element={<MyChallenges />} />
        <Route path="/profile" element={<Userprofile />} />
        <Route path="/sports" element={<Sports />} />
        <Route path="/challenge/:action" element={<AddChallenge />} />
        <Route path="/team/:action" element={<AddTeam />} />
        <Route path="/bonus/:action" element={<AddBonus />} />
        <Route path="/sport/:action" element={<AddSport />} />
      </Routes>
    </>
  );
}

export default App;
