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
