import React, { Component, Fragment } from "react";
import { Link } from "react-router-dom";
import ChallengeTeamPanel from "../../components/ChallengeTeamPanel/ChallengeTeamPanel";
import withRouter from "../withRouter";
import "./Challenge.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMedal, faPencil } from "@fortawesome/free-solid-svg-icons";
import ChallengeTeamPanelAreaGraph from "../../components/ChallengeTeamPanelAreaGraph/ChallengeTeamPanelAreaGraph";
import ChallengeTeamBarGraph from "../../components/ChallengeTeamBarGraph/ChallengeTeamBarGraph";
import ChallengeMembers from "../../components/ChallengeMembers/ChallengeMembers";
import Button from "../../components/ui/button/Button";
import GlobalVariables from "../../GlobalVariables.js";
import ChallengeSportsRow from "../../components/ChallengeSportsRow/ChallengeSportsRow";
import ChallengeBonusRow from "../../components/ChallengeBonusRow/ChallengeBonusRow";

/**
 * Displays all information for a given challenge
 * @author Jason Patrick Duffy, Robin Hackh
 */
class Challenge extends Component {
  constructor(props) {
    super(props);

    this.state = {
      challengeID: props.location.state.challengeID,
      challenge: [],
      distance: 0,
      activities: [],
      pastBonuses: [],
      currentBonuses: [],
      futureBonuses: [],
      teams: [],
      sports: [],
      image: "",
      teamID: 0,
      challengeLoaded: false,
      teamsLoaded: false,
      imageLoaded: false,
      activitiesLoaded: false,
      pastBonusesLoaded: false,
      currentBonusesLoaded: false,
      futureBonusesLoaded: false,
      sportsLoaded: false,
    };

    this.teamTableMaker = this.teamTableMaker.bind(this);
    this.bonusTableMaker = this.bonusTableMaker.bind(this);
    this.distanceDisplay = this.distanceDisplay.bind(this);
    this.distanceDisplayPercentage = this.distanceDisplayPercentage.bind(this);

    this.teamChange = this.teamChange.bind(this);
  }

