import React, { Component } from "react";
import "./css/Home.css";
import Button from "./Button";
import ChallengeOverview from "./ChallengeOverview";

/**
 * Home page of the App
 * 
 * @author Robin Hackh
 */
class Home extends Component {
  constructor() {
    super();
    this.state = { currentChallenge: [], pastChallenge: [] };
  }

  async componentDidMount() {
    let response = await fetch("http://localhost:8081/challenges/?type=current", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({ currentChallenge: resData });
    response = await fetch("http://localhost:8081/challenges/?type=past", { method: "GET", credentials: "include" });
    resData = await response.json();
    this.setState({ pastChallenge: resData });

    const pageLoading = document.getElementById("page_loading");
    pageLoading.parentNode.removeChild(pageLoading);
    document.getElementById("page").style.display = "block";
  }

  render() {
    return (
      <>
        <section className="background_white">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_8">
                <span className="underline_center">Aktive Challenges</span>
              </div>
              <ul className="col challenge_list">
                {this.state.currentChallenge.map(item => (
                  <li className="challenge_list_item" key={item.id}>
                    <ChallengeOverview id={item.id}/>
                  </li>
                ))}
              </ul>
              <div className="center_content mg_t_2">
                <Button color="orange" txt="Neue Challenge" />
              </div>
            </div>
          </div>
        </section>

        <section className="background_lightblue">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_8">
                <span className="underline_center">Vorhergegangene Challenges</span>
              </div>
              <ul className="col challenge_list">
              {this.state.pastChallenge.map(item => (
                  <li className="challenge_list_item" key={item.id}>
                    <ChallengeOverview id={item.id}/>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </section>
      </>
    );
  }
}

export default Home;
