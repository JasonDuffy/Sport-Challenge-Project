import React, { Component } from "react";
import { useState } from "react";
import Button from "./Button";
import "./css/AddChallenge.css";

/**
 * Add Challenge page of the App
 *
 * @author Robin Hackh
 */
class AddChallenge extends Component {
  constructor() {
    super();
    this.state = {
      challengeName: "",
      challengeDescription: "",
      challengeDistanceGoal: 0,
      challengeStartDate: "",
      challengeEndDate: "",
      allSport: []
    };

    this.challengeNameChange = this.challengeNameChange.bind(this);
    this.challengeDescriptionChange = this.challengeDescriptionChange.bind(this);
    this.challengeDistanceGoalChange = this.challengeDistanceGoalChange.bind(this);
    this.challengeStartDateChange = this.challengeStartDateChange.bind(this);
    this.challengeEndDateChange = this.challengeEndDateChange.bind(this);
    this.submitHandle = this.submitHandle.bind(this);
  }

  challengeNameChange(event){
    this.setState({challengeName: event.target.value});
  }

  challengeDescriptionChange(event){
    this.setState({challengeDescription: event.target.value});
  }
  challengeDistanceGoalChange(event){
    this.setState({challengeDistanceGoal: event.target.value});
  }
  challengeStartDateChange(event){
    this.setState({challengeStartDate: event.target.value});
  }
  challengeEndDateChange(event){
    this.setState({challengeEndDate: event.target.value});
  }

  async submitHandle(event) {
    event.preventDefault();
    let response = await fetch("http://localhost:8081/sport/all/", { method: "GET", credentials: "include" });
    let resData = await response.json();
    console.log(resData);
    const challengeImageEl = document.getElementById("challenge_image");
    const sportCheckboxEl = document.getElementsByClassName("form_sport_checkbox");
  
    for (const iterator of sportCheckboxEl) {
      console.log("value: " + iterator.checked + "  id: " + iterator.dataset.sportId);
    }

    console.log("Name " + this.state.challengeName);
    console.log("Iamge " + challengeImageEl.files[0].size);
    console.log("Description " + this.state.challengeDescription);
    console.log("Distance Goal " + this.state.challengeDistanceGoal);
    console.log("Start date " + this.state.challengeStartDate);
    console.log("End date " + this.state.challengeEndDate);
  }

  async componentDidMount(){
    let response = await fetch("http://localhost:8081/sport/all/", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({allSport: resData});
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center">Challenge hinzufügen</span>
            </div>
            <div className="form_container">
              <form onSubmit={this.submitHandle}>
                <div className="form_input_container pd_1">
                  <h2>Gib der Challenge einen Namen</h2>
                  <input
                    className="mg_t_2"
                    type="text"
                    value={this.state.challengeName}
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
                  <input id="challenge_image" className="mg_t_2" type="file"></input>
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
                    min={0}
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
                      {this.state.allSport.map(item => (
                      <tr key={item.id}>
                      <td>{item.name}</td>
                      <td>{item.factor}</td>
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
                        value={this.state.challengeStartDate}
                        onChange={this.challengeStartDateChange}
                      ></input>
                      <span className="form_input_date_separator mg_x_1">----</span>
                      <input
                        id="form_input_end_date"
                        type="datetime-local"
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

export default AddChallenge;
