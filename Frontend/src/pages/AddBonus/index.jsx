import React, { Component } from "react";
import Button from "../../components/ui/button/Button";
import "./AddBonus.css";
import "../../components/form/Form.css";
import withRouter from "../withRouter";
import GlobalVariables from "../../GlobalVariables.js"

/**
 * Add Bonus page of the App
 *
 * @author Mason Schönherr
 */
let bonusNameHeading = "";

class AddBonus extends Component{
  constructor(props) {
    super(props);

    //state for the input elements
    this.state = {
      bonusName: "",
      bonusDescription: "", 
      bonusFactor: 1,
      bonusStartDate: "",
      bonusEndDate: "", 
      challengeId: 0,
      allSport: [],
      challengesData: [],
    };

      //bind is needed for changing the state
      this.bonusNameChange = this.bonusNameChange.bind(this);
      this.bonusDescriptionChange = this.bonusDescriptionChange.bind(this);
      this.bonusFactorChange = this.bonusFactorChange.bind(this);
      this.bonusStartDateChange = this.bonusStartDateChange.bind(this);
      this.bonusEndDateChange = this.bonusEndDateChange.bind(this);
      this.challengeIdChange = this.challengeIdChange.bind(this);
      this.submitHandle = this.submitHandle.bind(this);
      this.allSport = this.fillSport.bind(this);
    }

  bonusNameChange(event) {
    this.setState({ bonusName: event.target.value });
  }
  bonusDescriptionChange(event) {
    this.setState({ bonusDescription: event.target.value });
  }
  bonusFactorChange(event) {
    if (event.target.value >= 1) {
      this.setState({ bonusFactor: event.target.value });
    } else if (event.target.value == 0) {
      this.setState({ bonusFactor: 1 });
    }
  }
  bonusStartDateChange(event) {
    this.setState({ bonusStartDate: event.target.value });
    this.setState({ bonusEndDate: "" });
  }
  bonusEndDateChange(event) {
    this.setState({ bonusEndDate: event.target.value });
  }

  challengeIdChange(event) {
    this.setState({ challengeId: event.target.value }, () => {
      this.fillSport();
    });
  }

  //Fills the table with the accordings sports after picking a challenge
  async fillSport() {
    let challengeResponse = await fetch("http://localhost:8081/challenge-sports/challenges/" + this.state.challengeId + "/", { method: "GET", credentials: "include" });
    let challengeResData = await challengeResponse.json();

    for (const item of challengeResData) {
      let sportResponse = await fetch("http://localhost:8081/sports/" + item.sportID + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();

      item.name = sportResData.name;
    }
    this.setState({ allSport: challengeResData});
  }

  clearAllInputs(){
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");

    this.setState({ bonusName: "" });
    this.setState({ bonusDescription: "" });
    this.setState({ bonusFactor: 1 });
    this.setState({ bonusStartDate: "" });
    this.setState({ bonusEndDate: "" });
    this.setState({ challengeId: 0 });

    for (const element of sportCheckboxEl) {
      element.checked = false;
    }
  }

  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
    window.scrollTo(0, 0);
  }

  async submitHandle(event) {
    event.preventDefault();

    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
    const dateOptions = { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" };
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");

    let startDate = new Date(this.state.bonusStartDate);
    let endDate = new Date(this.state.bonusEndDate);
    let startDateFormat = startDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let endDateFormat = endDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let sportCheckedId = [];
    

    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.remove("success");

    if (this.state.bonusName === "") {
      this.showInputErrorMessage("Bitte gebe deinem Bonus einen Namen!");
      return;
    }

    //Creates the JSON object corresponding to the bonus object in the Backend
    let bonusJsonObj = {};
    bonusJsonObj.name = this.state.bonusName;
    bonusJsonObj.description = this.state.bonusDescription;
    bonusJsonObj.factor = this.state.bonusFactor;
    bonusJsonObj.startDate = startDateFormat;
    bonusJsonObj.endDate = endDateFormat;

    for (let i = 0; i < sportCheckboxEl.length; i++) {
      if (sportCheckboxEl[i].checked) {
        sportCheckedId.push(sportCheckboxEl[i].dataset.sportId);
      }
    }

    //Checks if min one Sport is checked
    if (sportCheckedId.length === 0) {
    this.showInputErrorMessage("Du musst mindestens eine Sportart für deinen Bonus auswählen!");
    return;
    }

    //Checks if the a name is null
    if (this.state.bonusName === "") {
      this.showInputErrorMessage("Bitte gebe deinem Bonus einen Namen!");
      return;
    }

    //Checks for Edit in URL
    if(this.props.params.action === "Edit") {
      let bonusResponse = await fetch(GlobalVariables.serverURL + "/bonus/" + this.props.params.id, + "/", { method: "PUT", body: JSON.stringify(bonusJsonObj), credentials: "include", headers: { "Content-Type": "application/json"}});
      if(bonusResponse.ok){
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Der Bonus wurde erfolgreich aktualisiert!";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      }else{
        this.showInputErrorMessage("Beim editieren der Sportart ist etwas schief gelaufen: " + bonusResponse.status + " " + bonusResponse.statusText + "!");
      }
    }else{
      //Gives data to the Backend and writes it into the DB
      let bonusResponse = await fetch(GlobalVariables.serverURL + "/bonus/", { method: "POST", body: JSON.stringify(bonusJsonObj), credentials: "include", headers: { "Content-Type": "application/json"}});
      if (bonusResponse.ok) {
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Der Bonus wurde erolgreich erstellt! Du kannst noch weitere Bonuse erstellen!";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      } else {
        this.showInputErrorMessage("Beim erstellen des Bonuses ist etwas schief gelaufen: " + bonusResponse.status + " " + bonusResponse.statusText + "!");
      }
    }

      let challengeResponse = await fetch("http://localhost:8081/challenge-sport-bonuses/", { method: "GET", credentials: "include" });
      if(challengeResponse.ok){
        let challengeResData = [];
        challengeResponse.json().then((response) => {
          challengeResData.id = response.id;
          challengeResData.challengeSportID = response.challengeSportID;
          challengeResData.bonusID = response.bonusID;
          //challengeResData = challengeResponse.JSON();
    
          let bonusResponse = fetch("http://localhost:8081/bonuses/" + challengeResData + "/", { method: "POST", body: JSON.stringify(bonusJsonObj), credentials: "include", headers: { "Content-Type": "application/json" }});
          if (bonusResponse.ok) {
            infoContainerEl.classList.add("success");
            infoMessageEl.innerHTML = "Der Bonus wurde erolgreich erstellt! Du kannst noch weitere Bonuse erstellen!";
            window.scrollTo(0, 0);
            this.clearAllInputs();
          } else {
            this.showInputErrorMessage("Beim erstellen des Bonuses ist etwas schief gelaufen: " + bonusResponse.status + " " + bonusResponse.statusText + "!");
          }
        });
      }
      
    }
  }

