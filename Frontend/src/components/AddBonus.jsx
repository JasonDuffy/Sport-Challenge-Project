import React, { Component } from "react";
import Button from "./Button";
import "./css/AddBonus.css";
import "./css/Form.css";
import withRouter from "./withRouter";

/**
 * Add Bonus page of the App
 *
 * @author Mason Schönherr
 */
class AddBonus extends Component{
    constructor() {
        super();
        //state for the input elements
        this.state = {
          bonusName: "",
          bonusDescription: "",
          bonusFactor: 1,
          bonusStartDate: "",
          bonusEndDate: "" 
        };
    
        //bind is needed for changing the state
        this.bonusNameChange = this.bonusNameChange.bind(this);
        this.bonusDescriptionChange = this.bonusDescriptionChange.bind(this);
        this.bonusFactorChange = this.bonusFactorChange.bind(this);
        this.bonusStartDateChange = this.bonusStartDateChange.bind(this);
        this.bonusEndDateChange = this.bonusEndDateChange.bind(this);
        this.submitHandle = this.submitHandle.bind(this);
      }

      async componentDidMount(){
        console.log(this.props.params.action);
        //Loads all the Bonuses and writes them in to the table below
        let response = await fetch("http://localhost:8081/bonus/", { method: "GET", credentials: "include" });
        let resData = await response.json();
        this.setState({ allSport: resData });
      }

      submitHandle(event) {

        const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
        const dateOptions = { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" };
        const infoContainerEl = document.getElementById("form_info_container");
        const infoMessageEl = document.getElementById("form_info_message");

        let startDate = new Date(this.state.bonusStartDate);
        let endDate = new Date(this.state.bonusEndDate);
        let startDateFormat = startDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
        let endDateFormat = endDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
        let sportCheckedId = [];
        let bonusFactor = [];

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

        //Checks if min one Challenge is checked
        if (sportCheckedId.length === 0) {
        this.showInputErrorMessage("Du musst mindestens eine Sportart für deinen Bonus auswählen!");
        return;
        }

        //Creates body data for the fetch
        let fetchBodyData = new FormData();
        fetchBodyData.append("sportId", sportCheckedId);
        fetchBodyData.append("bonusFactor", bonusFactor);
        fetchBodyData.append("json", JSON.stringify(bonusJsonObj));

        //Gives data to the Backend and writes it into the DB
        fetch("http://localhost:8081/bonus/", { method: "POST", body: fetchBodyData, credentials: "include" })
        .then((response) => {
        if (response.ok) {
          response.json().then((resData => {
          infoContainerEl.classList.add("success");
          infoMessageEl.innerHTML = "Der Bonus wurde erolgreich erstellt!";
          window.scrollTo(0, 0);
          this.clearAllInputs();
          }));
        } else {
        this.showInputErrorMessage(
          "Beim erstellen des Bonuses ist etwas schief gelaufen: " + response.status + " " + response.statusText + "!"
        );
          }
        })
        .catch((error) => {
        this.showInputErrorMessage("Beim erstellen des Bonuses ist etwas schief gelaufen: " + error + "!");
        });

        for (let i = 0; i < sportCheckboxEl.length; i++) {
          if (sportCheckboxEl[i].checked) {
            sportCheckedId.push(sportCheckboxEl[i].dataset.sportId);
          }
        }
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

      clearAllInputs(){

        this.setState({ bonusName: "" });
        this.setState({ bonusDescription: "" });
        this.setState({ bonusFactor: 1 });
        this.setState({ bonusStartDate: "" });
        this.setState({ bonusEndDate: "" });
      }

    render() {
        return (
          <section className="background_white">
            <div className="section_container">
              <div className="section_content">
                <div className="heading_underline_center mg_b_10">
                  <span className="underline_center">Bonus hinzufügen</span>
                </div>
                <div className="form_container">
                  <form onSubmit={this.submitHandle}>
                    <div className="form_input_container pd_1">
                      <h2>Was ist der Anlass des Bonuses?</h2>
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
                      <h2>Beschreibe den Bonus</h2>
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
                      <Button color="orange" txt="Bonus festlegen" type="submit" />
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
