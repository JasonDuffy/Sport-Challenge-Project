import React, { Component } from "react";
import withRouter from "./withRouter";
import Button from "./Button";
import "./css/AddChallenge.css";
import "./css/Form.css";

/**
 * Add Challenge page of the App
 *
 * @author Robin Hackh
 */

class AddChallenge extends Component {
  constructor(props) {
    super(props);

    //state for the input elements
    this.state = {
      challengeName: "",
      challengeDescription: "",
      challengeDistanceGoal: 1,
      challengeStartDate: "",
      challengeEndDate: "",
      allSport: [],
    };

    //bind is needed for changing the state
    this.challengeNameChange = this.challengeNameChange.bind(this);
    this.challengeDescriptionChange = this.challengeDescriptionChange.bind(this);
    this.challengeDistanceGoalChange = this.challengeDistanceGoalChange.bind(this);
    this.challengeStartDateChange = this.challengeStartDateChange.bind(this);
    this.challengeEndDateChange = this.challengeEndDateChange.bind(this);
    this.clearAllInputs = this.clearAllInputs.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
  }

  challengeNameChange(event) {
    this.setState({ challengeName: event.target.value });
  }

  challengeDescriptionChange(event) {
    this.setState({ challengeDescription: event.target.value });
  }
  challengeDistanceGoalChange(event) {
    if (event.target.value >= 1) {
      this.setState({ challengeDistanceGoal: event.target.value });
    } else if (event.target.value === 0) {
      this.setState({ challengeDistanceGoal: 1 });
    }
  }
  challengeStartDateChange(event) {
    this.setState({ challengeStartDate: event.target.value });
    this.setState({ challengeEndDate: "" });
  }
  challengeEndDateChange(event) {
    this.setState({ challengeEndDate: event.target.value });
  }

  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
    window.scrollTo(0, 0);
  }

  clearAllInputs(){
    const challengeImageEl = document.getElementById("challenge_image");
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");

    challengeImageEl.value = "";
    this.setState({ challengeName: "" });
    this.setState({ challengeDescription: "" });
    this.setState({ challengeDistanceGoal: 1 });
    this.setState({ challengeStartDate: "" });
    this.setState({ challengeEndDate: "" });

    for (const element of sportCheckboxEl) {
      element.checked = false;
    }
  }

  submitHandle(event) {
    event.preventDefault();

    const challengeImageEl = document.getElementById("challenge_image");
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
    const sportNumberEl = document.getElementsByClassName("form_sport_number");
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    const dateOptions = { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" };

    let startDate = new Date(this.state.challengeStartDate);
    let endDate = new Date(this.state.challengeEndDate);
    let startDateFormat = startDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let endDateFormat = endDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let sportCheckedId = [];
    let sportCheckedFactor = [];

    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.remove("success");

    if (this.state.challengeName === "") {
      this.showInputErrorMessage("Bitte gebe deiner Challenge einen Namen!");
      return;
    }

    //Checks if the file is null and smaller than 10MB
    if (challengeImageEl.files[0] == null) {
      this.showInputErrorMessage("Bitte lade für deine Challenge ein Bild hoch!");
      return;
    } else if (challengeImageEl.files[0].size > 10000000) {
      this.showInputErrorMessage("Das Bild darf nicht größer als 10Mb sein!");
      return;
    } else if (/^image/.test(challengeImageEl.files[0].type) === false) {
      this.showInputErrorMessage("Es sind nur Bilder zum hochladen erlaubt!");
      return;
    }

    for (let i = 0; i < sportCheckboxEl.length; i++) {
      if (sportCheckboxEl[i].checked) {
        sportCheckedId.push(sportCheckboxEl[i].dataset.sportId);
        sportCheckedFactor.push(sportNumberEl[i].value);
      }
    }

    //Checks if min one Sport is checked
    if (sportCheckedId.length === 0) {
      this.showInputErrorMessage("Du musst mindestens eine Sportart für deine Challenge auswählen!");
      return;
    }

    //Creates the JSON object corresponding to the Challenge object in the Backend
    let challengeJsonObj = {};
    challengeJsonObj.name = this.state.challengeName;
    challengeJsonObj.description = this.state.challengeDescription;
    challengeJsonObj.startDate = startDateFormat;
    challengeJsonObj.endDate = endDateFormat;
    challengeJsonObj.targetDistance = this.state.challengeDistanceGoal;

    //Creates body data for the fetch
    let fetchBodyData = new FormData();
    fetchBodyData.append("sportId", sportCheckedId);
    fetchBodyData.append("sportFactor", sportCheckedFactor);
    fetchBodyData.append("file", challengeImageEl.files[0]);
    fetchBodyData.append("json", JSON.stringify(challengeJsonObj));

    //Gives data to the Backend and writes it into the DB
    fetch("http://localhost:8081/challenges/", { method: "POST", body: fetchBodyData, credentials: "include" })
      .then((response) => {
        if (response.ok) {
          response.json().then((resData => {
            infoContainerEl.classList.add("success");
            infoMessageEl.innerHTML = "Die Challenge wurde erolgreich erstellt! Wenn du möchtests kannst du eine weitere Challenge erstellen oder dir deine erstellte Challenge <a href=\"/Challenge/" + resData.id + "\">hier</a> ansehen";
            window.scrollTo(0, 0);
            this.clearAllInputs();
          }));
        } else {
          this.showInputErrorMessage(
            "Beim erstellen der Challenge ist etwas schief gelaufen: " + response.status + " " + response.statusText + "!"
          );
        }
      })
      .catch((error) => {
        this.showInputErrorMessage("Beim erstellen der Challenge ist etwas schief gelaufen: " + error + "!");
      });
  }

  async componentDidMount() {
    //Loads all the Sports and writes them in to the table below
    let response = await fetch("http://localhost:8081/sports/", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({ allSport: resData });
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center">Challenge hinzufügen</span>
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib der Challenge einen Namen</h2>
                  <input
                    className="mg_t_2"
                    type="text"
                    value={this.state.challengeName}
                    maxLength={15}
                    onChange={this.challengeNameChange}
                    placeholder="Challenge Name"
                  ></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle ein Bild für deine Challenge</h2>
                  <span className="form_input_description">
                    Das Bild repräsentiert deine Challenge auf der Startseite.
                    <br />
                    <br />
                    Das Bild sollte quadratisch sein.
                  </span>
                  <br />
                  <input id="challenge_image" className="mg_t_2" type="file" accept="image/*"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Beschreibe deine Challenge</h2>
                  <div className="form_input_description_content">
                    <textarea
                      className="mg_t_2"
                      type="textArea"
                      value={this.state.challengeDescription}
                      onChange={this.challengeDescriptionChange}
                      placeholder="Beschreibe deine Challenge"
                    ></textarea>
                  </div>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Definiere ein Ziel für deine Challenge</h2>
                  <span className="form_input_description">Gib hier die Kilometerzahl ein, die erreicht werden soll</span>
                  <br />
                  <input
                    className="mg_t_2"
                    type="number"
                    value={this.state.challengeDistanceGoal}
                    onChange={this.challengeDistanceGoalChange}
                    placeholder="Kilometer"
                  ></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Wähle die Sportarten aus, die an der Challenge teilnehmen dürfen</h2>
                  <table className="form_sport_table mg_t_2">
                    <thead>
                      <tr>
                        <th>Sportart</th>
                        <th>Multiplier</th>
                        <th>Select</th>
                      </tr>
                    </thead>
                    <tbody>
                      {this.state.allSport.map((item) => (
                        <tr key={item.id}>
                          <td>{item.name}</td>
                          <td>
                            <input
                              className="form_sport_number"
                              data-sport-id={item.id}
                              type="number"
                              step="0.1"
                              defaultValue={item.factor}
                              min={1.0}
                            ></input>
                          </td>
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
                  <h2>Wähle das Start- und Enddatum für deine Challenge</h2>
                  <div className="form_input_date_container mg_t_2">
                    <div className="form_input_date_content">
                      <input
                        id="form_input_start_date"
                        type="datetime-local"
                        required="required"
                        value={this.state.challengeStartDate}
                        onChange={this.challengeStartDateChange}
                      ></input>
                      <span className="form_input_date_separator mg_x_1">----</span>
                      <input
                        id="form_input_end_date"
                        type="datetime-local"
                        required="required"
                        min={this.state.challengeStartDate}
                        value={this.state.challengeEndDate}
                        onChange={this.challengeEndDateChange}
                      ></input>
                    </div>
                  </div>
                </div>
                <div className="center_content mg_t_2">
                  <Button color="orange" txt="Challenge erstellen" type="submit" />
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default withRouter(AddChallenge);
