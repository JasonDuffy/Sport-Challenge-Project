import React, { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
import "./css/MyChallengesTableRow.css";
import "./css/Form.css";
import GlobalVariables from "../GlobalVariables.js"

class MyChallengesTableRow extends Component {
  constructor(props) {
    super(props);

    this.state = {
      challengeName: "",
      selectedSport: 0,
      distance: 0,
      activitieSaveDate: "",
      sportName: "",
      challengeId: 0,
      allSports: [],
      editMode: false,
      deleted: false,
    };

    this.selectedSportChange = this.selectedSportChange.bind(this);
    this.distanceChange = this.distanceChange.bind(this);
    this.editRow = this.editRow.bind(this);
    this.saveChangedRow = this.saveChangedRow.bind(this);
    this.deleteRow = this.deleteRow.bind(this);
  }

  selectedSportChange(event){
    this.setState({ selectedSport: event.target.value });
    this.setState({ sportName: event.target.options[event.target.selectedIndex].text });
  }

  distanceChange(event){
    if (event.target.value >= 1) {
        this.setState({ distance: event.target.value });
        //If the user tries to write 0 in the Input field it will be turned to 1
      } else if (event.target.value === 0) {
        this.setState({ distance: 1 });
      }
  }


  async editRow() {
    let allChallengeSportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/challenges/" + this.state.challengeId + "/", { method: "GET", credentials: "include" });
    let allChallengeSportResData = await allChallengeSportResponse.json();
    let allSportsHelper = [];

    for (const challengeSport of allChallengeSportResData) {
        let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/" + challengeSport.sportID + "/", { method: "GET", credentials: "include" });
        let sportResData = await sportResponse.json();
        sportResData.id = challengeSport.id;
        allSportsHelper.push(sportResData);
    }

    this.setState({ editMode: true });
    this.setState({ allSports: allSportsHelper });
  }

  async saveChangedRow() {
    let activitieResponse = await fetch(GlobalVariables.serverURL + "/activities/" + this.props.id + "/", { method: "GET", credentials: "include" });
    let activitieResData = await activitieResponse.json();

    const dateOptions = {day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"};
    let activityJsonObj = {};
    activityJsonObj.challengeSportID = this.state.selectedSport;
    activityJsonObj.memberID = activitieResData.memberID;
    activityJsonObj.distance = this.state.distance;
    activityJsonObj.date = new Date().toLocaleDateString("de-GE", dateOptions).replace(" ", "");

    const activityResponse = await fetch(GlobalVariables.serverURL + "/activities/" + this.props.id + "/", {
      method: "PUT",
      headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
      body: JSON.stringify(activityJsonObj),
      credentials: "include",
    });

    this.setState({ activitieSaveDate: activityJsonObj.date});
    this.setState({ editMode: false });
  }

  async deleteRow(event) {
    const activityResponse = await fetch(GlobalVariables.serverURL + "/activities/" + this.props.id + "/", { method: "DELETE", credentials: "include" });
    if(activityResponse.ok){
        this.setState({ deleted: true });
    }
  }

  async componentDidMount() {
    let activityResponse = await fetch(GlobalVariables.serverURL + "/activities/" + this.props.id + "/", { method: "GET", credentials: "include" });
    let activityResData = await activityResponse.json();
    let challengeSportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/" + activityResData.challengeSportID + "/", {
      method: "GET",
      credentials: "include",
    });
    let challengeSportResData = await challengeSportResponse.json();
    let challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + challengeSportResData.challengeID + "/", { method: "GET", credentials: "include" });
    let challengeResData = await challengeResponse.json();
    let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/" + challengeSportResData.sportID + "/", { method: "GET", credentials: "include" });
    let sportResData = await sportResponse.json();

    this.setState({ challengeName: challengeResData.name });
    this.setState({ selectedSport: challengeSportResData.id });
    this.setState({ distance: activityResData.distance });
    this.setState({ activitieSaveDate: activityResData.date });
    this.setState({ sportName: sportResData.name });
    this.setState({ challengeId: challengeResData.id });
  }

  render() {
    if(this.state.deleted === true){
        return;
    }
    if (this.state.editMode === true) {
        return (
            <tr>
              <td>{this.state.challengeName}</td>
              <td>
                <select className="activity_sport_select" value={this.state.selectedSport} onChange={this.selectedSportChange}>
                    <option value={0} disabled>Sportart wählen</option>
                    {this.state.allSports.map((item) => (
                        <option key={item.id} value={item.id}>{item.name}</option>
                    ))}
                </select>
              </td>
              <td><input className="activity_distance_input" type="number" step={0.1} value={this.state.distance} onChange={this.distanceChange}></input></td>
              <td>{this.state.activitieSaveDate.split(",")[0] + " um " + this.state.activitieSaveDate.split(",")[1] + " Uhr"}</td>
              <td>
                <div className="row_edit_icon icon_faCheck" onClick={this.saveChangedRow}>
                  <FontAwesomeIcon icon={faCheck} size="lg" />
                </div>
              </td>
            </tr>
          );
    } else {
      return (
        <tr>
          <td>{this.state.challengeName}</td>
          <td>{this.state.sportName}</td>
          <td>{this.state.distance + " Km"}</td>
          <td>{this.state.activitieSaveDate.split(",")[0] + " um " + this.state.activitieSaveDate.split(",")[1] + " Uhr"}</td>
          <td>
            <div className="row_edit_icon icon_faPencil" onClick={this.editRow}>
              <FontAwesomeIcon icon={faPencil} />
            </div>
            <div className="row_edit_icon icon_faXmark" onClick={this.deleteRow}>
              <FontAwesomeIcon icon={faXmark} size="lg" />
            </div>
          </td>
        </tr>
      );
    }
  }
}

export default MyChallengesTableRow;
