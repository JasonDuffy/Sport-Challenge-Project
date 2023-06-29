import React, { Component } from "react";
import "./Home.css";
import Button from "../../components/ui/button/Button";
import ChallengeOverview from "../../components/ChallengeOverview/ChallengeOverview";
import GlobalVariables from "../../GlobalVariables.js";
import withRouter from "../withRouter";
import { Link } from "react-router-dom";

/**
 * Home page of the App
 *
 * @author Robin Hackh
 */
class Home extends Component {
  constructor(props) {
    super(props);
    this.state = { currentChallenge: [], pastChallenge: [], futureChallenge: [] };

    document.title = "Slash Challenge";
  }

  async componentDidMount() {
    let response = await fetch(GlobalVariables.serverURL + "/challenges/?type=current", { method: "GET", credentials: "include" });
    let resData = await response.json();
    this.setState({ currentChallenge: resData });
    response = await fetch(GlobalVariables.serverURL + "/challenges/?type=past", { method: "GET", credentials: "include" });
    resData = await response.json();
    this.setState({ pastChallenge: resData });
    response = await fetch(GlobalVariables.serverURL + "/challenges/?type=future", { method: "GET", credentials: "include" });
    resData = await response.json();
    this.setState({ futureChallenge: resData });

    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }

  componentWillUnmount(){
    document.getElementById("page_loading").style.display = "flex";
    document.getElementById("page").style.display = "none";
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
                {this.state.currentChallenge.map((item) => (
                  <li className="challenge_list_item" key={item.id}>
                    <ChallengeOverview id={item.id} />
                  </li>
                ))}
              </ul>
              <div className="center_content mg_t_2">
                <Link to="/Challenge/add" state={{ id: 0 }}>
                  <Button color="orange" txt="Challenge erstellen" />
                </Link>
              </div>
            </div>
          </div>
        </section>

        {this.state.futureChallenge.length > 0 && (
          <section className="background_lightblue">
            <div className="section_container">
              <div className="section_content">
                <div className="heading_underline_center mg_b_8">
                  <span className="underline_center">Zukünftige Challenges</span>
                </div>
                <ul className="col challenge_list">
                  {this.state.futureChallenge.map((item) => (
                    <li className="challenge_list_item" key={item.id}>
                      <ChallengeOverview id={item.id} />
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </section>
        )}

        {this.state.pastChallenge.length > 0 && (
          <section className={this.state.futureChallenge.length > 0 ? "background_white" : "background_lightblue"}>
            <div className="section_container">
              <div className="section_content">
                <div className="heading_underline_center mg_b_8">
                  <span className="underline_center">Vorherige Challenges</span>
                </div>
                <ul className="col challenge_list">
                  {this.state.pastChallenge.map((item) => (
                    <li className="challenge_list_item" key={item.id}>
                      <ChallengeOverview id={item.id} />
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </section>
        )}
      </>
    );
  }
}

export default withRouter(Home);
