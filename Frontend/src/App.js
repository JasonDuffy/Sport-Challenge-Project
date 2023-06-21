import React from "react";
import { Routes, Route } from "react-router-dom";
import AddChallenge from "./pages/AddChallenge";
import AddTeam from "./pages/AddTeam";
import Challenge from "./pages/Challenge";
import "./assets/root.css";
import Home from "./pages/Home";
import Login from "./pages/Login";
import MyChallenges from "./pages/MyChallenges";
import Navbar from "./components/Navbar/Navbar";
import Userprofile from "./pages/Profile";
import AddSport from "./pages/AddSport";
import AddBonus from "./pages/AddBonus";
import Sports from "./pages/Sports";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/Challenge/" element={<Challenge />} />
        <Route path="/My-Challenges" element={<MyChallenges />} />
        <Route path="/Profile" element={<Userprofile />} />
        <Route path="/Sports" element={<Sports />} />
        <Route path="/Challenge/:action" element={<AddChallenge />} />
        <Route path="/Team/:action" element={<AddTeam />} />
        <Route path="/Bonus/:action" element={<AddBonus />} />
        <Route path="/Sport/:action" element={<AddSport />} />
      </Routes>
    </>
  );
}

export default App;
