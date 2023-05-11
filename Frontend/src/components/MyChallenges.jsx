import React from "react";
import MyChallengeOverview from "./MyChallengeOverview";
import "./css/MyChallenges.css";

function MyChallenges() {
  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <div className="heading_underline_center mg_b_10">
            <span className="underline_center">Meine Challenges</span>
          </div>
          <ul className="col my_challenge_list">
            <li className="my_challenge_list_item">
                <MyChallengeOverview id={1} />
            </li>
            <li className="my_challenge_list_item">
                <MyChallengeOverview id={2} />
            </li>
            <li className="my_challenge_list_item">
                <MyChallengeOverview id={3} />
            </li>
          </ul>
        </div>
      </div>
    </section>
  );
}

export default MyChallenges;
