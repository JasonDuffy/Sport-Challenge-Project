import React from "react";
import { Routes, Route } from "react-router-dom";
import AddBonus from "./components/AddBonus";
import AddChallenge from "./components/AddChallenge";
import AddTeam from "./components/AddTeam";
import Challenge from "./components/Challenge";
import "./components/css/root.css";
import Home from "./components/Home";
import Login from "./components/Login";
import MyChallenges from "./components/MyChallenges";
import Navbar from "./components/Navbar";
import Userprofile from "./components/Userprofile";
import Test from "./components/Test";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Test" element={<Test />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/Challenge" element={<Challenge />} />
        <Route path="/My-Challenges" element={<MyChallenges />} />
        <Route path="/Profile" element={<Userprofile />} />
        <Route path="/Add/Challenge" element={<AddChallenge />} />
        <Route path="/Add/Team" element={<AddTeam />} />
        <Route path="/Add/Bonus" element={<AddBonus />} />
      </Routes>
    </>
  );
}

export default App;
