import React, { Component } from "react";
import withRouter from "../withRouter";
import Button from "../../components/ui/button/Button";
import "../../assets/css/form.css";
import GlobalVariables from "../../GlobalVariables.js"

/**
 * Add Sport page of the App
 *
 * @author Mason Schönherr
 */
let sportNameHeading = "";


class AddSport extends Component {
  constructor(props) {
    super(props);
    //state for the input elements
    this.state = {
      sportName: "",
      sportFactor: 1,
      loading: false,
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

    //Deactivate Button and add the loading circle
    this.setState({ loading: true });

    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");

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

    if(this.props.params.action === "Edit"){
      let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/" + this.props.params.id + "/", { method: "PUT", body: JSON.stringify(sportJsonObj), credentials: "include", headers: { "Content-Type": "application/json" }});
      if(sportResponse.ok){
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Die Sportart wurde erolgreich editiert!";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      }else{
        this.showInputErrorMessage("Beim editieren der Sportart ist etwas schief gelaufen: " + sportResponse.status + " " + sportResponse.statusText + "!");
      }
    }else{
      //Gives data to the Backend and writes it into the DB
      let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/", { method: "POST", body: JSON.stringify(sportJsonObj), credentials: "include", headers: { "Content-Type": "application/json" }});
      if(sportResponse.ok){
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML = "Die Sportart wurde erolgreich erstellt! Wenn du möchtests kannst du eine weitere Sportarten erstellen.";
        window.scrollTo(0, 0);
        this.clearAllInputs();
      }else{
        this.showInputErrorMessage("Beim erstellen der Sportart ist etwas schief gelaufen: " + sportResponse.status + " " + sportResponse.statusText + "!");
      }
    }

    //Activates the again Button and removes the loading circle
    this.setState({ loading: false });
  }

  async componentDidMount() {
    if (this.props.params.action === "Edit") {
      let sportResponse = await fetch(GlobalVariables.serverURL + "/sports/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();

      sportNameHeading = sportResData.name;
      this.setState({ sportName: sportResData.name });
      this.setState({ sportFactor: sportResData.factor });
    }

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              {this.props.params.action === "Edit" && <span className="underline_center">Sport {sportNameHeading} editieren</span>}
              {this.props.params.action === "Add" && <span className="underline_center">Sport hinzufügen</span>}
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib deiner Sportart einen Namen</h2>
                  <input className="mg_t_2" type="text" value={this.state.sportName} maxLength={15} onChange={this.sportNameChange} placeholder="Sport Name"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Lege einen Faktor für deine Sportart fest</h2>
                  <span className="form_input_description">
                    Die mit deiner Sportart zurückgelegten Kilometer werden mit diesem Faktor multipliziert.
                  <br />
                  <br />
                    Dies ist der Standart Faktor, er kann beim erstellen einer Challenge spezifisch für diese angepasst werden. 
                  </span>
                  <br />
                  <input className="mg_t_2" type="number" value={this.state.sportFactor} onChange={this.sportFactorChange}></input>
                </div>
                <div className="center_content mg_t_2">
                  {this.props.params.action === "Edit" && <Button color="orange" txt="Änderungen speichern" type="submit" loading={this.state.loading} />}
                  {this.props.params.action === "Add" && <Button color="orange" txt="Sportart hinzufügen" type="submit" loading={this.state.loading} />}
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
