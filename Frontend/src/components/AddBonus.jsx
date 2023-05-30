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
      allSport: []
    };
    
      //bind is needed for changing the state
      this.bonusNameChange = this.bonusNameChange.bind(this);
      this.bonusDescriptionChange = this.bonusDescriptionChange.bind(this);
      this.bonusFactorChange = this.bonusFactorChange.bind(this);
      this.bonusStartDateChange = this.bonusStartDateChange.bind(this);
      this.bonusEndDateChange = this.bonusEndDateChange.bind(this);
      this.submitHandle = this.submitHandle.bind(this);
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
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");

    this.setState({ bonusName: "" });
    this.setState({ bonusDescription: "" });
    this.setState({ bonusFactor: 1 });
    this.setState({ bonusStartDate: "" });
    this.setState({ bonusEndDate: "" });

    for (const element of sportCheckboxEl) {
      element.checked = false;
    }
  }

  async submitHandle(event) {
    event.preventDefault();

    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
    const dateOptions = { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" };
    const sportNumberEl = document.getElementsByClassName("form_sport_number");
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

    //Checks for Edit in URL
    if(this.props.params.action === "Edit") {
      let bonusResponse = await fetch("http://localhost:8081/bonus/" + this.props.params.id, + "/", { method: "PUT", body: JSON.stringify(bonusJsonObj), credentials: "include", headers: { "Content-Type": "application/json"}});
      if(bonusResponse.ok){
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Der Bonus wurde erfolgreich editiert!";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      }else{
        this.showInputErrorMessage("Beim editieren der Sportart ist etwas schief gelaufen: " + bonusResponse.status + " " + bonusResponse.statusText + "!");
      }
    }else{

      //Gives data to the Backend and writes it into the DB
      let bonusResponse = await fetch("http://localhost:8081/bonus/", { method: "POST", body: JSON.stringify(bonusJsonObj), credentials: "include", headers: { "Content-Type": "application/json"}});
      if (bonusResponse.ok) {
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Der Bonus wurde erolgreich erstellt! Du kannst noch weitere Bonuse erstellen!";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      } else {
        this.showInputErrorMessage("Beim erstellen des Bonuses ist etwas schief gelaufen: " + bonusResponse.status + " " + bonusResponse.statusText + "!");
      }
    }

    for (let i = 0; i < sportCheckboxEl.length; i++) {
      if (sportCheckboxEl[i].checked) {
        sportCheckedId.push(sportCheckboxEl[i].dataset.sportId);
      }
    }
  }

  async componentDidMount(){
    //Loads all the Bonuses and writes them in to the table below
    if ( this.props.params.action === "Edit"){
      let bonusResponse = await fetch("http://localhost:8081/bonus/" + this.props.params.id, + "/", { method: "GET", credentials: "include" });
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
                              className="form_sport_number"
                              data-sport-id={item.id}
                              type="number"
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