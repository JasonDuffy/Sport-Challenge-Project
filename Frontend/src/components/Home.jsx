import React from "react";
import "./css/Home.css";
import ChallengeList from "./ChallengeList";
import Button from "./Button";

function Home() {
  return (
    <>
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_8">
              <span className="underline_center">Aktive Challenges</span>
            </div>
            <ChallengeList />
            <div className="center_content mg_t_2">
              <Button color="orange" txt="Neue Challenge" />
            </div>
          </div>
        </div>
      </section>

      <section className="background_lightblue">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_8">
              <span className="underline_center">
                Vorhergegangene Challenges
              </span>
            </div>
            <ChallengeList />
          </div>
        </div>
      </section>
    </>
  );
}

export default Home;