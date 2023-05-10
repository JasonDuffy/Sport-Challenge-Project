import React, { Component } from "react";
import { useState } from "react";
import Button from "./Button";
import "./css/AddBonus.css";
import "./css/Form.css";

class AddBonus extends Component(){

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
                        maxLength={15}
                        onChange={this.challengeNameChange}
                        placeholder="Challenge Name"
                      ></input>
                    </div>
                    </form>
                </div>
                </div>
            </div>
        </section>
        )
    }
}

export default AddBonus;