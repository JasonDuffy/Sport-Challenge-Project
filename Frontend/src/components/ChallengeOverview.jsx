import React from "react";
import PropTypes from "prop-types";
import Button from "./Button";
import { useState, useEffect } from "react";
import "./css/ChallengeOverview.css";
import { useNavigate } from "react-router-dom";

/**
 * Displays a Challenge with Image, Overlay and Data.
 * 
 * @author Robin Hackh
 * @param id ID of the Challenge which should be displayed 
 */

function ChallengeOverview(props) {
  const navigate = useNavigate();
  const [challengeName, setChallengeName] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [distanceDone, setDistanceDone] = useState(0);
  const [distanceGoal, setDistanceGoal] = useState(0);
  const [challengeInfo, setChallengeInfo] = useState("");
  const [imageSource, setImageSource] = useState("");

  function openChallenge() {
    navigate("/Challenge/" + props.id);
  }

  useEffect(() => {
    async function getChallengeData(){
      const challengeResponse = await fetch("http://localhost:8081/challenges/" + props.id + "/", { method: "GET", credentials: "include" });
      const challengeResData = await challengeResponse.json();
      const imageResponse = await fetch("http://localhost:8081/images/" + challengeResData.imageID + "/", { method: "GET", credentials: "include" });
      const imageResData = await imageResponse.json();

      setChallengeName(challengeResData.name);
      setStartDate(challengeResData.startDate.split(",")[0]);
      setEndDate(challengeResData.endDate.split(",")[0]);
      setDistanceDone(0);
      setDistanceGoal(challengeResData.targetDistance);
      setChallengeInfo(challengeResData.description);
      setImageSource("data:" + imageResData.type + ";base64, " + imageResData.data);
    }

    getChallengeData();
  }, []);

    return (
    <>
      <div className="challenge_bg">
        <img src={imageSource} alt="Challenge-Image" className="challenge_bg_image"></img>
        <div className="challenge_bg_color"></div>
        <img src={require(`../images/Challenge-Overlay.png`)} alt="SCP" className="challenge_bg_image_overlay"></img>
      </div>
      <div className="challenge_wrap">
        <h1 className="challenge_title">{challengeName}</h1>
        <div className="challenge_date">
          {startDate}-{endDate}
        </div>
        <div className="challenge_distance">
          {distanceDone}/{distanceGoal}
        </div>
        <div className="challenge_info">{challengeInfo}</div>
        <div className="challenge_btns">
          <Button color="white" txt="Infos anzeigen" action={openChallenge} />
        </div>
      </div>
    </>
  );
}

ChallengeOverview.propTypes = {
  id: PropTypes.number.isRequired,
};

export default ChallengeOverview;
