import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router";
import MyChallengesTableRow from "../../components/MyChallengesTableRow/MyChallengesTableRow.jsx";
import MyChallengeOverview from "../../components/MyChallengeOverview/MyChallengeOverview.jsx";
import InfoMessage from "../../components/form/InfoMessage/InfoMessage";
import "./MyChallenges.css";
import { fetchActivityIDs, fetchChallengeIDs, fetchLoggedInMember } from "./MyChallenges";

/**
 * @author Robin Hackh
 */

function MyChallenges() {
  const navigate = useNavigate();
  const location = useLocation();

  const [challengeIDs, setChallengeIDs] = useState([]);
  const [activityIDs, setActivityIDs] = useState([]);
  const [loggedInID, setLoggedInID] = useState(0);
  
  document.title = "Slash Challenge - Meine Challenges";
  
  useEffect(() => {
    async function load(){
        let memberResData, activityIDarray, challengeIDarray;

        memberResData = await fetchLoggedInMember();
        activityIDarray = await fetchActivityIDs(memberResData.userID);
        challengeIDarray = await fetchChallengeIDs(memberResData.userID);

        activityIDarray.sort(function(a, b){ // Sort by latest entry
          return b - a;
        });

        setLoggedInID(memberResData.userID);
        setActivityIDs(activityIDarray);
        setChallengeIDs(challengeIDarray);
    }

    load();
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, []);

  //Component unmount
  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    };
  }, []);

  return <>
  <section className="background_white">
    <div className="section_container">
      <div className="section_content">
        <div className="heading_underline_center mg_b_8">
          <span className="underline_center">Meine Challenges</span>
        </div>
        <InfoMessage />
        <ul className="col my_challenge_list">
          {challengeIDs.map((item) => (
            <li key={item} className="my_challenge_list_item">
              <MyChallengeOverview challengeID={item} memberID={loggedInID} activityIDArray={activityIDs} setActivityIDArray={setActivityIDs} />
            </li>
          ))}
        </ul>
      </div>
    </div>
  </section>
  <section className="background_lightblue">
    <div className="section_container">
      <div className="section_content">
        <div className="heading_underline_center mg_b_8">
          <span className="underline_center">Meine Aktivitäten</span>
        </div>
        <div>
          <table className="last_activites_table">
            <thead>
              <tr>
                <th>Name der Challenge</th>
                <th>Sportart</th>
                <th>Distanz</th>
                <th>Eingetragen am</th>
                <th>Aktion</th>
              </tr>
            </thead>
            <tbody>
              {activityIDs.map((item) => (
                <MyChallengesTableRow key={item} activityID={item} />
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</>;
}

export default MyChallenges;
