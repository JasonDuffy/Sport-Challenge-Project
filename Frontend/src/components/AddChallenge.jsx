import React from "react";
import { useState } from "react";
import Button from "./Button";
import "./css/AddChallenge.css";

/**
 * Add Challenge page of the App
 * 
 * @author Robin Hackh
 */
function AddChallenge() {
  const [challengeName, setChallengeName] = useState("");
  const [challengeImage, setChallengeImage] = useState("");
  const [challengeDescription, setChallengeDescription] = useState("");
  const [challengeDistanceGoal, setChallengeDistanceGoal] = useState("");
  const [challengeStartDate, setChallengeStartDate] = useState("");
  const [challengeEndDate, setChallengeEndDate] = useState("");

  function challengeNameChange(event) {
    setChallengeName(event.target.value);
  }

  function challengeImageChange(event) {
    setChallengeImage(event.target.value);
  }

  function challengeDescriptionChange(event) {
    setChallengeDescription(event.target.value);
  }

  function challengeDistanceGoalChange(event) {
    setChallengeDistanceGoal(event.target.value);
  }

  function challengeStartDateChange(event) {
    setChallengeStartDate(event.target.value);
  }

  function challengeEndDateChange(event) {
    setChallengeEndDate(event.target.value);
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <div className="heading_underline_center mg_b_10">
            <span className="underline_center">Challenge hinzufügen</span>
          </div>
          <div className="form_container">
            <form>
              <div className="form_input_container pd_1">
                <h2>Gib der Challenge einen Namen</h2>
                <input
                  className="mg_t_2"
                  type="text"
                  value={challengeName}
                  onChange={challengeNameChange}
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
                <input className="mg_t_2" type="file" value={challengeImage} onChange={challengeImageChange}></input>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Beschreibe deine Challenge</h2>
                <div className="form_input_description_content">
                  <textarea
                    className="mg_t_2"
                    type="textArea"
                    value={challengeDescription}
                    onChange={challengeDescriptionChange}
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
                  value={challengeDistanceGoal}
                  onChange={challengeDistanceGoalChange}
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
                    <tr>
                      <td>Fahrrad</td>
                      <td>0</td>
                      <td>
                        <input className="form_table_checkbox" type="checkbox"></input>
                      </td>
                    </tr>
                    <tr>
                      <td>Laufen</td>
                      <td>3</td>
                      <td>
                        <input className="form_table_checkbox" type="checkbox"></input>
                      </td>
                    </tr>
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
                      value={challengeStartDate}
                      onChange={challengeStartDateChange}
                    ></input>
                    <span className="form_input_date_separator mg_x_1">----</span>
                    <input
                      id="form_input_end_date"
                      type="datetime-local"
                      value={challengeEndDate}
                      onChange={challengeEndDateChange}
                    ></input>
                  </div>
                </div>
              </div>
              <div className="center_content mg_t_2">
                <Button color="orange" txt="Challenge erstellen" type="submit"/>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default AddChallenge;
