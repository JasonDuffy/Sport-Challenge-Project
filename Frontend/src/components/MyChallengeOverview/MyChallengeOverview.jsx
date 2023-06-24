import React, { Component } from "react";
import "./MyChallengeOverview.css";
import "../form/Form.css";
import Button from "../ui/button/Button";
import GlobalVariables from "../../GlobalVariables.js"

class MyChallengeOverview extends Component {
  constructor(props) {
    super(props);

    //state for the input elements
    this.state = {
      challengeName: "",
      challengeStartDate: "",
      challengeEndDate: "",
      challengeDistanceGoal: 0,
      challengeDistanceDone: 0,
      challengeDescription: "",
      challengeImageSource: "",
      challengeSports: [],
      activityDistance: 1,
      activitySportId: 0,
      loading: false,
    };

    //bind is needed for changing the state
    this.activityDistanceChange = this.activityDistanceChange.bind(this);
    this.activitySportIdChange = this.activitySportIdChange.bind(this);
    this.openChallenge = this.openChallenge.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
  }

  activityDistanceChange(event) {
    if (event.target.value >= 1) {
      this.setState({ activityDistance: event.target.value });
      //If the user tries to write 0 in the Input field it will be turned to 1
    } else if (event.target.value === 0) {
      this.setState({ activityDistance: 1 });
    }
  }

  activitySportIdChange(event) {
    this.setState({ activitySportId: event.target.value });
  }

  openChallenge() {
    window.location.href = "/Challenge/" + this.props.id;
  }

  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container_" + this.props.id);
    const infoMessageEl = document.getElementById("form_info_message_" + this.props.id);
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
  }

  showInputSuccesMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container_" + this.props.id);
    const infoMessageEl = document.getElementById("form_info_message_" + this.props.id);
    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.add("success");
    infoMessageEl.innerText = message;
  }

  async submitHandle(event) {
    event.preventDefault();

    //Deactivate Button and add the loading circle
    this.setState({ loading: true });

    if (this.state.activitySportId === 0) {
      this.showInputErrorMessage("Bitte wähle eine Sportart aus!");
      return;
    }

    const dateOptions = {day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"};
    let activityJsonObj = {};
    activityJsonObj.challengeSportID = this.state.activitySportId;
    activityJsonObj.memberID = this.props.memberId;
    activityJsonObj.distance = this.state.activityDistance;
    activityJsonObj.date = new Date().toLocaleDateString("de-GE", dateOptions).replace(" ", "");

    const activityResponse = await fetch(GlobalVariables.serverURL + "/activities/", {
      method: "POST",
      headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
      body: JSON.stringify(activityJsonObj),
      credentials: "include",
    });
    if(activityResponse.ok){
      this.showInputSuccesMessage("Deine Aktivität wurde erfolgreich zur Challenge hinzugefügt!");
    }else{
      this.showInputErrorMessage("Beim hinzufügen deiner Aktivität ist ein Fehler aufgetreten: " + activityResponse.status + " " + activityResponse.statusText + "!");
    }

    //Activates the again Button and removes the loading circle
    this.setState({ loading: false });
  }

  async componentDidMount() {
    let challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + this.props.id + "/", { method: "GET", credentials: "include" });
    let challengeResData = await challengeResponse.json();
    let imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + challengeResData.imageID + "/", { method: "GET", credentials: "include" });
    let imageResData = await imageResponse.json();
    let distanceResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + this.props.id + "/distance/", { method: "GET", credentials: "include" });
    let distanceResData = await distanceResponse.json();
    let challengeSportResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + this.props.id + "/challenge-sports/", { method: "GET", credentials: "include" });
    let challengeSportResData = await challengeSportResponse.json();

    for (let i = 0; i < challengeSportResData.length; i++) {
      let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/" + challengeSportResData[i].sportID + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();
      challengeSportResData[i].sportName = sportResData.name;
    }

    this.setState({ challengeName: challengeResData.name });
    this.setState({ challengeStartDate: challengeResData.startDate.split(",")[0] });
    this.setState({ challengeEndDate: challengeResData.endDate.split(",")[0] });
    this.setState({ challengeDistanceGoal: challengeResData.targetDistance });
    this.setState({ challengeDescription: challengeResData.description });
    this.setState({ challengeImageSource: "data:" + imageResData.type + ";base64, " + imageResData.data });
    this.setState({ challengeDistanceDone: distanceResData });
    this.setState({ challengeSports: challengeSportResData });
  }

  render() {
    return (
      <div className="my_challenge_container">
        <div className="my_challenge_bg">
          <img src={this.state.challengeImageSource} alt="Challenge-Image" className="challenge_bg_image"></img>
          <div className="my_challenge_bg_color"></div>
        </div>
        <div className="my_challenge_wrap">
          <h1 className="my_challenge_title">{this.state.challengeName}</h1>
          <div className="my_challenge_date">
            {this.state.challengeStartDate}-{this.state.challengeEndDate}
          </div>
          <div className="my_challenge_distance">
            {this.state.challengeDistanceDone}/{this.state.challengeDistanceGoal}
          </div>
          <div className="my_challenge_info">{this.state.challengeDescription}</div>
          <div className="my_challenge_btns">
            <Button color="white" txt="Infos anzeigen" action={this.openChallenge} />
          </div>
        </div>
        <div className="my_challenge_form_container pd_1">
          <div id={"form_info_container_" + this.props.id} className="pd_1 mg_b_2 form_info_container">
            <span id={"form_info_message_" + this.props.id} className="form_info_message"></span>
          </div>
          <form onSubmit={this.submitHandle}>
            <h2>Wie viel Kilometer hast du zurückgelegt?</h2>
            <input className="mg_t_1" type="number" step={0.1} value={this.state.activityDistance} onChange={this.activityDistanceChange}></input>
            <div className="mg_t_2">
              <h2>Sportart auswählen</h2>
              <select className="mg_t_1" value={this.state.activitySportId} onChange={this.activitySportIdChange}>
                <option value={0} disabled>Sportart wählen</option>
                {this.state.challengeSports.map((item) => (
                  <option key={item.id} value={item.id}>
                    {item.sportName}
                  </option>
                ))}
              </select>
            </div>
            <div className="center_content mg_t_2">
              <Button color="orange" txt="Aktivität hinzufügen" type="submit" loading={this.state.loading} />
            </div>
          </form>
        </div>
      </div>
    );
  }
}

export default MyChallengeOverview;
