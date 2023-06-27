import React, { Component } from "react";
import withRouter from "../withRouter";
import Button from "../../components/ui/button/Button";
import "./AddChallenge.css";
import "../../components/form/Form.css";
import GlobalVariables from "../../GlobalVariables.js";
import AddHeading from "../../components/AddHeading/AddHeading";
import InfoMessage from "../../components/form/InfoMessage/InfoMessage";

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
      challengeNameHeading: "",
      challengeDescription: "",
      challengeDistanceGoal: 1,
      challengeStartDate: "",
      challengeEndDate: "",
      imageSource: "",
      imageID: 0,
      allSport: [],
      loading: false,
    };

    //bind is needed for changing the state
    this.challengeNameChange = this.challengeNameChange.bind(this);
    this.challengeDescriptionChange = this.challengeDescriptionChange.bind(this);
    this.challengeDistanceGoalChange = this.challengeDistanceGoalChange.bind(this);
    this.challengeStartDateChange = this.challengeStartDateChange.bind(this);
    this.challengeEndDateChange = this.challengeEndDateChange.bind(this);
    this.previewImageChange = this.previewImageChange.bind(this);
    this.clearAllInputs = this.clearAllInputs.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
  }

  //==================================================START STATE CHANGE FUNCTIONS==================================================
  challengeNameChange(event) {
    this.setState({ challengeName: event.target.value });
  }

  challengeDescriptionChange(event) {
    this.setState({ challengeDescription: event.target.value });
  }

  challengeDistanceGoalChange(event) {
    if (event.target.value >= 1) {
      this.setState({ challengeDistanceGoal: event.target.value });
      //If the user tries to write 0 in the Input field it will be turned to 1
    } else if (event.target.value === 0) {
      this.setState({ challengeDistanceGoal: 1 });
    }
  }

  challengeStartDateChange(event) {
    this.setState({ challengeStartDate: event.target.value });
    this.setState({ challengeEndDate: "" }); //Sets End Date to nothing so it can´t be before the start date
  }

  challengeEndDateChange(event) {
    this.setState({ challengeEndDate: event.target.value });
  }

  previewImageChange(event) {
    this.setState({ imageSource: URL.createObjectURL(event.target.files[0]) });
  }
  //==================================================END STATE CHANGE FUNCTIONS==================================================

  //==================================================START OUTSOURCED FUNCTIONS==================================================
  //Displays a red box with the given message at the top of the Site (Scrolls automaticly to it)
  showInputErrorMessage(message) {
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    infoContainerEl.classList.add("error");
    infoMessageEl.innerText = message;
    window.scrollTo(0, 0);
  }

  clearAllInputs() {
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

  //Converts the date which comes from the DB to the fomrat the date input fields can display
  dateToInputFormat(dateString) {
    const dateSplitByComma = dateString.split(",");
    const dateSplitByPoint = dateSplitByComma[0].split(".");

    return dateSplitByPoint[2] + "-" + dateSplitByPoint[1] + "-" + dateSplitByPoint[0] + "T" + dateSplitByComma[1];
  }
  //==================================================END OUTSOURCED FUNCTIONS==================================================

  //==================================================START COMPONENT FUNCTIONS==================================================
  async submitHandle(event) {
    event.preventDefault(); //prevents the reload

    //Deactivate Button and add the loading circle
    this.setState({ loading: true });

    //Vars needed in the function
    const challengeImageEl = document.getElementById("challenge_image");
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
    const sportNumberEl = document.getElementsByClassName("form_sport_number");
    const infoContainerEl = document.getElementById("form_info_container");
    const infoMessageEl = document.getElementById("form_info_message");
    const dateOptions = {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    };

    let startDate = new Date(this.state.challengeStartDate);
    let endDate = new Date(this.state.challengeEndDate);
    let startDateFormat = startDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let endDateFormat = endDate.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
    let sportCheckedId = [];
    let sportCheckedFactor = [];

    //the info message box which displays error/success messages is resetet
    infoContainerEl.classList.remove("error");
    infoContainerEl.classList.remove("success");

    //==================================================START OF THE INPUT CHECK==================================================
    //Checks if the a name is null
    if (this.state.challengeName === "") {
      this.showInputErrorMessage("Bitte gebe deiner Challenge einen Namen!");
      return;
    }

    //Does not check the image if Challenge is Edited and no new picture is uploaded because of the existing image
    if (this.props.params.action == "add" && challengeImageEl.files[0] != null) {
      //Checks if the file is an image and smaller than 10MB
      if (challengeImageEl.files[0].size > 10000000) {
        this.showInputErrorMessage("Das Bild darf nicht größer als 10Mb sein!");
        return;
      } else if (/^image/.test(challengeImageEl.files[0].type) === false) {
        this.showInputErrorMessage("Es sind nur Bilder zum hochladen erlaubt!");
        return;
      }
    }

    //Saves the SportId and the input factor if the checkbox of the sport is checked
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

    //NOTE: Date is checked by HTML
    //==================================================END OF THE INPUT CHECK==================================================

    //==================================================START TO BACKEND START==================================================
    //Creates the JSON object corresponding to the Challenge object in the Backend
    let challengeJsonObj = {};
    challengeJsonObj.name = this.state.challengeName;
    challengeJsonObj.description = this.state.challengeDescription;
    challengeJsonObj.startDate = startDateFormat;
    challengeJsonObj.endDate = endDateFormat;
    challengeJsonObj.imageID = 0;
    challengeJsonObj.targetDistance = this.state.challengeDistanceGoal;

    //==================================================ADD CHALLENGE==================================================
    if (this.props.params.action == "add") {
      //Creates body data for the fetch
      let fetchBodyData = new FormData();
      fetchBodyData.append("sportId", sportCheckedId);
      fetchBodyData.append("sportFactor", sportCheckedFactor);

      if (challengeImageEl.files[0] == null) {
        fetchBodyData.append("file", new File([""], "empty"));
      } else {
        fetchBodyData.append("file", challengeImageEl.files[0]);
      }

      fetchBodyData.append("json", JSON.stringify(challengeJsonObj));

      //Gives data to the Backend and writes it into the DB
      let challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/", { method: "POST", body: fetchBodyData, credentials: "include" });
      if (challengeResponse.ok) {
        let challengeResData = await challengeResponse.json();
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML =
          'Die Challenge wurde erolgreich erstellt! Wenn du möchtests kannst du eine weitere Challenge erstellen oder dir deine erstellte Challenge <a href="/Challenge/' +
          challengeResData.id +
          '">hier</a> ansehen';
        window.scrollTo(0, 0);
        this.clearAllInputs();
      } else {
        this.showInputErrorMessage("Beim erstellen der Challenge ist etwas schief gelaufen: " + challengeResponse.status + " " + challengeResponse.statusText + "!");
      }

      //==================================================UPDATE CHALLENGE==================================================
    } else if (this.props.params.action == "edit") {
      //If a new Image is set it will update the image corresponding to the challenge
      if (challengeImageEl.files[0] != null) {
        //Creates body data for the update image fetch
        let fetchImageBodyData = new FormData();
        fetchImageBodyData.append("file", challengeImageEl.files[0]);

        //Gives data to the Backend and updates the Image
        let imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + this.state.imageID + "/", {
          method: "PUT",
          body: fetchImageBodyData,
          credentials: "include",
        });
        if (!imageResponse.ok) {
          this.showInputErrorMessage("Beim editieren der Challenge ist etwas schief gelaufen: " + imageResponse.status + " " + imageResponse.statusText + "!");
        }
      }

      //Selects all sportCheckboxes which are checked
      const sportCheckboxCheckedEl = document.querySelectorAll('[class*="form_sport_checkbox"][type="checkbox"]:checked');
      let challengeSportFactor = []; //Array with the factors of inputs (only from checked sports)
      let sportIdInDB = []; //Array which contains the SportId's that should be updatet
      let challengeSportId = []; //Array which contains the ChallengeSportId's needed for the update

      const challengeID = this.props.location.state.challengeID;

      //All challengeSports that contains the Challenge ID
      let sportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/challenges/" + challengeID + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();

      //Search and saves the Sports that need a update
      for (const sportData of sportResData) {
        sportIdInDB.push(document.querySelector('[data-sport-id="' + sportData.sportID + '"][type="checkbox"]').dataset.sportId);
        challengeSportId.push(sportData.id);
      }

      //Saves the factor given by the user for the checked sports
      for (const sportCheckedCheckbox of sportCheckboxCheckedEl) {
        challengeSportFactor.push(document.querySelector('[data-sport-id="' + sportCheckedCheckbox.dataset.sportId + '"][type="number"]').value);
      }

      //Creates body data for the update challenge-sport fetch
      //Set's the default params
      let challengeSportJsonObj = {};
      challengeSportJsonObj.id = 0;
      challengeSportJsonObj.challengeID = challengeID;

      for (let i = 0; i < sportCheckboxCheckedEl.length; i++) {
        //remaining data for the fetch
        challengeSportJsonObj.factor = challengeSportFactor[i];
        challengeSportJsonObj.sportID = sportCheckboxCheckedEl[i].dataset.sportId;

        //If the Challenge sport already exists in the DB it will be updated, added otherwise
        if (sportIdInDB.includes(sportCheckboxCheckedEl[i].dataset.sportId)) {
          //UPDATE Challenge Sport

          sportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/" + challengeSportId[0] + "/", {
            method: "PUT",
            headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
            body: JSON.stringify(challengeSportJsonObj),
            credentials: "include",
          });

          challengeSportId.shift(); //removes first element of the list (the one that has been updated)

          if (!sportResponse.ok) {
            this.showInputErrorMessage("Beim editieren der Challenge ist etwas schief gelaufen: " + sportResponse.status + " " + sportResponse.statusText + "!");
          }
        } else {
          //ADD new Challenge Sport

          sportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/", {
            method: "POST",
            headers: { Accept: "application/json", "Content-Type": "application/json" }, //Needed: Backend will not accept without
            body: JSON.stringify(challengeSportJsonObj),
            credentials: "include",
          });

          if (!sportResponse.ok) {
            this.showInputErrorMessage("Beim editieren der Challenge ist etwas schief gelaufen: " + sportResponse.status + " " + sportResponse.statusText + "!");
          }
        }
      }

      //Creates body data for the create Challenge fetch
      let fetchChallengeBodyData = new FormData();

      if (this.state.imageID == null) {
        fetchChallengeBodyData.append("imageId", 0);
      } else {
        fetchChallengeBodyData.append("imageId", this.state.imageID);
      }

      fetchChallengeBodyData.append("json", JSON.stringify(challengeJsonObj));

      //Gives data to the Backend and updates the Challenge
      let challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + challengeID + "/", {
        method: "PUT",
        body: fetchChallengeBodyData,
        credentials: "include",
      });

      //Show success message if everthing worked, error message otherwise
      if (challengeResponse.ok) {
        let challengeResData = await challengeResponse.json();
        infoContainerEl.classList.add("success");
        infoMessageEl.innerHTML =
          'Die Challenge wurde erolgreich editiert! Wenn du möchtests kannst du eine weitere Challenge erstellen oder dir deine editierte Challenge <a href="/Challenge/' +
          challengeResData.id +
          '">hier</a> ansehen';
        window.scrollTo(0, 0);
        this.clearAllInputs();
      } else {
        this.showInputErrorMessage("Beim editieren der Challenge ist etwas schief gelaufen: " + challengeResponse.status + " " + challengeResponse.statusText + "!");
      }
    }
    //==================================================END FETCH TO BACKEND==================================================

    //Activates the again Button and removes the loading circle
    this.setState({ loading: false });
  }

  async componentDidMount() {
    //Loads all the Sports and writes them in to the table below
    let response = await fetch(GlobalVariables.serverURL + "/sports/", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({ allSport: resData });

    //If the component is in edit mode
    if (this.props.params.action === "edit") {
      //fetch the data of the challenge from the backend
      const challengeID = this.props.location.state.challengeID;
      let challengeResponse = await fetch(GlobalVariables.serverURL + "/challenges/" + challengeID + "/", { method: "GET", credentials: "include" });
      let challengeResData = await challengeResponse.json();
      let sportResponse = await fetch(GlobalVariables.serverURL + "/challenge-sports/challenges/" + challengeID + "/", { method: "GET", credentials: "include" });
      let sportResData = await sportResponse.json();

      // Only load image when image exists
      if (challengeResData.imageID != null) {
        let imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + challengeResData.imageID + "/", { method: "GET", credentials: "include" });
        let imageResData = await imageResponse.json();
        this.setState({ imageSource: "data:" + imageResData.type + ";base64, " + imageResData.data });
      }

      //prefills the Input fields with the current challenge data
      this.setState({ challengeNameHeading: challengeResData.name });
      this.setState({ challengeName: challengeResData.name });
      this.setState({ imageID: challengeResData.imageID });
      this.setState({ challengeDescription: challengeResData.description });
      this.setState({ challengeDistanceGoal: challengeResData.targetDistance });
      this.setState({ challengeStartDate: this.dateToInputFormat(challengeResData.startDate) });
      this.setState({ challengeEndDate: this.dateToInputFormat(challengeResData.endDate) });

      for (let i = 0; i < sportResData.length; i++) {
        document.querySelector('[data-sport-id="' + sportResData[i].sportID + '"][type="checkbox"]').checked = true;
      }
    }

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }
  //==================================================END COMPONENT FUNCTIONS==================================================

  //==================================================HTML OF THE COMPONENT==================================================
  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <AddHeading action={this.props.params.action} entitie="Challenge" name={this.state.challengeNameHeading} />
            <InfoMessage />
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib der Challenge einen Namen</h2>
                  <input
                    className="mg_t_2"
                    type="text"
                    value={this.state.challengeName}
                    maxLength={64}
                    onChange={this.challengeNameChange}
                    placeholder="Challenge Name"
                  ></input>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <div>
                    <h2>Wähle ein Bild für deine Challenge</h2>
                    <span className="form_input_description">
                      Das Bild repräsentiert deine Challenge auf der Startseite.
                      <br />
                      <br />
                      Das Bild sollte quadratisch sein.
                    </span>
                    <br />
                    <input id="challenge_image" className="mg_t_2" type="file" accept="image/*" onChange={this.previewImageChange}></input>
                  </div>
                  {this.state.imageSource != "" && (
                    <div className="mg_t_2">
                      <img src={this.state.imageSource} alt="Aktuelles Bild der Challenge" height={200} width={200}></img>
                    </div>
                  )}
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Beschreibe deine Challenge</h2>
                  <div className="form_input_description_content">
                    <textarea
                      className="mg_t_2"
                      type="textArea"
                      maxLength={400}
                      value={this.state.challengeDescription}
                      onChange={this.challengeDescriptionChange}
                      placeholder="Beschreibe deine Challenge"
                    ></textarea>
                  </div>
                </div>
                <div className="form_input_container pd_1 mg_t_2">
                  <h2>Definiere ein Ziel für deine Challenge</h2>
                  <span className="form_input_description">
                    Gebe hier die zu erreichende Punktzahl ein. Die Punktzahl besteht aus den geleisteten Kilometern, verrechnet mit dem jeweiligen Faktor der Sportart
                    und eventuellen Boni.
                  </span>
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
                            <input className="form_sport_number" data-sport-id={item.id} type="number" step="0.1" defaultValue={item.factor} min={1.0}></input>
                          </td>
                          <td>
                            <input className="form_table_checkbox form_sport_checkbox" data-sport-id={item.id} type="checkbox"></input>
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
                  {this.props.params.action === "edit" && <Button color="orange" txt="Änderungen speichern" type="submit" loading={this.state.loading} />}
                  {this.props.params.action === "add" && <Button color="orange" txt="Challenge erstellen" type="submit" loading={this.state.loading} />}
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
