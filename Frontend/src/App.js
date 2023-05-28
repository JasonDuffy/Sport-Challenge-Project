import React from "react";
import { Routes, Route } from "react-router-dom";
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
import AddSport from "./components/AddSport";
import AddBonus from "./components/AddBonus";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Test" element={<Test />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/Challenge/:id" element={<Challenge />} />
        <Route path="/My-Challenges" element={<MyChallenges />} />
        <Route path="/Profile" element={<Userprofile />} />
        <Route path="/:action/Challenge/:id" element={<AddChallenge />} />
        <Route path="/:action/Team/:id" element={<AddTeam />} />
        <Route path="/:action/Bonus/:id" element={<AddBonus />} />
        <Route path="/:action/Sport/:id" element={<AddSport />} />
      </Routes>
    </>
  );
}

export default App;
