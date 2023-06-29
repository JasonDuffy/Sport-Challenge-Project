/**
 * @author Robin Hackh
 */

import { useEffect, useState } from "react";
import NumberInput from "../form/NumberInput/NumberInput";
import Button from "../ui/button/Button";
import Dropdown from "../form/Dropdown/Dropdown";
import "./MyChallengeOverview.css";
import { fetchChallengeData, saveActivity } from "./MyChallengeOverview";
import { Link } from "react-router-dom";

function MyChallengeOverview({ challengeID, memberID, activityIDArray, setActivityIDArray }) {
  //Challenge
  const [challengeName, setChallengeName] = useState("");
  const [challengeStartDate, setChallengeStartDate] = useState("");
  const [challengeEndDate, setChallengeEndDate] = useState("");
  const [challengeDistanceGoal, setChallengeDistanceGoal] = useState(0);
  const [challengeDistanceDone, setChallengeDistanceDone] = useState(0);
  const [challengeDescription, setChallengeDescription] = useState("");
  const [challengeImageSource, setChallengeImageSource] = useState("");
  const [challengeSports, setChallengeSports] = useState([]);

  //Input
  const [activityDistance, setActivityDistance] = useState(0);
  const [activitySportID, setActivitySportID] = useState("0");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function load() {
      let pageData = await fetchChallengeData(challengeID);

      setChallengeName(pageData.challengeResData.name);
      setChallengeStartDate(pageData.challengeResData.startDate.split(",")[0]);
      setChallengeEndDate(pageData.challengeResData.endDate.split(",")[0]);
      setChallengeDistanceGoal(pageData.challengeResData.targetDistance);
      setChallengeDescription(pageData.challengeResData.description);
      setChallengeImageSource("data:" + pageData.imageResData.type + ";base64, " + pageData.imageResData.data);
      setChallengeDistanceDone(pageData.distanceResData);
      setChallengeSports(pageData.challengeSportResData);
    }

    load();
  }, []);

  function showChallengeInfoMessage(message, error) {
    const infoContainerEl = document.getElementById("form_info_container_" + challengeID);
    const infoMessageEl = document.getElementById("form_info_message_" + challengeID);
    infoMessageEl.innerText = message;
  
    if (error === true) {
      infoContainerEl.classList.remove("success");
      infoContainerEl.classList.add("error");
    } else {
      infoContainerEl.classList.remove("error");
      infoContainerEl.classList.add("success");
    }
  }
  
  function hideChallengeInfoMessage() {
    const infoContainerEl = document.getElementById("form_info_container_" + challengeID);
    const infoMessageEl = document.getElementById("form_info_message_" + challengeID);
    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.remove("success");
    infoMessageEl.innerText = "";
  }

  async function submitHandle(event) {
    event.preventDefault();
    setLoading(true);
    hideChallengeInfoMessage();

    if (Number(activitySportID) < 0.1) {
        showChallengeInfoMessage("Bitte wähle eine Sportart aus!", true);
      return;
    }

    const dateOptions = {day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"};

    let activityObj = {};
    activityObj.challengeSportID = activitySportID;
    activityObj.memberID = memberID;
    activityObj.distance = activityDistance;
    activityObj.date = new Date().toLocaleDateString("de-GE", dateOptions).replace(" ", "");

    let activityResData = await saveActivity(activityObj);
    if(activityResData !== null){
        let updatetActivityIDArray = [...activityIDArray];
        updatetActivityIDArray.push(activityResData.id);
        setActivityIDArray(updatetActivityIDArray);
        showChallengeInfoMessage("Deine Aktivität wurde erfolgreich zur Challenge hinzugefügt!", false);
    } else {
        showChallengeInfoMessage("Beim hinzufügen deiner Aktivität ist ein Fehler aufgetreten!", true);
    }

    setLoading(false);
  }

  return (
    <div className="my_challenge_container">
      <div className="my_challenge_bg">
        <img src={challengeImageSource} alt="Challenge-Image" className="challenge_bg_image"></img>
        <div className="my_challenge_bg_color"></div>
      </div>
      <div className="my_challenge_wrap">
        <h1 className="my_challenge_title">{challengeName}</h1>
        <div className="my_challenge_date">
          {challengeStartDate}-{challengeEndDate}
        </div>
        <div className="my_challenge_distance">
          {challengeDistanceDone}/{challengeDistanceGoal}
        </div>
        <div className="my_challenge_info">{challengeDescription}</div>
        <div className="my_challenge_btns">
          <Link to="/challenge" state={{ challengeID: challengeID }}>
            <Button color="white" txt="Infos anzeigen" />
          </Link>
        </div>
      </div>
      <div className="my_challenge_form_container pd_1">
        <div id={"form_info_container_" + challengeID} className="pd_1 mg_b_2 form_info_container">
          <span id={"form_info_message_" + challengeID} className="form_info_message"></span>
        </div>
        <form onSubmit={submitHandle}>
          <h2>Wie viel Kilometer hast du zurückgelegt?</h2>
          <NumberInput className="mg_t_1" value={activityDistance} setValue={setActivityDistance} min={0.1} step={0.1} />
          <div className="mg_t_2">
            <h2>Sportart auswählen</h2>
            <Dropdown className="mg_t_1" value={activitySportID} setValue={setActivitySportID} options={challengeSports} defaultOption="Sportart wählen" />
          </div>
          <div className="center_content mg_t_2">
            <Button color="orange" txt="Aktivität hinzufügen" type="submit" loading={loading} />
          </div>
        </form>
      </div>
    </div>
  );
}

export default MyChallengeOverview;
