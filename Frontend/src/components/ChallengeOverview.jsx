import React from "react";
import PropTypes from "prop-types";
import Button from "./Button";
import "./css/ChallengeOverview.css";

const challengeName = "Challenge-1";
const fromDate = "01.01.2022";
const toDate = "01.01.2023";
const distanceDone = 906;
const distanceGoal = 3000;
const challengeInfo =
  "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kas";

/**
 *
 * @param background
 *
 */

function ChallengeOverview(props) {
  return (
    <>
      <div className="challenge_bg">
        <img
          src={require(`../images/${props.background}.png`)}
          alt="Challenge-Image"
          className="challenge_bg_image"
        ></img>
        <div className="challenge_bg_color"></div>
        <img
          src={require(`../images/Challenge-Overlay.png`)}
          alt="SCP"
          className="challenge_bg_image_overlay"
        ></img>
      </div>
      <div className="challenge_wrap">
        <h1 className="challenge_title">{challengeName}</h1>
        <div className="challenge_date">
          {fromDate}-{toDate}
        </div>
        <div className="challenge_distance">
          {distanceDone}/{distanceGoal}
        </div>
        <div className="challenge_info">{challengeInfo}</div>
        <div className="challenge_btns">
          <Button color="white" txt="Infos Anzeigen" action={openChallenge}/>
        </div>
      </div>
    </>
  );
}

ChallengeOverview.propTypes = {
  background: PropTypes.string.isRequired,
};

const openChallenge = function(){
  window.location.href = window.location.href + "Challenge";
}

export default ChallengeOverview;