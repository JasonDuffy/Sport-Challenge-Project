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

      componentDidMount(){
        console.log(this.props.params.action);
      }

      submitHandle(event) {

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
