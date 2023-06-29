import { useEffect, useState } from "react";
import "./MyChallengesTableRow.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
import NumberInput from "../form/NumberInput/NumberInput";
import Dropdown from "../form/Dropdown/Dropdown";
import { fetchActivityData, fetchActivitySports, fetchDeleteActivity, updateActivity } from "./MyChallengesTableRow";

/**
 * @author Robin Hackh
 */

function MyChallengesTableRow({ activityID }) {
  const [challengeName, setChallengeName] = useState("");
  const [sportName, setSportName] = useState("");
  const [distance, setDistance] = useState(0);
  const [memberID, setMemberID] = useState(0);
  const [activitieSaveDate, setActivitieSaveDate] = useState("");
  const [selectedSport, setSelectedSport] = useState("0");
  const [challengeID, setChallengeID] = useState(0);
  const [allSports, setAllSports] = useState([]);
  const [editMode, setEditMode] = useState(false);
  const [deleted, setDeleted] = useState(false);

  useEffect(() => {
    async function load() {
      let pageData;
      pageData = await fetchActivityData(activityID);

      setChallengeName(pageData.challengeResData.name);
      setSelectedSport(String(pageData.challengeSportResData.id));
      setDistance(pageData.activityResData.distance);
      setActivitieSaveDate(pageData.activityResData.date);
      setSportName(pageData.sportResData.name);
      setChallengeID(pageData.challengeResData.id);
      setMemberID(pageData.activityResData.memberID);
    }

    load();
  }, [activitieSaveDate]);

  async function saveChangedActivity(event) {
    const dateOptions = {day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"};

    let activityObj = {};
    activityObj.challengeSportID = Number(selectedSport);
    activityObj.memberID = memberID;
    activityObj.distance = distance;
    activityObj.date = new Date().toLocaleDateString("de-GE", dateOptions).replace(" ", "");

    if(await updateActivity(activityID, activityObj) === true){
        setActivitieSaveDate(activityObj.date);
        setEditMode(false);
    }
  }

  async function editActivity(event) {
    let sportsResData = await fetchActivitySports(challengeID);

    setAllSports(sportsResData);
    setEditMode(true);
  }

  async function deleteActivity(event) {
    if (window.confirm("Möchtest du die Aktivität wirklich löschen?") === true) {
      if ((await fetchDeleteActivity(activityID)) === true) {
        setDeleted(true);
      }
    }
  }

  if (deleted === true) return;
  if (editMode === true) {
    return (
      <tr>
        <td>{challengeName}</td>
        <td>
          <Dropdown className="activity_sport_select" value={selectedSport} setValue={setSelectedSport} options={allSports} defaultOption="Sportart wählen" />
        </td>
        <td>
          <NumberInput className="activity_distance_input" value={distance} setValue={setDistance} min={0.1} step={0.1} />
        </td>
        <td>{activitieSaveDate.split(",")[0] + " um " + activitieSaveDate.split(",")[1] + " Uhr"}</td>
        <td>
          <div className="row_edit_icon icon_faCheck" onClick={saveChangedActivity}>
            <FontAwesomeIcon icon={faCheck} size="lg" />
          </div>
        </td>
      </tr>
    );
  } else {
    return (
      <tr>
        <td>{challengeName}</td>
        <td>{sportName}</td>
        <td>{distance + " Km"}</td>
        <td>{activitieSaveDate.split(",")[0] + " um " + activitieSaveDate.split(",")[1] + " Uhr"}</td>
        <td>
          <div className="row_edit_icon icon_faPencil" onClick={editActivity}>
            <FontAwesomeIcon icon={faPencil} />
          </div>
          <div className="row_edit_icon icon_faXmark" onClick={deleteActivity}>
            <FontAwesomeIcon icon={faXmark} size="lg" />
          </div>
        </td>
      </tr>
    );
  }
}

export default MyChallengesTableRow;
