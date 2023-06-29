import { useLocation, useNavigate, useParams } from "react-router-dom";
import InfoMessage, { hideInfoMessage } from "../../components/form/InfoMessage/InfoMessage";
import AddHeading from "../../components/AddHeading/AddHeading";
import { useEffect, useRef, useState } from "react";
import "./AddTeam.css";
import "../../components/form/TextInput/TextInput.css";
import TextInput from "../../components/form/TextInput/TextInput";
import Dropdown from "../../components/form/Dropdown/Dropdown";
import ImageSelecter from "../../components/form/ImageSelecter/ImageSelecter";
import { checkTeamInput, fetchAvailableMembers, fetchDeleteTeam, fetchFormData, fetchImageData, fetchTeamData, saveOrUpdateTeam } from "./AddTeam";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleDown, faGripVertical } from "@fortawesome/free-solid-svg-icons";
import Button from "../../components/ui/button/Button";
import { act } from "@testing-library/react";

/**
 * @author Robin Hackh
 */

function AddTeam() {
  const action = useParams().action.toLocaleLowerCase();
  const navigate = useNavigate();
  const location = useLocation();

  const [triggerState, setTriggerState] = useState(false);
  const [teamHeadingName, setTeamHeadingName] = useState();
  const [teamName, setTeamName] = useState("");
  const [challengeID, setChallengeID] = useState("0");
  const [challengeDropdownData, setChallengeDropdownData] = useState([]);
  const [teamImage, setTeamImage] = useState();
  const [teamImageID, setTeamImageID] = useState(0);
  const [teamImageSource, setTeamImageSource] = useState();
  const [availableMembersData, setAvailableMembersData] = useState([]);
  const [displayAvailableMembersData, setDisplayAvailableMembersData] = useState([]);
  const [selectedMembersData, setSelectedMembersData] = useState([]);
  const [loading, setLoading] = useState(false);

  //Ref needed to acces state in eventListener
  const availableMembersDataRef = useRef(availableMembersData);
  const displayAvailableMembersDataRef = useRef(displayAvailableMembersData);
  const selectedMembersDataRef = useRef(selectedMembersData);

  //Load Component
  useEffect(() => {
    async function load() {
      let pageData;

      if (action === "edit") {
        let teamData, memberData;
        pageData = await fetchTeamData(location.state.id);

        teamData = pageData.teamResData;
        memberData = pageData.memberDataArray;

        setTeamHeadingName(teamData.name);
        setTeamName(teamData.name);
        setChallengeID(String(teamData.challengeID));
        setTeamImageID(teamData.imageID);
        setSelectedMembersData(memberData);

        //Use defualt image if no image was set
        if (teamData.imageID === null || teamData.imageID === 0) {
          setTeamImageSource(require("../../assets/images/Default-Team.png"));
        } else {
          pageData = await fetchImageData(teamData.imageID);
          setTeamImageSource("data:" + pageData.type + "; base64, " + pageData.data);
        }

        document.title = "Slash Challenge - " + teamData.name + " bearbeiten";

        pageData = await fetchFormData(teamData.challengeID);
      } else {
        document.title = "Slash Challenge - Neues Team erstellen";

        pageData = await fetchFormData(0);
      }

      setChallengeDropdownData(pageData.challengeDropdownResData);

      if (location.state.challengeID !== null && action === "add") {
        setChallengeID(String(location.state.challengeID));
      }
    }

    load();
    dragHandler();
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, [action]);

  //Component unmount
  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    };
  }, []);

  //Update Refs on state change
  useEffect(() => {
    availableMembersDataRef.current = availableMembersData;
  }, [availableMembersData]);

  useEffect(() => {
    displayAvailableMembersDataRef.current = displayAvailableMembersData;
  }, [displayAvailableMembersData]);

  useEffect(() => {
    selectedMembersDataRef.current = selectedMembersData;
  }, [selectedMembersData]);

  //If the challegneID changes update the available members
  useEffect(() => {
    async function reloadTable() {
      const memberResData = await fetchAvailableMembers(challengeID);
      setAvailableMembersData(memberResData);
      setDisplayAvailableMembersData(memberResData);

      if (action !== "edit") setSelectedMembersData([]);
    }

    reloadTable();
  }, [challengeID]);

  //member search
  function searchMember(event) {
    let displayBufferMember = [];

    for (const availableMember of availableMembersData) {
      if (availableMember.fullName.toLocaleLowerCase().includes(event.target.value.toLocaleLowerCase())) {
        displayBufferMember.push(availableMember);
      }
    }

    setDisplayAvailableMembersData(displayBufferMember);
  }

  //member drag or drop
  function dragHandler() {
    let dragged = null;
    let triggerStateValue = false;
    let listener = document.addEventListener;

    listener("dragstart", (event) => {
      dragged = event.target;
      return dragged;
    });

    listener("dragover", (event) => {
      document.getElementById("member_available_dropzone_overlay").classList.add("show_drag_overlays");
      document.getElementById("member_in_team_dropzone_overlay").classList.add("show_drag_overlays");
      return event.preventDefault();
    });

    listener("drop", (event) => {
      event.preventDefault();

      document.getElementById("member_available_dropzone_overlay").classList.remove("show_drag_overlays");
      document.getElementById("member_in_team_dropzone_overlay").classList.remove("show_drag_overlays");

      //If dropped where it is already do nothing
      if (dragged.parentNode.id === event.target.id.replace("_overlay", "")) return;

      if (event.target.id === "member_in_team_dropzone_overlay") {
        const index = dragged.dataset.memberPositionId;

        //Adds the member to the selectedMembers
        selectedMembersDataRef.current.push(displayAvailableMembersDataRef.current[index]);
        setSelectedMembersData(selectedMembersDataRef.current);

        //Removes the member from the availableMembers array
        let indexInAvailableMembersArray = availableMembersDataRef.current.indexOf(displayAvailableMembersDataRef.current[index]);
        availableMembersDataRef.current.splice(indexInAvailableMembersArray, 1);
        setAvailableMembersData(availableMembersDataRef.current);

        //Reset search
        document.getElementById("member_search_input").value = "";
        setDisplayAvailableMembersData(availableMembersDataRef.current);

        //triggers a reload of the states
        //Needed states do not reload becuase of ref
        triggerStateValue = !triggerStateValue;
        setTriggerState(triggerStateValue);
      } else if (event.target.id === "member_available_dropzone_overlay") {
        const index = dragged.dataset.memberPositionId;

        //Adds the member to the availableMembers array
        availableMembersDataRef.current.push(selectedMembersDataRef.current[index]);
        setAvailableMembersData(availableMembersDataRef.current);

        //Removes the member from the selectedMembers array
        selectedMembersDataRef.current.splice(index, 1);
        setSelectedMembersData(selectedMembersDataRef.current);

        //Reset search
        document.getElementById("member_search_input").value = "";
        setDisplayAvailableMembersData(availableMembersDataRef.current);

        //triggers a reload of the states
        //Needed states do not reload becuase of ref
        triggerStateValue = !triggerStateValue;
        setTriggerState(triggerStateValue);
      } else {
        return;
      }
    });
  }

  /**
   * Form Submit
   */
  async function submitHandle(event) {
    event.preventDefault();

    setLoading(true);
    hideInfoMessage();

    let resData;

    let teamObj = {};
    if (checkTeamInput(teamName, challengeID, teamImage, selectedMembersData.length)) {
      teamObj.name = teamName;
      teamObj.challengeID = Number(challengeID);

      resData = await saveOrUpdateTeam(location.state.id, teamObj, selectedMembersData, teamImage, teamImageID, action);
    }

    if ((challengeID == 0 || challengeID == null) && resData != null) {
      navigate("/");
    } else if (resData != null) {
      navigate("/challenge", { state: { challengeID: challengeID } });
    }

    setLoading(false);
  }

  async function deleteTeam(event) {
    event.preventDefault();

    if (window.confirm("Möchten Sie das Team " + teamName + " wirklich löschen?")) {
      //if delete was successful
      if (await fetchDeleteTeam(location.state.id)) {
        navigate("/challenge", { state: { challengeID: challengeID } });
      }
    }
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <AddHeading action={action} entity="Team" name={teamHeadingName} />
          <InfoMessage />
          <div className="form_container">
            <form onSubmit={submitHandle}>
              <div className="form_input_container pd_1">
                <h2>Gebe deinem Team einen Namen</h2>
                <TextInput className="mg_t_2" value={teamName} setValue={setTeamName} maxLength={32} placeholder="Team Name" />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle eine Challenge aus, der das Team angehören soll</h2>
                <Dropdown className="mg_t_2" value={challengeID} setValue={setChallengeID} defaultOption="Challenge wählen" options={challengeDropdownData} />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <div>
                  <h2>Wähle ein Bild für dein Team (Optional)</h2>
                  <span className="form_input_description">
                    Das Bild repräsentiert dein Team in Challenges.
                    <br />
                    <br />
                    Es sollte quadratisch sein.
                  </span>
                  <br />
                  <ImageSelecter className={"mg_t_2"} value={teamImage} setValue={setTeamImage} alt="Aktuelles Bild des Teams" source={teamImageSource} />
                </div>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle deine Teammitglieder aus</h2>
                <div className="team_member_container mg_t_3">
                  <div className="team_member_content">
                    <input id="member_search_input" className="scp_text_input" type="text" placeholder="Suche nach Teilnehmern" onChange={searchMember}></input>
                    <div id="member_available_dropzone" className="member_available mg_t_1">
                      <div id="member_available_dropzone_overlay"></div>
                      {displayAvailableMembersData.map((item, index) => (
                        <div className="drag_member" draggable="true" key={item.id} data-member-id={item.id} data-member-position-id={index}>
                          <p>{item.fullName}</p>
                          <FontAwesomeIcon icon={faGripVertical} />
                        </div>
                      ))}
                    </div>
                  </div>
                  <div className="team_member_content mg_l_3">
                    <span className="form_input_description">Teilnehmer in deinem Team</span>
                    <div id="member_in_team_dropzone" className="member_in_team mg_t_1">
                      <div id="member_in_team_dropzone_overlay"></div>
                      {selectedMembersData.map((item, index) => (
                        <div className="drag_member" draggable="true" key={item.id} data-member-id={item.id} data-member-position-id={index}>
                          <p>{item.fullName}</p>
                          <FontAwesomeIcon icon={faGripVertical} />
                        </div>
                      ))}
                      {selectedMembersData.length === 0 && (
                        <div id="drag_target_overlay">
                          <FontAwesomeIcon icon={faCircleDown} size="3x" />
                          <p>Ziehe Teilnehmer hier rein</p>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
              <div className="center_content mg_t_2">
                {action === "edit" && (
                  <>
                    <Button color="orange" txt="Team bearbeiten" type="submit" loading={loading} />{" "}
                    <Button className="mg_l_2" action={deleteTeam} color="red" txt="Team löschen" loading={loading} />
                  </>
                )}
                {action === "add" && <Button color="orange" txt="Team hinzufügen" type="submit" loading={loading} />}
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default AddTeam;
