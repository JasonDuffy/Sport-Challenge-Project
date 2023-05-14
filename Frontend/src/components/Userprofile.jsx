import React, { Component } from "react";
import withRouter from "./withRouter";
import Button from "./Button";
import "./css/Form.css";
/**
 * Page that shows the user profile of the current user and allows editing
 * 
 * @author Jason Patrick Duffy
 */
class Userprofile extends Component {
    constructor(props) {
        super(props);

        //state for the input elements
        this.state = {
            userID: 0,
            imageID: 0,
            email: "",
            firstName: "",
            lastName: "",
            image: "",
        };

        //bind is needed for changing the state
        this.userFirstNameChange = this.userFirstNameChange.bind(this);
        this.userLastNameChange = this.userLastNameChange.bind(this);
        this.userImageChange = this.userImageChange.bind(this);

        this.submitHandler = this.submitHandler.bind(this);

        this.pageLoad();
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

    //Called by constructor when the page is set up 
    pageLoad() {
        fetch("http://localhost:8081/members/loggedIn/", { method: "GET", credentials: "include" })
            .then((response) => {
                if (response.ok) {
                    response.json().then((resData => {
                        this.setState({ userID: resData.userID });
                        this.setState({ email: resData.email });
                        this.setState({ firstName: resData.firstName });
                        this.setState({ lastName: resData.lastName }, () => {
                            this.updateHeading();
                        });
                        this.setState({ imageID: resData.imageID });
                        this.loadImage(resData.imageID);
                    }));


                } else {
                    console.log("User is not logged in");
                }
            })
            .catch((error) => {
                console.log("Something went wrong");
            });
    }

    // Loads image from given ID and places it in state variable
    loadImage(imageID) {
        fetch("http://localhost:8081/images/" + imageID + "/", { method: "GET", credentials: "include" })
            .then((response) => {
                if (response.ok) {
                    response.json().then((resData => {
                        this.setState({ image: "data:" + resData.type + ";base64, " + resData.data });
                    }));
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
    showSuccessMessage(message){
        const infoContainer = document.getElementById("form_info_container");
        const infoMessage = document.getElementById("form_info_message");
        infoContainer.classList.add("success");
        infoMessage.innerText = message;
        window.scrollTo(0, 0);
    }

    // Updates the greeting to display the current user's name
    updateHeading(){
        document.getElementById("headingText").innerHTML = "Willkommen zurück, " + this.state.firstName + " " + this.state.lastName;
    }

    // Called when submit button is pressed
    submitHandler(event) {
        event.preventDefault();

        const infoContainer = document.getElementById("form_info_container");
        infoContainer.classList.remove("error");
        infoContainer.classList.remove("success");

        const userImage = document.getElementById("user_image");

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

        if (userImage.files[0] != null){
            // Loads image
            fetch("http://localhost:8081/images/", { method: "POST", body: imageBodyData, credentials: "include" })
                .then((response) => {
                    if (response.ok) {
                        response.json().then(resData => {
                            this.setState({ image: "data:" + resData.type + ";base64, " + resData.data });
                            this.setState({ imageID: resData.id }, () => {
                                this.upload();
                            })
                        });
                    }
                })
                .catch((error) => {
                    console.log("Something went wrong, " + error);
                });
        }
        else {
            this.upload();
        }
    }

    upload(){
        let userJsonObj = {};
        userJsonObj.email = this.state.email;
        userJsonObj.firstName = this.state.firstName;
        userJsonObj.lastName = this.state.lastName;
        userJsonObj.imageID = this.state.imageID;

        fetch("http://localhost:8081/members/" + this.state.userID + "/", { method: "PUT", body: JSON.stringify(userJsonObj), credentials: "include", headers: { 'Content-Type': 'application/json' } })
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

    render() {
        return (
            <section className="background_white">
                <div className="section_container">
                    <div className="section_content">
                        <div className="heading_underline_center mg_b_10">
                            <span className="underline_center" id="headingText">Willkommen zurück, Platzhalter</span>
                        </div>
                        <div id="form_info_container" className="pd_1 mg_b_2">
                            <span id="form_info_message"></span>
                        </div>
                        <div className="form_container">
                            <form onSubmit={this.submitHandler}>
                                <div className="centered pd_1">
                                    <h2>Wähle ein Bild für deine Visage</h2>
                                    <div className="">
                                        <img src={this.state.image} alt="User Image" className=""></img>
                                    </div>
                                    <span className="form_input_description">
                                        Das Bild sollte quadratisch sein.
                                    </span>
                                    <br />
                                    <input id="user_image" className="mg_t_2" type="file" accept="image/*"></input>
                                </div>
                                <div className="form_input_container pd_1 mg_t_2">
                                    <h2>Vorname</h2>
                                    <input
                                        className="mg_t_2"
                                        type="text"
                                        value={this.state.firstName}
                                        maxLength={25}
                                        onChange={this.userFirstNameChange}
                                        placeholder="Vorname"
                                    ></input>
                                </div>
                                <div className="form_input_container pd_1 mg_t_2">
                                    <h2>Nachname</h2>
                                    <input
                                        className="mg_t_2"
                                        type="text"
                                        value={this.state.lastName}
                                        maxLength={25}
                                        onChange={this.userLastNameChange}
                                        placeholder="Nachname"
                                    ></input>
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

export default withRouter(Userprofile);