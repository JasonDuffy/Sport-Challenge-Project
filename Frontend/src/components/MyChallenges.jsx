import React from "react";
import MyChallengeOverview from "./MyChallengeOverview";
import "./css/MyChallenges.css";
import { Component } from "react";

class MyChallenges extends Component {
  constructor(props) {
    super(props);

    this.state = {
      challengeIDs: [],
    };
  }

  async componentDidMount() {
    let loggedInMemberResponse = await fetch("http://localhost:8081/members/loggedIn/", { method: "GET", credentials: "include" });
    let loggedInMemberResData = await loggedInMemberResponse.json();
    //EINFÜGEN FÜR DYNAMISCHE GENERIERUNG -> AKTUELL NICH MÖGLICH, DA MAX MUSTERMANN KEINER CHALLENGE ANGEHÖRT
    //let challengeIDsResponse = await fetch("http://localhost:8081/challenges/members/" + loggedInMemberResData.userID + "/", { method: "GET", credentials: "include" });
    let challengeIDsResponse = await fetch("http://localhost:8081/challenges/members/3/", { method: "GET", credentials: "include" });
    let challengeIDsResData = await challengeIDsResponse.json();
    this.setState({ challengeIDs: challengeIDsResData});
  }

  render() {
    return (
      <section className="background_white">
        <div className="section_container">
          <div className="section_content">
            <div className="heading_underline_center mg_b_10">
              <span className="underline_center">Meine Challenges</span>
            </div>
            <ul className="col my_challenge_list">
              {this.state.challengeIDs.map((item) => (
                <li key={item} className="my_challenge_list_item">
                  <MyChallengeOverview id={item} />
                </li>
              ))}
            </ul>
          </div>
        </div>
      </section>
    );
  }
}

export default MyChallenges;
