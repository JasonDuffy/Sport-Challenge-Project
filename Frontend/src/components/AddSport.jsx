import React, { Component } from "react";
import { useState } from "react";
import Button from "./Button";
import "./css/AddSport.css";
import "./css/Form.css";

/**
 * Add Sport page of the App
 *
 * @author Mason Schönherr
 */
class AddSport extends Component{
    constructor() {
        super();
        //state for the input elements
        this.state = {
          sportName: "",
          sportFactor: 1,
          allSport: [],
        };
    
        //bind is needed for changing the state
        this.sportNameChange = this.sportNameChange.bind(this);
        this.sportFaktorChange = this.sportFaktorChange.bind(this);
        this.submitHandle = this.submitHandle.bind(this);
      }

      submitHandle(event){

      }

      sportNameChange(event) {
        this.setState({ sportName: event.target.value });
      }
      sportFaktorChange(event) {
        if (event.target.value >= 1) {
          this.setState({ sportFaktor: event.target.value });
        } else if (event.target.value == 0) {
          this.setState({ sportFaktor: 1 });
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
                <div className="form_container">
                  <form onSubmit={this.submitHandle}>
                    <div className="form_input_container pd_1">
                      <h2>Welche Sportart?</h2>
                      <input
                        className="mg_t_2"
                        type="text"
                        value={this.state.sportName}
                        maxLength={15}
                        onChange={this.sportNameChange}
                        placeholder="Sport Name"
                      ></input>
                    </div>
                    <div className="form_input_container pd_1 mg_t_2">
                        <h2>Wähle ein Bild für deine Sportart</h2>
                        <span className="form_input_description">
                         Das Bild repräsentiert deine Sportart
                    <br />
                    <br />
                         Das Bild sollte quadratisch sein.
                        </span>
                    <br />
                        <input id="sport_image" className="mg_t_2" type="file"></input>
                    </div>
                    <div className="form_input_container pd_1 mg_t_2">
                      <h2>Welchen Faktor soll der Sport haben?</h2>
                      <br />
                      <input
                        className="mg_t_2"
                        type="number"
                        value={this.state.sportFactor}
                        onChange={this.sportFaktorChange}
                        placeholder="Faktor"
                      ></input>
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

export default AddSport;