  async componentDidMount(){
    //Loads all the Sports and writes them in to the table below
    let response = await fetch("http://localhost:8081/sports/", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({ allSport: resData });

    const challengeResponse = await fetch("http://localhost:8081/challenges/?type=current", { method: "GET", credentials: "include" });
    const challengeResData = await challengeResponse.json();
    this.setState({ challengesData: challengeResData });

    //If it is in edit mode
    if ( this.props.params.action === "Edit"){
      let bonusResponse = await fetch(GlobalVariables.serverURL + "/bonus/" + this.props.params.id, + "/", { method: "GET", credentials: "include" });
      let bonusResData = await bonusResponse.json();
      const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
  
      bonusNameHeading = bonusResData.name;
      this.setState({ bonusName: bonusResData.name });
      this.setState({ bonusDescription: bonusResData.description });
      this.setState({ bonusFactor: bonusResData.factor });
      this.setState({ bonusStartDate: this.dateToInputFormat(bonusResData.startDate) });
      this.setState({ bonusEndDate: this.dateToInputFormat(bonusResData.endDate) });
  
      for(const element of sportCheckboxEl) {
        element.checked = false;
      }
    }

    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";

    const data = this.props.location.state.id;

    console.log("ID:---" + data);
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              {this.props.params.action === "Edit" && <span className="underline_center">Bonus {bonusNameHeading} editieren</span>}
              {this.props.params.action === "Add" && <span className="underline_center">Bonus hinzufügen</span>}
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib dem Bonus einen Namen</h2>
                  <input
                    className="mg_t_2"
                    type="text"
                    value={this.state.bonusName}
                    maxLength={15}
                    onChange={this.bonusNameChange}
                    placeholder="Bonus Name"
                  ></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Was ist der Anlass des Bonuses?</h2>
                  <div className="form_input_description_content">
                    <textarea
                      className="mg_t_2"
                      type="textArea"
                      value={this.state.bonusDescription}
                      onChange={this.bonusDescriptionChange}
                      placeholder="Beschreibe den Bonus"
                    ></textarea>
                  </div>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle eine Challenge für den Bonus aus</h2>
                  <select className="mg_t_1" value={this.state.challengeId} onChange={this.challengeIdChange}>
                    <option value={0} disabled>
                      Challenge wählen
                    </option>
                    {this.state.challengesData.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name}
                      </option>
                    ))}
                  </select>
                </div>
                  <div className="form_input_container pd_1 mg_t_2">
                    <h2>Welchen Faktor soll der Bonus haben?</h2>
                    <br />
                    <input
                      className="mg_t_2"
                      type="number"
                      value={this.state.bonusFactor}
                      onChange={this.bonusFactorChange}
                      placeholder="Faktor"
                    ></input>
                  </div>
                  <div className="form_input_container pd_1 mg_t_2">
                    <h2>Wähle die Sportarten aus, die von dem Bonus beeinflusst werden</h2>
                    <table className="form_sport_table mg_t_2">
                      <thead>
                        <tr>
                        <th>Sportart</th>
                        <th>Select</th>
                        </tr>
                      </thead>
                    <tbody>
                      {this.state.allSport.map((item) => (
                       <tr key={item.id}>
                          <td>{item.name}</td>
                          <td>
                            <input
                              className="form_table_checkbox form_sport_checkbox"
                              data-sport-id={item.id}
                              type="checkbox"
                            ></input>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle das Start- und Enddatum für den Bonus</h2>
                  <div className="form_input_date_container mg_t_2">
                    <div className="form_input_date_content">
                      <input
                        id="form_input_start_date"
                        type="datetime-local"
                        required="required"
                        value={this.state.bonusStartDate}
                        onChange={this.bonusStartDateChange}
                      ></input>
                      <span className="form_input_date_separator mg_x_1">----</span>
                      <input
                        id="form_input_end_date"
                        type="datetime-local"
                        required="required"
                        min={this.state.bonusStartDate}
                        value={this.state.bonusEndDate}
                        onChange={this.bonusEndDateChange}
                      ></input>
                    </div>
                 </div>
                </div>
                <div className="center_content mg_t_2">
                  {this.props.params.action === "Edit" && <Button color="orange" txt="Bonus editieren" type="submit" />}
                  {this.props.params.action === "Add" && <Button color="orange" txt="Bonus festlegen" type="submit" />}
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default withRouter(AddBonus);