  async componentDidMount() {
    // CHALLENGE FETCH --------------------------------------
    let challenge = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/", { method: "GET", credentials: "include" });
    let challengeResData = await challenge.json();

    // Convert challenge dates to usable dates
    // Regex for date conversion
    const dateRegex = "(\\d{2})\\.(\\d{2})\\.(\\d{4})\\,(\\d{2})\\:(\\d{2})";

    // Convert string date into JS date and add correctly formatted string to object
    challengeResData.rawStartDate = challengeResData.startDate; // Save for later use
    challengeResData.rawEndDate = challengeResData.endDate;

    let date = challengeResData.startDate.match(dateRegex);
    challengeResData.startDate = new Date(date[3], date[2] - 1, date[1]); // Separate date and time
    date = challengeResData.endDate.match(dateRegex);
    challengeResData.endDate = new Date(date[3], date[2] - 1, date[1]);

    document.title = "Slash Challenge - " + challengeResData.name;

    this.setState({ challenge: challengeResData }, () => {
      this.setState({ challengeLoaded: true });
    });

    // TEAMS FETCH --------------------------------------
    let teams = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/teams/", { method: "GET", credentials: "include" });
    let teamsResData = await teams.json();

    // Add avgDistance and image to each team
    if (teamsResData.length > 0) {
      for (const team of teamsResData) {
        let avg = await fetch(GlobalVariables.serverURL + "/teams/" + team.id + "/avgDistance/", { method: "GET", credentials: "include" });
        let avgResData = await avg.text();
        team.avgDistance = parseFloat(avgResData).toFixed(2);
        if (isNaN(team.avgDistance)) {
          team.avgDistance = (0).toFixed(2);
        }

        if (team.imageID !== 0) {
          let imageResponse = await fetch(GlobalVariables.serverURL + "/images/" + team.imageID + "/", { method: "GET", credentials: "include" });
          let imageResData = await imageResponse.json();
          team.imageSource = "data:" + imageResData.type + ";base64, " + imageResData.data;
        } else {
          team.imageSource = require("../../assets/images/Default-Team.png");
        }
      }

      this.setState({ teams: teamsResData }, () => {
        this.setState({ teamID: teamsResData.at(0).id }, () => {
          this.setState({ teamsLoaded: true });
        });
      });
    } else {
      this.setState({ teamsLoaded: true });
    }

    // CHALLENGE IMAGE FETCH --------------------------------------
    if (this.state.challenge.imageID != null) {
      let image = await fetch(GlobalVariables.serverURL + "/images/" + this.state.challenge.imageID + "/", { method: "GET", credentials: "include" });
      let imageResData = await image.json();

      this.setState({ image: "data:" + imageResData.type + ";base64, " + imageResData.data }, () => {
        this.setState({ imageLoaded: true });
      });
    } else {
      this.setState({ image: require(`../../assets/images/Default-Challenge.png`) }, () => {
        this.setState({ imageLoaded: true });
      });
    }

    // ACTIVITIES FETCH --------------------------------------
    let challengeActivities = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/activities/", {
      method: "GET",
      credentials: "include",
    });
    let challengeActivitiesResData = await challengeActivities.json();

    this.setState({ activities: challengeActivitiesResData }, () => {
      this.setState({ activitiesLoaded: true });
    });

    // PAST BONUSES FETCH --------------------------------------
    let past = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/bonuses/?type=past", { method: "GET", credentials: "include" });
    let pastResData = await past.json();

    this.setState({ pastBonuses: pastResData }, () => {
      this.setState({ pastBonusesLoaded: true });
    });

    // CURRENT BONUSES FETCH --------------------------------------
    let current = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/bonuses/?type=current", { method: "GET", credentials: "include" });
    let currentResData = await current.json();

    this.setState({ currentBonuses: currentResData }, () => {
      this.setState({ currentBonusesLoaded: true });
    });

    // FUTURE BONUSES FETCH --------------------------------------
    let future = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/bonuses/?type=future", { method: "GET", credentials: "include" });
    let futureResData = await future.json();

    this.setState({ futureBonuses: futureResData }, () => {
      this.setState({ futureBonusesLoaded: true });
    });

    // CHALLENGE SPORTS FETCH --------------------------------------
    let sports = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/sports/", { method: "GET", credentials: "include" });
    let sportsResData = await sports.json();

    this.setState({ sports: sportsResData }, () => {
      this.setState({ sportsLoaded: true });
    });

    // CHALLENGE DISTANCE FETCH --------------------------------------
    let challengeDistance = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/distance/", { method: "GET", credentials: "include" });
    let challengeDistanceResData = await challengeDistance.json();

    this.setState({ distance: challengeDistanceResData });

    //Select first teamTab
    document.querySelector('[data-team-id="' + this.state.teamID + '"]').classList.add("selected_panel_tab");

    // REMOVE LOADING ICON  --------------------------------------
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }

  componentWillUnmount() {
    document.getElementById("page_loading").style.display = "flex";
    document.getElementById("page").style.display = "none";
  }

  /**
   * Decides if a medal or the position should be displayed
   * @param position Position for which the decision should be taken
   * @returns Medal or position
   */
  medalDecider(position) {
    switch (position) {
      case 1:
        return <FontAwesomeIcon icon={faMedal} color="gold" />;
      case 2:
        return <FontAwesomeIcon icon={faMedal} color="silver" />;
      case 3:
        return <FontAwesomeIcon icon={faMedal} color="#cd7f32" />;
      default:
        return position;
    }
  }

  /**
   * Creates a new table row and returns it
   * @param position Leaderboard position of the team
   * @param team Team DTO object with added avgDistance
   * @returns New table row
   */
  teamRowMaker(position, team) {
    return (
      <tr key={position}>
        <td>{this.medalDecider(position)}</td>
        <td>{team.name}</td>
        <td>{team.avgDistance}</td>
      </tr>
    );
  }

  /**
   * Creates the team table
   * @returns Team table in div
   */
  teamTableMaker() {
    let position = 1;

    const data = structuredClone(this.state.teams);

    data.sort(function (a, b) {
      return b.avgDistance - a.avgDistance;
    });

    return (
      <div>
        <table className="last_activites_table">
          <thead>
            <tr>
              <th>Position</th>
              <th>Name</th>
              <th>Durchschnittliche Punktzahl</th>
            </tr>
          </thead>
          <tbody>
            {data.map((item) => this.teamRowMaker(position++, item))}
            {data.length === 0 && (
              <tr>
                <td colSpan={3}>
                  <span>Zur Zeit nehmen keine Teams teil.</span>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    );
  }

  /**
   * Creates the bonus table
   * @returns Bonus table in div
   */
  bonusTableMaker(type) {
    if (type == "past") {
      return (
        <div>
          <table className="last_activites_table">
            <thead>
              <tr>
                <th>Bonus</th>
                <th>Beschreibung</th>
                <th>Sportarten</th>
                <th>Beginn</th>
                <th>Ende</th>
                <th>Faktor</th>
              </tr>
            </thead>
            <tbody>
              {this.state.pastBonuses.map((item) => (
                <ChallengeBonusRow key={item.id} type={type} bonus={structuredClone(item)} />
              ))}
            </tbody>
          </table>
        </div>
      );
    } else if (type == "current") {
      return (
        <div>
          <table className="last_activites_table">
            <thead>
              <tr>
                <th>Bonus</th>
                <th>Beschreibung</th>
                <th>Sportarten</th>
                <th>Beginn</th>
                <th>Ende</th>
                <th>Faktor</th>
                <th>Aktion</th>
              </tr>
            </thead>
            <tbody>
              {this.state.currentBonuses.map((item) => (
                <ChallengeBonusRow key={item.id} type={type} bonus={structuredClone(item)} />
              ))}
              {this.state.currentBonuses.length === 0 && (
                <tr>
                  <td colSpan={7}>
                    <span>Zur Zeit läuft keine Bonusaktion.</span>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      );
    } else {
      return (
        <div>
          <table className="last_activites_table">
            <thead>
              <tr>
                <th>Bonus</th>
                <th>Beschreibung</th>
                <th>Sportarten</th>
                <th>Beginn</th>
                <th>Ende</th>
                <th>Faktor</th>
              </tr>
            </thead>
            <tbody>
              {this.state.futureBonuses.map((item) => (
                <ChallengeBonusRow key={item.id} type={type} bonus={structuredClone(item)} />
              ))}
            </tbody>
          </table>
        </div>
      );
    }
  }

  /**
   * Creates the sports table
   * @returns Sports table in div
   */
  sportTableMaker() {
    return (
      <div>
        <table className="last_activites_table">
          <thead>
            <tr>
              <th>Sportart</th>
              <th>Faktor</th>
              <th>Effektiver Faktor gerade</th>
            </tr>
          </thead>
          <tbody>
            {this.state.sports.map((item) => (
              <ChallengeSportsRow key={item.id} challengeID={this.state.challengeID} sport={structuredClone(item)} />
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  /**
   * Returns the computed distance as a formatted string
   * @returns Computed distance in string
   */
  distanceDisplay() {
    let distanceString = this.state.distance + " / " + this.state.challenge.targetDistance;
    return distanceString;
  }

  distanceDisplayPercentage() {
    let percentage = ((this.state.distance * 100) / this.state.challenge.targetDistance).toFixed(2);
    return percentage + " %";
  }

  teamChange(event) {
    let targetEl = event.target;
    if (targetEl.tagName === "IMG") targetEl = targetEl.parentNode;

    document.querySelector('[data-team-id="' + this.state.teamID + '"]').classList.remove("selected_panel_tab");
    targetEl.classList.add("selected_panel_tab");

    this.setState({ teamID: targetEl.dataset.teamId });
  }

  render() {
    if (
      !this.state.challengeLoaded ||
      !this.state.teamsLoaded ||
      !this.state.imageLoaded ||
      !this.state.activitiesLoaded ||
      !this.state.pastBonusesLoaded ||
      !this.state.currentBonusesLoaded ||
      !this.state.futureBonusesLoaded ||
      !this.state.sportsLoaded
    )
      return;

    return (
      <>
        <section className="background_white">
          <div className="section_container">
            <div className="section_content">
              <div className="challenge_image_centered mg_t_5">
                <div className="challenge_image_container">
                  <div className="challenge_image_overlay">
                    <div className="challenge_overlay_edit">
                      <Link to="/challenge/edit" state={{ id: this.state.challengeID }}>
                        <FontAwesomeIcon icon={faPencil} size="2x" />
                      </Link>
                    </div>
                    <div className="challenge_overlay_heading mg_b_2">
                      <div className="challenge_overlay_name">{this.state.challenge.name}</div>
                      <div className="challenge_overlay_date">
                        <span>
                          {this.state.challenge.rawStartDate.split(",")[0] + ", " + this.state.challenge.rawStartDate.split(",")[1] + " Uhr "}-{" "}
                          {this.state.challenge.rawEndDate.split(",")[0] + ", " + this.state.challenge.rawEndDate.split(",")[1] + " Uhr"}
                        </span>
                      </div>
                    </div>

                    <div className="challenge_overlay_distance mg_b_2">
                      <div className="challenge_percentage">
                        <this.distanceDisplayPercentage />
                      </div>
                      <this.distanceDisplay />
                      <br />
                      Kilometer
                    </div>
                    <div className="challenge_overlay_description">{this.state.challenge.description}</div>
                  </div>
                  <div className="challenge_image">
                    <img src={this.state.image} alt="Bild der Challenge"></img>
                  </div>
                </div>
              </div>
              <div className="center_content mg_t_2">
                <Link to="/team/add" state={{ id: 0, challengeID: this.state.challengeID }}>
                  <Button color="orange" txt="Challenge beitreten" />
                </Link>
              </div>
            </div>
          </div>
        </section>

        <section className="background_lightblue">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center">
                <span className="underline_center" id="memberText">
                  Teilnehmende Teams
                </span>
                <div className="mg_t_6">
                  <div className="challengeMembers">
                    {this.state.teams.map((item) => (
                      <ChallengeMembers key={item.id} team={structuredClone(item)} />
                    ))}
                  </div>
                  {this.state.teams.length === 0 && <span>Zur Zeit nehmen keine Teams teil.</span>}
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="background_white">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center">
                <span className="underline_center" id="bonusText">
                  Laufende Bonusaktionen
                </span>
                <div className="mg_t_6">{this.bonusTableMaker("current")}</div>
                <div className="center_content mg_t_2">
                  <Link to="/bonus/add" state={{ id: 0, challengeID: this.state.challengeID }}>
                    <Button color="orange" txt="Neuen Bonus erstellen" />
                  </Link>
                </div>
              </div>
            </div>

            {this.state.futureBonuses.length > 0 && (
              <div className="section_content">
                <div className="heading_underline_center">
                  <span className="underline_center" id="futureBonusText">
                    Zukünftige Bonusaktionen
                  </span>
                  <div className="mg_t_6">{this.bonusTableMaker("future")}</div>
                </div>
              </div>
            )}

            {this.state.pastBonuses.length > 0 && (
              <div className="section_content">
                <div className="heading_underline_center">
                  <span className="underline_center" id="pastBonusText">
                    Vorherige Bonusaktionen
                  </span>
                  <div className="mg_t_6">{this.bonusTableMaker("past")}</div>
                </div>
              </div>
            )}
          </div>
        </section>

        <section className="background_lightblue">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center">
                <span className="underline_center" id="sportText">
                  Sportarten dieser Challenge
                </span>
                <div className="mg_t_6">{this.sportTableMaker()}</div>
              </div>
            </div>
          </div>
        </section>

        <section className="background_white">
          <div className="section_container">
            <div className="section_content">
              <div className="heading_underline_center mg_b_7">
                <span className="underline_center" id="statsText">
                  Statistiken
                </span>
              </div>
              <div className="teamTable">
                <this.teamTableMaker />
              </div>
              {this.state.activities.length > 0 && this.state.teams.length > 0 && (
                <div className="graph_container mg_t_3">
                  <ChallengeTeamPanelAreaGraph
                    key="challengeArea"
                    activities={structuredClone(this.state.activities)}
                    width="50%"
                    lineColor="#C63328"
                    fillColor="#ff9f00"
                    startDate={structuredClone(this.state.challenge.startDate)}
                    endDate={structuredClone(this.state.challenge.endDate)}
                  />

                  <ChallengeTeamBarGraph key={"challengeBar"} teams={structuredClone(this.state.teams)} width="50%" lineColor="#C63328" fillColor="#ff9f00" />
                </div>
              )}
            </div>
          </div>
        </section>

        <section className="background_white">
          <div className="section_container">
            {this.state.teams.length > 0 && (
              <div>
                <div className="section_content">
                  <div className="team_panel_container">
                    <div className="team_pnael_tab_row">
                      {this.state.teams.map((item) => (
                        <Fragment key={item.id}>
                          <div className="team_panel_tab" data-team-id={item.id} onClick={this.teamChange}>
                            <img src={item.imageSource} alt="Team Logo"></img>
                          </div>
                        </Fragment>
                      ))}
                    </div>
                    <div className="team_panel">
                      <ChallengeTeamPanel
                        key="teamPanel"
                        id={this.state.teamID}
                        challenge={structuredClone(this.state.challenge)}
                        width="100%"
                        height={400}
                        lineColor="#00ff00"
                        fillColor="#ff0000"
                      />
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </section>
      </>
    );
  }
}
export default withRouter(Challenge);
