import React from "react";
import "./css/MyChallengeOverview.css";
import "./css/Form.css";
import Button from "./Button";
import { useNavigate } from "react-router-dom";

function MyChallengeOverview(props) {
  const navigate = useNavigate();
  
  function openChallenge() {
    navigate("/Challenge/" + props.id);
  }

  return (
    <div className="my_challenge_container">
      <div className="my_challenge_bg">
        <img src={require(`../images/Challenge.png`)} alt="Challenge-Image" className="challenge_bg_image"></img>
        <div className="my_challenge_bg_color"></div>
      </div>
      <div className="my_challenge_wrap">
        <h1 className="my_challenge_title">Challenge-1</h1>
        <div className="my_challenge_date">01.01.2022-01.01.2023</div>
        <div className="my_challenge_distance">906/3000</div>
        <div className="my_challenge_info">
          Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore
          magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kas
        </div>
        <div className="my_challenge_btns">
          <Button color="white" txt="Infos anzeigen" action={openChallenge} />
        </div>
      </div>
      <div className="my_challenge_form_container pd_1">
        <form>
          <h2>Wie viel Kilometer hast du zurückgelegt?</h2>
          <input className="mg_t_1" type="number" defaultValue={60}></input>
          <div className="mg_t_2">
            <h2>Sportart auswählen</h2>
            <select className="mg_t_1">
              <option>Laufen</option>
              <option>Radfahren</option>
            </select>
          </div>
          <div className="center_content mg_t_2">
            <Button color="orange" txt="Aktivität hinzufügen" type="submit" />
          </div>
        </form>
      </div>
    </div>
  );
}

export default MyChallengeOverview;
