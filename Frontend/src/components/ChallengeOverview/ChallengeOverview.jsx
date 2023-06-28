import React from "react";
import PropTypes from "prop-types";
import Button from "../ui/button/Button";
import { useState, useEffect } from "react";
import "./ChallengeOverview.css";
import { useNavigate } from "react-router-dom";
import GlobalVariables from "../../GlobalVariables.js"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGift } from "@fortawesome/free-solid-svg-icons";

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
  const [bonusData, setBonusData] = useState("");

  function openChallenge() {
    navigate("/challenge/", { state: { challengeID: props.id}});
  }

  useEffect(() => {
    async function getChallengeData(){
      const challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + props.id + "/", { method: "GET", credentials: "include" });
      const challengeResData = await challengeResponse.json();

      const bonusResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + props.id + "/bonuses/?type=current", { method: "GET", credentials: "include" });
      const bonusResData = await bonusResponse.json();

      if (challengeResData.imageID != null){
        const imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + challengeResData.imageID + "/", { method: "GET", credentials: "include" });
        const imageResData = await imageResponse.json();
        setImageSource("data:" + imageResData.type + ";base64, " + imageResData.data);
      } else {
        setImageSource(require(`../../assets/images/Default-Challenge.png`));
      }
      const challengeDistanceResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + props.id + "/distance/", { method: "GET", credentials: "include" });
      const challengeDistanceResData = await challengeDistanceResponse.json();

      setChallengeName(challengeResData.name);
      setStartDate(challengeResData.startDate.split(",")[0]);
      setEndDate(challengeResData.endDate.split(",")[0]);
      setDistanceGoal(challengeResData.targetDistance);
      setChallengeInfo(challengeResData.description);
      setDistanceDone(challengeDistanceResData);
      setBonusData(bonusResData);
    }

    getChallengeData();
  }, []);

    return (
    <>
      <div className="challenge_bg">
        <img src={imageSource} alt="Challenge-Image" className="challenge_bg_image"></img>
        <div className="challenge_bg_color"></div>
        <img src={require(`../../assets/images/Challenge-Overlay.png`)} alt="SCP" className="challenge_bg_image_overlay"></img>
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
        <div className="challenge_bonus">
            {bonusData.length > 0 && (<FontAwesomeIcon icon={faGift} />)}
        </div>
      </div>
    </>
  );
}

ChallengeOverview.propTypes = {
  id: PropTypes.number.isRequired,
};

export default ChallengeOverview;
