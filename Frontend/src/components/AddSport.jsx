import React, { Component } from "react";
import withRouter from "./withRouter";
import Button from "./Button";
import "./css/Form.css";

/**
 * Add Sport page of the App
 *
 * @author Mason Schönherr
 */
class AddSport extends Component {
  constructor(props) {
    super(props);
    //state for the input elements
    this.state = {
      sportName: "",
      sportFactor: 1,
    };

    //bind is needed for changing the state
    this.sportNameChange = this.sportNameChange.bind(this);
    this.sportFactorChange = this.sportFactorChange.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
  }

  sportNameChange(event) {
    this.setState({ sportName: event.target.value });
  }

  sportFactorChange(event) {
    if (event.target.value >= 1) {
      this.setState({ sportFactor: event.target.value });
    } else if (event.target.value == 0) {
      this.setState({ sportFactor: 1 });
    }
  }

  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
    window.scrollTo(0, 0);
  }

  clearAllInputs() {
    this.setState({ sportName: ""});
    this.setState({ sportFactor: 1});
  }

  async submitHandle(event) {
    event.preventDefault();

    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");

    let sportCheckedId = [];
    let sportCheckedFactor = [];

    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.remove("success");

    if (this.state.sportName === "") {
      this.showInputErrorMessage("Bitte gebe deiner Sportart einen Namen!");
      return;
    }

    //Creates the JSON object corresponding to the Sport object in the Backend
    let sportJsonObj = {};
    sportJsonObj.name = this.state.sportName;
    sportJsonObj.factor = this.state.sportFactor;

    //Gives data to the Backend and writes it into the DB
    fetch("http://localhost:8081/sports/", {
      method: "POST",
      body: JSON.stringify(sportJsonObj),
      credentials: "include",
      headers: { "Content-Type": "application/json" },
    })
      .then((response) => {
        if (response.ok) {
          response.json().then((resData) => {
            infoContainerEl.classList.add("success");
            infoMessageEl.innerHTML = "Die Sportart wurde erolgreich erstellt! Wenn du möchtests kannst du eine weitere Sportarten erstellen.";
            window.scrollTo(0, 0);
            this.clearAllInputs();
          });
        } else {
          this.showInputErrorMessage("Beim erstellen der Sportart ist etwas schief gelaufen: " + response.status + " " + response.statusText + "!");
        }
      })
      .catch((error) => {
        this.showInputErrorMessage("Beim erstellen der Sportart ist etwas schief gelaufen: " + error + "!");
      });
  }

  async componentDidMount() {
    if (this.props.params.action === "Edit") {
      let sportResponse = await fetch("http://localhost:8081/sports/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();

      this.setState({ sportName: sportResData.name });
      this.setState({ sportFactor: sportResData.factor });
    }
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center">Sport hinzufügen</span>
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Welche Sportart?</h2>
                  <input className="mg_t_2" type="text" value={this.state.sportName} maxLength={15} onChange={this.sportNameChange} placeholder="Sport Name"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Welchen Factor soll der Sport haben?</h2>
                  <br />
                  <input className="mg_t_2" type="number" value={this.state.sportFactor} onChange={this.sportFactorChange} placeholder="Factor"></input>
                </div>
                <div className="center_content mg_t_2">
                  <Button color="orange" txt="Sport festlegen" type="submit" />
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default withRouter(AddSport);
