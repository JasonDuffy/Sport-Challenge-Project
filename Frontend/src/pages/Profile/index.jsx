import React, { Component } from "react";
import Button from "../../components/ui/button/Button";
import "../../assets/css/form.css";
import "./Profile.css";
import GlobalVariables from "../../GlobalVariables.js";
/**
 * Page that shows the user profile of the current user and allows editing
 *
 * @author Jason Patrick Duffy, Robin Hackh
 */
class Userprofile extends Component {
  constructor(props) {
    super(props);

    document.title = "Slash Challenge - Mein Profil";

    //state for the input elements
    this.state = {
      userID: 0,
      imageID: 0,
      email: "",
      firstName: "",
      lastName: "",
      image: "",
      communication: false,
    };

    //bind is needed for changing the state
    this.userFirstNameChange = this.userFirstNameChange.bind(this);
    this.userLastNameChange = this.userLastNameChange.bind(this);
    this.userImageChange = this.userImageChange.bind(this);
    this.userCommunicationChange = this.userCommunicationChange.bind(this);

    this.submitHandler = this.submitHandler.bind(this);
  }

  userFirstNameChange(event) {
    this.setState({ firstName: event.target.value });
  }
  userLastNameChange(event) {
    this.setState({ lastName: event.target.value });
  }
  userImageChange(event) {
    this.setState({ image: event.target.value });
  }
  userCommunicationChange(event) {
    this.setState({ communication: event.target.checked });
  }

  //Called automatically when the page is loaded
  componentDidMount() {
    // Get information of logged in user, save it in state variables and load image
    fetch(GlobalVariables.serverURL + "/members/loggedIn/", { method: "GET", credentials: "include" })
      .then((response) => {
        if (response.ok) {
          response.json().then((resData) => {
            this.setState({ userID: resData.userID });
            this.setState({ email: resData.email });
            this.setState({ firstName: resData.firstName });
            this.setState({ lastName: resData.lastName }, () => {
              this.updateHeading();
            });
            if (resData.imageID != 0) {
              this.setState({ imageID: resData.imageID });
              this.loadImage(resData.imageID);
            } else {
              this.setState({ image: require(`../../assets/images/Default-User.png`) });
            }
            this.setState({ communication: resData.communication }, () => {
              const checkBox = document.getElementById("communicationCheckBox");
              checkBox.checked = this.state.communication;
            });
          });
        } else {
          console.log("User is not logged in");
        }
      })
      .catch((error) => {
        console.log("Something went wrong");
      });

    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }

  componentWillUnmount() {
    document.getElementById("page_loading").style.display = "flex";
    document.getElementById("page").style.display = "none";
  }

  // Loads image from given ID and places it in state variable
  loadImage(imageID) {
    fetch(GlobalVariables.serverURL + "/images/" + imageID + "/", { method: "GET", credentials: "include" })
      .then((response) => {
        if (response.ok) {
          response.json().then((resData) => {
            this.setState({ image: "data:" + resData.type + ";base64, " + resData.data });
          });
        } else {
          console.log("Image was not found");
        }
      })
      .catch((error) => {
        console.log("Something went wrong");
      });
  }

  // Info message with red background
  showErrorMessage(message) {
    const infoContainer = document.getElementById("form_info_container");
    const infoMessage = document.getElementById("form_info_message");
    infoContainer.classList.add("error");
    infoMessage.innerText = message;
    window.scrollTo(0, 0);
  }

  // Info message with green background
  showSuccessMessage(message) {
    const infoContainer = document.getElementById("form_info_container");
    const infoMessage = document.getElementById("form_info_message");
    infoContainer.classList.add("success");
    infoMessage.innerText = message;
    window.scrollTo(0, 0);
  }

  // Updates the greeting to display the current user's name
  updateHeading() {
    document.getElementById("headingText").innerHTML = "Willkommen zurück, " + this.state.firstName + " " + this.state.lastName;
  }

  // Called when submit button is pressed
  submitHandler(event) {
    event.preventDefault();

    const infoContainer = document.getElementById("form_info_container");
    infoContainer.classList.remove("error");
    infoContainer.classList.remove("success");

    let userImage = document.getElementById("user_image");

    //Checks image size
    if (userImage.files[0] != null && userImage.files[0].size > 10000000) {
      this.showErrorMessage("Das Bild darf nicht größer als 10Mb sein!");
      return;
    }
    if (userImage.files[0] != null && /^image/.test(userImage.files[0].type) === false) {
      this.showErrorMessage("Es sind nur Bilder zum Hochladen erlaubt!");
      return;
    }

    let imageBodyData = new FormData();
    imageBodyData.append("file", userImage.files[0]);

    if (userImage.files[0] != null) {
      // Uploads image
      fetch(GlobalVariables.serverURL + "/images/", { method: "POST", body: imageBodyData, credentials: "include" })
        .then((response) => {
          if (response.ok) {
            response.json().then((resData) => {
              this.setState({ image: "data:" + resData.type + ";base64, " + resData.data });
              this.setState({ imageID: resData.id }, () => {
                userImage.value = null; //Clear upload form
                this.upload();
              });
            });
          }
        })
        .catch((error) => {
          console.log("Something went wrong, " + error);
        });
    } else {
      this.upload();
    }
  }

  // Uploads user profile information to server
  upload() {
    let userJsonObj = {};
    userJsonObj.email = this.state.email;
    userJsonObj.firstName = this.state.firstName;
    userJsonObj.lastName = this.state.lastName;
    userJsonObj.imageID = this.state.imageID;
    userJsonObj.communication = this.state.communication;

    fetch(GlobalVariables.serverURL + "/members/" + this.state.userID + "/", {
      method: "PUT",
      body: JSON.stringify(userJsonObj),
      credentials: "include",
      headers: { "Content-Type": "application/json" },
    })
      .then((response) => {
        if (response.ok) {
          this.showSuccessMessage("Dein Profil wurde erfolgreich aktualisiert!");
          this.updateHeading();
        }
      })
      .catch((error) => {
        console.log("Something went wrong, " + error);
      });
  }

  // HTML script to render
  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center" id="headingText">
                Willkommen zurück, Platzhalter
              </span>
            </div>
            <div id="form_info_container" className="pd_1 mg_b_2">
              <span id="form_info_message"></span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandler}>
                <div className="centered pd_1">
                  <div className="center_content">
                    <img src={this.state.image} alt="User Image" className="round_image"></img>
                  </div>
                </div>
                <div className="form_input_container pd_1">
                  <h2>Wähle ein Profilbild aus.</h2>
                  <span className="form_input_description">Das Bild sollte quadratisch sein.</span>
                  <br />
                  <input id="user_image" className="mg_t_2" type="file" accept="image/*"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_1">
                  <h2>Vorname</h2>
                  <input className="mg_t_2" type="text" value={this.state.firstName} maxLength={25} onChange={this.userFirstNameChange} placeholder="Vorname"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_1">
                  <h2>Nachname</h2>
                  <input className="mg_t_2" type="text" value={this.state.lastName} maxLength={25} onChange={this.userLastNameChange} placeholder="Nachname"></input>
                </div>
                <div className="form_input_container pd_1 mg_t_1">
                  <label className="checkbox">
                    <input id="communicationCheckBox" onChange={this.userCommunicationChange} type="checkbox"></input>
                    <a>Ich möchte bei neuen Challenges, Boni und Inaktivität per E-Mail kontaktiert werden.</a>
                  </label>
                </div>
                <div className="center_content mg_t_2">
                  <Button color="orange" txt="Änderungen speichern" type="submit" />
                </div>
              </form>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default Userprofile;
